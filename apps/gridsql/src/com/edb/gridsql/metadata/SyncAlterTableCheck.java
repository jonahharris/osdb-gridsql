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

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterAddCheck;
import com.edb.gridsql.parser.SqlAlterTable;

/**
 * SyncAlterTablePrimaryKey class synchornizes the MetaData DB after a ALTER
 * TABLE PRIMARY KEY has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 */

public class SyncAlterTableCheck implements IMetaDataUpdate {
    // This is not used anymore - executeDDLOnMultipleNodes takes care of
    // updates to MetaData DB
    private SqlAlterAddCheck aSqlAlterTableCheck;

    private SqlAlterTable aSqlAlterTable;

    /**
     * Constructor Creates an Object for Updating and refresing the MetaData DB
     * as per the SqlAlterTablePrimaryKey (CREATE TABLE ...)
     */
    public SyncAlterTableCheck(SqlAlterAddCheck aSqlAlterTableCheck) {
        this.aSqlAlterTableCheck = aSqlAlterTableCheck;
        aSqlAlterTable = aSqlAlterTableCheck.getParent();
    }

    /**
     * Method execute() Updates the MetaData DB as per the CREATE TABLE
     * statement stored in aSqlAlterTablePrimaryKey
     */
    public void execute(XDBSessionContext client) throws Exception {
        // -------------------------------
        // xsysconstraints for check
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

        String xsysconstraints = "INSERT INTO xsysconstraints "
                + "(constid, tableid, consttype, Idxid, issoft, constname) "
                + "VALUES (" + constid + ","
                + aSqlAlterTable.getTable().getSysTableid() + "," + "'C'" + ","
                + "null," + "0" + "," + "'"
                + aSqlAlterTableCheck.getConstraintName() + "'" + ")";

        MetaData.getMetaData().executeUpdate(xsysconstraints);
        int checkid;
        sqlCommand = "SELECT max(constid) FROM xsyschecks";
        rs = MetaData.getMetaData().executeQuery(sqlCommand);
        try {
            rs.next();
            checkid = rs.getInt(1) + 1;
        } finally {
            rs.close();
        }

        String xsyschecks = "INSERT INTO xsyschecks "
                + "(checkid, constid, seqno, checkstmt) " + "VALUES ("
                + checkid + "," + constid + "," + "1," + "'"
                + aSqlAlterTableCheck.getCheckDef() + "'" + ")";

        MetaData.getMetaData().executeUpdate(xsyschecks);
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
