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
import java.util.Vector;

import com.edb.gridsql.common.util.MetaDataUtil;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.DescribeTable;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.TableNameHandler;

public class SqlDescribeTable extends ObjectDepthFirst implements IXDBSql,
        IExecutable {

    private XDBSessionContext client;

    private String tableName;

    public SqlDescribeTable(XDBSessionContext client) {
        this.client = client;
    }

    @Override
    public Object visit(DescribeTable n, Object obj) {
        Object _ret = null;
        n.f0.accept(this, obj);
        TableNameHandler aTableNameHandler = new TableNameHandler(client);
        n.f1.accept(aTableNameHandler, obj);
        tableName = aTableNameHandler.getTableName();
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
    public LockSpecification getLockSpecs() {
        LockSpecification aLspec = new LockSpecification(new Vector(),
                new Vector());
        return aLspec;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection getNodeList() {
        return Collections.EMPTY_LIST;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        if (this.client.getSysDatabase().isTableExists(tableName)) {
            return ExecutionResult.createResultSetResult(
                    ExecutionResult.COMMAND_SHOW, MetaDataUtil
                            .getDescribeTable(tableName, client));
        } else if (this.client.getSysDatabase().isViewExists(tableName)) {
            return ExecutionResult.createResultSetResult(
                    ExecutionResult.COMMAND_SHOW, MetaDataUtil.getDescribeView(
                            tableName, client));
        } else {
            XDBServerException ex = new XDBServerException("Object "
                    + tableName + " has not been found in database "
                    + client.getDBName());
            throw ex;

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
