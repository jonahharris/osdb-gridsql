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
public class ConnectMessage extends CommandMessage {
    private static final long serialVersionUID = 8585413334511623568L;

    private String database;

    private String jdbcDriver;

    private String jdbcString;

    private String jdbcUser;

    private String jdbcPassword;

    private int maxConns;

    private int minConns;

    private long timeOut;

    /** Parameterless constructor required for serialization */
    public ConnectMessage() {
    }

    /**
     * @param messageType
     */
    protected ConnectMessage(int messageType) {
        super(messageType);
    }

    /**
     * @return the database name
     */
    @Override
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database name
     */
    @Override
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the fully qualified name of JDBC class of the driver
     */
    @Override
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * @return the JDBC URI
     */
    @Override
    public String getJdbcString() {
        return jdbcString;
    }

    /**
     * @return the user name
     */
    @Override
    public String getJdbcUser() {
        return jdbcUser;
    }

    /**
     * @return the password
     */
    @Override
    public String getJdbcPassword() {
        return jdbcPassword;
    }

    /**
     * @return the max number of connections in the pool 
     */
    @Override
    public int getMaxConns() {
        return maxConns;
    }

    /**
     * @return the min number of connections in the pool
     */
    @Override
    public int getMinConns() {
        return minConns;
    }

    /**
     * @return the timeout
     */
    @Override
    public long getTimeOut() {
        return timeOut;
    }

    /**
     * @param string the fully qualified name of JDBC class of the driver
     */
    @Override
    public void setJdbcDriver(String string) {
        jdbcDriver = string;
    }

    /**
     * @param string the JDBC URI
     */
    @Override
    public void setJdbcString(String string) {
        jdbcString = string;
    }

    /**
     * @param string the user name 
     */
    @Override
    public void setJdbcUser(String string) {
        jdbcUser = string;
    }

    /**
     * @param string the password
     */
    @Override
    public void setJdbcPassword(String string) {
        jdbcPassword = string;
    }

    /**
     * @param maxConns the max number of connections in the pool
     */
    @Override
    public void setMaxConns(int maxConns) {
        this.maxConns = maxConns;
    }

    /**
     * @param minConns the min number of connections in the pool
     */
    @Override
    public void setMinConns(int minConns) {
        this.minConns = minConns;
    }

    /**
     * @param timeOut the timeout
     */
    @Override
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
}
