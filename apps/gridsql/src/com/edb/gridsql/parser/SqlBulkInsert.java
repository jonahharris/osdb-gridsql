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

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.XMessage;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.partitions.PartitionMap;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.metadata.scheduler.LockType;

/**
 *  
 */
public class SqlBulkInsert implements IXDBSql, IExecutable {
    private static final XLogger logger = XLogger
            .getLogger(SqlBulkInsert.class);

    private SysTable table;

    private XDBSessionContext client;

    private String address;

    /**
     * 
     */
    public SqlBulkInsert(String cmd, XDBSessionContext client) {
        this.client = client;
        String tableName;
        int pos = cmd.indexOf(XMessage.ARGS_DELIMITER);
        if (pos < 0) {
            tableName = cmd;
        } else {
            tableName = cmd.substring(0, pos);
            address = cmd.substring(pos + XMessage.ARGS_DELIMITER.length());
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

            return table.getNodeList();

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

            return HIGH_COST;

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
                lockSpecs.add(table, LockType.get(
                        LockType.LOCK_SHARE_WRITE_INT, false));
            }
            return lockSpecs;

        } finally {
            logger.exiting(method);
        }
    }

    public SysTable getSysTable() {
        return table;
    }

    public void startLoaders(Engine engine) throws Exception {
        if (address != null) {
            engine.startLoaders(table.getTableName(), address, getNodeList(),
                    client);
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
        if (!client.isInTransaction()) {
            engine.beginTransaction(client, getNodeList());
        }
        startLoaders(engine);
        PartitionMap map = table.getPartitionMap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(map);
                oos.flush();
                return ExecutionResult
                        .createSerializedObjectResult(
                                ExecutionResult.COMMAND_BULK_INSERT, baos
                                        .toByteArray());
            } finally {
                oos.close();
            }
        } finally {
            baos.close();
        }
    }
}
