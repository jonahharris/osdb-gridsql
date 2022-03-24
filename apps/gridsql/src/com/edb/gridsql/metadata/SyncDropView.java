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
 * SyncDropUser.java
 * 
 *  
 */
package com.edb.gridsql.metadata;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.SqlDropView;

/**
 *  
 */
public class SyncDropView implements IMetaDataUpdate {
    private static final XLogger logger = XLogger.getLogger(SyncDropView.class);

    private SqlDropView parent;

    private SysDatabase database;

    /**
     * 
     */
    public SyncDropView(SqlDropView parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.server.XDBSessionContext)
     */
    public void execute(XDBSessionContext client) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] { client });
        try {

            database = client.getSysDatabase();

            String commandStr = "delete from xsysviewscolumns where viewid = "
                    + database.getSysView(parent.getViewrName()).getViewid();
            MetaData.getMetaData().executeUpdate(commandStr);

            commandStr = "delete from xsysviewdeps where viewid = "
                    + database.getSysView(parent.getViewrName()).getViewid();
            MetaData.getMetaData().executeUpdate(commandStr);

            commandStr = "delete from xsysviews where viewid = "
                    + database.getSysView(parent.getViewrName()).getViewid();
            if (MetaData.getMetaData().executeUpdate(commandStr) != 1) {
                throw new XDBServerException(
                        "Failed to delete row from \"xsysviews\"");
            }
            // commandStr = "delete from xsysviewscolumns where viewid = " +
            // database.getSysView(parent.getViewrName()).getViewid();
            // MetaData.getMetaData().executeUpdate(commandStr) ;
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
        final String method = "refresh";
        logger.entering(method);
        try {

            database.dropSysView(parent.getViewrName());

        } finally {
            logger.exiting(method);
        }
    }

}
