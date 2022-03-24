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
 * SqlDropTempTables.java
 * 
 *  
 */
package com.edb.gridsql.parser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.misc.combinedresultset.ServerResultSetImpl;
import com.edb.gridsql.queryproc.QueryCombiner;

/**
 *  
 */
public class SqlDropTempTables implements IXDBSql, IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlDropTempTables.class);

    private XDBSessionContext client;

    private String rsKey;

    private ResultSet rs;

    private Collection<String> dropOnNodes;

    private Collection<String> dropOnCoordinator;

    /**
     * 
     */
    public SqlDropTempTables(String rsKey, XDBSessionContext client) {
        this.client = client;
        this.rsKey = rsKey;
    }

    public SqlDropTempTables(ResultSet rs, XDBSessionContext client) {
        this.client = client;
        this.rs = rs;
        if (rs instanceof ServerResultSetImpl) {
            ServerResultSetImpl srs = (ServerResultSetImpl) rs;
            dropOnNodes = srs.getFinalNodeTempTableList();
            dropOnCoordinator = srs.getFinalCoordTempTableList();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        // Use only current nodes, it should be possible to get node list from
        // ServerResultSetImpl
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    public long getCost() {
        return LOW_COST;
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
        return rs != null;
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

            if (isPrepared()) {
                return;
            }
            rs = client.getResultSet(rsKey);
            if (rs instanceof ServerResultSetImpl) {
                ServerResultSetImpl srs = (ServerResultSetImpl) rs;
                dropOnNodes = srs.getFinalNodeTempTableList();
                dropOnCoordinator = srs.getFinalCoordTempTableList();
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

            if (!isPrepared()) {
                prepare();
            }
            try {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ignore) {
                    }
                }
                if (dropOnNodes != null && !dropOnNodes.isEmpty()) {
                    engine.dropNodeTempTables(dropOnNodes, getNodeList(),
                            client);
                }
                if (dropOnCoordinator != null && !dropOnCoordinator.isEmpty()) {
                    QueryCombiner qc = new QueryCombiner(client, "");
                    qc.dropTempTables(dropOnCoordinator);
                }
            } finally {
                client.closeCursor(rsKey);
            }

            return ExecutionResult
                    .createSuccessResult(ExecutionResult.COMMAND_DROP_TABLE);

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
