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
/**
 *
 */
package com.edb.gridsql.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.edb.gridsql.communication.message.NodeMessage;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.MultinodeExecutor;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.ExecDirect;
import com.edb.gridsql.parser.core.syntaxtree.NodeListOptional;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.syntaxtree.NodeToken;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

public class SqlExecDirect extends ObjectDepthFirst implements IXDBSql,
        IExecutable, IPreparable {
    private XDBSessionContext client;

    private List<DBNode> nodeList;

    private String command;

    /**
     *
     */
    public SqlExecDirect(XDBSessionContext client) {
        this.client = client;
    }

    /**
     * Grammar production: f0 -> <EXEC_> f1 -> <DIRECT_> f2 -> <ON_> f3 -> (
     * <ALL_> | ( <NODE_> | <NODES_> ) <DECIMAL_LITERAL> ( "," <DECIMAL_LITERAL> )* )
     * f4 -> <STRING_LITERAL>
     */
    @Override
    public Object visit(ExecDirect n, Object argu) {
        Object _ret = null;
        switch (n.f3.which) {
        case 0: // ALL
            nodeList = new ArrayList<DBNode>(client.getSysDatabase()
                    .getDBNodeList());
            break;
        case 1:
            nodeList = new ArrayList<DBNode>();
            NodeSequence ns = (NodeSequence) n.f3.choice;
            /**
             * Grammar production: f0 -> ( <NODE_> | <NODES_> ) f1 ->
             * <DECIMAL_LITERAL> f2 -> ( "," <DECIMAL_LITERAL> )*
             */
            NodeToken nt = (NodeToken) ns.elementAt(1);
            int nodeID = Integer.parseInt(nt.tokenImage);
            nodeList.add(client.getSysDatabase().getDBNode(nodeID));
            for (Iterator it = ((NodeListOptional) ns.elementAt(2)).nodes
                    .iterator(); it.hasNext();) {
                NodeSequence ns1 = (NodeSequence) it.next();
                nodeID = Integer
                        .parseInt(((NodeToken) ns1.nodes.get(1)).tokenImage);
                nodeList.add(client.getSysDatabase().getDBNode(nodeID));
            }
            break;
        }
        command = n.f4.tokenImage;
        // Strip quotes
        command = command.substring(1, command.length() - 1).replaceAll("''",
                "'");
        return _ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return nodeList;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    public long getCost() {
        return ILockCost.LOW_COST;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs()
     */
    public LockSpecification<SysTable> getLockSpecs() {
        Collection<SysTable> emptyList = Collections.emptyList();
        return new LockSpecification<SysTable>(emptyList, emptyList);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        MultinodeExecutor aMultinodeExecutor = client
                .getMultinodeExecutor(nodeList);
        List<NodeMessage> resultMsgs = new ArrayList<NodeMessage>(
                aMultinodeExecutor.execute(command, nodeList,
                        nodeList.size() == 1));
        Map<Integer,ExecutionResult> execResults = new TreeMap<Integer,ExecutionResult>();
        for (NodeMessage aMessage : resultMsgs) {
            if (aMessage.getMessageType() == NodeMessage.MSG_EXEC_COMMAND_RESULT) {
                execResults.put(aMessage.getSourceNodeID(),
                        ExecutionResult.createRowCountResult(
                                ExecutionResult.COMMAND_UNKNOWN,
                                aMessage.getNumRowsResult()));
            } else if (aMessage.getMessageType() == NodeMessage.MSG_EXEC_QUERY_RESULT) {
                execResults.put(aMessage.getSourceNodeID(),
                        ExecutionResult.createResultSetResult(
                                ExecutionResult.COMMAND_UNKNOWN,
                                aMessage.getResultSet()));
            }
        }
        return ExecutionResult.createMultipleResult(
                ExecutionResult.COMMAND_DIRECT_EXEC, execResults);
    }

    public boolean isPrepared() {
        return true;
    }

    public void prepare() throws Exception {
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
