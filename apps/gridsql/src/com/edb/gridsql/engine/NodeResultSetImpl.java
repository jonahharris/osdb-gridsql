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
 * NodeResultSetImpl.java
 * 
 *  
 */
package com.edb.gridsql.engine;

import java.sql.SQLException;

import org.apache.log4j.Level;

import com.edb.gridsql.common.ResultSetImpl;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.communication.AbstractAgent;
import com.edb.gridsql.communication.CoordinatorAgent;
import com.edb.gridsql.communication.IMessageListener;
import com.edb.gridsql.communication.NodeAgent;
import com.edb.gridsql.communication.SendMessageHelper;
import com.edb.gridsql.communication.message.NodeMessage;
import com.edb.gridsql.engine.io.ResultSetResponse;
import com.edb.gridsql.engine.io.XMessage;
import com.edb.gridsql.exception.XDBBaseException;
import com.edb.gridsql.exception.XDBServerException;

/**
 *  
 */
public class NodeResultSetImpl extends ResultSetImpl implements
        IMessageListener {
    private static final XLogger logger = XLogger
            .getLogger(NodeResultSetImpl.class);

    private AbstractAgent agent = null;

    private SendMessageHelper sendHelper = null;

    private NodeMessage nodeMessage = null;

    private Integer sessionID = null;

    private NodeMessage nextMessage = null;

    /**
     * 
     * @param nodeMessage
     * @throws java.sql.SQLException
     */
    public NodeResultSetImpl(NodeMessage nodeMessage) throws SQLException {
        super();
        this.nodeMessage = nodeMessage;
        int target = nodeMessage.getTargetNodeID().intValue();
        sessionID = nodeMessage.getSessionID();
        agent = target == 0 ? (AbstractAgent) CoordinatorAgent.getInstance()
                : (AbstractAgent) NodeAgent.getNodeAgent(target);
        sendHelper = new SendMessageHelper(target, sessionID, agent);
        this.responseMessage = getNextResponse(nodeMessage, null);

        resetRawRows();
        this.columnMeta = responseMessage.getColumnMetaData();
    }

    /**
     * Collect messages with rows of remote ResultSet
     * 
     * @see com.edb.gridsql.communication.IMessageListener#processMessage(com.edb.gridsql.communication.message.NodeMessage)
     * @param message
     * @return
     */
    public synchronized boolean processMessage(NodeMessage message) {
        if (nextMessage != null) {
            // Development error
            XDBServerException ex = new XDBServerException(
                    "Previous message has not been consumed");
            logger.throwing(ex);
            throw ex;

        }
        if ((message.getMessageType() == NodeMessage.MSG_RESULT_ROWS || message
                .getMessageType() == NodeMessage.MSG_ABORT)
                && nodeMessage.getSourceNodeID() == message.getSourceNodeID()
                && nodeMessage.getRequestId() == message.getRequestId()) {
            nextMessage = message;
            notify();
            return true;
        }
        return false;
    }

    /**
     * 
     * @throws java.sql.SQLException
     */
    @Override
    protected synchronized void setNextResultSet() throws SQLException {
        logger.log(Level.INFO, "Asking node %0% for more rows",
                new Object[] { new Integer(nodeMessage.getSourceNodeID()) });
        NodeMessage requestNM = NodeMessage
                .getNodeMessage(NodeMessage.MSG_RESULT_ROWS_REQUEST);
        requestNM.setResultSetID(nodeMessage.getResultSetID());
        sendHelper.sendReplyMessage(nodeMessage, requestNM);
        // TODO wait timeout ?
        while (nextMessage == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new SQLException("Result set is closed");
            }
        }
        if (nextMessage.getMessageType() == NodeMessage.MSG_ABORT) {
            XDBBaseException ex = nextMessage.getCause();
            if (ex != null) {
                logger.catching(ex);
            }
            SQLException se = new SQLException(
                    "Can not fetch more rows: " + ex == null ? "reason unknown"
                            : ex.getMessage());
            logger.throwing(se);
            throw se;
        }
        responseMessage = getNextResponse(nextMessage, responseMessage);
        nextMessage = null;
    }

    /**
     * Extract data from incoming message
     * 
     * @param message
     * @param response
     * @return
     */
    private ResultSetResponse getNextResponse(NodeMessage message,
            ResultSetResponse response) {
        if (response == null) {
            response = new ResultSetResponse();
        }
        byte[] resultSetData = message.getResultSetData();
        // reading response
        byte[] header = new byte[XMessage.HEADER_SIZE];
        System.arraycopy(resultSetData, 0, header, 0, XMessage.HEADER_SIZE);
        response.setHeaderBytes(header);
        int len = response.getPacketLength() - XMessage.HEADER_SIZE;
        byte[] inputBytes = new byte[len];
        System.arraycopy(resultSetData, XMessage.HEADER_SIZE, inputBytes, 0,
                len);
        response.setMessage(inputBytes);
        return response;
    }

    @Override
    public void close() throws SQLException {
        if (!responseMessage.isLastPacket()) {
            // Close RS on node
            NodeMessage closeMsg = NodeMessage
                    .getNodeMessage(NodeMessage.MSG_RESULT_CLOSE);
            closeMsg.setResultSetID(nodeMessage.getResultSetID());
            sendHelper.sendReplyMessage(nodeMessage, closeMsg);
        }
        // Remove the RS from ME
        NodeMessage closeMsg = NodeMessage
                .getNodeMessage(NodeMessage.MSG_RESULT_CLOSE);
        closeMsg.setResultSetID(nodeMessage.getResultSetID());
        closeMsg.setSessionID(nodeMessage.getSessionID());
        closeMsg.setRequestId(nodeMessage.getRequestId());
        closeMsg.setSourceNodeID(nodeMessage.getTargetNodeID());
        sendHelper.sendMessage(nodeMessage.getTargetNodeID(), closeMsg);
    }
}
