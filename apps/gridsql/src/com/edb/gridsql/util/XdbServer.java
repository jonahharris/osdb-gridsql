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
package com.edb.gridsql.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import com.edb.gridsql.common.CommandLog;
import com.edb.gridsql.common.util.Property;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.NodeDBConnectionInfo;
import com.edb.gridsql.server.Server;

/**
 * The <code>XDBServer</code> class implements the Extend DB Server. The
 * server uses asynchronous network I/O and <code>PooledExecutor</code> to
 * provide scalability and speed. It employs a message passing architechure to
 * talk to underlying nodes in the system.
 * 
 * 
 */

public class XdbServer {

    /** the logger for the server */
    private static final XLogger logger = XLogger.getLogger(XdbServer.class);

    private static final String USAGE = "Parameters:  [-d database_list] [-x] [-m]\n"
            + "where <database_list> is a space-separated list of Nodes to launch\n"
            + " -x Start node databases" + " -m Start Metadata database";

    private static void printUsage() {
        System.out.println(USAGE);
    }

    /**
     * 
     * @param args
     * 
     */

    public static void main(String args[]) {
        try {
            // Set up a simple configuration that logs on the console.
            if (Property.get("log4j.configuration") != null
                    && (new File(Property.get("log4j.configuration"))).exists()) {
                PropertyConfigurator.configure(Property
                        .get("log4j.configuration"));
            } else if ((new File("log4j.properties")).exists()) {
                PropertyConfigurator.configure("log4j.properties");
            } else {
                PropertyConfigurator.configure(Property.getProperties());
            }

            Map<String, List<String>> commands = null;

            try {
                commands = ParseArgs.parse(args, "dmx");
            } catch (Exception e) {
                // this one is ok to keep as System.err.println
                System.err.println("Error: " + e.getMessage());
                printUsage();
                System.exit(-1);
            }

            // suppress logging to console
            CommandLog.setAdditivity(false);

            boolean doUnderlyingDBs = commands.containsKey("-x");
            boolean doMetadataDB = commands.containsKey("-m");

            if (doMetadataDB) {
                // try and start metadata database
                // (Maybe we should simplify this one with its own simple
                // xdb.config value - just for metadata)
                DbGateway aGwy = new DbGateway();
                aGwy.startDbOnNodes(new HashMap<String, String>(),
                        new NodeDBConnectionInfo[] { MetaData
                                .getMetadataDBConnectionInfo() });
            }

            Server server = new Server();

            if (doUnderlyingDBs) {
                DbGateway aGwy = new DbGateway();
                aGwy.startDbOnNodes(new HashMap<String, String>(), server
                        .getNodeDBConnectionInfos(commands.get("-d")));
            }

            server.initDatabases(commands.get("-d"));
            server.open();

            logger.info("Starting server thread.");
            new Thread(server).start();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
