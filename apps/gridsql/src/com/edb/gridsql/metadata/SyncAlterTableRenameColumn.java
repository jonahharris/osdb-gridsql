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

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterRenameColumn;

/**
 * SyncAlterTableAddColumn class synchornizes the MetaData DB after a ALTER
 * TABLE ADD COLUMN has been successful on the user DB
 *
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 */

public class SyncAlterTableRenameColumn implements IMetaDataUpdate {

    private SqlAlterRenameColumn aSqlAlterRenameColumn;

    private boolean updatePartColumn;

    /**
     * Constructor Creates an Object for Updating and refresing the MetaData DB
     * as per the SqlAlterAddColumn (CREATE TABLE ...)
     */
    public SyncAlterTableRenameColumn(SqlAlterRenameColumn aSqlAlterRenameColumn) {
        this.aSqlAlterRenameColumn = aSqlAlterRenameColumn;
    }

    /**
     * Method execute() Updates the MetaData DB as per the ALTER TABLE ADD COL
     * statement stored in aSqlAlterAddColumn
     */
    public void execute(XDBSessionContext client) throws Exception {
        // -----------------------------
        // update the xsyscolumn table etc
        // -----------------------------
        String statement = "UPDATE xsyscolumns SET colname = '"
                + aSqlAlterRenameColumn.getNewName().replace("'", "''")
                + "' WHERE colid = "
                + aSqlAlterRenameColumn.getSysColumn().getColID();
        MetaData.getMetaData().executeUpdate(statement);
        updatePartColumn = aSqlAlterRenameColumn.getParent().getTable()
                .getPartitionedColumn() == aSqlAlterRenameColumn.getSysColumn();
        if (updatePartColumn) {
            statement = "UPDATE xsystables SET partcol = '"
                    + aSqlAlterRenameColumn.getNewName().replace("'", "''")
                    + "' WHERE tableid = "
                    + aSqlAlterRenameColumn.getParent().getTable().getTableId();
            MetaData.getMetaData().executeUpdate(statement);
        }
    }

    /**
     * refresh() Refreshes the MetaData cahce by reading in the table
     * information just created
     */
    public void refresh() throws Exception {
        // refresh MetaData cache for new table added
        SysTable table = aSqlAlterRenameColumn.getParent().getTable();
        table.readTableInfo();
        if (updatePartColumn) {
            table.setPartitioning(aSqlAlterRenameColumn.getNewName(),
                    table.getPartitionScheme(), table.getPartitionMap());
        }
    }
}
