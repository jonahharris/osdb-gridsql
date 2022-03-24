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
 * SqlBulkInsert.java
 * 
 *  
 */
package com.edb.gridsql.parser;

import java.util.Collection;
import java.util.Collections;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.XMessage;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.LockSpecification;

/**
 *  
 */
public class SqlBulkInsertNext implements IXDBSql, IExecutable {
    private static final XLogger logger = XLogger
            .getLogger(SqlBulkInsertNext.class);

    private XDBSessionContext client;

    private SysTable table;

    private boolean serial = false;

    private int range = 10000;

    /**
     * 
     */
    public SqlBulkInsertNext(String cmd, XDBSessionContext client) {
        this.client = client;
        int pos = cmd.indexOf(XMessage.ARGS_DELIMITER);
        String tableName = cmd.substring(0, pos);
        pos += XMessage.ARGS_DELIMITER.length();
        if (cmd.startsWith("SERIAL" + XMessage.ARGS_DELIMITER, pos)) {
            serial = true;
            pos += 6 + XMessage.ARGS_DELIMITER.length();
        }
        try {
            range = Integer.parseInt(cmd.substring(pos));
        } catch (NumberFormatException nfe) {
            // ignore
        }
        table = client.getSysDatabase().getSysTable(tableName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        final String method = "getNodeList";
        logger.entering(method, new Object[] {});
        try {

            return Collections.emptyList();

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    public long getCost() {
        final String method = "getCost";
        logger.entering(method, new Object[] {});
        try {

            return LOW_COST;

        } finally {
            logger.exiting(method);
        }
    }

    private LockSpecification<SysTable> lockSpecs = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs()
     */
    public LockSpecification<SysTable> getLockSpecs() {
        final String method = "getLockSpecs";
        logger.entering(method, new Object[] {});
        try {

            if (lockSpecs == null) {
                lockSpecs = new LockSpecification<SysTable>();
            }
            return lockSpecs;

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

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.engine.IExecutable#execute(com.edb.gridsql.engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        long first;
        if (serial) {
            first = table.getSerialHandler().allocateRange(range, client);
        } else {
            first = table.getRowIDHandler().allocateRange(range, client);
        }
        return ExecutionResult.createGeneratorRangeResult(
                ExecutionResult.COMMAND_BULK_INSERT, first);
    }
}
