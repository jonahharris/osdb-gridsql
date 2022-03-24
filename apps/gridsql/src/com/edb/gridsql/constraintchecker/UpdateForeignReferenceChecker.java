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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysConstraint;
import com.edb.gridsql.metadata.SysForeignKey;
import com.edb.gridsql.metadata.SysReference;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.parser.Parser;
import com.edb.gridsql.parser.SqlSelect;

/**
 * 
 */
public class UpdateForeignReferenceChecker extends AbstractConstraintChecker {
    private static final XLogger logger = XLogger
            .getLogger(UpdateForeignReferenceChecker.class);

    private static final String TABLE_ALIAS1 = "t1";

    private static final String TABLE_ALIAS2 = "t2";

    private static final String TABLE_ALIAS3 = "t3";

    /**
     * @param targetTable
     * @param client
     */
    public UpdateForeignReferenceChecker(SysTable targetTable,
            XDBSessionContext client) {
        super(targetTable, client);
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
            Collection sysFks = targetTable.getSysReferences();
            for (Iterator it = sysFks.iterator(); it.hasNext();) {
                SysReference reference = (SysReference) it.next();
                if (reference.getDistributedCheck()) {
                    boolean touched = false;
                    Collection<SysColumn> refKeys = new HashSet<SysColumn>();
                    Collection fks = reference.getForeignKeys();
                    for (Iterator iter = fks.iterator(); iter.hasNext();) {
                        SysForeignKey fk = (SysForeignKey) iter.next();
                        SysColumn col = targetTable.getSysColumn(fk
                                .getRefcolid());
                        if (columnsInvolved.contains(col)) {
                            touched = true;
                        } else {
                            refKeys.add(col);
                        }
                    }
                    if (touched) {
                        keysToCheck.add(reference);
                        colsToAdd.addAll(refKeys);
                    }
                }
            }
            return colsToAdd;

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * Find referenced foreign keys that being modified and new value is not
     * created in other row. SELECT 1 FROM <foreign> t1 LEFT JOIN <temp> t2 ON
     * t1.<key> = t2.<key>_new LEFT JOIN <temp> t3 ON t1.<key> = t3.<key>_old
     * WHERE t2.<rowid>_old IS NULL AND t3.<rowid>_old IS NOT NULL
     * 
     * @see com.edb.gridsql.constraintchecker.AbstractConstraintChecker#prepareConstraint(java.lang.Object)
     * @param constraint 
     * @throws java.lang.Exception 
     * @return 
     */
    @Override
    protected Map<IExecutable, ViolationCriteria> prepareConstraint(
            Object constraint) throws Exception {
        final String method = "prepareConstraint";
        logger.entering(method, new Object[] {});
        try {

            SysConstraint sysConstraint = ((SysReference) constraint)
                    .getConstraint();
            ViolationCriteria criteria = new ViolationCriteria();
            criteria.violationType = VIOLATE_IF_NOT_EMPTY;
            criteria.message = sysConstraint.toString();
            SysTable foreignTable = sysConstraint.getSysTable();
            StringBuffer sbCondition1_2 = new StringBuffer();
            StringBuffer sbCondition1_3 = new StringBuffer();
            Collection fks = ((SysReference) constraint).getForeignKeys();
            for (Iterator iter = fks.iterator(); iter.hasNext();) {
                SysForeignKey fk = (SysForeignKey) iter.next();
                SysColumn col = foreignTable.getSysColumn(fk.getColid());
                SysColumn refCol = targetTable.getSysColumn(fk.getRefcolid());
                sbCondition1_2.append(TABLE_ALIAS1).append(".");
                sbCondition1_2.append(col.getColName()).append("=");
                sbCondition1_2.append(TABLE_ALIAS2).append(".");
                sbCondition1_2.append(refCol.getColName()).append("_new AND ");
                sbCondition1_3.append(TABLE_ALIAS1).append(".");
                sbCondition1_3.append(col.getColName()).append("=");
                sbCondition1_3.append(TABLE_ALIAS3).append(".");
                sbCondition1_3.append(refCol.getColName()).append("_old AND ");
            }
            sbCondition1_2.setLength(sbCondition1_2.length() - 5);
            sbCondition1_3.setLength(sbCondition1_3.length() - 5);
            StringBuffer sbSelect = new StringBuffer("SELECT 1 FROM ");
            sbSelect.append(foreignTable.getTableName()).append(" ").append(
                    TABLE_ALIAS1);
            sbSelect.append(" LEFT JOIN ");
            sbSelect.append(tempTable.getTableName()).append(" ").append(
                    TABLE_ALIAS2);
            sbSelect.append(" ON ").append(sbCondition1_2);
            sbSelect.append(" LEFT JOIN ");
            sbSelect.append(tempTable.getTableName()).append(" ").append(
                    TABLE_ALIAS3);
            sbSelect.append(" ON ").append(sbCondition1_3);
            sbSelect.append(" WHERE ");
            for (Iterator<SysColumn> it = targetTable.getRowID().iterator(); it
                    .hasNext();) {
                SysColumn column = it.next();
                sbSelect.append(TABLE_ALIAS2).append(".").append(
                        column.getColName()).append("_old IS NULL AND ");
                sbSelect.append(TABLE_ALIAS3).append(".").append(
                        column.getColName()).append("_old IS NOT NULL AND ");
            }
            sbSelect.setLength(sbSelect.length() - 5);
            Parser parser = new Parser(client);
            parser.parseStatement(sbSelect.toString());
            SqlSelect select = (SqlSelect) parser.getSqlObject();
            select.addSkipPermissionCheck(targetTable.getTableName());
            select.addSkipPermissionCheck(foreignTable.getTableName());
            select.prepare();
            return Collections.singletonMap((IExecutable) select, criteria);

        } finally {
            logger.exiting(method);
        }
    }
}
