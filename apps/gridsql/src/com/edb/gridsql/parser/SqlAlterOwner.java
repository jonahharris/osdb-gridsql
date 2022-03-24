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
package com.edb.gridsql.parser;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncAlterOwner;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysUser;
import com.edb.gridsql.parser.core.syntaxtree.OwnerDef;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 */
public class SqlAlterOwner extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterOwner.class);

    private XDBSessionContext client;

    private SysDatabase database;

    private SqlAlterTable parent;

    private SysUser user = null;

    /**
     * @param table
     * @param client
     */
    public SqlAlterOwner(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        database = client.getSysDatabase();
        this.parent = parent;
    }

    /**
     * Grammar production:
     * f0 -> <OWNER_TO_>
     * f1 -> ( <PUBLIC_> | Identifier(prn) )
     */
    @Override
    public Object visit(OwnerDef n, Object argu) {
        Object _ret = null;
        if (n.f1.which == 1) {
            user = database.getSysUser((String) n.f1.accept(new IdentifierHandler(), argu));
        }
        return _ret;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    /**
     * @return Returns the columnName.
     */
    public SysUser getUser() {
        return user;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    private boolean prepared = false;

    public boolean isPrepared() {
        return prepared;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#prepare()
     */
    public void prepare() throws Exception {
        final String method = "prepare";
        logger.entering(method, new Object[] {});
        try {

            if (!isPrepared()) {
                if (getParent().getTable().getParentTable() != null) {
                    throw new XDBServerException(
                            "Owner of child table can not be changed");
                }
                if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA
                        && client.getCurrentUser() != parent.getTable()
                                .getOwner()) {
                    XDBSecurityException ex = new XDBSecurityException("Only "
                            + SysLogin.USER_CLASS_DBA_STR
                            + " or owner can change table ownership");
                    logger.throwing(ex);
                    throw ex;
                }
                if (user != null
                        && user.getUserClass() == SysLogin.USER_CLASS_STANDARD) {
                    XDBSecurityException ex = new XDBSecurityException(
                            SysLogin.USER_CLASS_STANDARD_STR
                                    + " user can not own table");
                    logger.throwing(ex);
                    throw ex;
                }
                prepared = true;
            }

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            if (!isPrepared()) {
                prepare();
            }
            SyncAlterOwner sync = new SyncAlterOwner(this);
            MetaData meta = MetaData.getMetaData();
            meta.beginTransaction();
            try {
                sync.execute(client);
                meta.commitTransaction(sync);
            } catch (Exception e) {
                logger.catching(e);
                meta.rollbackTransaction();
                throw e;
            }
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
