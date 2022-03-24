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
package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutableRequest;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.SysUser;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.Kill;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

public class SqlKill extends ObjectDepthFirst implements IXDBSql, IExecutable,
        IPreparable {
    private XDBSessionContext client;

    private int requestID;

    private XDBSessionContext targetSession;

    private ExecutableRequest targetRequest;

    /**
     * 
     */
    public SqlKill(XDBSessionContext client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    public long getCost() {
        return ILockCost.LOW_COST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs()
     */
    public LockSpecification<SysTable> getLockSpecs() {
        Collection<SysTable> empty = Collections.emptyList();
        return new LockSpecification<SysTable>(empty, empty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql..Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        if (targetSession != null) {
            if (targetRequest == null) {
                targetSession.kill();
            } else {
                targetSession.kill(targetRequest);
            }
        }
        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_KILL);
    }

    private boolean prepared;

    public boolean isPrepared() {
        return prepared;
    }

    public void prepare() throws Exception {
        prepared = true;
        SysUser user = client.getCurrentUser();
        XDBSessionContext targetSession = null;
        if (requestID > 0) {
            Map<ExecutableRequest, XDBSessionContext> execRequests = client
                    .getRequests();
            for (Map.Entry<ExecutableRequest, XDBSessionContext> entry : execRequests
                    .entrySet()) {
                if (entry.getKey().getRequestID() == requestID) {
                    targetSession = entry.getValue();
                    targetRequest = entry.getKey();
                    break;
                }
            }
        }
        if (targetSession != null) {
            if (user.getUserClass() == SysLogin.USER_CLASS_DBA
                    || targetSession.getCurrentUser() == user) {
                this.targetSession = targetSession;
            } else {
                throw new XDBSecurityException("Permission denied");
            }
        }
    }

    /**
     * Grammar production: f0 -> <KILL_> f1 -> <INT_LITERAL>
     */
    @Override
    public Object visit(Kill n, Object argu) {
        try {
            requestID = Integer.parseInt(n.f1.tokenImage);
        } catch (NumberFormatException nfe) {
            requestID = -1;
        }
        return null;
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
