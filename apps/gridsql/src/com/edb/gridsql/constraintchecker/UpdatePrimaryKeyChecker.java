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
package com.edb.gridsql.constraintchecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysIndex;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.Parser;
import com.edb.gridsql.parser.SqlSelect;

/**
 * 
 */
public class UpdatePrimaryKeyChecker extends AbstractConstraintChecker {
    private static final XLogger logger = XLogger
            .getLogger(InsertPrimaryKeyChecker.class);

    private static final String TABLE_ALIAS1 = "t1";

    private static final String TABLE_ALIAS2 = "t2";

    private static final String TABLE_ALIAS3 = "t3";

    /**
     * 
     * @param targetTable 
     * @param client 
     */

    public UpdatePrimaryKeyChecker(SysTable targetTable,
            XDBSessionContext client) {
        super(targetTable, client);
    }

    /**
     * Validate untouched rows with temp table: SELECT 1 FROM target t1 INNER
     * JOIN temp t2 ON t1.KEY = t2.KEY_new LEFT OUTER JOIN temp t3 ON t1.<rowid> =
     * t3.<rowid>_old WHERE t3.<rowid>_old IS NULL Validate temp table: SELECT
     * 1 FROM temp t1, temp t2 WHERE t1.KEY_new = t2.KEY_new and t1.<rowid>_old <>
     * t2.<rowid>_old
     * 
     * @param idx 
     * @throws java.lang.Exception 
     * @return 
     */
    @Override
    protected Map<IExecutable, ViolationCriteria> prepareConstraint(Object idx)
            throws Exception {
        ViolationCriteria criteria = new ViolationCriteria();
        criteria.violationType = VIOLATE_IF_NOT_EMPTY;
        criteria.message = idx.toString();
        Map<IExecutable, ViolationCriteria> validateSQL = new HashMap<IExecutable, ViolationCriteria>();
        StringBuffer sbSelect = new StringBuffer("select 1 from ");
        sbSelect.append(targetTable.getTableName()).append(" ").append(
                TABLE_ALIAS1);
        sbSelect.append(" INNER JOIN ").append(tempTable.getTableName())
                .append(" ");
        sbSelect.append(TABLE_ALIAS2).append(" ON ");
        for (Iterator it = ((SysIndex) idx).getKeyColumns().iterator(); it
                .hasNext();) {
            SysColumn col = (SysColumn) it.next();
            sbSelect.append(TABLE_ALIAS1).append(".").append(col.getColName())
                    .append("=");
            sbSelect.append(TABLE_ALIAS2).append(".").append(col.getColName());
            sbSelect.append("_new AND ");
        }
        sbSelect.setLength(sbSelect.length() - 4);
        sbSelect.append("LEFT OUTER JOIN ");
        sbSelect.append(tempTable.getTableName()).append(" ").append(
                TABLE_ALIAS3);
        sbSelect.append(" ON ");
        for (Iterator<SysColumn> it = targetTable.getRowID().iterator(); it
                .hasNext();) {
            SysColumn column = it.next();
            if (column.isNullable()) {
                sbSelect.append("(");
            }
            sbSelect.append(TABLE_ALIAS1).append(".").append(
                    column.getColName()).append(" = ");
            sbSelect.append(TABLE_ALIAS3).append(".").append(
                    column.getColName()).append("_old");
            if (column.isNullable()) {
                sbSelect.append(" OR ");
                sbSelect.append(TABLE_ALIAS1).append(".").append(
                        column.getColName()).append(" IS NULL AND ");
                sbSelect.append(TABLE_ALIAS3).append(".").append(
                        column.getColName()).append("_old IS NULL)");
            }
            sbSelect.append(" AND ");
        }
        sbSelect.setLength(sbSelect.length() - 5);
        sbSelect.append(" WHERE ");
        for (Iterator<SysColumn> it = targetTable.getRowID().iterator(); it
                .hasNext();) {
            SysColumn column = it.next();
            sbSelect.append(TABLE_ALIAS3).append(".").append(
                    column.getColName()).append("_old IS NULL AND ");
        }
        sbSelect.setLength(sbSelect.length() - 5);
        Parser parser = new Parser(client);
        parser.parseStatement(sbSelect.toString());
        SqlSelect select = (SqlSelect) parser.getSqlObject();
        select.addSkipPermissionCheck(targetTable.getTableName());
        select.prepare();
        validateSQL.put(select, criteria);
        StringBuffer sbSelfSelect = new StringBuffer("select 1 from ");
        sbSelfSelect.append(tempTable.getTableName()).append(" ").append(
                TABLE_ALIAS1);
        sbSelfSelect.append(", ").append(tempTable.getTableName()).append(" ")
                .append(TABLE_ALIAS2);
        sbSelfSelect.append(" WHERE ");
        for (Iterator it = ((SysIndex) idx).getKeyColumns().iterator(); it
                .hasNext();) {
            SysColumn col = (SysColumn) it.next();
            sbSelfSelect.append(TABLE_ALIAS1).append(".").append(
                    col.getColName()).append("_new=");
            sbSelfSelect.append(TABLE_ALIAS2).append(".").append(
                    col.getColName());
            sbSelfSelect.append("_new AND ");
        }
        for (Iterator<SysColumn> it = targetTable.getRowID().iterator(); it
                .hasNext();) {
            SysColumn column = it.next();
            if (column.isNullable()) {
                sbSelfSelect.append("(");
            }
            sbSelfSelect.append(TABLE_ALIAS1).append(".").append(
                    column.getColName()).append("_old <> ");
            sbSelfSelect.append(TABLE_ALIAS2).append(".").append(
                    column.getColName()).append("_old AND ");
            if (column.isNullable()) {
                sbSelfSelect.append(" OR ");
                sbSelfSelect.append(TABLE_ALIAS1).append(".").append(
                        column.getColName()).append(" IS NULL AND ");
                sbSelfSelect.append(TABLE_ALIAS2).append(".").append(
                        column.getColName()).append("_old IS NULL)");
            }
            sbSelect.append(" AND ");
        }
        sbSelfSelect.setLength(sbSelfSelect.length() - 5);
        parser = new Parser(client);
        parser.parseStatement(sbSelfSelect.toString());
        select = (SqlSelect) parser.getSqlObject();
        select.addSkipPermissionCheck(targetTable.getTableName());
        select.prepare();
        validateSQL.put(select, criteria);
        return validateSQL;
    }


    /**
     * 
     * @param columnsInvolved 
     * @param keysToCheck 
     * @return 
     * @see com.edb.gridsql.ConstraintChecker.AbstractConstraintChecker#scanConstraints(java.util.Collection,
     *      java.util.Collection)
     */

    @Override
    protected Collection<SysColumn> scanConstraints(
            Collection<SysColumn> columnsInvolved,
            Collection keysToCheck) {
        final String method = "scanConstraints";
        logger.entering(method, new Object[] {});
        try {

            Collection<SysColumn> colsToAdd = new HashSet<SysColumn>();
            Collection<SysIndex> keys = targetTable
                    .getAllUniqueAndPrimarySysIndexes();
            for (Iterator<SysIndex> it = keys.iterator(); it.hasNext();) {
                SysIndex anIndex = it.next();
                if (anIndex.isDistributed) {
                    boolean touched = false;
                    Collection<SysColumn> columns = new ArrayList<SysColumn>(
                            anIndex.keycnt);
                    for (Iterator<SysColumn> itcol = anIndex.getKeyColumns()
                            .iterator(); itcol.hasNext();) {
                        SysColumn column = itcol.next();
                        if (columnsInvolved.contains(column)) {
                            touched = true;
                        } else {
                            columns.add(column);
                        }
                    }
                    if (touched) {
                        keysToCheck.add(anIndex);
                        colsToAdd.addAll(columns);
                    }
                }
            }
            return colsToAdd;

        } finally {
            logger.exiting(method);
        }
    }
}
