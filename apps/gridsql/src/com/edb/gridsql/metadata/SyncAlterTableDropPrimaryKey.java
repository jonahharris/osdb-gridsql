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
 * SyncAlterTableDropPrimaryKey.java
 *
 *  
 */

package com.edb.gridsql.metadata;

// import other packages
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterDropPrimarykey;

/**
 * SyncAlterTableDropPrimaryKey class synchornizes the MetaData DB after a ALTER
 * TABLE DROP PRIMARY KEY has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 * 
 * 
 */
public class SyncAlterTableDropPrimaryKey implements IMetaDataUpdate {

    private SqlAlterDropPrimarykey aSqlAlterTableDropPrimaryKey;

    /** Creates a new instance of SyncAlterTableDropPrimaryKey */
    public SyncAlterTableDropPrimaryKey(SqlAlterDropPrimarykey aSqlAlterTable) {
        this.aSqlAlterTableDropPrimaryKey = aSqlAlterTable;
    }

    /**
     * Method execute() Updates the MetaData DB as per the ALTER TABLE statement
     * stored in aSqlAlterTableDropPrimaryKey
     */
    public void execute(XDBSessionContext client) throws Exception {
        /*
         * This will remove the information from the meta data tables.
         * 
         * The order in which we execute the delete statements xSysConstraints
         * 
         * If the index corresponding to the primary key is syscreated; drop the
         * index as well.
         */

        // find the primary Constraint id
        SysTable theTable = aSqlAlterTableDropPrimaryKey.getParent().getTable();

        SysConstraint primaryConstraint = theTable.getPrimaryConstraint();

        // ----------------------
        // xSysConstraints
        // ----------------------

        String xSysConstraints = "delete from xsysconstraints where constid = "
                + primaryConstraint.getConstID();

        MetaData.getMetaData().executeUpdate(xSysConstraints);

        // ----------------------
        // xSysIndexes
        // ----------------------
        SysIndex primIndex = theTable.getSysIndex(primaryConstraint.getIdxID());
        MetaUtils.dropIndex(primIndex.idxid);
    }

    /**
     * refresh() Refreshes the MetaData cache by reading in the table
     * information for the primary key just dropped
     */
    public void refresh() throws Exception {
        // refresh MetaData structure.
        aSqlAlterTableDropPrimaryKey.getParent().getTable().readTableInfo();
    }
}
