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
 * SqlAlterAddPrimary.java
 *
 *
 */

package com.edb.gridsql.parser;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.SyncAlterTableCheck;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.parser.core.syntaxtree.CheckDef;
import com.edb.gridsql.parser.core.syntaxtree.Constraint;
import com.edb.gridsql.parser.core.syntaxtree.SQLComplexExpression;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.QueryConditionHandler;
import com.edb.gridsql.parser.handler.QueryTreeTracker;

/**
 * Class for adding a PRIMARY KEY to a table
 *
 *
 */

public class SqlAlterAddCheck extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterAddCheck.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String constraintName = null;

    private String checkDef;

    private String[] commands;

    /**
     * @param table
     * @param client
     */
    public SqlAlterAddCheck(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * Grammar production:
     * f0 -> <CONSTRAINT_>
     * f1 -> Identifier(prn)
     */
    @Override
    public Object visit(Constraint n, Object argu) {
        constraintName = (String) n.f1.accept(new IdentifierHandler(), argu);
        return null;
    }

    @Override
    public Object visit(SQLComplexExpression n, Object argu) {
        QueryConditionHandler qch = new QueryConditionHandler(new Command(
                Command.CREATE, this, new QueryTreeTracker(), client));
        n.accept(qch, argu);
        checkDef = qch.aRootCondition.getCondString();
        return null;
    }

    /**
     * Grammar production: f0 -> <CHECK_> f1 -> "(" f2 ->
     * skip_to_matching_brace(prn) f3 -> ")"
     */

    @Override
    public Object visit(CheckDef n, Object argu) {
        checkDef = n.f2.str;
        return null;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public String getCheckDef() {
        return checkDef;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    public boolean isPrepared() {
        return commands != null;
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

            parent.getTable().ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            if (constraintName == null) {
                constraintName = "CHK_" + parent.getTableName().toUpperCase();
            }
            String sql = "ADD CONSTRAINT " + IdentifierHandler.quote(constraintName) + " CHECK ("
                    + checkDef + ")";
            parent.addCommonCommand(sql);

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
            if (commands != null && commands.length != 0) {
                engine.executeDDLOnMultipleNodes(commands,
                        parent.getNodeList(), new SyncAlterTableCheck(this),
                        client);
            }
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
