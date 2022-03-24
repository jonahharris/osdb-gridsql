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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.MultinodeExecutor;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.parser.core.syntaxtree.IsolationLevel;
import com.edb.gridsql.parser.core.syntaxtree.Node;
import com.edb.gridsql.parser.core.syntaxtree.NodeChoice;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.syntaxtree.SetProperty;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
/**
 *
 *
 */
public class SqlSetProperty extends ObjectDepthFirst implements IXDBSql,
        IExecutable {

    private static final String TRANSACTION_ISOLATION = "TRANSACTION ISOLATION LEVEL";

    private String propertyToSet = null;
    private String propertyValue = null;
    private XDBSessionContext client;
    private List<DBNode> nodeList;
    private int desiredLevel = Connection.TRANSACTION_NONE;

    /**
     *
     */
    public SqlSetProperty(XDBSessionContext client) {
        this.client = client;

        nodeList = new ArrayList<DBNode>(client.getSysDatabase()
                    .getDBNodeList());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection<DBNode> getNodeList() {
        return nodeList;
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

    /**
     * Grammar production:
     * f0 -> <SET_>
     * f1 -> ( Identifier(prn) [ <TO_> | "=" ] ( <STRING_LITERAL> | Identifier(prn) | <ON_> | <TRUE_> | <FALSE_> ) | <TRANSACTION_> <ISOLATION_LEVEL_> IsolationLevel(prn) )
     */
    @Override
    public Object visit(SetProperty n, Object argu) {
        if (n.f1.which == 0) {
            NodeSequence ns = (NodeSequence) n.f1.choice;
            IdentifierHandler ih = new IdentifierHandler();
            propertyToSet = (String) ((Node) ns.nodes.get(0)).accept(ih, argu);
            NodeChoice nc = (NodeChoice) ns.nodes.get(2);
            switch (nc.which) {
                //STRING_LITERAL
                case 0:
                //ON
                case 2:
                //TRUE
                case 3:
                //FALSE
                case 4:
                    propertyValue = nc.choice.toString();
                    break;
                //IDENTIFIER
                case 1:
                    propertyValue = (String) nc.accept(ih, argu);
                    break;
            }
        } else {
            propertyToSet = TRANSACTION_ISOLATION;
            n.f1.choice.accept(this, argu);
            nodeList = Collections.emptyList();
        }
        return null;
    }

    /**
     * Grammar production:
     * f0 -> ( <SERIALIZABLE_> | <REPEATABLE_READ_> | <READ_COMMITTED_> | <READ_UNCOMMITTED_> )
     */
    @Override
    public Object visit(IsolationLevel n, Object argu) {
        switch (n.f0.which) {
        case 0:
            desiredLevel = Connection.TRANSACTION_SERIALIZABLE;
            break;
        case 1:
            desiredLevel = Connection.TRANSACTION_REPEATABLE_READ;
            break;
        case 2:
            desiredLevel = Connection.TRANSACTION_READ_COMMITTED;
            break;
        case 3:
            desiredLevel = Connection.TRANSACTION_READ_UNCOMMITTED;
            break;
        }
        return null;
    }

    /**
     * Pass on the SET command to the underlying connections,
     * and persist them for this session.
     *
     * @param engine Execution engine
     * @return success code
     */
    public ExecutionResult execute(Engine engine) throws Exception {

        /** TODO: parse out some "special" settings that need
         * to be handled at the GridSQL level, instead of
         * just passing on. */
        if (propertyToSet == TRANSACTION_ISOLATION) {
            if (desiredLevel != Connection.TRANSACTION_NONE) {
                if (client.isInTransaction()) {
                    engine.commitTransaction(client, getNodeList());
                }

                client.setTransactionIsolation(desiredLevel);
            }
            return ExecutionResult.createSuccessResult(ExecutionResult.COMMAND_SHOW);
        }

        // JDBC requires unicode
        if (propertyToSet.equalsIgnoreCase("client_encoding")) {
            if (!Props.XDB_CLIENT_ENCODING_IGNORE && !"UNICODE".equalsIgnoreCase(propertyValue)) {
                throw new SQLException ("Setting client_encoding is not allowed; must use UNICODE");
            }
            return ExecutionResult.createSuccessResult(ExecutionResult.COMMAND_SHOW);
        }

        /* We need to flag to persist these connections for our
         session so that the property can be set on all of the
         underlying connections. */
        client.setUsedSet();

        try {
            String sqlStatement = "SET " + propertyToSet + " TO ";

            // Do not double-add quotes
            if (propertyValue.startsWith("'") || propertyValue.startsWith("\"")) {
                sqlStatement += propertyValue;
            } else {
                sqlStatement += "'" + propertyValue + "'";
            }

            MultinodeExecutor aMultinodeExecutor = client
                    .getMultinodeExecutor(getNodeList());

            aMultinodeExecutor.executeCommand(sqlStatement, getNodeList(), true);

            // Execute it on the designated coordinator connection, too
            Connection oConn = client.getAndSetCoordinatorConnection();
            Statement stmt = oConn.createStatement();
            stmt.execute(sqlStatement);
        } catch (Exception e) {
            /* We just ignore errors here- there may be some issues
             * with some of these settings and there are limitations
             * being in a clustered environment. We still try though,
             * since enabling here and ignoring errors will
             * help with driver/utility compatibility.
             */
        }

        return ExecutionResult
                .createSuccessResult(ExecutionResult.COMMAND_SHOW);
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
