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

import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.MultinodeExecutor;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.Truncate;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.TableNameHandler;

public class SqlTruncate extends ObjectDepthFirst implements IXDBSql,
        IExecutable, IPreparable {
    private XDBSessionContext client;

    private String aTableName;

    private String truncateStatement;

    private SysTable table;

    /**
     *
     */
    public SqlTruncate(XDBSessionContext client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return table.getNodeList();
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
        return new LockSpecification<SysTable>(empty, Collections.singletonList(table));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        if (!isPrepared()) {
            prepare();
        }
        MultinodeExecutor aMultinodeExecutor = client.getMultinodeExecutor(table.getNodeList());
        aMultinodeExecutor.executeCommand(truncateStatement, table.getNodeList(), true);
        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_TRUNCATE);
    }

    public boolean isPrepared() {
        return truncateStatement != null;
    }

    public void prepare() throws Exception {
        table = client.getSysDatabase().getSysTable(aTableName);
        table.ensurePermission(client.getCurrentUser(), SysPermission.PRIVILEGE_DELETE);
        // TRUNCATE cannot be used if there are foreign-key references
        // to the table from other tables. Checking validity in such
        // cases would require table scans, and the whole point is not
        // to do one.
        if (!table.getSysReferences().isEmpty()) {
            throw new XDBServerException(
                    "cannot truncate a table referenced in a foreign key constraint");
        }
        truncateStatement = "TRUNCATE TABLE " + IdentifierHandler.quote(table.getTableName());
    }

    /**
     * Grammar production:
     * f0 -> <TRUNCATE_>
     * f1 -> [ <TABLE_> ]
     * f2 -> TableName(prn)
     */
    @Override
    public Object visit(Truncate n, Object argu) {
        Object _ret = null;
        TableNameHandler tnh = new TableNameHandler(client);
        n.f2.accept(tnh, argu);
        aTableName = tnh.getTableName();
        return _ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    public boolean needCoordinatorConnection() {
        return true;
    }

}
