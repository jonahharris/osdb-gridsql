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
 *  
 *
 *
 */
package com.edb.gridsql.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

/**
 *  
 */
public class XdbDbStart {
    /**
     * 
     * @param errorMsg
     */
    private static void terminate(String errorMsg) {
        System.err.println(errorMsg);
        System.err
                .println("Parameters: <connect> -d <database> [-w <waittimeout>]\n"
                        + "\twhere <connect> is -j jdbc:edb://<host>:<port>/<database>?user=<username>&password=<password>\n"
                        + "\tor [-h <host>] [-s <port>] -d <database> -u <user> [-p <password>]\n"
                        + "\t-h <host> : Host name or IP address where XDBServer is running. Default is localhost\n"
                        + "\t-s <port> : XDBServer's port. Default is 6453\n"
                        + "\t-d <database> : Name of databases to start.\n"
                        + "\t-u <user>, -p <password> : Login to the database\n"
                        + "\t-w <waittimeout> : Timeout (seconds) to wait for database is online. Default is 60.");
        System.exit(1);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Connection con = null;
        Statement stmt = null;
        String startDBstmt = null;
        try {
            Map<String, List<String>> m = ParseArgs.parse(args, "jhsdupw");

            String database = ParseArgs.getStrArg(m, "-d");
            String waitTimeout = ParseArgs.getStrArg(m, "-w");

            con = Util.connect(m, true);
            stmt = con.createStatement();
            startDBstmt = "START DATABASE " + database;

            if (waitTimeout != null) {
                startDBstmt += " WAITTIMEOUT " + waitTimeout;
            }

            stmt.execute(startDBstmt);
            System.out.println("Database(s) " + database + " started.");

        } catch (Exception e) {
            if (startDBstmt != null)
                System.err.println("The following command was sent: " + startDBstmt); 
            terminate(e.getMessage());
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }

                if (con != null) {
                    con.close();
                }
            } catch (SQLException sqle) {
            }
        }

        System.exit(0);
    }
}
