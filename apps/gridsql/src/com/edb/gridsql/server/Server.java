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
 * XDBServer.java
 *
 *  
 */
package com.edb.gridsql.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.edb.gridsql.common.util.Property;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.communication.CoordinatorAgent;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.NodeDBConnectionInfo;
import com.edb.gridsql.protocol.ProtocolManager;

/**
 * This class is responsible for server initialization and incoming client
 * connection handling. It starts and initializes server parts: Metadata,
 * Engine, Coordinator Agent and listens for client connections. When client
 * connection is accepted, it creates new XDBSessionContext instance and passes
 * the connection/session pair over to a ProtocolManager
 * 
 * @see com.edb.gridsql.metadata.MetaData
 * @see com.edb.gridsql.engine.Engine
 * @see com.edb.gridsql.communication.CoordinatorAgent
 * @see com.edb.gridsql.engine.XDBSessionContext
 * @see com.edb.gridsql.protocol.ProtocolManager
 */

public class Server implements Runnable {

    /** the logger for the server */
    private static final XLogger logger = XLogger.getLogger(Server.class);

    /** the port to listen on */
    private int port;

    /** the protocol manager */
    private ProtocolManager theManager;

    private ServerSocketChannel channel = null;

    private Selector selector = null;

    /**
     * Creates a new instance ofServer, gets an instance of CoordinatorAgent and
     * restarts remote node if any is running on the network.
     * 
     * @see com.edb.gridsql.communication.CoordinatorAgent
     */
    public Server() {
        CoordinatorAgent.getInstance().resetNodes(true);
    }

    /**
     * Get information required to start backend node databases if it is needed.
     * 
     * @param databases
     *            names of databases needed
     * @return array of NodeDBConnectionInfo objects
     */
    public NodeDBConnectionInfo[] getNodeDBConnectionInfos(
            Collection<String> databases) {
        return MetaData.getMetaData().getNodeDBConnectionInfos(databases);
    }

    /**
     * Start and initialize databases: load metadata, initialize pools, etc.
     * 
     * @param databases
     *            names of databases to start
     */
    public void initDatabases(Collection<String> databases) {
        if (databases != null) {
            CoordinatorAgent agent = CoordinatorAgent.getInstance();
            for (String dbName : databases) {
                agent.initDatabase(dbName);
            }
        }
    }

    /**
     * The main task for the server process is to wait for connections. Once a
     * connection is received, the Server process hands this off to a
     * ProtocolManager to handle. It first confirms that it can do so by looking
     * at the maximum number of clients it can handle. It rejects the connection
     * if maximum limit has been reached.
     */
    public void run() {
        try {
            // wait for something interesting to happen
            // log.debug("Waiting for clients to connect");
            for (;;) {
                logger.debug("Waiting for clients to connect");
                if (selector.select() > 0) {
                    Set readyKeys = selector.selectedKeys();
                    Iterator readyIter = readyKeys.iterator();

                    // walk thru the set
                    while (readyIter.hasNext()) {
                        // get the key
                        SelectionKey key = (SelectionKey) readyIter.next();

                        // remove the current key
                        readyIter.remove();

                        if (key.isAcceptable()) {
                            // new connection attempt
                            ServerSocketChannel serverChannel = (ServerSocketChannel) key
                                    .channel();

                            SocketChannel client = serverChannel.accept();

                            // hand off the client connection to the
                            // ProtocolManager
                            logger.debug("Client connection request arrived");
                            try {
                                if (client.isConnectionPending()) {
                                    client.finishConnect();
                                }
                                theManager.addClient(client);
                            } catch (Exception e) {
                                theManager.rejectClient(client, e);
                                try {
                                    client.close();
                                } catch (IOException ioe) {
                                    logger.catching(ioe);
                                }
                            }
                        }
                    } // end while readyIter
                } // end if select()
            } // end for
        } catch (ClosedChannelException e) {
            return;
        } catch (IOException e) {
            XLogger.getLogger("Server").fatal(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Instantiate a ProtocolManager and start listening for incoming
     * connections
     * 
     * @throws IOException
     *             if it is failed to open server socket
     */
    public void open() throws IOException {
        this.port = Property.getInt("xdb.port", 6453);
        try {
            Class pmClass = Class.forName(Props.XDB_PROTOCOL_HANDLER_CLASS);
            Method pmFactory = pmClass.getMethod("getProtocolManager", new Class[0]);
            this.theManager = (ProtocolManager) pmFactory.invoke(null, new Object[0]);
        } catch (Exception e) {
            throw new IOException("Can not initialize the Protocol Handler" + e.getMessage());
        }
        selector = Selector.open();
        // create a server socket channel and bind to port
        channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        InetSocketAddress isa = new InetSocketAddress(port);
        channel.socket().bind(isa);

        // register interest in Connection Attempts by clients
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }
}
