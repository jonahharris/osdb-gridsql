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
 * XDBIllegalStateException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

import com.edb.gridsql.communication.message.NodeMessage;

/**
 * Raised when unexpected message is received. Message can be MSG_ABORT,
 * indicating that operation we are waiting for is failed, or any other message,
 * indicating lack of synchronization, caused by development error
 * 
 *  
 */
public class XDBUnexpectedMessageException extends XDBBaseException {
    private static final long serialVersionUID = -2409016551178671829L;

    private XDBBaseException cause;

    /**
     * 
     */
    public XDBUnexpectedMessageException() {
    }

    /**
     * 
     * @param msg 
     * @param nodeID 
     * @param message 
     */
    public XDBUnexpectedMessageException(int nodeID, String message,
            NodeMessage msg) {
        super(nodeID, message + " " + msg);
        if (msg != null && msg.getMessageType() == NodeMessage.MSG_ABORT) {
            cause = msg.getCause();
            if (cause != null) {
                nodeID = cause.getNodeID();
            }
        }
    }

}
