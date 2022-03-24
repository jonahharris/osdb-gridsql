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

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.parser.SqlAlterTableSpace;

/**
 * 
 * 
 */
public class SyncAlterTablespace implements IMetaDataUpdate {
    private static final XLogger logger = XLogger
            .getLogger(SyncDropTablespace.class);

    private SqlAlterTableSpace parent;

    /**
     * 
     */
    public SyncAlterTablespace(SqlAlterTableSpace parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.server.XDBSessionContext)
     */
    public void execute(XDBSessionContext client) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            MetaData meta = MetaData.getMetaData();
            int tablespaceID = parent.getTablespace().getTablespaceID();
            String command = "UPDATE xsystablespaces SET tablespacename = '"
                    + parent.getNewName() + "' WHERE tablespaceid = "
                    + tablespaceID;
            meta.executeUpdate(command);

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#refresh()
     */
    public void refresh() throws Exception {
        MetaData meta = MetaData.getMetaData();
        SysTablespace tablespace = parent.getTablespace();
        meta.removeTablespace(tablespace);
        tablespace.setName(parent.getNewName());
        meta.addTablespace(tablespace);
    }

}
