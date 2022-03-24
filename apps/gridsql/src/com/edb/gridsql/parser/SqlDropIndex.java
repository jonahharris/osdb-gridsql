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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.HelperSysIndex;
import com.edb.gridsql.metadata.SyncDropIndex;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysIndex;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.DropIndex;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.TableNameHandler;

/**
 * This class is used as an information class for dropping the index
 *
 */
public class SqlDropIndex extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger.getLogger(SqlDropIndex.class);

    private String indexName;

    private String tableName;

    private XDBSessionContext client;

    private SysDatabase database;

    private SysTable table;

    private SysIndex index;

    private String dropIndexSQL;

    public SqlDropIndex(XDBSessionContext client) {
        this.client = client;
        database = client.getSysDatabase();
    }

    /**
     * Grammar production:
     * f0 -> <DROP_>
     * f1 -> <INDEX_>
     * f2 -> Identifier(prn)
     * f3 -> [ <ON_> TableName(prn) ]
     *
     * @param n
     * @param argu
     * @return
     */
    @Override
    public Object visit(DropIndex n, Object argu) {
        Object _ret = null;
        indexName = (String) n.f2.accept(new IdentifierHandler(), argu);
        n.f3.accept(this, argu);
        if (n.f3.present()) {
            TableNameHandler aTableNameHandler = new TableNameHandler(client);
            n.f3.node.accept(aTableNameHandler, argu);
            tableName = aTableNameHandler.getTableName();
        }
        return _ret;
    }

    /**
     *
     * @return returns the index name
     */
    public String getIndexName() {
        return indexName;
    }

    public String getTableName() {
        return table.getTableName();
    }

    public LockSpecification<SysTable> getLockSpecs() {
        Collection<SysTable> readObjects = Collections.singletonList(table);
        Collection<SysTable> writeObjects = Collections.emptyList();
        return new LockSpecification<SysTable>(readObjects, writeObjects);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return table.getNodeList();
    }

    public long getCost() {
        return LOW_COST;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        final String method = "isPrepared";
        logger.entering(method, new Object[] {});
        try {

            return index != null;

        } finally {
            logger.exiting(method);
        }
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

            if (tableName == null) {
                HelperSysIndex aHelperSysIndex = database
                        .getSysIndexByName(indexName);
                if (aHelperSysIndex == null) {
                    throw new XDBServerException(
                            ErrorMessageRepository.NO_INDEX_FOUND + "( "
                                    + indexName + " )", 0,
                            ErrorMessageRepository.NO_INDEX_FOUND_CODE);
                }

                table = aHelperSysIndex.aSysTable;
                index = aHelperSysIndex.aSysIndex;
            } else {
                table = database.getSysTable(tableName);
                index = table.getSysIndex(indexName);
                if (index == null) {
                    throw new XDBServerException(
                            ErrorMessageRepository.NO_SUCH_INDEX + "( "
                                    + indexName + " )", 0,
                            ErrorMessageRepository.NO_SUCH_INDEX_CODE);
                }

            }
            if (table.getParentTable() != null
                    && table.getParentTable().getSysIndex(index.idxid) == index) {
                throw new XDBServerException("Can not drop inherited index");
            }
            if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA
                    && table.getOwner() != client.getCurrentUser()) {
                XDBSecurityException ex = new XDBSecurityException(
                        "You are not allowed to drop indexes on table "
                                + tableName);
                logger.throwing(ex);
                throw ex;
            }
            // if the index is not a primary index - check if it is referenced
            // else where; ie. if this index is used for defining any
            // constraints.
            // We allow indexes associated with a primary key to be deleted
            // independently (unlike some DBs).
            if (table.getPrimaryIndex() != index) {
                if (index.is_constrained) {

                    throw new XDBServerException(
                            ErrorMessageRepository.INDEX_REF_IN_CONSTRAINT
                                    + "( " + indexName + " )", 0,
                            ErrorMessageRepository.INDEX_REF_IN_CONSTRAINT_CODE);
                }
            }
            // also make sure the index is not syscreated
            // syscreated indexes will be automatically deleted
            // with their parent stuff.
            if (index.issyscreated == 1) {
                throw new XDBServerException(
                        ErrorMessageRepository.XDB_CREATED_INDEX + "( "
                                + indexName + " )", 0,
                        ErrorMessageRepository.XDB_CREATED_INDEX_CODE);
            }
            List<SysColumn> columns = index.getKeyColumns();
            if (columns.size() != 1
                    || columns.get(0) != table.getSerialColumn()) {
                dropIndexSQL = "DROP INDEX " + IdentifierHandler.quote(index.idxname);
                if (tableName != null) {
                    dropIndexSQL = dropIndexSQL + " ON "
                        + IdentifierHandler.quote(table.getTableName());
                }
            }

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

            engine.executeDDLOnMultipleNodes(dropIndexSQL, table.getNodeList(),
                    new SyncDropIndex(this), client);
            return ExecutionResult
                    .createSuccessResult(ExecutionResult.COMMAND_DROP_INDEX);

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
        return false;
    }

}
