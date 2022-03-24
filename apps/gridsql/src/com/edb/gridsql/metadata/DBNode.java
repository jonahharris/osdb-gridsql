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
package com.edb.gridsql.metadata;

import java.util.Enumeration;
import java.util.Properties;

import com.edb.gridsql.common.util.Property;

/**
 *  
 * 
 */
public class DBNode {

    private static final int POOL_SIZE = 5;

    private static final long POOL_TIMEOUT = 60000L;

    private Node aNode;

    private SysDatabase aDatabase;

    private boolean online = false;

    private int poolSize = POOL_SIZE;

    private long poolTimeout = POOL_TIMEOUT;

    /**
     * 
     */
    DBNode(Node aNode, SysDatabase aDatabase) {
        this.aNode = aNode;
        this.aDatabase = aDatabase;
        aNode.addDbNode(this);
        aDatabase.addDbNode(this);
    }

    void remove() {
        // Just to ensure this lock was acquired
        aNode.removeDBNode(aDatabase.getDbname());
        aDatabase.removeDBNode(aNode.getNodeid());
    }

    /**
     * @return the database
     */
    public SysDatabase getDatabase() {
        return aDatabase;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return aNode;
    }

    /**
     * @return is the database online (client can connect to it)
     */
    public boolean isOnline() {
        synchronized (MetaData.getMetaData().getStartupLock()) {
            return online;
        }
    }

    /**
     * 
     */
    public void setOnline() {
        Object startupLock = MetaData.getMetaData().getStartupLock();
        synchronized (startupLock) {
            online = true;
            startupLock.notifyAll();
        }
    }

    /**
     * 
     */
    public void setOffline() {
        Object startupLock = MetaData.getMetaData().getStartupLock();
        synchronized (startupLock) {
            online = false;
            startupLock.notifyAll();
        }
    }

    /**
     * 
     * @return the JDBC URI
     */
    public String getJdbcString() {
        return aNode.getJdbcString(aDatabase.getDbname());
    }

    /**
     * @return the max size of the connection pool 
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * @return the timeout
     */
    public long getPoolTimeout() {
        return poolTimeout;
    }

    /**
     * @return nodeId the node ID
     */
    public int getNodeId() {
        return aNode.getNodeid();
    }

    @Override
    public String toString() {
        return "{" + aDatabase + "," + aNode + "}";
    }

    private NodeDBConnectionInfo connectionInfo;

    public NodeDBConnectionInfo getNodeDBConnectionInfo() {
        if (connectionInfo == null) {
            Properties props = new Properties();
            String defaultPrefix = "xdb.default.custom.";
            String nodePrefix = "xdb.node." + aNode.getNodeid() + ".custom.";
            Enumeration propertyNames = Property.getProperties()
                    .propertyNames();
            while (propertyNames.hasMoreElements()) {
                String name = (String) propertyNames.nextElement();
                if (name.startsWith(nodePrefix)) {
                    String key = name.substring(nodePrefix.length());
                    String value = Property.get(name);
                    props.setProperty(key, value);
                } else if (name.startsWith(defaultPrefix)) {
                    String key = name.substring(nodePrefix.length());
                    String value = Property.get(name);
                    if (!props.containsKey(key)) {
                        props.setProperty(key, value);
                    }
                }
            }
            connectionInfo = new NodeDBConnectionInfo(aNode.getNodeid(), aNode
                    .getSHost(), aNode.getPort(), aNode
                    .getNodeDatabaseString(aDatabase.getDbname()), aNode
                    .getJdbcUser(), aNode.getJdbcPassword(), props);
        }
        return connectionInfo;
    }
}
