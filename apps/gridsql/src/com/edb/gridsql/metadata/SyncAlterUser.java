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
 * SyncAlterUser.java
 * 
 *  
 */
package com.edb.gridsql.metadata;

import java.sql.PreparedStatement;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.SqlAlterUser;

/**
 *  
 */
public class SyncAlterUser implements IMetaDataUpdate {
    private static final XLogger logger = XLogger
            .getLogger(SyncAlterUser.class);

    private SqlAlterUser parent;

    private String password;

    private String userClass;

    /**
     * 
     */
    public SyncAlterUser(SqlAlterUser parent) {
        this.parent = parent;
        password = parent.getPassword();
        userClass = parent.getUserClass();
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

            StringBuffer command = new StringBuffer("update xsysusers set ");
            if (password != null) {
                command.append("userpwd = ?");
                if (userClass != null) {
                    command.append(", usertype = ?");
                }
            } else {
                command.append("usertype = ?");
            }
            command.append(" where userid = ").append(
                    parent.getUser().getUserID());
            PreparedStatement ps = MetaData.getMetaData().prepareStatement(
                    command.toString());
            int current = 1;
            if (password != null) {
                ps.setString(current++, SysLogin.encryptPassword(password));
            }
            if (userClass != null) {
                ps.setString(current++, userClass);
            }
            if (ps.executeUpdate() != 1) {
                throw new XDBServerException(
                        "Failed to update row in \"xsysusers\"");
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

            SysUser user = parent.getUser();
            if (password != null) {
                user.getLogin().setPassword(password, true);
            }
            if (userClass != null) {
                user.getLogin().setUserClass(userClass);
            }
        } finally {
            logger.exiting(method);
        }
    }

}
