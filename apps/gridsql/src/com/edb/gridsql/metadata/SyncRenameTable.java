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
 * SyncRenameTable.java
 * 
 *  
 */
package com.edb.gridsql.metadata;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlRenameTable;

/**
 *  
 */
public class SyncRenameTable implements IMetaDataUpdate {
    private SysDatabase database;

    private SqlRenameTable renameTable;

    /**
     * 
     */
    public SyncRenameTable(SqlRenameTable aRenameTable) {
        renameTable = aRenameTable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.MetaData.SysDatabase)
     */
    public void execute(XDBSessionContext client) throws Exception {
        database = MetaData.getMetaData().getSysDatabase(client.getDBName());
        SysTable tableToRename = database.getSysTable(renameTable
                .getOldTableName());
        int tableid = tableToRename.getSysTableid();
        String sql = "update xsystables set tablename = '"
                + renameTable.getNewTableName() + "' where tableid = "
                + tableid;
        MetaData.getMetaData().executeUpdate(sql);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#refresh()
     */
    public void refresh() throws Exception {
        database.renameSysTable(renameTable.getOldTableName(), renameTable
                .getNewTableName());
    }

}
