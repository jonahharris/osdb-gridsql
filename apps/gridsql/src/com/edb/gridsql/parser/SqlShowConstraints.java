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
package com.edb.gridsql.parser;

import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.edb.gridsql.common.ColumnMetaData;
import com.edb.gridsql.common.ResultSetImpl;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.datatypes.VarcharType;
import com.edb.gridsql.engine.datatypes.XData;
import com.edb.gridsql.metadata.MetaData;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysConstraint;
import com.edb.gridsql.metadata.SysDatabase;
import com.edb.gridsql.metadata.SysForeignKey;
import com.edb.gridsql.metadata.SysIndex;
import com.edb.gridsql.metadata.SysPermission;
import com.edb.gridsql.metadata.SysReference;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;
import com.edb.gridsql.optimizer.QueryTree;
import com.edb.gridsql.parser.core.syntaxtree.ShowConstraints;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.QueryTreeTracker;
import com.edb.gridsql.parser.handler.TableNameHandler;

/**
 * 
 * 
 */

/**
 * This class implements the functionalty for update statistics depending on the
 * query executed
 */
public class SqlShowConstraints extends ObjectDepthFirst implements IXDBSql,
        IExecutable {

    private XDBSessionContext client;

    public QueryTree aQueryTree = null;

    Command commandToExecute;

    public String TableName;

    public SqlShowConstraints(XDBSessionContext client) {
        this.client = client;
        // I will add a new command object to the QueryTree handler
        // and then set that particular object with the command object
        commandToExecute = new Command(Command.SELECT, this,
                new QueryTreeTracker(), client);

        // Secondly I will create a Query Tree Tracker object and will also
        // set it here in all other location below this when ever a QueryTree
        // Handler is created the query tree tracker will always set it.
        aQueryTree = new QueryTree();
    }

    @Override
    public Object visit(ShowConstraints n, Object obj) {
        Object _ret = null;
        n.f0.accept(this, obj);
        TableNameHandler aTableNameHandler = new TableNameHandler(client);
        n.f2.accept(aTableNameHandler, obj);
        TableName = aTableNameHandler.getTableName();
        return _ret;
    }

    /**
     * This will return the cost of executing this statement in time , milli
     * seconds
     */

    public long getCost() {
        return ILockCost.LOW_COST;
    }

    /**
     * This return the Lock Specification for the system
     *
     * @param theMData
     * @return
     */
    public LockSpecification getLockSpecs() {
        LockSpecification aLspec = new LockSpecification(
                Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        return aLspec;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Parser.IXDBSql#getNodeList()
     */
    public Collection getNodeList() {
        return Collections.EMPTY_LIST;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql.Engine.Engine)
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        short flags = 0;

        ColumnMetaData[] headers = new ColumnMetaData[] {

                new ColumnMetaData("constname", "constname", 250,
                        Types.VARCHAR, 0, 0, TableName, flags, false),

                new ColumnMetaData("type", "type", 10, Types.VARCHAR, 0, 0,
                        TableName, flags, false),

                new ColumnMetaData("sourcetable", "sourcetable", 1024,
                        Types.VARCHAR, 0, 0, TableName, flags, false),
                new ColumnMetaData("sourcecolumns", "sourcecolumns", 1024,
                        Types.VARCHAR, 0, 0, TableName, flags, false),

                new ColumnMetaData("desttable", "desttable", 1024,
                        Types.VARCHAR, 0, 0, TableName, flags, false),
                new ColumnMetaData("destcolumns", "destcolumns", 1024,
                        Types.VARCHAR, 0, 0, TableName, flags, false), };
        Vector rows = new Vector();

        SysDatabase db = MetaData.getMetaData().getSysDatabase(
                client.getDBName());

        SysTable theSysTable = db.getSysTable(TableName);
        theSysTable.ensurePermission(client.getCurrentUser(),
                SysPermission.PRIVILEGE_SELECT);
        List theConstraintList = theSysTable.getConstraintList();
        String aConstrName = "null";
        String aType = "";
        String aSourceTable = "null";
        StringBuffer aSourceColumns = new StringBuffer();
        String aTargetTable = "null";
        StringBuffer aTargetColumns = new StringBuffer();

        for (Iterator iter = theConstraintList.iterator(); iter.hasNext();) {
            aConstrName = "null";
            aType = "";
            aSourceTable = TableName;
            aSourceColumns = new StringBuffer();
            aTargetTable = "null";
            aTargetColumns = new StringBuffer();

            SysConstraint theConstraint = (SysConstraint) iter.next();
            aConstrName = theConstraint.getConstName();
            aType = "" + theConstraint.getConstType();
            if (aType.equals("R")) {
                SysReference theFk = theSysTable
                        .getFkSysReference(theConstraint.getConstID());

                Vector Fks = theFk.getForeignKeys();
                for (Iterator iterator = Fks.iterator(); iterator.hasNext();) {
                    SysForeignKey el = (SysForeignKey) iterator.next();
                    SysColumn col1 = el.getReferencedSysColumn(db);
                    SysColumn col2 = el.getReferringSysColumn(db);
                    aSourceColumns.append(col2.getColName()).append(", ");
                    aTargetColumns.append(col1.getColName()).append(", ");
                    aSourceTable = col2.getSysTable().getTableName();
                    aTargetTable = col1.getSysTable().getTableName();
                    aType = "FK";
                }
            } else if (aType.equals("P")) {
                aSourceColumns = new StringBuffer();
                aTargetColumns = new StringBuffer();
                aTargetColumns.append("null");
                aTargetTable = "null";
                List<SysColumn> theSysColumns = theSysTable.getPrimaryKey();
                for (SysColumn theCol : theSysColumns) {
                    aSourceColumns.append(theCol.getColName()).append(", ");
                }
                aType = "PK";
            } else if (aType.equals("U")) {
                aSourceColumns = new StringBuffer();
                aTargetColumns = new StringBuffer();
                SysIndex theSysIndex = theSysTable.getSysIndex(theConstraint
                        .getIdxID());
                List<SysColumn> theSysColumns = theSysIndex.getKeyColumns();
                for (SysColumn theCol : theSysColumns) {
                    aSourceColumns.append(theCol.getColName()).append(", ");
                }
            }
            if (aSourceColumns != null && aSourceColumns.length() > 2
                    && !aSourceColumns.toString().equals("null")) {
                aSourceColumns.delete(aSourceColumns.length() - 2,
                        aSourceColumns.length());
            }
            if (aTargetColumns != null && aTargetColumns.length() > 2
                    && !aTargetColumns.toString().equals("null")) {
                aTargetColumns.delete(aTargetColumns.length() - 2,
                        aTargetColumns.length());
            }
            rows.add(new XData[] { new VarcharType(aConstrName),
                    new VarcharType(aType), new VarcharType(aSourceTable),
                    new VarcharType(aSourceColumns.toString()),
                    new VarcharType(aTargetTable),
                    new VarcharType(aTargetColumns.toString()) });
        }

        Vector vRefRefList = theSysTable.getSysReferences();
        for (Iterator it = vRefRefList.iterator(); it.hasNext();) {
            aSourceColumns = new StringBuffer();
            aTargetColumns = new StringBuffer();

            SysReference aReferringTab = (SysReference) it.next();
            // aConstrName =
            // (theSysTable.getConstraint(aReferringTab.constid)).getConstName();
            Vector Fks = aReferringTab.getForeignKeys();
            for (Iterator iterator = Fks.iterator(); iterator.hasNext();) {
                SysForeignKey el = (SysForeignKey) iterator.next();
                SysColumn col1 = el.getReferencedSysColumn(db);
                SysColumn col2 = el.getReferringSysColumn(db);
                aSourceColumns.append(col2.getColName()).append(", ");
                aTargetColumns.append(col1.getColName()).append(", ");
                aSourceTable = col2.getSysTable().getTableName();
                aTargetTable = col1.getSysTable().getTableName();
                aConstrName = aReferringTab.getConstraint().getConstName();
            }

            aType = "R";
            if (aSourceColumns != null && aSourceColumns.length() > 2) {
                aSourceColumns.delete(aSourceColumns.length() - 2,
                        aSourceColumns.length());
            }
            if (aTargetColumns != null && aTargetColumns.length() > 2) {
                aTargetColumns.delete(aTargetColumns.length() - 2,
                        aTargetColumns.length());
            }
            rows.add(new XData[] { new VarcharType(aConstrName),
                    new VarcharType(aType), new VarcharType(aSourceTable),
                    new VarcharType(aSourceColumns.toString()),
                    new VarcharType(aTargetTable),
                    new VarcharType(aTargetColumns.toString()) });

        }
        return ExecutionResult.createResultSetResult(
                ExecutionResult.COMMAND_SHOW, new ResultSetImpl(headers, rows));
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
