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
import java.util.HashMap;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.MultinodeExecutor;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.IMetaDataUpdate;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncDropTablespace;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.SysTablespace;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.DropTablespace;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

public class SqlDropTableSpace extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlDropTableSpace.class);

    private XDBSessionContext client;

    private String tablespaceName;

    private SysTablespace tablespace;

    private HashMap<DBNode,String> statements = null;

    public SqlDropTableSpace(XDBSessionContext client) {
        this.client = client;
    }

    /**
     * Grammar production:
     * f0 -> <DROP_>
     * f1 -> <TABLESPACE_>
     * f2 -> Identifier(prn)
     */
    @Override
    public Object visit(DropTablespace n, Object argu) {
        tablespaceName = (String) n.f2.accept(new IdentifierHandler(), argu);
        return null;
    }

    public SysTablespace getTablespace() {
        return tablespace;
    }

    public long getCost() {
        return LOW_COST;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        if (!isPrepared()) {
            try {
                prepare();
            } catch (Exception ignore) {
                logger.catching(ignore);
            }
        }
        return statements.keySet();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs()
     */
    public LockSpecification<SysTable> getLockSpecs() {
        Collection<SysTable> empty = Collections.emptyList();
        return new LockSpecification<SysTable>(empty, empty);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return statements != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#prepare()
     */
    public void prepare() throws Exception {
        tablespace = MetaData.getMetaData().getTablespace(tablespaceName);
        if (tablespace == null) {
            throw new XDBServerException("Tablespace \"" + tablespaceName
                    + "\" does not exist");
        }
        if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA) {
            throw new XDBSecurityException("Only DBA user can drop tablespaces");
        }
        // Check references on the tablespace or rely on Metadata DB constraints
        statements = new HashMap<DBNode,String>();
        for (Integer nodeID : tablespace.getLocations().keySet()) {
            DBNode dbNode = client.getSysDatabase().getDBNode(nodeID);
            String statement = "DROP TABLESPACE "
                + IdentifierHandler.quote(tablespaceName + "_" + nodeID);
            statements.put(dbNode, statement);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        if (!isPrepared()) {
            prepare();
        }
        IMetaDataUpdate metaUpdate = new SyncDropTablespace(this);
        MetaData meta = MetaData.getMetaData();
        meta.beginTransaction();
        try {
            metaUpdate.execute(client);
            MultinodeExecutor executor = client
                    .getMultinodeExecutor(getNodeList());
            executor.executeCommand(statements, true);
            meta.commitTransaction(metaUpdate);
        } catch (Exception ex) {
            logger.catching(ex);
            meta.rollbackTransaction();
            logger.throwing(ex);
            throw ex;
        }
        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_DROP_TABLESPACE);
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
