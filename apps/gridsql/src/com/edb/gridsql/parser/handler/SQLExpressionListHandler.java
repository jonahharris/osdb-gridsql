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

import com.edb.gridsql.parser.Command;
import com.edb.gridsql.parser.core.syntaxtree.SQLExpressionList;
import com.edb.gridsql.parser.core.syntaxtree.SQLExpressionListItem;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This class constructs the list of SqlExpression objects that is utilized
 * by QueryConditionHandler class.
 */
public class SQLExpressionListHandler extends ObjectDepthFirst {
    Command commandToExecute;

    public SQLExpressionListHandler(Command commandToExecute) {
        this.commandToExecute = commandToExecute;
    }

    Vector vSqlExpressionList = new Vector();

    /**
     * f0 -> SQLExpressionListItem(prn) f1 -> ( "," SQLExpressionListItem(prn) )*
     */
    @Override
    public Object visit(SQLExpressionList n, Object argu) {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        return null;
    }

    /**
     * f0 -> SQLSimpleExpression(prn)
     */
    @Override
    public Object visit(SQLExpressionListItem n, Object argu) {
        SQLExpressionHandler aSqlExpressionHandler = new SQLExpressionHandler(
                commandToExecute);
        n.f0.accept(aSqlExpressionHandler, argu);
        vSqlExpressionList.add(aSqlExpressionHandler.aroot);
        return null;
    }

}
