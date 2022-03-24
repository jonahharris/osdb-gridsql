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

import java.sql.Connection;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Vector;

import com.edb.gridsql.common.ColumnMetaData;
import com.edb.gridsql.common.ResultSetImpl;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.datatypes.VarcharType;
import com.edb.gridsql.engine.datatypes.XData;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;

/**
 * 
 * 
 */
public class SqlShowTranIsolation extends ObjectDepthFirst implements IXDBSql,
        IExecutable {

    private XDBSessionContext client;

    /**
     * 
     */
    public SqlShowTranIsolation(XDBSessionContext client) {
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

    public ExecutionResult execute(Engine engine) throws Exception {
        String tranIsolation;
        switch (client.getTransactionIsolation()) {
        case Connection.TRANSACTION_READ_UNCOMMITTED:
            tranIsolation = "read uncommitted";
            break;
        case Connection.TRANSACTION_READ_COMMITTED:
            tranIsolation = "read committed";
            break;
        case Connection.TRANSACTION_REPEATABLE_READ:
            tranIsolation = "repeatable read";
            break;
        case Connection.TRANSACTION_SERIALIZABLE:
            tranIsolation = "serializable";
            break;
        default:
            tranIsolation = "unknown";
        }
        ColumnMetaData[] headers = new ColumnMetaData[] {

        new ColumnMetaData("TRANSACTION_ISOLATION", "TRANSACTION_ISOLATION",
                25, Types.VARCHAR, 0, 0, "", (short) 0, false)

        };
        Vector<XData[]> rows = new Vector<XData[]>();
        rows.add(new XData[] { new VarcharType(tranIsolation) });
        return ExecutionResult.createResultSetResult(
                ExecutionResult.COMMAND_SHOW, new ResultSetImpl(headers, rows));
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
