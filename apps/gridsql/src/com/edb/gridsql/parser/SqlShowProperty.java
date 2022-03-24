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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.core.syntaxtree.ShowProperty;

/**
 * 
 * 
 */
public class SqlShowProperty extends ObjectDepthFirst implements IXDBSql,
        IExecutable {
    
    private String propertyToShow = null;
    private XDBSessionContext client;
    /**
     * 
     */
    public SqlShowProperty(XDBSessionContext client) {
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
        Collection<SysTable> empty = Collections.emptySet();
        return new LockSpecification<SysTable>(empty, empty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    
    @Override
    public Object visit(ShowProperty n, Object argu) {
        propertyToShow = n.f1.choice.toString();
        return null;
    }
    
    public ExecutionResult execute(Engine engine) throws Exception {
        
        String query = "SHOW " + propertyToShow;
        
        try
        {
            Connection oConn = client.getAndSetCoordinatorConnection();
            try {
                Statement stmt = oConn.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                return ExecutionResult.createResultSetResult(ExecutionResult.COMMAND_SHOW, rs);
            } catch (SQLException se) {
                try { 
                    oConn.rollback();
                } catch (SQLException ignore) {
                }
                throw se;
            }
        }
        catch (SQLException se)
        {            
            return ExecutionResult.createErrorResult(se);
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

