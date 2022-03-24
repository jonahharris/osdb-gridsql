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
 * SqlStartDatabase.java
 *
 *
 */

package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.MessageTypes;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.Node;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.syntaxtree.StartDatabase;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 *
 *
 */
public class SqlStartDatabase extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlStartDatabase.class);

    private XDBSessionContext client;

    private boolean prepared = false;

    //private String dbName;

    // Default one minute
    private int timeout = 60;

    private String[] dbListStr;

    /** Creates a new instance of SqlStartDatabase */
    public SqlStartDatabase(XDBSessionContext client) {
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
                            "You are not allowed to start database");
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
                for (String element : dbListStr) {
                    newClient.useDB(element, MessageTypes.CONNECTION_MODE_ADMIN);
                    newClient.login(client.getCurrentUser().getLogin());
                    newClient.startDatabase(timeout);
                }

            } finally {
                newClient.logout();
            }

        }
        finally {
            logger.exiting(method);
        }
        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_START_DATABASE);
    }

    /**
     * Grammar production:
     * f0 -> <START_DB_>
     * f1 -> Identifier(prn)
     * f2 -> ( "," Identifier(prn) )*
     * f3 -> [ <WAIT_TIMEOUT_> <INT_LITERAL> ]
     */
    @Override
    public Object visit(StartDatabase n, Object argu) {
    Object ret_ = null;
        dbListStr = new String[n.f2.size() + 1];
        String firstNode = (String) n.f1.accept(new IdentifierHandler(), argu);
        int i = 0;
        dbListStr[i++] = firstNode;
        Enumeration dbEnum = n.f2.elements();
        while (dbEnum.hasMoreElements()) {
            Object nextDb = dbEnum.nextElement();
            Node actualDb = (Node) ((NodeSequence) nextDb).nodes.elementAt(1);
            dbListStr[i++] = (String) actualDb.accept(new IdentifierHandler(), argu);;
        }
        //timeout = n.f3.present();
        if (n.f3.present()) {
            NodeSequence seq = (NodeSequence) n.f3.node;
            try {
                timeout = Integer.parseInt(seq.nodes.get(1).toString());
            } catch (NumberFormatException nfe) {
                // ignore
            }
        }
        return ret_;
    }
}
