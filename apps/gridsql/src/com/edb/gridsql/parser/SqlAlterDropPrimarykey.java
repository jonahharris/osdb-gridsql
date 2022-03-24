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
 * SqlAlterDropPrimarykey.java
 *
 *
 */
package com.edb.gridsql.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.edb.gridsql.common.util.ParseCmdLine;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SyncAlterTableDropPrimaryKey;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysIndex;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 * Class SqlAlterDropPrimarykey Removes a Primary key Defined on a table.
 *
 *
 */

public class SqlAlterDropPrimarykey extends ObjectDepthFirst implements
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterDropPrimarykey.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String[] commands;

    /**
     * Constructor
     */
    public SqlAlterDropPrimarykey(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
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
        Vector<String> comm = new Vector<String>();
        String sql;

        final String method = "prepare";
        logger.entering(method, new Object[] {});
        try {

            // table has a primary key defined ?
            SysTable theTable = parent.getTable();
            theTable.ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            List<SysColumn> primaryKey = theTable.getPrimaryKey();
            if (primaryKey == null || primaryKey.size() == 0) {
                throw new XDBServerException(
                        ErrorMessageRepository.NO_PRIMARY_UNQIUE_INDEX + " ( "
                                + theTable.getTableName() + " )", 0,
                        ErrorMessageRepository.NO_PRIMARY_UNQIUE_INDEX_CODE);
            }

            // check primary key index for any references defined on it
            SysIndex primaryIndex = theTable.getPrimaryIndex();
            if (primaryIndex == null) {
                // FATAL error !!
                throw new XDBServerException(
                        ErrorMessageRepository.NO_PRIMARY_UNQIUE_INDEX + " ( "
                                + theTable.getTableName() + " )", 0,
                        ErrorMessageRepository.NO_PRIMARY_UNQIUE_INDEX_CODE);
            }
            if (theTable.getPrimaryConstraint().getSysTable() != theTable) {
                throw new XDBServerException(
                        "Can not drop inherited primary key");
            }
            if (theTable.isIndexReferenced(primaryIndex)) {
                throw new XDBServerException(
                        ErrorMessageRepository.PRIMARYKEY_REFRENCED, 0,
                        ErrorMessageRepository.PRIMARYKEY_REFRENCED_CODE);
            }
            HashMap<String,String> arguments = new HashMap<String,String>();
            arguments.put("table", IdentifierHandler.quote(theTable.getTableName()));
            arguments.put("constr_name", IdentifierHandler.quote(primaryIndex.idxname));

            sql = ParseCmdLine.substitute(
                    Props.XDB_SQLCOMMAND_ALTERTABLE_DROPPRIMAY, arguments);
            if (Props.XDB_SQLCOMMAND_ALTERTABLE_DROPPRIMAY_TO_PARENT) {
                parent.addCommonCommand(sql);
            } else {
                comm.add(sql);
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
                    new SyncAlterTableDropPrimaryKey(this), client);
            return null;

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * @return
     */
    public SqlAlterTable getParent() {
        return parent;
    }
}
