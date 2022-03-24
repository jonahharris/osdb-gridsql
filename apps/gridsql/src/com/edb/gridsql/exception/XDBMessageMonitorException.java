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
 * XDBMessageMonitorException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

import java.util.Collection;
import java.util.Collections;

/**
 *  
 */
public class XDBMessageMonitorException extends XDBServerException {

    /**
     * 
     */
    private static final long serialVersionUID = -6750621973347603238L;

    private Collection<Integer> nodes;

    /**
     * 
     * @param nodeId 
     * @param reason 
     * @return 
     */
    private static final String prepareMessage(int nodeId, String reason) {
        return (nodeId == 0 ? "Server" : "Node " + nodeId)
                + " has aborted execution, cause is: " + reason;
    }

    /**
     * @param cause the exception cause
     * @param remainingNodes the list of nodes those still have not responded 
     */
    public XDBMessageMonitorException(XDBBaseException cause,
            Collection<Integer> remainingNodes) {
        super(prepareMessage(cause.getNodeID(), cause.getMessage()), cause);
        nodes = remainingNodes;
    }

    /**
     * 
     * @param nodeId 
     * @param message 
     * @param remainingNodes 
     */
    public XDBMessageMonitorException(int nodeId, String message,
            Collection<Integer> remainingNodes) {
        super(prepareMessage(nodeId, message));
        nodes = remainingNodes;
    }

    /**
     * 
     * @return 
     */
    public Collection<Integer> getRemainingNodes() {
        if (nodes == null) {
            return Collections.emptyList();
        } else {
            return nodes;
        }
    }
}
