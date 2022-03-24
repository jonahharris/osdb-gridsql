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

import java.util.ArrayList;
import java.util.HashMap;

import com.edb.gridsql.common.util.ParseCmdLine;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SyncAlterTableAddColumn;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.core.syntaxtree.Node;
import com.edb.gridsql.parser.core.syntaxtree.NodeChoice;
import com.edb.gridsql.parser.core.syntaxtree.NodeOptional;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 * For adding a new Column
 */
public class SqlAlterAddColumn extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterAddColumn.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private SqlCreateTableColumn aSqlCreateTableColumn;

    // If the user specifes that the position is First then the
    // column has to be create at the first location in the table
    boolean positionFirst = false;

    // Incase the user specifies the position after which we have to
    // create the column - isPositionAfter should be changed to true
    // and the after column should contain the name of the column
    // We then need to verify if the column Name is a valid column name.
    private String afterColumnName = null;

    private String[] commands;

    /**
     * @param table
     * @param client
     */
    public SqlAlterAddColumn(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * f0 -> [ <COLUMN_> ] f1 -> ColumnDeclare(prn) f2 -> [ <FIRST_> | <AFTER_>
     * <IDENTIFIER_NAME> ]
     */
    @Override
    public Object visit(NodeSequence n, Object argu) {
        Object _ret = null;
        Node columnDeclare = n.elementAt(1);
        aSqlCreateTableColumn = new SqlCreateTableColumn(parent
                .getCommandToExecute());
        // Extract the column
        columnDeclare.accept(aSqlCreateTableColumn, null);
        // Get the position
        NodeOptional position = (NodeOptional) n.elementAt(2);
        if (position.present()) {
            NodeChoice aNodeChoice = (NodeChoice) position.node;
            switch (aNodeChoice.which) {
            case 0:
                positionFirst = true;
                break;
            case 1:
                NodeSequence aPositionDetail = (NodeSequence) aNodeChoice.choice;
                afterColumnName = (String) aPositionDetail.elementAt(1).accept(
                        new IdentifierHandler(), argu);
                break;
            }

        }
        return _ret;
    }

    /**
     * @return Returns the aSqlCreateTableColumn.
     */
    public SqlCreateTableColumn getColDef() {
        return aSqlCreateTableColumn;
    }

    /**
     * @return Returns the parent.
     */
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
        ArrayList<String> comm = new ArrayList<String>();
        logger.entering(method, new Object[] {});
        try {

            SysTable table = parent.getTable();
            table.ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            if (table.getSysColumn(aSqlCreateTableColumn.columnName) != null) {
                throw new XDBServerException(
                        "The Table Already Has A Column Named "
                                + aSqlCreateTableColumn.columnName);
            }
            if (aSqlCreateTableColumn.isSerial()
                    && table.getSerialColumn() != null) {
                throw new XDBServerException(
                        "The Table Already Has A Serial Column");
            }
            String sql = Props.XDB_SQLCOMMAND_ALTERTABLE_ADDCOLUMN;
            HashMap<String, String> arguments = new HashMap<String, String>();
            arguments.put("table", IdentifierHandler.quote(table.getTableName()));
            arguments.put("colname", aSqlCreateTableColumn.rebuildString());

            sql = ParseCmdLine.substitute(sql, arguments);
            if (Props.XDB_SQLCOMMAND_ALTERTABLE_ADDCOLUMN_TO_PARENT) {
                parent.addCommonCommand(sql);
            } else {
                comm.add(sql);
            }

            if (afterColumnName != null) {
                if (table.getSysColumn(afterColumnName) == null) {
                    throw new XDBServerException(
                            "The Table Does Not Have A Column Named "
                                    + afterColumnName);
                }
                // TODO
            }
            if (aSqlCreateTableColumn.isSerial()) {
                String index = "create index "
                        + SqlCreateTableColumn.IDX_SERIAL_NAME + " on "
                        + table.getTableName() + " ( "
                        + aSqlCreateTableColumn.columnName + " ) ";
                comm.add(index);
            }
            commands = comm.toArray(new String[comm.size()]);
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

            engine.executeDDLOnMultipleNodes(commands, parent.getNodeList(),
                    new SyncAlterTableAddColumn(this), client);
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
