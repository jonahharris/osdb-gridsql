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

import com.edb.gridsql.optimizer.OrderByElement;
import com.edb.gridsql.parser.Command;
import com.edb.gridsql.parser.core.syntaxtree.NodeToken;
import com.edb.gridsql.parser.core.syntaxtree.OrderByItem;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This class is responsible to take care of the ORDER BY clause in a query at the parser level.
 */
public class OrderByClauseHandler extends ObjectDepthFirst {
    Command commandToExecute;

    /**
     * Class constructor
     * @param commandToExecute 
     */
    public OrderByClauseHandler(Command commandToExecute) {
        this.commandToExecute = commandToExecute;
    }

    /**
     * This variable contains the order by list -which is a collection of
     * orderbyElement class.
     */
    public Vector<OrderByElement> orderByList = new Vector<OrderByElement>();

    /**
     * f0 -> SQLSimpleExpression(prn) f1 -> [ "ASC" | "DESC" ]
     * @param n 
     * @param argu 
     * @return 
     */
    @Override
    public Object visit(OrderByItem n, Object argu) {
        SQLExpressionHandler aSqlExpressionHandler = new SQLExpressionHandler(
                commandToExecute);
        n.f0.accept(aSqlExpressionHandler, argu);
        OrderByElement aOrderByElement = new OrderByElement();

        aOrderByElement.orderExpression = aSqlExpressionHandler.aroot;
        orderByList.add(aOrderByElement);

        aOrderByElement.orderDirection = OrderByElement.ASC;
        n.f1.accept(this, aOrderByElement);
        return null;
    }

    /**
     * The information whether the order by clause is ASC or DESC is extracted
     * from the user specified string here
     * 
     * @param n
     * @param argu
     * @return
     */
    @Override
    public Object visit(NodeToken n, Object argu) {
        if (n.tokenImage.equalsIgnoreCase("ASC")) {
            OrderByElement argument = (OrderByElement) argu;
            argument.orderDirection = OrderByElement.ASC;
        }

        if (n.tokenImage.equalsIgnoreCase("DESC")) {
            OrderByElement argument = (OrderByElement) argu;
            argument.orderDirection = OrderByElement.DESC;
        }
        return null;
    }
}
