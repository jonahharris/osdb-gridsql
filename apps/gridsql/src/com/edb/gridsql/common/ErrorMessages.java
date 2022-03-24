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
package com.edb.gridsql.common;

import com.edb.gridsql.engine.io.MessageTypes;
import com.edb.gridsql.engine.io.ResponseMessage;

/**
 * 
 *  
 */
public class ErrorMessages {

    public static final int CONNECT_ERROR = 101;

    public static final int CONNECTION_TIMEOUT = 102;

    public static final int AN_ERROR_HAS_OCCURRED = 103;

    public static final int INVALID_COMMAND = 104;

    public static final int FEATURE_NOT_SUPPORTED = 105;

    public static final int SYNTAX_ERROR = 106;

    /**
     * 
     * @param t 
     * @return 
     */
    public static ResponseMessage getErrorMessage(Throwable t) {
        ResponseMessage response = new ResponseMessage(
                (byte) MessageTypes.RESP_ERROR_MESSAGE, 0);

        response.storeInt(AN_ERROR_HAS_OCCURRED);
        response.storeString(t.getMessage());
        for (t = t.getCause(); t != null; t = t.getCause()) {
            response.storeByte((byte) 1);
            response.storeInt(AN_ERROR_HAS_OCCURRED);
            response.storeString(t.getMessage());
        }
        response.storeByte((byte) 0);
        return response;
    }
}
