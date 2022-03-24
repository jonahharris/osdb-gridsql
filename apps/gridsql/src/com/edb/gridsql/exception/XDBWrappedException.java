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
 * XDBWrappedException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

/**
 *  
 */
public class XDBWrappedException extends XDBBaseException {
    /**
     * 
     */
    private static final long serialVersionUID = 8646201999174421550L;
    private StackTraceElement[] stackTrace;

    /**
     * 
     */
    public XDBWrappedException() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param nodeID the node ID
     * @param cause the cause
     */
    public XDBWrappedException(int nodeID, Throwable cause) {
        super(nodeID, cause.getClass().getName() + " : " + cause.getMessage());
        stackTrace = cause.getStackTrace();
    }

    /**
     * 
     * @return 
     */
    public StackTraceElement[] getCauseStackTrace() {
        return stackTrace;
    }
}
