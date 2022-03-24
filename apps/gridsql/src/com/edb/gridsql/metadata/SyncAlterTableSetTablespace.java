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

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterSetTablespace;
import com.edb.gridsql.parser.SqlAlterTable;

/**
 * 
 * 
 */
public class SyncAlterTableSetTablespace implements IMetaDataUpdate {
    private SqlAlterSetTablespace aSqlAlterSetTablespace;

    private SqlAlterTable aSqlAlterTable;

    /**
     * 
     */
    public SyncAlterTableSetTablespace(
            SqlAlterSetTablespace aSqlAlterSetTablespace) {
        this.aSqlAlterSetTablespace = aSqlAlterSetTablespace;
        aSqlAlterTable = aSqlAlterSetTablespace.getParent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.server.XDBSessionContext)
     */
    public void execute(XDBSessionContext client) throws Exception {
        String command = "UPDATE xsystables SET tablespaceid = "
                + aSqlAlterSetTablespace.getTablespace().getTablespaceID()
                + " WHERE tableid = " + aSqlAlterTable.getTable().getTableId();
        MetaData.getMetaData().executeUpdate(command);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#refresh()
     */
    public void refresh() throws Exception {
        aSqlAlterTable.getTable().setTablespaceID(
                aSqlAlterSetTablespace.getTablespace().getTablespaceID());
    }
}
