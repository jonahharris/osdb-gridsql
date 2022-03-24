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
package com.edb.gridsql.protocol;

import java.nio.channels.SocketChannel;

/**
 * The <code>ProtocolManager</code> provides an interface for different
 * protocols to be handled by the <code>XDBServer</code>
 *  
 * @see com.edb.gridsql.server.Server
 */
public interface ProtocolManager {
    /**
     * adds a client connection to be managed by the protocol manager
     * @param channel 
     * @param clientContext 
     */
    void addClient(SocketChannel channel);

    /**
     * remove a client connection from the protocol manager
     * @param channel 
     */
    void removeClient(SocketChannel channel);

    /**
     * Write error message to the channel   
     * @param channel
     * @param e
     */
    void rejectClient(SocketChannel channel, Exception e);
}
