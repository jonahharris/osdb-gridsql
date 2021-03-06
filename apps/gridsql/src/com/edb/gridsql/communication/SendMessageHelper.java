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
 *  
 */
package com.edb.gridsql.communication;

import java.util.Collection;
import java.util.Iterator;

import com.edb.gridsql.communication.message.NodeMessage;
import com.edb.gridsql.metadata.DBNode;

/**
 * Utility class to simplify sending message from different targets. This class 
 * is thread-safe.
 *   
 */
public class SendMessageHelper {
    private int nodeID;

    private Integer sessionID;

    private AbstractAgent agent;

    /**
     * Create new instance of SendMessageHelper
     * 
     * @param sourceNodeID
     *            To assign to all messages being sent
     * @param sessionID
     *            To assign to all messages being sent
     * @param agent
     *            NodeAgent or CoordinatorAgent to be used to send messages
     */
    public SendMessageHelper(int sourceNodeID, Integer sessionID,
            AbstractAgent agent) {
        nodeID = sourceNodeID;
        this.sessionID = sessionID;
        this.agent = agent;
    }

    /**
     * Creates message of specified type and sends it to specified node
     * 
     * @param requestId 
     * @param targetNodeId 
     * @param msgType 
     */
    public void sendMessage(Integer targetNodeId, int msgType, int requestId) {
        NodeMessage outNodeMessage = NodeMessage.getNodeMessage(msgType);
        outNodeMessage.setRequestId(requestId);
        sendMessage(targetNodeId, outNodeMessage);
    }

    /**
     * Sends supplied message to specified node
     * 
     * @param targetNodeId
     * @param outNodeMessage
     */
    public void sendMessage(Integer targetNodeId, NodeMessage outNodeMessage) {
        outNodeMessage.setSourceNodeID(nodeID);
        outNodeMessage.setSessionID(sessionID);
        outNodeMessage.setTargetNodeID(targetNodeId);
        agent.sendMessage(outNodeMessage);
    }

    /**
     * Sends supplied message to the destination nodes already specified in the
     * message
     * 
     * @param outNodeMessage
     */
    public void sendMessageByDestInMessage(NodeMessage outNodeMessage) {
        outNodeMessage.setSourceNodeID(nodeID);
        outNodeMessage.setSessionID(sessionID);
        agent.sendMessage(outNodeMessage);
    }

    /**
     * Creates message of specified type and sends it to all nodes from
     * specified list
     * 
     * @param requestId 
     * @param destinations 
     * @param msgType 
     */
    public void sendMessageToList(Collection destinations, int msgType,
            int requestId) {
        NodeMessage aMessage = NodeMessage.getNodeMessage(msgType);
        aMessage.setRequestId(requestId);
        sendMessageToList(destinations, aMessage);
    }

    /**
     * Sends supplied message to all nodes from specified list
     * 
     * @param destinations
     * @param aMessage
     */
    public void sendMessageToList(Collection destinations, NodeMessage aMessage) {
        Integer[] destinationsArray = new Integer[destinations.size()];
        int i = 0;
        for (Iterator it = destinations.iterator(); it.hasNext();) {
            Object next = it.next();
            destinationsArray[i++] = (next instanceof DBNode) ? new Integer(
                    ((DBNode) next).getNode().getNodeid()) : (Integer) next;
        }
        aMessage.setSourceNodeID(nodeID);
        aMessage.setSessionID(sessionID);
        aMessage.setTargetNodeIDs(destinationsArray);
        agent.sendMessage(aMessage);
    }

    /**
     * Creates message of specified type and sends it to source of the <CODE>origin</CODE>
     * 
     * @param origin
     * @param msgType
     */
    public void sendReplyMessage(NodeMessage origin, int msgType) {
        NodeMessage outNodeMessage = NodeMessage.getNodeMessage(msgType);
        sendReplyMessage(origin, outNodeMessage);
    }

    /**
     * Sends supplied message to source of the <CODE>origin</CODE>
     * 
     * @param origin
     * @param outNodeMessage
     */
    public void sendReplyMessage(NodeMessage origin, NodeMessage outNodeMessage) {
        outNodeMessage.setSessionID(origin.getSessionID());
        outNodeMessage.setRequestId(origin.getRequestId());
        outNodeMessage.setSourceNodeID(nodeID);
        sendMessage(new Integer(origin.getSourceNodeID()), outNodeMessage);
    }
}