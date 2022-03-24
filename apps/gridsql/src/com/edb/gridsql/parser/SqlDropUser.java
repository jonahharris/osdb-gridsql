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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncDropUser;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.SysUser;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.DropUser;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

public class SqlDropUser extends ObjectDepthFirst implements IXDBSql,
IPreparable {
    private static final XLogger logger = XLogger.getLogger(SqlDropUser.class);

    private XDBSessionContext client;

    private String iUserName = null;

    private SysLogin login;

    public SqlDropUser(XDBSessionContext client) {
        this.client = client;
    }

    // ******************************
    // BEGIN GRAMMAR
    // ******************************

    /**
     * Grammar production:
     * f0 -> <DROP_>
     * f1 -> <USER_>
     * f2 -> Identifier(prn)
     */
    @Override
    public Object visit(DropUser n, Object argu) {
        Object _ret = null;

        iUserName = (String) n.f2.accept(new IdentifierHandler(), argu);

        return _ret;
    }

    /**
     * This will return the cost of executing this statement in time , milli
     * seconds
     */

    public long getCost() {
        return ILockCost.LOW_COST;
    }

    /**
     * This return the Lock Specification for the system
     *
     * @param theMData
     * @return
     */
    public LockSpecification<SysTable> getLockSpecs() {
        Collection<SysTable> empty = Collections.emptyList();
        LockSpecification<SysTable> aLspec = new LockSpecification<SysTable>(
                empty, empty);
        return aLspec;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        Collection<DBNode> empty = Collections.emptyList();
        return new ArrayList<DBNode>(empty);
    }

    /**
     * @return Returns the iUserName.
     */
    public String getUserName() {
        return iUserName;
    }

    /**
     * @return Returns the user.
     */
    public SysLogin getLogin() {
        return login;
    }

    public boolean isPrepared() {
        return login != null;
    }

    public void prepare() throws Exception {
        if (!isPrepared()) {
            if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA) {
                XDBSecurityException ex = new XDBSecurityException("Only "
                        + SysLogin.USER_CLASS_DBA_STR + " can drop users");
                logger.throwing(ex);
                throw ex;
            }
            login = MetaData.getMetaData().getSysLogin(iUserName);
            // This method will throw an exception if user owns any objects
            login.canSetUserClass(SysLogin.USER_CLASS_STANDARD_STR);
            // Check if user has any permissions
            for (SysDatabase database : MetaData.getMetaData().getSysDatabases()) {
                SysUser dbUser = database.getSysUser(iUserName);
                String granted = dbUser.getGrantedStr();
                if (granted != null) {
                    XDBSecurityException ex = new XDBSecurityException("User "
                            + iUserName + " has permissions on some objects: "
                            + granted);
                    logger.throwing(ex);
                    throw ex;
                }
            }
        }
    }

    public ExecutionResult execute(Engine engine) throws Exception {
        if (!isPrepared()) {
            prepare();
        }
        SyncDropUser sync = new SyncDropUser(this);
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
        return ExecutionResult
        .createSuccessResult(ExecutionResult.COMMAND_DROP_USER);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    public boolean needCoordinatorConnection() {
        return false;
    }
}
