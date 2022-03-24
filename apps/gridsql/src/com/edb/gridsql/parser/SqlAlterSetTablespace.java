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

import java.util.HashMap;
import java.util.Map;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBSecurityException;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SyncAlterTableSetTablespace;
import com.edb.gridsql.metadata.SysLogin;
import com.edb.gridsql.metadata.SysTablespace;
import com.edb.gridsql.parser.core.syntaxtree.SetTablespace;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 *
 *
 */
public class SqlAlterSetTablespace extends ObjectDepthFirst implements
        IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterSetTablespace.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String tablespaceName = null;

    private SysTablespace tablespace = null;

    private Map<DBNode,String> commands = null;

    /**
     *
     */
    public SqlAlterSetTablespace(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * Grammar production:
     * f0 -> <SET_>
     * f1 -> <TABLESPACE_>
     * f2 -> Identifier(prn)
     */
    @Override
    public Object visit(SetTablespace n, Object argu) {
        tablespaceName = (String) n.f2.accept(new IdentifierHandler(), argu);
        return null;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    public SysTablespace getTablespace() {
        return tablespace;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return tablespace != null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#prepare()
     */
    public void prepare() throws Exception {
        final String method = "prepare";
        logger.entering(method, new Object[] {});
        try {

            if (!isPrepared()) {
                if (client.getCurrentUser().getUserClass() != SysLogin.USER_CLASS_DBA) {
                    XDBSecurityException ex = new XDBSecurityException("Only "
                            + SysLogin.USER_CLASS_DBA_STR
                            + " can change table workspace");
                    logger.throwing(ex);
                    throw ex;
                }
                SysTablespace newTablespace = MetaData.getMetaData()
                        .getTablespace(tablespaceName);
                commands = new HashMap<DBNode,String>();
                for (DBNode dbNode : parent.getTable().getNodeList()) {
                    if (!newTablespace.getLocations().containsKey(dbNode.getNodeId())) {
                        throw new XDBServerException("Tablespace "
                                + IdentifierHandler.quote(tablespaceName)
                                + " does not exist on Node "
                                + dbNode.getNodeId());
                    }
                    String command = "ALTER TABLE "
                        + IdentifierHandler.quote(parent.getTableName())
                        + " SET TABLESPACE "
                        + IdentifierHandler.quote(tablespaceName + "_" + dbNode.getNodeId());
                    commands.put(dbNode, command);
                }
                tablespace = newTablespace;
            }

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            engine.executeDDLOnMultipleNodes(commands,
                    new SyncAlterTableSetTablespace(this), client);
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
