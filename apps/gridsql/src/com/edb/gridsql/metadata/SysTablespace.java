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

import java.util.Map;

/**
 *
 *
 */
public class SysTablespace {
    private int tablespaceID;

    private String tablespaceName;

    private Map<Integer,String> locations;

    private int ownerID;

    /**
     * @param locations
     * @param ownerid
     * @param tablespaceid
     * @param name
     */
    public SysTablespace(int tablespaceid, String name, int ownerid,
            Map<Integer,String> locations) {
        this.locations = locations;
        ownerID = ownerid;
        tablespaceID = tablespaceid;
        tablespaceName = name;
    }

    /**
     * @return Returns the locations.
     */
    public Map<Integer,String> getLocations() {
        return locations;
    }

    /**
     * @return Returns the ownerID.
     */
    public int getOwnerID() {
        return ownerID;
    }

    /**
     * @return Returns the tablespaceID.
     */
    public int getTablespaceID() {
        return tablespaceID;
    }

    /**
     * @return Returns the tablespaceName.
     */
    public String getTablespaceName() {
        return tablespaceName;
    }

    /**
     * @return Returns the tablespaceName for the specified node
     */
    public String getNodeTablespaceName(int nodeId) {
        // we generate the name, but long term this can be made to be
        // arbitrary and put in the locations Map
        return tablespaceName + "_" + nodeId;
    }
    
    /**
     * @param newName
     */
    void setName(String newName) {
        tablespaceName = newName;
    }
}
