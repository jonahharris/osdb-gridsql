/*****************************************************************************
 * Copyright (C) 2008 EnterpriseDB Corporation.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses or write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 *
 * You can contact EnterpriseDB, Inc. via its website at
 * http://www.enterprisedb.com
 *
 ****************************************************************************/
/*
 * parserHelper.java
 *
 * This contains various methods for helping with manual parsing.
 * 
 */

package com.edb.gridsql.parser;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.optimizer.AttributeColumn;
import com.edb.gridsql.optimizer.QueryCondition;
import com.edb.gridsql.optimizer.QueryTree;
import com.edb.gridsql.optimizer.RelationNode;
import com.edb.gridsql.optimizer.SqlExpression;

/**
 * A group of helper methods for manual parsing.
 *
 * 
 */
public class ParserHelper {

    /** Creates a new instance of parserHelper */
    public ParserHelper() {
    }

    /**
     * Get a simple condition like col = 3. If it is not simple, we just give
     * up.
     *
     * This assumes it is in the format of "column simple_operation value"
     *
     * @param Lexer
     *            aLexer
     * @param SysTable
     *            aSysTable,
     * @param QueryTree
     *            aQueryTree
     * @param client
     * @return QueryCondition
     */
    public static QueryCondition getSimpleCondition(Lexer aLexer,
            SysTable aSysTable, QueryTree aQueryTree, XDBSessionContext client) {

        // Get left expression as a condition
        QueryCondition leftCond = new QueryCondition();
        leftCond.setCondType(QueryCondition.QC_SQLEXPR);
        leftCond.setExpr(createSimpleSqlExpression(aLexer, aSysTable,
                aQueryTree.getRelationNodeList().get(0), client));
        if (leftCond.getExpr() == null) {
            // parser error
            return null;
        }

        String operator;
        if (aLexer.hasMoreTokens()) {
            if (aLexer.isOperator(aLexer.peekToken(0))) {
                operator = aLexer.nextToken();
            } else {
                // Perhaps the expression has return type boolean
                return leftCond;
            }
        } else {
            // Perhaps the expression has return type boolean
            return leftCond;
        }

        // Get right expression as a condition
        QueryCondition rightCond = new QueryCondition();
        rightCond.setCondType(QueryCondition.QC_SQLEXPR);
        rightCond.setExpr(createSimpleSqlExpression(aLexer, aSysTable,
                aQueryTree.getRelationNodeList().get(0), client));
        if (rightCond.getExpr() == null) {
            // parser error
            return null;
        }

        // put the two together
        QueryCondition aQC = new QueryCondition();
        aQC.setCondType(QueryCondition.QC_RELOP);
        aQC.setOperator(operator);
        aQC.setLeftCond(leftCond);
        aQC.setRightCond(rightCond);
        aQC.getRelationNodeList().addAll(aQueryTree.getRelationNodeList());

        return aQC;
    }

    /**
     * Takes two QueryConditions and combines them, typically with AND. This is
     * used when manually parsing UPDATE and DELETE and ANDing the conditions
     * tgether
     *
     * @param QueryCondition
     *            leftQC
     * @param String
     *            operator
     * @param QueryCondition
     *            rightQC
     *
     * @return QueryCondition
     */
    public static QueryCondition chainQueryConditions(QueryCondition leftQC,
            String operator, QueryCondition rightQC) {
        // Note, when we later handle SELECT, we want these instead in
        // a list.
        QueryCondition newQC = new QueryCondition();
        newQC.setCondType(QueryCondition.QC_CONDITION);
        newQC.setOperator(operator);
        newQC.setLeftCond(leftQC);
        newQC.setRightCond(rightQC);

        return newQC;
    }

    /**
     * Create a SqlExpression for a column.
     *
     * @param String
     *            columnName
XTE     * @param SysTable
     *            aSysTable
     * @param RelationNode
     *            aRelationNode
     *
     * @return SqlExpression
     */
    private static SqlExpression createColumnSqlExpression(String columnName,
            SysTable aSysTable, RelationNode aRelationNode) {

        SysColumn aSysColumn = aSysTable.getSysColumn(columnName);

        if (aSysColumn == null) {
            throw new XDBServerException("Invalid column name " + columnName);
        }

        // now we can add the column.
        ExpressionType anET = new ExpressionType();
        anET.type = aSysColumn.getColType();
        anET.length = aSysColumn.getColumnLength();

        AttributeColumn anAC = new AttributeColumn();
        anAC.setTableName(aSysTable.getTableName());
        anAC.columnName = columnName;
        // anAC.columnAlias = columnAlias;

        // there should just be one relationNode here
        anAC.relationNode = aRelationNode;

        SqlExpression aSE = new SqlExpression();
        aSE.setExprType(SqlExpression.SQLEX_COLUMN);
        aSE.setColumn(anAC);
        aSE.setExprDataType(anET);

        return aSE;
    }

    /**
     * Support simple SQL expressions:<br>
     * &lt;operator&gt; ::= (&quot;+&quot; | &quot;-&quot; | &quot;*&quot; |
     * &quot;/&quot; | &quot;%&quot;| &quot;MOD&quot;| &quot;DIV&quot;)<br>
     * &lt;operand&gt; ::= (&lt;column&gt; | &lt;const&gt;)<br>
     * &lt;expression&gt; ::= ([&quot;(&quot;] (
     * &lt;operand&gt; | &lt;expression&gt;) [&lt;operator&gt;
     * (&lt;operand&gt; | &lt;expression&gt;)] [&quot;)&quot;])
     * @param aLexer
     * @param aSysTable
     * @param aRelationNode
     * @param client
     * @return
     */
    public static SqlExpression createSimpleSqlExpression(Lexer aLexer,
            SysTable aSysTable, RelationNode aRelationNode,
            XDBSessionContext client) {
        if (!aLexer.hasMoreTokens()) {
            return null;
        }

        boolean paren;
        SqlExpression lhExpr = null;
        String token = aLexer.nextToken();
        if ("(".equals(token)) {
            paren = true;
            // get the expression until balancing paren
            lhExpr = createSimpleSqlExpression(aLexer, aSysTable,
                    aRelationNode, client);
        } else {
            paren = false;
            // column or constant
            if (Character.isDigit(token.charAt(0))) {
                // numeric constant
                lhExpr = new SqlExpression();
                lhExpr.setExprType(SqlExpression.SQLEX_CONSTANT);
                lhExpr.setConstantValue(token);
                lhExpr.setExprString(token);
            } else if (token.charAt(0) == '\'') {
                // string or date/time
                lhExpr = new SqlExpression();
                lhExpr.setExprType(SqlExpression.SQLEX_CONSTANT);
                lhExpr.setConstantValue(token);
                lhExpr.setExprString(token);

                // TODO handle other constant types

            } else if (Character.isLetter(token.charAt(0)) ||
                    token.charAt(0) == '_') {
                // identifier probably qualified column name
                if (aSysTable.getTableName().equalsIgnoreCase(token) &&
                        ".".equals(aLexer.peekToken(0))) {
                    // skip dot
                    aLexer.nextToken();
                    // get actual column name
                    token = aLexer.nextToken();
                /*    
                } else if (token.toUpperCase().startsWith("CURRENT")) {
                    lhExpr = new SqlExpression();
                    if (token.equalsIgnoreCase("CURRENT")
                            || token.equalsIgnoreCase("CURRENT_TIMESTAMP")) {
                        lhExpr.getCurrentTimestamp();
                    } else if (token.equalsIgnoreCase("CURRENT_TIME")) {
                        lhExpr.getCurrentTime();
                    } else if (token.equalsIgnoreCase("CURRENT_DATE")) {
                        lhExpr.getCurrentDate();
                    } else if (token.equalsIgnoreCase("CURRENT_USER")) {
                        lhExpr.setExprType(SqlExpression.SQLEX_CONSTANT);
                        lhExpr.setConstantValue("'" + client.getCurrentUser().getName()+ "'");
                        lhExpr.setExprString(lhExpr.getConstantValue());
                    }
                } else if (token.equalsIgnoreCase("SYSDATE")) {
                    lhExpr = new SqlExpression();
                    lhExpr.getSysDate();
                } else if (token.equalsIgnoreCase("USER")) {
                    lhExpr = new SqlExpression();
                    lhExpr.setExprType(SqlExpression.SQLEX_CONSTANT);
                    lhExpr.setConstantValue("'" + client.getCurrentUser().getName() + "'");
                    lhExpr.setExprString(lhExpr.getConstantValue());
                    */
                }
                // only create column expression if we did not create it above for timestamps
                if (lhExpr == null) {
                    lhExpr = createColumnSqlExpression(token, aSysTable,
                                aRelationNode);
                }
            }
        }

        // check if we have already done
        if (paren) {
            if (")".equals(aLexer.peekToken(0))) {
                return lhExpr;
            }
        } else {
            if (!aLexer.hasMoreTokens()) {
                return lhExpr;
            }
        }

        // check if we failed to get first exception
        if (lhExpr == null) {
            return null;
        }

        SqlExpression theExpr = new SqlExpression();
        theExpr.setExprType(SqlExpression.SQLEX_OPERATOR_EXPRESSION);

        // next should be an operator
        token = aLexer.peekToken(0);
        if (token.charAt(0) == '+' || token.charAt(0) == '-' ||
                token.charAt(0) == '*' || token.charAt(0) == '/' ||
                token.charAt(0) == '%' || "DIV".equalsIgnoreCase(token) ||
                "MOD".equalsIgnoreCase(token)) {
            theExpr.setOperator(aLexer.nextToken());
        } else {
            return lhExpr;
        }

        SqlExpression rhExpr = createSimpleSqlExpression(aLexer, aSysTable,
                aRelationNode, client);

        // check if we failed to get second exception
        if (rhExpr == null) {
            return null;
        }

        theExpr.setLeftExpr(lhExpr);

        // Take into account precedence
        if (rhExpr.getExprType() == SqlExpression.SQLEX_OPERATOR_EXPRESSION
                && getOperatorPrecedence(theExpr.getOperator()) > getOperatorPrecedence(rhExpr.getOperator())) {
            theExpr.setRightExpr(rhExpr.getLeftExpr());
            rhExpr.setLeftExpr(theExpr);
            theExpr = rhExpr;
        } else {
            theExpr.setRightExpr(rhExpr);
        }
        theExpr.setExprDataType(SqlExpression
                    .setExpressionResultType(theExpr, aSysTable.getSysDatabase()));

        return theExpr;
    }

    private static int getOperatorPrecedence(String operator) {
        if ("+".equals(operator) || "-".equals(operator)) {
            return 1;
        } else { // * / % DIV MOD
            return 2;
        }
    }

    /**
     * Parses the projections for a SELECT from a single table.
     *
     * @param String
     *            projString
     * @param QueryTree
     *            aQueryTree
     * @param RelationNode
     *            aRelationNode
     * @param SysTable
     *            aSysTable
     *
     */
    public static boolean parseProjectionsForSingleTable(String projString,
            QueryTree aQueryTree, RelationNode aRelationNode,
            SysTable aSysTable, XDBSessionContext client) {

        // Now we go back and do projection list
        Lexer aLexer = new Lexer(projString.trim());

        if (!aLexer.hasMoreTokens()) {
            // No projection list
            return false;
        }

        while (aLexer.hasMoreTokens()) {
            // special handling for * or tablename.*
            boolean foundStar = false;

            if ("*".equals(aLexer.peekToken(0))) {
                foundStar = true;
                //swallow token
                aLexer.nextToken();
            } else if (aSysTable.getTableName().equalsIgnoreCase(aLexer.peekToken(0)) &&
                    ".".equals(aLexer.peekToken(1)) && "*".equals(aLexer.peekToken(2))) {
                aLexer.nextToken();
                aLexer.nextToken();
                aLexer.nextToken();
            }
            if (foundStar) {
                // We need to add all columns
                // populateProjectionList seemed to leave off ExprDataType
                // for AC, so we do this by hand instead.
                for (SysColumn aSysColumn : aSysTable.getColumns()) {
                    if (aSysColumn.getColName().equalsIgnoreCase(
                            SqlCreateTableColumn.XROWID_NAME)) {
                        continue;
                    }

                    SqlExpression aColumnSE = createColumnSqlExpression(
                            aSysColumn.getColName(), aSysTable, aRelationNode);

                    aRelationNode.getProjectionList().add(aColumnSE);
                    aQueryTree.getProjectionList().add(aColumnSE);
                }
                if (aLexer.hasMoreTokens()) {
                    if (!",".equals(aLexer.nextToken())) {
                        return false;
                    }
                }
                continue;
            }

            SqlExpression aSE = createSimpleSqlExpression(aLexer, aSysTable,
                    aRelationNode, client);
            // now check for a column alias
            if (aLexer.hasMoreTokens()) {
                String token = aLexer.nextToken();
                if (!",".equals(token) &&
                        token.matches("^[a-zA-Z_]([a-zA-Z0-9_$#])*+$")
                   ) {
                    aSE.setAlias(token);

                    if (aLexer.hasMoreTokens()) {
                        if (!",".equals(aLexer.nextToken())) {
                            return false;
                        }
                    }
                }
            }

            aRelationNode.getProjectionList().add(aSE);
            aQueryTree.getProjectionList().add(aSE);

        }
        return true;
    }
}
