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
 * SqlShutdownServer.java
 *
 */

package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;

import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.MessageTypes;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.ShutdownXDB;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 *
 *
 */
public class SqlShutdownServer extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlShutdownServer.class);

    private XDBSessionContext client;

    private boolean prepared = false;
    // force mode option that when true force the database shutdown
    private boolean force = false;

    /** Creates a new instance of SqlShutdownServer */
    public SqlShutdownServer(XDBSessionContext client) {
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
                            "You are not allowed to shutdown server");
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
            SysLogin clientLogin = client.getCurrentUser().getLogin();

            try {
                for (SysDatabase database : MetaData.getMetaData()
                        .getSysDatabases()) {
                    // Zahid: Skipping the stop db process if it's virtual admin db, it actually results
                    // in setting certain variables to null that are later used
                    if (database.getDbname().equalsIgnoreCase(Props.XDB_ADMIN_DATABASE)) {
                        continue;
                    }

                    if (database.isStarted()) {
                        // if FORCE option is true, force the database shutdown even if the db is active
                        if (force) {
                            newClient.useDB(database.getDbname(),
                                    MessageTypes.CONNECTION_MODE_ADMIN);
                            newClient.login(clientLogin);
                            newClient.shutdownDatabase();
                        } else {
                            throw new XDBServerException("Database " + database.getDbname() +
                                    " is still online. Use force mode option to force a shutdown.");
                        }
                    }
                }
            } finally {
                newClient.logout();
            }
            client.shutdown();
            return null;

        } finally {
            logger.exiting(method);
        }
    }

    @Override
    public Object visit(ShutdownXDB n, Object argu) {

        force = n.f1.present();
        return null;

    }
}
