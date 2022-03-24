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
 * OrderByClause.java
 *
 */

package com.edb.gridsql.optimizer;

/**
 * 
 * DS
 */
public class OrderByElement implements IRebuildString {

    public static final int ASC = 1;

    public static final int DESC = 2;

    public SqlExpression orderExpression;

    public int orderDirection = ASC;

    /** Creates a new instance of OrderByClause */
    public OrderByElement() {

    }

    /**
     * 
     * @return 
     */

    public String getDirectionString() {
        switch (orderDirection) {
        case ASC:
            return "ASC";

        case DESC:
            return "DESC";

        default:
            // raise error?
        }
        return "";
    }

    /**
     * 
     * @return 
     */

    public String rebuildString() {
        String orderByString = "";
        orderExpression.rebuildExpression();
        orderByString = orderExpression.getExprString() + " " + getDirectionString();
        return orderByString;
    }
}
