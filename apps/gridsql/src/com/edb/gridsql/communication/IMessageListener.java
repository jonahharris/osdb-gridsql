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
package com.edb.gridsql.communication;

/**
 * This interface have to be implemented by classes willing to receive
 * NodeMessages from a Connector
 * 
 * @see com.edb.gridsql.communication.message.NodeMessage
 * @see com.edb.gridsql.communication.AbstractConnector
 * @see com.edb.gridsql.communication.LocalConnector
 * 
 *  
 * @version 1.0
 */
public interface IMessageListener {
    /**
     * Someone calls this method when it have incoming message in queue for
     * process. Queue does not processing until method returns, but messages
     * keep arriving. Perform long tasks in another thread to avoid flooding of
     * the caller's message queue.
     * 
     * @param message
     *            The message
     * @return true if message "consumed", that is no more processing required
     */
    public boolean processMessage(
            com.edb.gridsql.communication.message.NodeMessage message);
}