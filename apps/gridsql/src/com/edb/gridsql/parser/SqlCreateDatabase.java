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
 * SqlCreateDatabase.java
 *
 *
 */

package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
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
import com.edb.gridsql.parser.core.syntaxtree.CreateDatabase;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.util.DbGateway;

/**
 *
 *
 */
public class SqlCreateDatabase extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlCreateDatabase.class);

    private XDBSessionContext client;

    private boolean prepared = false;

    private String[] nodeList;

    private String dbName;

    private boolean manual = false;

    /** Creates a new instance of SqlCreateDatabase */
    public SqlCreateDatabase(XDBSessionContext client) {
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
                            "You are not allowed to create database");
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
                newClient.useDB(dbName, MessageTypes.CONNECTION_MODE_CREATE);
                newClient.login(client.getCurrentUser().getLogin());
                ExecutionResult result = newClient.createDatabase(nodeList);
                if (!manual) {
                    NodeDBConnectionInfo[] connectionInfos = newClient
                            .getConnectionInfos(nodeList);
                    HashMap<String, String> valueMap = new HashMap<String, String>();
                    DbGateway aGwy = new DbGateway();
                    aGwy.createDbOnNodes(valueMap, connectionInfos);
                }
                newClient.persistDatabase();
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
     * f0 -> <CREATE_DB_>
     * f1 -> Identifier(prn)
     * f2 -> [ [ <WITH_> ] <OWNER_> [ "=" ] Identifier(prn) ]
     * f3 -> [ <MANUAL_> ]
     * f4 -> <ON_>
     * f5 -> ( <NODE_> | <NODES_> )
     * f6 -> <INT_LITERAL>
     * f7 -> ( "," <INT_LITERAL> )*
     */
    @Override
    public Object visit(CreateDatabase n, Object argu) {
        dbName = (String) n.f1.accept(new IdentifierHandler(), argu);
        // TODO Database ownership is not supported at the moment
        manual = n.f3.present();
        nodeList = new String[n.f7.size() + 1];
        int i = 0;
        nodeList[i++] = n.f6.tokenImage;
        Enumeration nodeEnum = n.f7.elements();
        while (nodeEnum.hasMoreElements()) {
            Object nextNode = nodeEnum.nextElement();
            Object actualNode = ((NodeSequence) nextNode).nodes.elementAt(1);
            nodeList[i++] = actualNode.toString();
        }
        return null;
    }
}
