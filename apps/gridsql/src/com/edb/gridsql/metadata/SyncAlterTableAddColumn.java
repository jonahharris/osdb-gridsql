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
import java.util.Vector;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterAddColumn;

// import other packages

/**
 * SyncAlterTableAddColumn class synchornizes the MetaData DB after a ALTER
 * TABLE ADD COLUMN has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 */

public class SyncAlterTableAddColumn implements IMetaDataUpdate {

    private SysDatabase database;

    private SqlAlterAddColumn aSqlAlterAddColumn;

    boolean DEBUG;

    /**
     * Constructor Creates an Object for Updating and refresing the MetaData DB
     * as per the SqlAlterAddColumn (CREATE TABLE ...)
     */
    public SyncAlterTableAddColumn(SqlAlterAddColumn aSqlAlterAddColumn) {
        this.aSqlAlterAddColumn = aSqlAlterAddColumn;
    }

    /**
     * Method execute() Updates the MetaData DB as per the ALTER TABLE ADD COL
     * statement stored in aSqlAlterAddColumn
     */
    public void execute(XDBSessionContext client) throws Exception {
        database = client.getSysDatabase();
        // -----------------------------
        // update the xsyscolumn table etc
        // -----------------------------
        Vector colDefs = new Vector();
        colDefs.addElement(aSqlAlterAddColumn.getColDef());

        // which table
        SysTable sysTable = aSqlAlterAddColumn.getParent().getTable();
        int tableid = sysTable.getSysTableid();
        // request MetaData to add a table column
        // first find out the maximum seq number in the columns
        // and use that for beginSeq - to add the column to the end
        int maxSeq = MetaUtils.getMaxColSeqNum(database, aSqlAlterAddColumn
                .getParent().getTableName());
        MetaUtils.addTableColumns(maxSeq, colDefs, tableid);
    }

    /**
     * refresh() Refreshes the MetaData cahce by reading in the table
     * information just created
     */
    public void refresh() throws Exception {
        // refresh MetaData cache for new table added
        aSqlAlterAddColumn.getParent().getTable().readTableInfo();
    }
}
