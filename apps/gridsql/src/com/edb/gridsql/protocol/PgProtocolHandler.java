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
package com.edb.gridsql.protocol;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;

/**
 *
 *
 */
public class PgProtocolHandler extends
        AbstractProtocolHandler<PgProtocolSession> {
    /** the logger for ProtocolHandler */
    private static XLogger logger = XLogger.getLogger(PgProtocolHandler.class);

    private static PgProtocolHandler theProtocolHandler = null;

    /**
     *
     * @return
     */
    public static PgProtocolHandler getProtocolManager() {
        if (theProtocolHandler == null) {
            try {
                theProtocolHandler = new PgProtocolHandler();
            } catch (IOException ioe) {
                logger.catching(ioe);
            }
        }
        return theProtocolHandler;
    }

    protected PgProtocolHandler() throws IOException {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.protocol.AbstractProtocolHandler#closeClient(java.lang.Object)
     */
    @Override
    protected void closeClient(PgProtocolSession clientContext) {
        if (clientContext != null) {
            clientContext.close();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.protocol.ProtocolManager#rejectClient(java.nio.channels.SocketChannel,
     *      java.lang.Exception)
     */
    public void rejectClient(SocketChannel channel, Exception e) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.protocol.AbstractProtocolHandler#createClient()
     */
    @Override
    protected PgProtocolSession createClient(SocketChannel channel)
            throws Exception {
        return new PgProtocolSession(this, channel,
                XDBSessionContext.createSession());
    }
}
