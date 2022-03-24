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

import java.util.Collection;
import java.util.Collections;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncCreateUser;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.CreateUser;
import com.edb.gridsql.parser.core.syntaxtree.NodeChoice;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

public class SqlCreateUser extends ObjectDepthFirst implements IXDBSql,
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlCreateUser.class);

    private XDBSessionContext client;

    private SysDatabase database;

    private String iUserName = null;

    private String iPassword = null;

    private int iUserClass = SysLogin.USER_CLASS_RESOURCE;

    public SqlCreateUser(XDBSessionContext client) {
        this.client = client;
        this.database = client.getSysDatabase();
    }

    // Used to create defaul DB user
    public SqlCreateUser(SysDatabase database, String userName,
            String password, int userClass) {
        this.database = database;
        iUserName = userName;
        iPassword = password;
        iUserClass = userClass;
        // Skip validation
        prepared = true;
    }

    // ******************************
    // BEGIN GRAMMAR
    // ******************************

    /**
     * Grammar production:
     * f0 -> <CREATE_>
     * f1 -> <USER_>
     * f2 -> Identifier(prn)
     * f3 -> <PASSWORD_>
     * f4 -> Identifier(prn)
     * f5 -> [ <DBA_> | <RESOURCE_> | <STANDARD_> ]
     */
    @Override
    public Object visit(CreateUser n, Object argu) {
        Object _ret = null;
        IdentifierHandler ih = new IdentifierHandler();
        iUserName = (String) n.f2.accept(ih, argu);
        iPassword = (String) n.f4.accept(ih, argu);
        if (n.f5.present()) {
            switch (((NodeChoice) n.f5.node).which) {
            case 0:
                iUserClass = SysLogin.USER_CLASS_DBA;
                break;
            case 1:
                iUserClass = SysLogin.USER_CLASS_RESOURCE;
                break;
            case 2:
                iUserClass = SysLogin.USER_CLASS_STANDARD;
                break;
            }
        }
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
        return new LockSpecification<SysTable>(empty, empty);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return Collections.emptyList();
    }

    /**
     * @return Returns the iPassword.
     */
    public String getPassword() {
        return iPassword;
    }

    /**
     * @return Returns the iUserClass.
     */
    public int getUserClass() {
        return iUserClass;
    }

    /**
     * @return Returns the iUserClass.
     */
    public String getUserClassStr() {
        switch (iUserClass) {
        case SysLogin.USER_CLASS_DBA:
            return SysLogin.USER_CLASS_DBA_STR;
        case SysLogin.USER_CLASS_RESOURCE:
            return SysLogin.USER_CLASS_RESOURCE_STR;
        case SysLogin.USER_CLASS_STANDARD:
            return SysLogin.USER_CLASS_STANDARD_STR;
        default:
            return SysLogin.USER_CLASS_RESOURCE_STR;
        }
    }

    /**
     * @return Returns the iUserName.
     */
    public String getUserName() {
        return iUserName;
    }

    private boolean prepared = false;

    public boolean isPrepared() {
        return prepared;
    }

    public void prepare() throws Exception {
        if (!prepared) {
            if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA) {
                XDBSecurityException ex = new XDBSecurityException("Only "
                        + SysLogin.USER_CLASS_DBA_STR + " can create users");
                logger.throwing(ex);
                throw ex;
            }
            if (client.getSysDatabase().hasSysUser(iUserName)) {
                XDBServerException ex = new XDBServerException("User "
                        + iUserName + " already exists");
                logger.throwing(ex);
                throw ex;
            }
        }
        prepared = true;
    }

    public ExecutionResult execute(Engine engine) throws Exception {

        final String method = "execute";
        logger.entering(method, new Object[] { engine });
        try {

            if (!isPrepared()) {
                prepare();
            }
            SyncCreateUser sync = new SyncCreateUser(this);
            MetaData meta = MetaData.getMetaData();
            meta.beginTransaction();
            try {
                sync.execute(database);
                meta.commitTransaction(sync);
            } catch (Exception e) {
                logger.catching(e);
                meta.rollbackTransaction();
                throw e;
            }
            return ExecutionResult
                    .createSuccessResult(ExecutionResult.COMMAND_CREATE_USER);

        } finally {
            logger.exiting(method);
        }
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
