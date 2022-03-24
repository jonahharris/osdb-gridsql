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
 * XDBWrappedSQLException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

import java.sql.SQLException;

/**
 *  
 */
public class XDBWrappedSQLException extends XDBWrappedException {
    /**
     * 
     */
    private static final long serialVersionUID = 3547307180011580785L;

    private String sqlState;

    private int errorCode;

    private XDBWrappedSQLException nextException = null;

    /**
     * 
     */
    public XDBWrappedSQLException() {
        super();
    }

    /**
     * @param nodeID
     * @param cause
     */
    public XDBWrappedSQLException(int nodeID, SQLException cause) {
        super(nodeID, cause);
        sqlState = cause.getSQLState();
        errorCode = cause.getErrorCode();
        SQLException next = cause.getNextException();
        if (next != null) {
            nextException = new XDBWrappedSQLException(nodeID, next);
        }
    }

    /**
     * @return Returns the errorCode.
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @return Returns the sqlState.
     */
    public String getSQLState() {
        return sqlState;
    }

    /**
     * @return Returns the nextException.
     */
    public XDBWrappedSQLException getNextException() {
        return nextException;
    }
}
