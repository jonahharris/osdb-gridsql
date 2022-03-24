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
 * XDBBaseException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

/**
 * Common parent for all XDB Exceptions. Do not create instances of this class,
 * choose some descendant, or create it ;-) Features: must be serializable, all
 * descendant must be serializable too. Carries nodeID where exception is
 * raised.
 * 
 *  
 */
public class XDBBaseException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 1901275235123733732L;

    public static final int NODE_UNKNOWN = -1;

    protected int nodeID = NODE_UNKNOWN;

    /**
     * Default parameterless constructor. It is not used normally, just to
     * ensure
     */
    public XDBBaseException() {
    }

    /**
     * @param nodeID
     * @param message
     */
    public XDBBaseException(int nodeID, String message) {
        super(message);
        this.nodeID = nodeID;
    }

    /*
     * Does not have Constructor from Throwable, because it can violate
     * serializability rule. Keep it to descendants, that are Exception wrappers
     */

    /**
     * @return Returns the ID of the Node where exception were raised.
     */
    public int getNodeID() {
        return nodeID;
    }
}
