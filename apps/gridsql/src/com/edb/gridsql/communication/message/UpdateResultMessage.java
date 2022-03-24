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
/**
 * 
 */
package com.edb.gridsql.communication.message;

/**
 * 
 * 
 */
public class UpdateResultMessage extends NodeMessage {
    private static final long serialVersionUID = -4912283601966603476L;

    /**
     * Only for EXEC_COMMAND_RESULT
     */
    private int numRowsResult;

    /** Parameterless constructor required for serialization */
    public UpdateResultMessage() {
    }

    /**
     * @param messageType
     */
    public UpdateResultMessage(int messageType) {
        super(messageType);
    }

    @Override
    public void setNumRowsResult(int numRows) {
        numRowsResult = numRows;
    }

    @Override
    public int getNumRowsResult() {
        return numRowsResult;
    }
}
