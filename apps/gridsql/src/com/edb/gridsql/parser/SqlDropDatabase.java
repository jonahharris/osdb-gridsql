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
 * SqlDropDatabase.java
 *
 *
 *
 *
 *
 */

package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.MessageTypes;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.NodeDBConnectionInfo;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.DropDatabase;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.util.DbGateway;

/**
 *
 *
 */
public class SqlDropDatabase extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlDropDatabase.class);

    private XDBSessionContext client;

    private boolean prepared = false;

    private boolean forceDrop = false;

    private String dbName;

    /** Creates a new instance of SqlDropDatabase */
    public SqlDropDatabase(XDBSessionContext client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return prepared;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
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
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    public boolean needCoordinatorConnection() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#prepare()
     */
    public void prepare() throws Exception {
        final String method = "prepare";
        logger.entering(method);
        try {

            if (!prepared) {
                if (client.getCurrentUser().getLogin().getUserClass() != SysLogin.USER_CLASS_DBA) {
                    throw new XDBSecurityException(
                            "You are not allowed to drop the database");
                }
            }
            prepared = true;

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
        logger.entering(method, new Object[] { engine });
        try {

            if (!isPrepared()) {
                prepare();
            }

            XDBSessionContext newClient = XDBSessionContext.createSession();
            try {
                ExecutionResult result = null;
                try {
                    newClient.useDB(dbName, MessageTypes.CONNECTION_MODE_ADMIN);
                    newClient.login(client.getCurrentUser().getLogin());
                    result = newClient.dropDatabase();
                } catch (Exception e) {
                    if (!forceDrop) {
                        throw e;
                    }
                    result = ExecutionResult
                            .createSuccessResult(ExecutionResult.COMMAND_DROP_DATABASE);
                }
                NodeDBConnectionInfo[] connectionInfos = newClient
                        .getConnectionInfos(null);
                HashMap<String, String> valueMap = new HashMap<String, String>();
                DbGateway aGwy = new DbGateway();
                aGwy.setForce(forceDrop);
                aGwy.dropDbOnNodes(valueMap, connectionInfos);
                return result;
            } finally {
                newClient.logout();
            }

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * Grammar production:
     * f0 -> <DROP_DB_>
     * f1 -> Identifier(prn)
     * f2 -> [ <FORCE_> ]
     */
    @Override
    public Object visit(DropDatabase n, Object argu) {
        dbName = (String) n.f1.accept(new IdentifierHandler(), argu);
        forceDrop = n.f2.present();
        return null;
    }

}
