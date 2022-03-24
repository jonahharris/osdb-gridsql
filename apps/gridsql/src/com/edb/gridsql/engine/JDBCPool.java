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
 */
package com.edb.gridsql.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Level;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.exception.XDBServerException;

/**
 *  
 * 
 */
public class JDBCPool extends ObjectPool<Connection> {
    private static final XLogger logger = XLogger.getLogger(JDBCPool.class);

    private String url;

    private String user;

    private String passwd;

    /**
     * 
     * @param driver 
     * @param url 
     * @param user 
     * @param passwd 
     * @param minSize 
     * @param maxSize 
     */
    public JDBCPool(String driver, String url, String user, String passwd,
            int minSize, int maxSize) {
        super(minSize, maxSize);
        this.url = url;
        this.user = user;
        this.passwd = passwd;
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            logger.catching(e);
        }
    }

    
    /**
     * 
     * @throws com.edb.gridsql.exception.XDBServerException 
     * @return 
     * @see com.edb.gridsql.server.ObjectPool#createEntry()
     */

    @Override
    protected Connection createEntry() throws XDBServerException {
        final String method = "createEntry";
        logger.entering(method);
        Connection conn = null;

        try {

            if ((user == null || user.length() == 0)
                    && (passwd == null || passwd.length() == 0)) {
                conn = DriverManager.getConnection(url);
            } else {
                conn = DriverManager.getConnection(url, user, passwd);
            }
            logger.log(Level.INFO, "Created connection: %0%",
                    new Object[] { conn });
            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            logger.log(Level.INFO, "Set autocommit false on: %0%",
                    new Object[] { conn });
            return conn;

        } catch (SQLException e) {
            throw new XDBServerException("Can not establish connection", e);
        } finally {
            logger.exiting(method, conn);
        }
    }

    /**
     * 
     * @param entry 
     * @see com.edb.gridsql.server.ObjectPool#destroyEntry(java.lang.Object)
     */

    @Override
    protected void destroyEntry(Connection entry) {
        final String method = "destroyEntry";
        logger.entering(method, new Object[] { entry });
        try {

            logger.log(Level.INFO, "Closing connection: %0%",
                    new Object[] { entry });
            (entry).close();
            logger.log(Level.INFO, "Closed connection: %0%",
                    new Object[] { entry });

        } catch (SQLException e) {
            logger.catching(e);
        } finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     * @throws com.edb.gridsql.exception.XDBServerException 
     * @return 
     */

    public Connection getConnection() throws XDBServerException {
        final String method = "getConnection";
        logger.entering(method);
        Connection conn = null;
        try {

            conn = getObject();
            logger.log(Level.INFO, "Getting connection: %0%",
                    new Object[] { conn });
            return conn;

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     * @param connection 
     * @throws com.edb.gridsql.exception.XDBServerException 
     */

    public void releaseConnection(Connection connection)
            throws XDBServerException {
        final String method = "releaseConnection";
        logger.entering(method, new Object[] { connection });
        try {

            logger.log(Level.INFO, "Releasing connection: %0%",
                    new Object[] { connection });
            releaseObject(connection);
            logger.log(Level.INFO, "Released connection: %0%",
                    new Object[] { connection });

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     * @param connection 
     */

    public void destroyConnection(Connection connection) {
        final String method = "destroyConnection";
        logger.entering(method, new Object[] { connection });
        try {

            logger.log(Level.INFO, "Releasing connection: %0%",
                    new Object[] { connection });
            // Call connection.close() would block, if connection hang.
            destroyObject(connection, false);
            logger.log(Level.INFO, "Released connection: %0%",
                    new Object[] { connection });

        } catch (Exception e) {
            logger.catching(e);
        } finally {
            logger.exiting(method);
        }
    }
}
