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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.edb.gridsql.common.util.ParseCmdLine;
import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IPreparable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SyncAlterTablePrimaryKey;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysIndex;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.core.syntaxtree.ColumnNameList;
import com.edb.gridsql.parser.core.syntaxtree.Constraint;
import com.edb.gridsql.parser.core.syntaxtree.PrimaryKeyDef;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.ColumnNameListHandler;
import com.edb.gridsql.parser.handler.IdentifierHandler;

/**
 * Class for adding a PRIMARY KEY to a table
 *
 *
 */

public class SqlAlterAddPrimary extends ObjectDepthFirst implements IPreparable {
    private static final XLogger logger = XLogger
            .getLogger(SqlAlterAddPrimary.class);

    private XDBSessionContext client;

    private SqlAlterTable parent;

    private String constraintName = null;

    private List<String> columnNameList = null;

    private int indexIdToUse = -1; // id of already existing Unique index to

    // use for the primary key

    private String[] commands;

    /**
     * @param table
     * @param client
     */
    public SqlAlterAddPrimary(SqlAlterTable parent, XDBSessionContext client) {
        this.client = client;
        this.parent = parent;
    }

    /**
     * Grammar production: f0 -> <CONSTRAINT_> f1 ->
     * IdentifierAndUnreservedWords(prn)
     */
    @Override
    public Object visit(Constraint n, Object argu) {
        constraintName = (String) n.f1.accept(this, argu);
        return null;
    }

    @Override
    public Object visit(ColumnNameList n, Object argu) {
        ColumnNameListHandler aNameListHandler = new ColumnNameListHandler();
        n.accept(aNameListHandler, null);
        columnNameList = aNameListHandler.getColumnNameList();
        return null;
    }

    /**
     * Grammar production: f0 -> <PRIMARYKEY_> f1 -> "(" f2 ->
     * ColumnNameList(prn) f3 -> ")"
     */

    @Override
    public Object visit(PrimaryKeyDef n, Object argu) {
        ColumnNameListHandler aNameListHandler = new ColumnNameListHandler();
        n.f2.accept(aNameListHandler, null);
        if (columnNameList != null) {
            throw new XDBServerException("multiple primary keys for table "
                    + parent.getTableName() + " are not allowed");
        }

        columnNameList = aNameListHandler.getColumnNameList();
        return null;
    }

    public SqlAlterTable getParent() {
        return parent;
    }

    /**
     *
     * @return returns the index id used to create the primary key -1 if no such
     *         index exists
     */
    public int getIndexIDUsed() {
        return indexIdToUse;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public List<String> getColumnNames() {
        return columnNameList;
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
        LinkedList<String> comm = new LinkedList<String>();
        String sql;
        logger.entering(method, new Object[] {});
        try {

            SysTable table = parent.getTable();
            table.ensurePermission(client.getCurrentUser(),
                    SysPermission.PRIVILEGE_ALTER);
            for (Object element : columnNameList) {
                String columnName = (String) element;
                SysColumn column = table.getSysColumn(columnName);
                if (column == null) {
                    throw new XDBServerException(
                            ErrorMessageRepository.COLUMN_NOT_IN_TABLE + " ("
                                    + columnName + ", " + table.getTableName()
                                    + ")", 0,
                            ErrorMessageRepository.COLUMN_NOT_IN_TABLE_CODE);
                }
            }
            List<SysColumn> oldKey = table.getPrimaryKey();
            if (oldKey != null && oldKey.size() > 0) {
                throw new XDBServerException(
                        ErrorMessageRepository.PRIMARY_INDEX_ALREADY_PRESENT
                                + " (" + table.getTableName() + " )",
                        0,
                        ErrorMessageRepository.PRIMARY_INDEX_ALREADY_PRESENT_CODE);
            }
            SysIndex indexToUse = table.getPrimaryOrUniqueIndex(columnNameList);
            indexIdToUse = indexToUse == null ? -1 : indexToUse.idxid;

            StringBuffer aColumnList = new StringBuffer();

            for (String element : columnNameList) {
                aColumnList.append(IdentifierHandler.quote(element)).append(", ");
            }
            aColumnList.setLength(aColumnList.length() - 2);

            HashMap<String,String> arguments = new HashMap<String,String>();
            arguments.put("table", IdentifierHandler.quote(table.getTableName()));
            arguments.put("col_list", aColumnList.toString());
            if (constraintName == null) {
                constraintName = "PK_IDX_" + table.getTableName();
            }
            arguments.put("constr_name", IdentifierHandler.quote(constraintName));

            sql = ParseCmdLine.substitute(
                    Props.XDB_SQLCOMMAND_ALTERTABLE_ADDPRIMARY, arguments);
            if (Props.XDB_SQLCOMMAND_ALTERTABLE_ADDPRIMARY_TO_PARENT) {
                parent.addCommonCommand(sql);
            } else {
                comm.add(sql);
            }

            commands = comm.toArray(new String[comm.size()]);

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

            engine.executeDDLOnMultipleNodes(commands, parent.getNodeList(),
                    new SyncAlterTablePrimaryKey(this), client);
            return null;

        } finally {
            logger.exiting(method);
        }
    }
}
