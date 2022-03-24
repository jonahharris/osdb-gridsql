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
 * SqlAlterAddPrimary.java
 *
 *
 */

package com.edb.gridsql.parser;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SyncAlterTableInherit;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.core.syntaxtree.Inherit;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.TableNameHandler;

/**
 * Class for adding a PRIMARY KEY to a table
 *
 *
 */

public class SqlAlterInherit extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterInherit.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String tableName = null;

    private SysTable table = null;

    private boolean noInherit;

    private String[] commands;

    /**
     * @param table
     * @param client
     */
    public SqlAlterInherit(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * Grammar production:
     * f0 -> [ <NO_> ]
     * f1 -> <INHERIT_>
     * f2 -> TableName(prn)
     */
    @Override
    public Object visit(Inherit n, Object argu) {
        noInherit = n.f0.present();
        TableNameHandler tnh = new TableNameHandler(client);
        n.f2.accept(tnh, argu);
        tableName = tnh.getTableName();
        return null;
    }

    public boolean isNoInherit() {
        return noInherit;
    }

    public String getTableName() {
        return tableName;
    }

    public SysTable getTable() {
        if (table == null) {
            table = client.getSysDatabase().getSysTable(tableName);
        }
        return table;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return commands != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#prepare()
     */
    public void prepare() throws Exception {
        final String method = "prepare";
        logger.entering(method, new Object[] {});
        try {

            commands = new String[0];
            parent.getTable().ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            SysTable parentTable = getTable();
            SysTable targetTable = parent.getTable();
            if (noInherit) {
                if (parentTable != targetTable.getParentTable()) {
                    throw new XDBServerException("Table "
                            + parent.getTableName()
                            + " does not inherits from " + tableName);
                }
            } else {
                if (targetTable.getParentTable() != null) {
                    throw new XDBServerException("Table "
                            + parent.getTableName()
                            + " already inherits from "
                            + targetTable.getParentTable().getTableName());
                }
                // Check partitioning
                if (!parentTable.getPartitionMap().equals(
                        targetTable.getPartitionMap())) {
                    throw new XDBServerException("Partitioning of table "
                            + parent.getTableName() + " differs from "
                            + tableName);
                }
                if (parentTable.getPartitionColumn() == null) {
                    if (targetTable.getPartitionColumn() != null) {
                        throw new XDBServerException("Partitioning of table "
                                + parent.getTableName() + " differs from "
                                + tableName);
                    }
                } else {
                    if (targetTable.getPartitionColumn() == null
                            || !targetTable.getPartitionColumn().equals(
                                    parentTable.getPartitionColumn())) {
                        throw new XDBServerException("Partitioning of table "
                                + parent.getTableName() + " differs from "
                                + tableName);
                    }
                }
                // Check columns
                if (parentTable.getColumns().size() > targetTable.getColumns().size()) {
                    throw new XDBServerException("Columns of table "
                            + parent.getTableName()
                            + " are not compatible with "
                            + tableName);
                }
                for (int i = 0; i < parentTable.getColumns().size(); i++) {
                    SysColumn parentColumn = parentTable.getColumns().get(i);
                    SysColumn targetColumn = targetTable.getColumns().get(i);
                    if (!parentColumn.getColName().equals(
                            targetColumn.getColName())) {
                        throw new XDBServerException(
                                "Columns of table "
                                        + parent.getTableName()
                                        + " are not compatible with "
                                        + tableName);
                    }
                    if (parentColumn.getColType() != targetColumn.getColType()) {
                        throw new XDBServerException(
                                "Columns of table "
                                        + parent.getTableName()
                                        + " are not compatible with "
                                        + tableName);
                    }
                    if (parentColumn.getColLength() != targetColumn.getColLength()) {
                        throw new XDBServerException(
                                "Columns of table "
                                        + parent.getTableName()
                                        + " are not compatible with "
                                        + tableName);
                    }
                    if (parentColumn.getColScale() != targetColumn.getColScale()) {
                        throw new XDBServerException(
                                "Columns of table "
                                        + parent.getTableName()
                                        + " are not compatible with "
                                        + tableName);
                    }
                    if (parentColumn.getColPrecision() != targetColumn.getColPrecision()) {
                        throw new XDBServerException(
                                "Columns of table "
                                        + parent.getTableName()
                                        + " are not compatible with "
                                        + tableName);
                    }
                }
            }
            String sql = "ALTER TABLE "
                    + IdentifierHandler.quote(parent.getTableName())
                    + (noInherit ? " NO" : "") + " INHERIT "
                    + IdentifierHandler.quote(tableName);
            commands = new String[] { sql };

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {
            if (commands != null && commands.length != 0) {
                engine.executeDDLOnMultipleNodes(commands,
                        parent.getNodeList(), new SyncAlterTableInherit(this),
                        client);
            }
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
