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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.edb.gridsql.common.util.ParseCmdLine;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SyncRenameTable;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.SysView;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.RenameTable;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.TableNameHandler;


/**
 * This Information class is used for renaming a particular table
 *
 */
public class SqlRenameTable extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlRenameTable.class);

    private XDBSessionContext client;

    private SysDatabase database;

    private SysTable table;

    private String renameTableSql;

    private String oldTableName;

    private String newTableName;

    public SqlRenameTable(XDBSessionContext client) {
        this.client = client;
        database = client.getSysDatabase();
    }

    /**
     * Grammar production: f0 -> <RENAME_> f1 -> <TABLE_> f2 -> TableName(prn)
     * f3 -> <TO_> f4 -> TableName(prn)
     */
    @Override
    public Object visit(RenameTable n, Object argu) {
        Object _ret = null;
        TableNameHandler aTableNameHandler = new TableNameHandler(client);
        n.f2.accept(aTableNameHandler, argu);
        TableNameHandler aTableNameHandlerNew = new TableNameHandler(client);
        n.f4.accept(aTableNameHandlerNew, argu);
        oldTableName = aTableNameHandler.getTableName();
        newTableName = aTableNameHandlerNew.getTableName();
        return _ret;
    }

    /**
     * @return Returns the newTableName.
     */
    public String getNewTableName() {
        return newTableName;
    }

    /**
     * @return Returns the oldTableName.
     */
    public String getOldTableName() {
        return oldTableName;
    }

    /**
     * This will return the cost of executing this statement in time , milli
     * seconds
     */
    public long getCost() {
        return LOW_COST;
    }

    /**
     * This return the Lock Specification for the system
     *
     * @param theMData
     * @return
     */
    public LockSpecification getLockSpecs() {
        LockSpecification lspec = new LockSpecification(Collections.EMPTY_LIST,
                Collections.singletonList(table));
        return lspec;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection getNodeList() {
        return new ArrayList(table.getNodeList());
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

            if (!isPrepared()) {
                prepare();
            }
            Vector nodeList = new Vector(table.getNodeList());
            SyncRenameTable renameUpdater = new SyncRenameTable(this);
            engine.executeDDLOnMultipleNodes(renameTableSql, nodeList,
                    renameUpdater, client);
            return ExecutionResult
                    .createSuccessResult(ExecutionResult.COMMAND_RENAME_TABLE);

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return table != null;
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

            table = database.getSysTable(oldTableName);
            Enumeration eViews = database.getAllViews();
            while (eViews.hasMoreElements()) {
                SysView view = (SysView) eViews.nextElement();
                if (view.hasDependedTable(table.getTableId())) {
                    if (!view.canRenameTable(table, client)) {
                        XDBSecurityException ex = new XDBSecurityException(
                                "cannot rename table " + oldTableName
                                        + " because other objects depend on it");
                        throw ex;
                    } else {
                        try {
                            view
                                    .renameTable(oldTableName, newTableName,
                                            client);
                        } catch (Exception e) {
                            XDBSecurityException ex = new XDBSecurityException(
                                    "cannot rename table "
                                            + oldTableName
                                            + " because other objects depend on it");
                            throw ex;
                        }
                    }
                }
            }

            if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA
                    && table.getOwner() != client.getCurrentUser()) {
                XDBSecurityException ex = new XDBSecurityException(
                        "You are not allowed to rename table " + oldTableName);
                logger.throwing(ex);
                throw ex;
            }
            if (database.isTableExists(newTableName)) {
                throw new XDBServerException("Could not rename table "
                        + oldTableName + " to " + newTableName
                        + ", target table already exists");
            }
            String template = Props.XDB_SQLCOMMAND_RENAMETABLE_TEMPLATE;
            HashMap<String,String> params = new HashMap<String,String>();
            params.put("oldname", IdentifierHandler.quote(oldTableName));
            params.put("newname", IdentifierHandler.quote(newTableName));
            renameTableSql = ParseCmdLine.substitute(template, params);

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    public boolean needCoordinatorConnection() {
        return true;
    }

}
