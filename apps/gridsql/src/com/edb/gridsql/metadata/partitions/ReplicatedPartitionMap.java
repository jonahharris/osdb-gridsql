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
package com.edb.gridsql.metadata.partitions;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysTable;

/**
 * 
 * 
 */
public class ReplicatedPartitionMap implements PartitionMap {
    /**
     * 
     */
    private static final long serialVersionUID = 7343460726531913439L;

    private Integer[] partitions;

    /** Parameterless constructor is required for serialization */
    public ReplicatedPartitionMap() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#findPartitions(java.lang.String)
     */
    public Collection<Integer> findPartitions(String key) {
        return allPartitions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#generateDistribution(java.util.Collection)
     */
    public void generateDistribution(Collection<Integer> partitionIdList) {
        partitions = partitionIdList
                .toArray(new Integer[partitionIdList.size()]);
        Arrays.sort(partitions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#getPartitions(java.lang.String)
     */
    public Collection<Integer> getPartitions(String key) {
        return allPartitions();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#joinPartitions()
     */
    public Collection<Integer> joinPartitions() {
        return Collections.singleton(partitions[0]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#allPartitions()
     */
    public Collection<Integer> allPartitions() {
        return Arrays.asList(partitions);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#getRedundancyLevel()
     */
    public int getRedundancyLevel() {
        return 0;
    }

    @Override
    public PartitionMap clone() {
        try {
            return (PartitionMap) super.clone();
        } catch (CloneNotSupportedException e) {
            // never thrown
            return null;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ReplicatedPartitionMap) {

            return Arrays.equals(partitions,
                    ((ReplicatedPartitionMap) other).partitions);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#readMapFromMetadataDB(com.edb.gridsql.MetaData.MetaData,
     *      int)
     */
    public void readMapFromMetadataDB(MetaData metadata, SysTable parent)
            throws SQLException {
        TreeSet<Integer> partSet = new TreeSet<Integer>();
        String query = "SELECT nodeid from xsystabparts WHERE tableid = "
                + parent.getTableId() + " ORDER BY nodeid";
        ResultSet rs = metadata.executeQuery(query);
        try {
            while (rs.next()) {
                partSet.add(rs.getInt(1));
            }
        } finally {
            rs.close();
        }
        if (partSet.isEmpty()) {
            // Lookup table - on all nodes
            for (DBNode dbNode : parent.getSysDatabase().getDBNodeList()) {
                partSet.add(dbNode.getNodeId());
            }
        }
        partitions = partSet.toArray(new Integer[partSet.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#storeMapToMetadataDB(com.edb.gridsql.MetaData.MetaData,
     *      int)
     */
    public void storeMapToMetadataDB(MetaData metadata, SysDatabase database,
            int tableID) throws SQLException {
        if (database.getDBNodeList().size() != partitions.length) {
            int partid = 0;
            String query = "SELECT max(partid) FROM xsystabparts";
            ResultSet rs = metadata.executeQuery(query);
            try {
                rs.next();
                partid = rs.getInt(1) + 1;
            } finally {
                rs.close();
            }
            for (Integer nodeID : partitions) {
                String insert = "INSERT INTO xsystabparts "
                        + "(partid, tableid, dbid, nodeid) " + "VALUES ("
                        + partid++ + ", " + tableID + ", " + database.getDbid()
                        + ", " + nodeID + ")";
                metadata.executeUpdate(insert);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.partitions.PartitionMap#removeMapFromMetadataDB(com.edb.gridsql.MetaData.MetaData,
     *      int)
     */
    public void removeMapFromMetadataDB(MetaData metadata, SysTable parent)
            throws SQLException {
        String delete = "DELETE FROM xsystabparts WHERE tableid = "
                + parent.getTableId();
        metadata.executeUpdate(delete);
    }
}
