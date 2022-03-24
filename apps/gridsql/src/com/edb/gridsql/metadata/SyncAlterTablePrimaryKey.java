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
package com.edb.gridsql.metadata;

// import standard java packeages used
import java.sql.ResultSet;
import java.util.List;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterAddPrimary;
import com.edb.gridsql.parser.SqlAlterTable;
import com.edb.gridsql.parser.SqlCreateIndexKey;

/**
 * SyncAlterTablePrimaryKey class synchornizes the MetaData DB after a ALTER
 * TABLE PRIMARY KEY has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 */

public class SyncAlterTablePrimaryKey implements IMetaDataUpdate {
    // This is not used anymore - executeDDLOnMultipleNodes takes care of
    // updates to MetaData DB
    private SqlAlterAddPrimary aSqlAlterTablePrimaryKey;

    private SqlAlterTable aSqlAlterTable;

    /**
     * Constructor Creates an Object for Updating and refresing the MetaData DB
     * as per the SqlAlterTablePrimaryKey (CREATE TABLE ...)
     */
    public SyncAlterTablePrimaryKey(SqlAlterAddPrimary aSqlAlterTablePrimaryKey) {
        this.aSqlAlterTablePrimaryKey = aSqlAlterTablePrimaryKey;
        aSqlAlterTable = aSqlAlterTablePrimaryKey.getParent();
    }

    /**
     * Method execute() Updates the MetaData DB as per the CREATE TABLE
     * statement stored in aSqlAlterTablePrimaryKey
     */
    public void execute(XDBSessionContext client) throws Exception {
        int indexid;
        int tableid;
        SysTable aSysTable;

        // keys are handled in two parts, create index, then unique index on
        // constraint
        // get the table id from the table we are created key for
        // There does not seem to be a clean (JDBC standard) way of
        // determining the serial id value just inserted.
        // This is dependent on the underlying database.
        // So, we requery. (yuck)
        aSysTable = aSqlAlterTable.getTable();
        tableid = aSysTable.getTableId();
        indexid = aSqlAlterTablePrimaryKey.getIndexIDUsed();

        if (indexid == -1) {
            List<String> columnNames = aSqlAlterTablePrimaryKey
                    .getColumnNames();
            SqlCreateIndexKey[] indexKeys = new SqlCreateIndexKey[columnNames
                    .size()];
            for (int i = 0; i < indexKeys.length; i++) {
                String colName = columnNames.get(i);
                indexKeys[i] = new SqlCreateIndexKey(aSysTable.getSysColumn(
                        colName).getColID(), colName, null, null); // coloperateor
            }
            indexid = MetaUtils.createIndex(indexid, aSqlAlterTablePrimaryKey
                    .getConstraintName(), tableid, true, indexKeys, aSysTable
                    .getTablespaceID(), null, null);// usingtype, wherepred
        } // done for index

        // -------------------------------
        // xsysconstraints for primary key
        // -------------------------------

        int constid;
        String sqlCommand = "SELECT max(constid) FROM xsysconstraints";
        ResultSet rs = MetaData.getMetaData().executeQuery(sqlCommand);
        try {
            rs.next();
            constid = rs.getInt(1) + 1;
        } finally {
            rs.close();
        }

        // checkissoft
        // String xsysconstraints = "INSERT INTO xsysconstraints "+
        // "(constid, tableid, consttype, Idxid, issoft, constname) "+
        // "VALUES ("+
        // constid + "," +
        // tableid+","+
        // "'P'"+","+
        // indexid+","
        // +"0"+","+
        // constrName+")";
        String xsysconstraints = "INSERT INTO xsysconstraints "
                + "(constid, tableid, consttype, Idxid, issoft, constname) "
                + "VALUES (" + constid + "," + tableid + "," + "'P'" + ","
                + indexid + "," + "0" + "," + "'"
                + aSqlAlterTablePrimaryKey.getConstraintName() + "'" + ")";

        MetaData.getMetaData().executeUpdate(xsysconstraints);
    }

    /**
     * refresh() Refreshes the MetaData cahce by reading in the table
     * information just created
     */
    public void refresh() throws Exception {
        // refresh MetaData structure, adding new index
        aSqlAlterTable.getTable().readTableInfo();
    }
}
