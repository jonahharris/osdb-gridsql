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
package com.edb.gridsql.engine.copy;

import com.edb.gridsql.common.util.XLogger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wrapper class to support both Edb and Postgres JDBC drivers when using
 * their COPY extension. This is a starting point to add support for other
 * drivers.
 */
public abstract class CopyManager {
    private static final XLogger logger = XLogger.getLogger(CopyManager.class);

    public static CopyManager getCopyManager(Connection connection) {
        try {
            if (connection instanceof com.edb.PGConnection) {
                return new EdbCopyManager((com.edb.PGConnection) connection);
            }
            if (connection instanceof org.postgresql.PGConnection) {
                return new PgCopyManager((org.postgresql.PGConnection) connection);
            }
        } catch (Exception ex) {
            logger.catching(ex);
        }
        throw new UnsupportedOperationException("The connection does not support COPY");
    }

    /**
     * Run specified COPY command using specified stream as a data source.
     * Copy is performed until end of stream is reached.
     * @param command
     *          must be a COPY FROM STDIN
     * @param in
     *          the data source
     * @return number of rows affected
     * @throws SQLException
     *          if SQL error occurs
     * @throws IOException
     *          problem of reading from the stream
     */
    public abstract long copyIn(String command, InputStream in) throws SQLException, IOException;

    /**
     * Run specified COPY command and create a CopyIn object to better control the process
     * @param command
     *          must be a COPY FROM STDIN
     * @return a CopyIn object
     * @throws SQLException
     *          if SQL error occurs
     */
    public abstract CopyIn copyIn(String command) throws SQLException;

    /**
     * Run specified COPY command using specified stream as a destination.
     * @param command
     *          must be a COPY TO STDOUT
     * @param out
     *          the destination
     * @return number of rows returned
     * @throws SQLException
     *          if SQL error occurs
     * @throws IOException
     *          problem of writing to the stream
     */
    public abstract long copyOut(String command, OutputStream out) throws SQLException, IOException;

    /**
     * Run specified COPY command and create a CopyOut object to better control the process
     * @param command
     *          must be a COPY TO STDOUT
     * @return a CopyOut object
     * @throws SQLException
     *          if SQL error occurs
     */
    public abstract CopyOut copyOut(String command) throws SQLException;
}
