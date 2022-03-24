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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;

import com.edb.gridsql.common.util.ParseCmdLine;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncAlterDropColumn;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.SysView;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 * Class for dropping a column
 */
public class SqlAlterDropColumn extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterAddColumn.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String columnName;

    private String[] commands = null;

    /**
     * @param table
     * @param client
     */
    public SqlAlterDropColumn(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * f0 -> <COLUMN_> f1 -> IdentifierAndUnreservedWords
     */
    @Override
    public Object visit(NodeSequence n, Object argu) {
        Object _ret = null;
        columnName = (String) n.elementAt(1).accept(new IdentifierHandler(), argu);
        return _ret;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    /**
     * @return Returns the columnName.
     */
    public String getColumnName() {
        return columnName;
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
        LinkedList<String> comm = new LinkedList<String>();
        String sql;

        logger.entering(method, new Object[] {});
        try {

            SysTable table = parent.getTable();
            table.ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            // also make sure that this table contains
            // the col we are trying to drop
            SysColumn sysCol = table.getSysColumn(columnName);
            if (sysCol == null) {
                throw new XDBServerException(
                        ErrorMessageRepository.COLUMN_NOT_IN_TABLE + " ( "
                                + columnName + " , " + table.getTableName()
                                + " ) ", 0,
                        ErrorMessageRepository.COLUMN_NOT_IN_TABLE_CODE);
            }
            //
            Enumeration eViews = parent.getDatabase().getAllViews();
            while (eViews.hasMoreElements()) {
                SysView view = (SysView) eViews.nextElement();
                if (view.hasDependedColumn(sysCol.getColID())) {
                    XDBSecurityException ex = new XDBSecurityException(
                            "cannot drop table " + table.getTableName()
                                    + " column " + sysCol.getColName()
                                    + " because other objects depend on it");
                    throw ex;
                }
            }

            // Make sure this is not a partitoning column
            if (table.getPartitionedColumn() == sysCol) {
                throw new XDBServerException(
                        ErrorMessageRepository.PARTITIONING_COLUMN_OF_TABLE
                                + " ( " + columnName + " , "
                                + table.getTableName() + " ) ",
                        0,
                        ErrorMessageRepository.PARTITIONING_COLUMN_OF_TABLE_CODE);
            }
            // Make sure this is not a primary key for this table
            if (sysCol.getIndexType() == MetaData.INDEX_TYPE_PRIMARY_KEY) {
                throw new XDBServerException(
                        ErrorMessageRepository.PRIMARYKEY_COLUMN_OF_TABLE
                                + " ( " + columnName + " , "
                                + table.getTableName() + " ) ", 0,
                        ErrorMessageRepository.PRIMARYKEY_COLUMN_OF_TABLE_CODE);
            }
            // Make sure there are NO foreign references to this column
            Enumeration e = sysCol.getChildColumns();
            if (e.hasMoreElements()) {
                throw new XDBServerException(
                        ErrorMessageRepository.COLUMN_REFFERENCES_EXIST + " ( "
                                + columnName + " , " + table.getTableName()
                                + " ) ", 0,
                        ErrorMessageRepository.COLUMN_REFFERENCES_EXIST_CODE);
            }

            if (table != sysCol.getSysTable()) {
                throw new XDBServerException("Can not drop inherited column");
            }

            HashMap<String,String> arguments = new HashMap<String,String>();
            arguments.put("table", IdentifierHandler.quote(table.getTableName()));
            arguments.put("column", IdentifierHandler.quote(columnName));
            sql = ParseCmdLine.substitute(
                    Props.XDB_SQLCOMMAND_ALTERTABLE_DROPCOLUMN, arguments);

            if (Props.XDB_SQLCOMMAND_ALTERTABLE_DROPCOLUMN_TO_PARENT) {
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
                    new SyncAlterDropColumn(this), client);
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
