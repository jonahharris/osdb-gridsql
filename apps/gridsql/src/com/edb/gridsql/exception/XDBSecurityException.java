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
 * XDBSecurityException.java
 * 
 *  
 */
package com.edb.gridsql.exception;

import java.sql.SQLException;

/**
 *  
 */
public class XDBSecurityException extends SQLException {

    /**
     * 
     */
    private static final long serialVersionUID = -3649579622748499755L;

    /**
     * @param reason
     */
    public XDBSecurityException(String reason) {
        super(reason);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param reason
     * @param SQLState
     */
    public XDBSecurityException(String reason, String SQLState) {
        super(reason, SQLState);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param reason
     * @param SQLState
     * @param vendorCode
     */
    public XDBSecurityException(String reason, String SQLState, int vendorCode) {
        super(reason, SQLState, vendorCode);
        // TODO Auto-generated constructor stub
    }

}
