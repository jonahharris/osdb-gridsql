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
public class BatchResultMessage extends NodeMessage {
    private static final long serialVersionUID = 2559383725919945421L;

    /** Only used for MSG_EXECUTE_BATCH_ACK */
    private int[] batchResult = null;

    /** Parameterless constructor required for serialization */
    public BatchResultMessage() {
    }

    /**
     * @param messageType
     */
    protected BatchResultMessage(int messageType) {
        super(messageType);
    }

    @Override
    public void setBatchResult(int[] result) {
        batchResult = result;
    }

    @Override
    public int[] getBatchResult() {
        return batchResult == null ? new int[0] : batchResult;
    }
}
