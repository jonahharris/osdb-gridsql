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
 * SyncAlterTableDropConstraint.java
 *
 *  
 */

package com.edb.gridsql.metadata;

// import standard java packeages used

// import other packages
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterDropConstraint;

/**
 * SyncAlterTableDropContraint class synchornizes the MetaData DB after a ALTER
 * TABLE DROP FOREIGN KEY has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 * 
 * 
 */
public class SyncAlterTableDropConstraint implements IMetaDataUpdate {

    private SysDatabase database;

    private SqlAlterDropConstraint aSqlAlterDropConstraint;

    /** Creates a new instance of SyncAlterTableDropConstraint */
    public SyncAlterTableDropConstraint(
            SqlAlterDropConstraint aSqlAlterDropConstraint) {
        this.aSqlAlterDropConstraint = aSqlAlterDropConstraint;
    }

    /**
     * Method execute() Updates the MetaData DB as per the ALTER TABLE statement
     * stored in aSqlAlterDropConstraint
     */
    public void execute(XDBSessionContext client) throws Exception {
        /*
         * This will remove the information from the meta data tables.
         * 
         * The order in which we execute the delete statements xSysForeignKeys
         * xSysReferences xSysConstraints if (indexToDrop) xSysIndexKeys
         * xSysIndexes
         */
        database = client.getSysDatabase();
        SysTable theTable = aSqlAlterDropConstraint.getParent().getTable();
        SysConstraint theConstraint = theTable
                .getConstraint(aSqlAlterDropConstraint.getConstraintName());

        int constid = theConstraint.getConstID();
        int refid = aSqlAlterDropConstraint.getRefId();

        // ----------------------
        // xSysForeignKeys
        // ----------------------
        String xSysForeignKeys = "delete from xsysforeignkeys where refid = "
                + refid;
        MetaData.getMetaData().executeUpdate(xSysForeignKeys);

        // ----------------------
        // xSysReferences
        // ----------------------
        String xSysReferences = "delete from xsysreferences where refid = "
                + refid;
        MetaData.getMetaData().executeUpdate(xSysReferences);

        // ----------------------
        // xSysChecks
        // ----------------------
        String xSysChecks = "delete from xsyschecks where constid = " + constid;
        MetaData.getMetaData().executeUpdate(xSysChecks);

        // ----------------------
        // xSysConstraints
        // ----------------------
        String xSysConstraints = "delete from xsysconstraints where constid = "
                + constid;
        MetaData.getMetaData().executeUpdate(xSysConstraints);

        // if we need to drop the index
        int indexid = aSqlAlterDropConstraint.getIndexIdToDrop();
        if (indexid >= 0) {
            MetaUtils.dropIndex(indexid);
        }
    }

    /**
     * refresh() Refreshes the MetaData cahce by reading in the table
     * information just created
     */
    public void refresh() throws Exception {
        // refresh MetaData structure for the tables concerned
        aSqlAlterDropConstraint.getParent().getTable().readTableInfo();
        if (aSqlAlterDropConstraint.getRefTableName() != null) {
            database.getSysTable(aSqlAlterDropConstraint.getRefTableName())
                    .readTableInfo();
        }
    }
}
