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
import java.util.Enumeration;

import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.MultinodeExecutor;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.Cluster;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.syntaxtree.NodeOptional;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

public class SqlCluster extends ObjectDepthFirst implements IXDBSql,
        IExecutable, IPreparable {
    private XDBSessionContext client;

    private String aTableName;

    private String aIndexName;

    private String clusterStatement;

    /**
     *
     */
    public SqlCluster(XDBSessionContext client) {
        this.client = client;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return client.getSysDatabase().getDBNodeList();
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
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        SysTable table = null;
        Collection<DBNode> nodeList = getNodeList();
        MultinodeExecutor aMultinodeExecutor = client.getMultinodeExecutor(nodeList);
        if (aTableName != null) {
            table = MetaData.getMetaData().getSysDatabase(client.getDBName())
                    .getSysTable(aTableName.trim());
            clusterTable(table, table.getNodeList(), aMultinodeExecutor,
                    clusterStatement, aIndexName);
        } else {
            Enumeration allTables = MetaData.getMetaData().getSysDatabase(
                    client.getDBName()).getAllTables();
            while (allTables.hasMoreElements()) {
                SysTable tab = (SysTable) allTables.nextElement();

                if (client.getCurrentUser().getUserClass() == SysLogin.USER_CLASS_DBA
                        || client.getCurrentUser() == tab.getOwner()) {
                    if (tab.getClusteridx() != null) {
                        String sql = "CLUSTER " + IdentifierHandler.quote(tab.getClusteridx())
                            + " ON " + IdentifierHandler.quote(tab.getTableName());
                        clusterTable(tab, tab.getNodeList(),
                                aMultinodeExecutor, sql, tab.getClusteridx());
                    }
                }
            }
        }
        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_CLUSTER);
    }

    public void clusterTable(SysTable table, Collection<DBNode> nodeList,
            MultinodeExecutor aMultinodeExecutor, String sqlStatement,
            String clusterIdx) {
        aMultinodeExecutor.executeCommand(sqlStatement, nodeList, true);

        String sql = "update xsystables set clusteridx = '" + clusterIdx
                + "' where tableid = " + table.getTableId();
        table.setClusteridx(clusterIdx);
        MetaData.getMetaData().executeUpdate(sql);

    }

    public boolean isPrepared() {
        return clusterStatement != null;
    }

    public void prepare() throws Exception {
        SysTable table = null;
        if (aIndexName == null && aTableName == null) {
            return;
        }
        if (aTableName != null) {
            table = MetaData.getMetaData().getSysDatabase(client.getDBName())
                    .getSysTable(aTableName.trim());

            if (aIndexName == null) {
                if (table.getClusteridx() == null) {
                    throw new XDBServerException(
                            "there is no previously clustered index for table \""
                                    + aTableName + "\"");
                }
                aIndexName = table.getClusteridx().trim();
            }
            clusterStatement = "CLUSTER " + IdentifierHandler.quote(aIndexName)
                + " ON " + IdentifierHandler.quote(aTableName);
        }
    }

    /**
     * Grammar production:
     * f0 -> <CLUSTER_>
     * f1 -> [ Identifier(prn) [ <ON_> Identifier(prn) ] ]
     */
    @Override
    public Object visit(Cluster n, Object argu) {
        Object _ret = null;
        if (n.f1.present()) {
            IdentifierHandler ih = new IdentifierHandler();
            NodeSequence ns0 = (NodeSequence) n.f1.node;
            NodeOptional no = (NodeOptional) ns0.elementAt(1);
            NodeSequence ns1 = (NodeSequence) no.node;
            if (ns1 == null) {
                // cluster tablename;
                aTableName = (String) ns0.elementAt(0).accept(ih, argu);
            } else {
                // cluster indexname on tablename;
                aIndexName = (String) ns0.elementAt(0).accept(ih, argu);
                aTableName = (String) ns1.elementAt(1).accept(ih, argu);
        }
        }
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
