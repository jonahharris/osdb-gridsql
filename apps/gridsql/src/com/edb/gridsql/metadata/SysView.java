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
package com.edb.gridsql.metadata;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Vector;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.ErrorMessageRepository;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.optimizer.QueryTree;
import com.edb.gridsql.optimizer.RelationNode;
import com.edb.gridsql.parser.Parser;
import com.edb.gridsql.parser.SqlSelect;
import com.edb.gridsql.parser.core.ParseException;

public class SysView {
    private static final XLogger logger = XLogger.getLogger(SysView.class);

    private SysDatabase database = null;

    private int viewid;

    private String viewName;

    private String viewText;

    private Vector<SysViewColumns> viewColumns;

    private Vector<SysColumn> viewDepends;

    // ----------------------------------------------------------------
    public SysView(SysDatabase database, int viewID, String viewName,
            String viewText) {
        this.database = database;
        this.viewid = viewID;
        this.viewName = viewName;
        this.viewText = viewText;
        viewColumns = new Vector<SysViewColumns>();
        viewDepends = new Vector<SysColumn>();
        readViewInfo();
    }

    void readViewInfo() throws XDBServerException {
        final String method = "readViewInfo";
        logger.entering(method);
        try {

            readViewColumns();

        } catch (Exception se) {

            throw new XDBServerException(
                    ErrorMessageRepository.METADATA_DB_INFO_READ_ERROR, se,
                    ErrorMessageRepository.METADATA_DB_INFO_READ_ERROR_CODE);

        }

        finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     */
    private void readViewColumns() throws Exception {

        String sqlquery = "SELECT * FROM xsysviewscolumns "
                + " WHERE viewid = " + viewid + " ORDER BY viewcolseqno";

        ResultSet rs = MetaData.getMetaData().executeQuery(sqlquery);

        viewColumns.clear();

        while (rs.next()) {
            SysViewColumns aViewColumn = new SysViewColumns(rs
                    .getInt("viewcolid"), viewid, rs.getInt("viewcolseqno"), rs
                    .getString("viewcolumn"), rs.getInt("coltype"), rs
                    .getInt("collength"), rs.getInt("colscale"), rs
                    .getInt("colprecision"));
            viewColumns.add(aViewColumn);
        }

        sqlquery = "SELECT * FROM xsysviewdeps " + " WHERE viewid = " + viewid
                + " ORDER BY tableid";

        rs = MetaData.getMetaData().executeQuery(sqlquery);

        viewDepends.clear();

        while (rs.next()) {
            viewDepends.add(database.getSysTable(rs.getInt("tableid"))
                    .getSysColumn(rs.getInt("columnid")));
        }
    }

    public int getViewid() {
        return viewid;
    }

    public void setViewid(int viewid) {
        this.viewid = viewid;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public String getViewText() {
        return viewText;
    }

    public void setViewText(String viewText) {
        this.viewText = viewText;
    }

    /**
     * @return Returns the viewColumns.
     */
    public Vector<SysViewColumns> getViewColumns() {
        return viewColumns;
    }

    public SysDatabase getSysDatabase() {
        return database;
    }

    public boolean hasDependedTable(int aTableid) {
        for (Iterator it = viewDepends.iterator(); it.hasNext();) {
            SysTable table = ((SysColumn) it.next()).getSysTable();
            if (table.getTableId() == aTableid) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDependedColumn(int aColid) {
        for (Iterator it = viewDepends.iterator(); it.hasNext();) {
            SysColumn col = (SysColumn) it.next();
            if (col.getColID() == aColid) {
                return true;
            }
        }
        return false;
    }

    public boolean canRenameTable(SysTable table, XDBSessionContext client) {
        // TODO Auto-generated method stub
        return false;
    }

    public void renameTable(String oldTableNmae, String table,
            XDBSessionContext client) throws XDBServerException,
            ParseException, IOException {

        Parser parser = new Parser(client);
        parser.parseStatement(viewText);
        SqlSelect select = (SqlSelect) parser.getSqlObject();
        // The SubQuery Tree will have the query tree which will
        // be filled up with the select statement information
        QueryTree aSubQueryTree = select.aQueryTree;
        int i = 0;
        for (Iterator it = aSubQueryTree.getRelationNodeList().iterator(); it
                .hasNext();) {
            RelationNode rel = (RelationNode) it.next();
            if (rel.getTableName().equals(oldTableNmae)) {
                // ((RelationNode)aSubQueryTree.relationNodeList.elementAt(i)).setTableName(table.getTableName());
                rel.setTableName(table);
                rel.setAlias(table);
            }
            i++;
        }

        aSubQueryTree.rebuildString();

    }

}