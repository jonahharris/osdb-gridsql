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
package com.edb.gridsql.metadata;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import com.edb.gridsql.engine.io.ResponseMessage;

/**
 * 
 * 
 */
public class NodeDBConnectionInfo implements Serializable {
    /**
     * Default serial version UID
     */
    private static final long serialVersionUID = 1817345309110297873L;

    private int nodeID;

    private String dbHost;

    private int dbPort;

    private String dbName;

    private String dbUser;

    private String dbPassword;

    private Properties extraProperties;

    /**
     * 
     */
    public NodeDBConnectionInfo() {
    }

    /**
     * Valid info could be only provided within MetaData package so restrict
     * access to this constructor to "package"
     * 
     * @param nodeid
     * @param host
     * @param port
     * @param name
     * @param user
     * @param password
     */
    NodeDBConnectionInfo(int nodeid, String host, int port, String name,
            String user, String password, Properties props) {
        nodeID = nodeid;
        dbHost = host;
        dbPort = port;
        dbName = name;
        dbUser = user;
        dbPassword = password;
        extraProperties = props == null ? new Properties() : props;
    }

    /**
     * Read connection info from ResponseMessage. Read must be in the same order
     * as in storeToMessage()
     * 
     * @param response
     */
    public NodeDBConnectionInfo(ResponseMessage response) {
        nodeID = response.readInt();
        dbHost = response.readString();
        dbPort = response.readInt();
        dbName = response.readString();
        dbUser = response.readString();
        dbPassword = response.readString();
        extraProperties = new Properties();
        int propCount = response.readInt();
        while (propCount > 0) {
            String key = response.readString();
            String value = response.readString();
            extraProperties.put(key, value);
            propCount--;
        }
    }

    /**
     * Host (name or address) where Node Database is running
     * 
     * @return Returns the dbHost.
     */
    public String getDbHost() {
        return dbHost;
    }

    /**
     * Name of Node Database (with N# modifier)
     * 
     * @return Returns the dbName.
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * Password to access Node Database
     * 
     * @return Returns the dbPassword.
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Port which database server is listening to
     * 
     * @return Returns the dbPort.
     */
    public int getDbPort() {
        return dbPort;
    }

    /**
     * User name to access Node Database
     * 
     * @return Returns the dbUser.
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * ID of the Node this Database belings to
     * 
     * @return Returns the nodeID.
     */
    public int getNodeID() {
        return nodeID;
    }

    public Map getProperties() {
        return Collections.unmodifiableMap(extraProperties);
    }
}
