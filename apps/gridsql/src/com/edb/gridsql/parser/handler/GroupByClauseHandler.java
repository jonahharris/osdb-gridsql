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
package com.edb.gridsql.parser.handler;

import java.util.Vector;

import com.edb.gridsql.optimizer.QueryCondition;
import com.edb.gridsql.parser.Command;
import com.edb.gridsql.parser.core.syntaxtree.HavingClause;
import com.edb.gridsql.parser.core.syntaxtree.SQLExpressionList;
import com.edb.gridsql.parser.core.syntaxtree.SQLExpressionListItem;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This class is responsible for handling the group by clause visitors. It holds
 * the information about the group by clause.
 * 
 */
public class GroupByClauseHandler extends ObjectDepthFirst {
    private Command commandToExecute;

    /**
     * Class constructor
     * @param commandToExecute 
     */
    public GroupByClauseHandler(Command commandToExecute) {
        this.commandToExecute = commandToExecute;
    }

    /**
     * This contains the list of expression for which we intend to do group by.
     */
    public Vector expressionList = new Vector();

    /**
     * This contains the root condition for the having clause if any.
     */
    public Vector havingList = new Vector();

    /**
     * f0 -> SQLExpressionListItem(prn) f1 -> ( "," SQLExpressionListItem(prn) )*
     * 
     * @param n
     * @param argu
     * @return
     */
    @Override
    public Object visit(SQLExpressionList n, Object argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> SQLSimpleExpression(prn)
     * @param n 
     * @param argu 
     * @return 
     */
    @Override
    public Object visit(SQLExpressionListItem n, Object argu) {
        SQLExpressionHandler sqlHandler = new SQLExpressionHandler(
                commandToExecute);
        n.f0.accept(sqlHandler, argu);
        expressionList.add(sqlHandler.aroot);
        return null;
    }

    /**
     * f0 -> "HAVING" f1 -> SQLComplexExpression(prn)
     * @param n 
     * @param argu 
     * @return 
     */
    @Override
    public Object visit(HavingClause n, Object argu) {
        n.f0.accept(this, argu);
        QueryConditionHandler qch = new QueryConditionHandler(commandToExecute);
        n.f1.accept(qch, argu);
        QueryCondition qc = qch.aRootCondition;
        havingList.add(qc);
        return null;
    }
}
