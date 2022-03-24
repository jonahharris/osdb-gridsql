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
package com.edb.gridsql.parser;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.List;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.optimizer.SqlExpression;
import com.edb.gridsql.parser.handler.IFunctionID;

/**
 * This class is used to by Insert Table Command, where we create a tuple to
 * insert This should be useful when we support bulk inserts
 */
public class Tuple {
    private XDBSessionContext client;

    /**
     *
     * @return returns the aSysTable to which this particular tuple belongs
     */
    public SysTable getaSysTable() {
        return aSysTable;
    }

    // Information Classes and data
    private SysTable aSysTable;

    private LinkedHashMap<SysColumn, SqlExpression> mapColumnValueList = new LinkedHashMap<SysColumn, SqlExpression>();

    /**
     * Constructor
     *
     * @param tableName
     *            The Table into ehich
     * @param columnNameList
     *            contains the column name list
     * @param valueList
     *            Contains the list of values which will be inserted in the
     *            table specified
     * @throws IllegalArgumentException -
     *             The function checks for all the column names and finds out if
     *             it exists in the metadata table, incase we are not able to
     *             find it we throw an exception
     */
    public Tuple(String tableName, List<SysColumn> columnList,
            List<SqlExpression> valueList, XDBSessionContext client) {
        this.client = client;
        aSysTable = client.getSysDatabase().getSysTable(getTableName(tableName));

        for (int i = 0; i < valueList.size(); i++) {
            SqlExpression value = valueList.get(i);
            if (value.getExprType() == SqlExpression.SQLEX_FUNCTION
                    && value.getFunctionId() == IFunctionID.CAST_ID) {
                value = setValuesOfTuple(columnList.get(i), valueList.get(i));
            }
            if (value.getExprType() == SqlExpression.SQLEX_PARAMETER) {
                value.setExprDataType(value.setExpressionResultType(value,
                        columnList.get(i)));
            }
            SysColumn column = columnList.get(i);
            if (value.getExprDataType() == null) {
                value.setExprDataType(new ExpressionType(column));
            }
            mapColumnValueList.put(column, value);
        }
    }

    /**
     * @param column
     * @param expression
     * @return
     */
    private SqlExpression setValuesOfTuple(SysColumn column, SqlExpression expr) {
        expr.rebuildExpression();
        expr.setExprDataType(SqlExpression.setExpressionResultType(expr,
                client.getSysDatabase()));
        return expr;
    }

    public String getValue(String columnName) {
        return getValue(aSysTable.getSysColumn(columnName));
    }

    public String getValue(SysColumn column) {
        SqlExpression theResult = getExpression(column);
        if (theResult != null
                && theResult.getExprType() == SqlExpression.SQLEX_CONSTANT
                && theResult.getConstantValue() != null) {
            int aType = column.getColType();
            try {
                if (aType == Types.DATE) {
                    theResult.setConstantValue(SqlExpression.normalizeDate(theResult.getConstantValue()));
                } else if (aType == Types.TIME) {
                    theResult.setConstantValue(SqlExpression.normalizeTime(theResult.getConstantValue()));
                }
            } catch (Exception ex) {
                // fallback
                aType = Types.TIMESTAMP;
            }
            if (aType == Types.TIMESTAMP) {
                theResult.setConstantValue(SqlExpression.normalizeTimeStamp(theResult.getConstantValue()));
            }
        }
        return theResult == null ? null : theResult.rebuildString(client);
    }

    public SqlExpression getExpression(String columnName) {
        return getExpression(aSysTable.getSysColumn(columnName));
    }

    public SqlExpression getExpression(SysColumn column) {
        SqlExpression expr = mapColumnValueList.get(column);
        if (expr == null) {
            try {
                expr = column.getDefaultExpr(client);
                mapColumnValueList.put(column, expr);
            } catch (Exception e) {
                // TODO handle
            }
        }
        return expr;
    }

    public String getTableName(String tableName) {
        return tableName;
    }
}
