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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.optimizer.SqlExpression;
import com.edb.gridsql.parser.handler.IdentifierHandler;

// -----------------------------------------------------------------
public class SqlCreateIndexKey {
    private int keyColumnId;

    private SqlExpression keyExpression;

    private List<String> keyColumnNames;

    private String keySequence;

    private String colOperator;

    // -----------------------------------
    /**
     * Constructor
     */
    public SqlCreateIndexKey(int keyColumnId, String keyColumnName,
            String keySequence, String colOperator) {
        this.keyColumnId = keyColumnId;
        this.keyExpression = null;
        this.keyColumnNames = Collections.singletonList(keyColumnName);
        this.keySequence = keySequence;
        this.colOperator = colOperator;
    }

    public SqlCreateIndexKey(SqlExpression keyExpression, String keySequence,
            String colOperator) {
        this.keyColumnId = -1;
        this.keyExpression = keyExpression;
        this.keyColumnNames = new LinkedList<String>();
        for (SqlExpression columnExpr : SqlExpression.getNodes(keyExpression,
                SqlExpression.SQLEX_COLUMN)) {
            keyColumnNames.add(columnExpr.getColumn().columnName);
        }
        this.keySequence = keySequence;
        this.colOperator = colOperator;
    }

    /**
     * Constructor
     *
     * @param keyColumnName
     *            name of the column
     * @param keySequence
     */
    public SqlCreateIndexKey(String keyColumnName, String keySequence,
            String colOperator) {
        this(-1, keyColumnName, keySequence, colOperator);
    }

    /**
     * @return Returns the keyColumnId.
     */
    public int getKeyColumnId() {
        return keyColumnId;
    }

    public String rebuildString() {
        String fullString;
        if (keyExpression == null) {
            fullString = IdentifierHandler.quote(keyColumnNames.get(0));
        } else {
            fullString = "(" + keyExpression.rebuildString() + ")";
        }

        if (Props.XDB_INDEX_USEASCDESC && keySequence != null) {
            fullString += " " + keySequence;
        }
        if (getColOperator() != null) {
            fullString += " " + getColOperator();
        }

        return fullString;
    }

    public boolean isAscending() {
        return keySequence == null || "ASC".equalsIgnoreCase(keySequence);
    }

    public List<String> getKeyColumnNames() {
        return keyColumnNames;
    }

    public String getColOperator() {
        return colOperator;
    }

    public void setColOperator(String colOperator) {
        this.colOperator = colOperator;
    }
}
