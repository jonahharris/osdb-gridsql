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
public class XdbDbStop {
    /**
     * 
     * @param errorMsg
     */
    private static void terminate(String errorMsg) {
        System.err.println(errorMsg);
        System.err
                .println("Parameters: <connect> -d <database>\n"
                        + "\twhere <connect> is -j jdbc:edb://<host>:<port>/<database>?user=<username>&password=<password>\n"
                        + "\tor [-h <host>] [-s <port>] -d <database> -u <user> [-p <password>]\n"
                        + "\t-h <host> : Host name or IP address where XDBServer is running. Default is localhost\n"
                        + "\t-s <port> : XDBServer's port. Default is 6453\n"
                        + "\t-d <database> : Name of databases to stop.\n"
                        + "\t-u <user>, -p <password> : Login to the database.");
        System.exit(1);
    }

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Connection con = null;
        Statement stmt = null;

        try {
            Map<String, List<String>> m = ParseArgs.parse(args, "hsdup");

            String database = ParseArgs.getStrArg(m, "-d");
            con = Util.connect(m, true);
            stmt = con.createStatement();
            String stopDBstmt = "STOP DATABASE " + database;
            stmt.execute(stopDBstmt);
            System.out.println("Database(s) " + database + " stopped.");

        } catch (Exception e) {
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
