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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.SqlCreateTableSpace;

/**
 * 
 * 
 */
public class SyncCreateTablespace implements IMetaDataUpdate {
    private static final XLogger logger = XLogger
            .getLogger(SyncCreateTablespace.class);

    private XDBSessionContext client;

    private SqlCreateTableSpace parent;

    private int tablespaceID;

    /**
     * 
     */
    public SyncCreateTablespace(SqlCreateTableSpace parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.server.XDBSessionContext)
     */
    public void execute(XDBSessionContext client) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            this.client = client;

            MetaData meta = MetaData.getMetaData();
            ResultSet rs = meta
                    .executeQuery("SELECT max(tablespaceid) FROM xsystablespaces");
            try {
                rs.next();
                tablespaceID = rs.getInt(1) + 1;
            } finally {
                rs.close();
            }

            String commandStr = "INSERT INTO xsystablespaces"
                    + " (tablespaceid, tablespacename, ownerid) VALUES ("
                    + tablespaceID + ", " + "'" + parent.getName() + "', "
                    + client.getCurrentUser().getUserID() + ")";
            meta.executeUpdate(commandStr);
            // Insert locations
            int locationID;
            rs = meta
                    .executeQuery("SELECT max(tablespacelocid) FROM xsystablespacelocs");
            try {
                rs.next();
                locationID = rs.getInt(1) + 1;
            } finally {
                rs.close();
            }

            commandStr = "INSERT INTO xsystablespacelocs"
                    + " (tablespacelocid, tablespaceid, filepath, nodeid)"
                    + " VALUES (?, ?, ?, ?)";
            PreparedStatement ps = meta.prepareStatement(commandStr);
            // Same for all locations
            ps.setInt(2, tablespaceID);
            for (Iterator it = parent.getLocations().entrySet().iterator(); it
                    .hasNext();) {
                Map.Entry entry = (Map.Entry) it.next();
                ps.setInt(1, locationID++);
                ps.setString(3, (String) entry.getValue());
                ps.setInt(4, ((DBNode) entry.getKey()).getNodeId());
                if (ps.executeUpdate() != 1) {
                    XDBServerException ex = new XDBServerException(
                            "Failed to insert row into \"xsystablespacelocs\"");
                    logger.throwing(ex);
                    throw ex;
                }
            }
        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#refresh()
     */
    public void refresh() throws Exception {
        HashMap locations = new HashMap();
        for (Iterator it = parent.getLocations().entrySet().iterator(); it
                .hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            locations.put(new Integer(((DBNode) entry.getKey()).getNodeId()),
                    entry.getValue());
        }
        SysTablespace tablespace = new SysTablespace(tablespaceID, parent
                .getName(), client.getCurrentUser().getUserID(), locations);
        MetaData.getMetaData().addTablespace(tablespace);
    }

}
