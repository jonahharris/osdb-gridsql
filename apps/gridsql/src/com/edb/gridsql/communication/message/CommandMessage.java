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
public class CommandMessage extends NodeMessage {
    private static final long serialVersionUID = 2294439851011666205L;

    private String command;

    /** Parameterless constructor required for serialization */
    public CommandMessage() {
    }

    /**
     * @param messageType
     */
    protected CommandMessage(int messageType) {
        super(messageType);
    }

    @Override
    public void setSqlCommand(String sqlCommandString) {
        command = sqlCommandString;
    }

    @Override
    public String getSqlCommand() {
        return command;
    }

    // MSG_TRAN_BEGIN_SAVEPOINT
    // MSG_TRAN_END_SAVEPOINT
    // MSG_TRAN_ROLLBACK_SAVEPOINT
    @Override
    public void setSavepoint(String savepoint) {
        this.command = savepoint;
    }

    @Override
    public String getSavepoint() {
        return command;
    }

    // MSG_SEND_DATA_INIT
    @Override
    public void setTargetTable(String tableName) {
        command = tableName;
    }

    @Override
    public String getTargetTable() {
        return command;
    }
}
