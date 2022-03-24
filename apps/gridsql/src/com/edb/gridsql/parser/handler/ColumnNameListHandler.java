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

import java.util.ArrayList;
import java.util.List;

import com.edb.gridsql.parser.core.syntaxtree.Identifier;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This class is responsible to handle any list which contains a list of
 * columnNames. It captures the visit functions for a ColumnNameList.
 */
public class ColumnNameListHandler extends ObjectDepthFirst {

    /**
     * This will contain the List of Name that this handler had to handle.
     */
    List<String> columnNameList = new ArrayList<String>();

    private IdentifierHandler ih = new IdentifierHandler();

    /**
     * A get function for accessing the information of this class
     *
     * @return This contains a list of String which will have the column names
     */
    public List<String> getColumnNameList() {
        return columnNameList;
    }

    /**
     * Constructor
     */
    public ColumnNameListHandler() {
    }

    /**
     * Grammar production: f0 -> <IDENTIFIER_NAME> | UnreservedWords(prn)
     * @param n
     * @param argu
     * @return
     */
    @Override
    public Object visit(Identifier n, Object argu) {
        columnNameList.add((String) n.accept(ih, argu));
        return null;
    }

}
