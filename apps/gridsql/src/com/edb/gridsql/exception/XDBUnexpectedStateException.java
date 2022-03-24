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

/**
 * Raised in com.edb.gridsql.Engine.NodeThread and com.edb.gridsql.Engine.NodeProdocerThread
 * when it can not do requested operation due to unexpected state
 * 
 *  
 */
public class XDBUnexpectedStateException extends XDBBaseException {
    /**
     * 
     */
    private static final long serialVersionUID = -7066065187595622523L;

    private int currentState;

    private int[] expectedStates;

    /**
     * 
     */
    public XDBUnexpectedStateException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nodeID the node ID
     * @param currentState encountered state ID 
     * @param expectedStates valid state IDs
     */
    public XDBUnexpectedStateException(int nodeID, int currentState,
            int[] expectedStates) {
        super(nodeID,
                "Could not perform requested operation due to unexpected state: "
                        + currentState);
        this.currentState = currentState;
        this.expectedStates = expectedStates;
    }

    /**
     * @return Returns the currentState.
     */
    public int getCurrentState() {
        return currentState;
    }

    /**
     * @return Returns the expectedStates.
     */
    public int[] getExpectedStates() {
        return expectedStates;
    }
}
