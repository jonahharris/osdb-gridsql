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
import com.edb.gridsql.parser.SqlDropUser;

/**
 *  
 */
public class SyncDropUser implements IMetaDataUpdate {
    private static final XLogger logger = XLogger.getLogger(SyncDropUser.class);

    private SqlDropUser parent;

    /**
     * 
     */
    public SyncDropUser(SqlDropUser parent) {
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

            String commandStr = "delete from xsysusers where userid = "
                    + parent.getLogin().getLoginID();
            if (MetaData.getMetaData().executeUpdate(commandStr) != 1) {
                throw new XDBServerException(
                        "Failed to delete row from \"xsysusers\"");
            }

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

            SysLogin login = parent.getLogin();
            MetaData.getMetaData().removeLogin(login);
            login.invalidate();

        } finally {
            logger.exiting(method);
        }
    }

}
