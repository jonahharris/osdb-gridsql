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
import com.edb.gridsql.parser.SqlAlterModifyColumn;

/**
 * SyncAlterTableAddColumn class synchornizes the MetaData DB after a ALTER
 * TABLE ADD COLUMN has been successful on the user DB
 * 
 * It implements IMetaDataUpdate. These methods are called from
 * Engine.executeDDLOnMultipleNodes();
 */

public class SyncAlterTableModifyColumn implements IMetaDataUpdate {

    private SqlAlterModifyColumn aSqlAlterModifyColumn;

    /**
     * Constructor Creates an Object for Updating and refresing the MetaData DB
     * as per the SqlAlterAddColumn (CREATE TABLE ...)
     */
    public SyncAlterTableModifyColumn(SqlAlterModifyColumn aSqlAlterModifyColumn) {
        this.aSqlAlterModifyColumn = aSqlAlterModifyColumn;
    }

    /**
     * Method execute() Updates the MetaData DB as per the ALTER TABLE ADD COL
     * statement stored in aSqlAlterAddColumn
     */
    public void execute(XDBSessionContext client) throws Exception {
        // -----------------------------
        // update the xsyscolumn table etc
        // -----------------------------
        MetaUtils.modifyTableColumn(aSqlAlterModifyColumn.getColDef(),
                aSqlAlterModifyColumn.getParent().getTable());
    }

    /**
     * refresh() Refreshes the MetaData cahce by reading in the table
     * information just created
     */
    public void refresh() throws Exception {
        // refresh MetaData cache for new table added
        aSqlAlterModifyColumn.getParent().getTable().readTableInfo();
    }
}
