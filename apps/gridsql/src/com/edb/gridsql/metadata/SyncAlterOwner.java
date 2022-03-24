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
 * SyncAlterOwner.java
 * 
 *  
 */
package com.edb.gridsql.metadata;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.SqlAlterOwner;

/**
 *  
 */
public class SyncAlterOwner implements IMetaDataUpdate {
    private static final XLogger logger = XLogger
            .getLogger(SyncAlterOwner.class);

    private SqlAlterOwner parent;

    /**
     * 
     */
    public SyncAlterOwner(SqlAlterOwner parent) {
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

            SysUser newOwner = parent.getUser();
            String command = "update xsystables set owner = "
                    + (newOwner == null ? "null" : "" + newOwner.getUserID())
                    + " where tableid = "
                    + parent.getParent().getTable().getTableId();
            if (MetaData.getMetaData().executeUpdate(command) != 1) {
                XDBServerException ex = new XDBServerException(
                        "Failed to update row in \"xsystables\"");
                logger.throwing(ex);
                throw ex;
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
        logger.entering(method, new Object[] {});
        try {

            parent.getParent().getTable().setOwner(parent.getUser());

        } finally {
            logger.exiting(method);
        }
    }

}
