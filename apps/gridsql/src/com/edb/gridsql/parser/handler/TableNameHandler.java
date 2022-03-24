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

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.syntaxtree.TableName;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * This class provides the parser-level handling for a table name that appears
 * in a query.
 */
public class TableNameHandler extends ObjectDepthFirst {

    private String tableName;

    private String referenceName;

    private boolean isTemporary;

    private XDBSessionContext client;

    public TableNameHandler(XDBSessionContext client) {
        this.client = client;
    }

    /**
     * It returns the table name represented by tableName attribute.
     * @return A String value represented by tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * It returns the table reference name represented by referenceName attribute.
     * @return A String value represented by referenceName.
     */
    public String getReferenceName() {
        return referenceName;
    }

    /**
     * It returns isTemporary flag that indicates if the table is temporary or not.
     * @return A boolean value represented by isTemporary flag.
     */
    public boolean isTemporary() {
        return isTemporary;
    }

    /**
     * Grammar production:
     * f0 -> ( Identifier(prn) | <TEMPDOT_> Identifier(prn) 
     * | <PUBLICDOT_> Identifier(prn) | <QPUBLICDOT_> Identifer(prn)
     * )
     */
    @Override
    public Object visit(TableName n, Object argu) {
        IdentifierHandler identifierHandler = new IdentifierHandler();
        switch (n.f0.which) {
            case 0:
            {
                referenceName = (String) n.f0.choice.accept(identifierHandler, argu);
                tableName = client.getTempTableName(referenceName);
                if (tableName == null) {
                    tableName = referenceName;
                    isTemporary = false;
                } else {
                    isTemporary = true;
                }
                break;
            }
            case 1:
            {
                tableName = "TEMP.";
                NodeSequence ns = (NodeSequence) n.f0.choice;
                tableName += (String) ns.elementAt(1).accept(identifierHandler, argu);
                referenceName = tableName;
                isTemporary = true;
                break;
            }
            // handle public.* case
            case 2:
            case 3:
            {
                n.f0.choice.accept(identifierHandler, argu);
                referenceName = identifierHandler.getIdentifier();
                tableName = identifierHandler.getIdentifier();
                isTemporary = false;
                break;
            }
        }
        return null;
    }
}
