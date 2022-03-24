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
 * SyncCreateUser.java
 *
 *
 */
package com.edb.gridsql.metadata;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.parser.ExpressionType;
import com.edb.gridsql.parser.SqlCreateView;

/**
 *
 */
public class SyncCreateView implements IMetaDataUpdate {
    private static final XLogger logger = XLogger
            .getLogger(SyncCreateView.class);

    private SqlCreateView parent;

    private SysDatabase database;

    private int viewid;

    /**
     *
     */
    public SyncCreateView(SqlCreateView parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#execute(com.edb.gridsql.server.XDBSessionContext)
     */
    public void execute(XDBSessionContext client) throws Exception {
        execute(client.getSysDatabase());
    }

    public void execute(SysDatabase database) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            this.database = database;

            MetaData meta = MetaData.getMetaData();
            ResultSet rs = meta
                    .executeQuery("SELECT max(viewid) FROM xsysviews");
            try {
                rs.next();
                viewid = rs.getInt(1) + 1;
            } finally {
                rs.close();
            }
            // Let's use prepared statement to make password literal database
            // independent
            String commandStr = "INSERT INTO xsysviews"
                    + " (dbid, viewid, viewname, viewtext)"
                    + " VALUES (?, ?, ?, ?)";
            PreparedStatement ps = meta.prepareStatement(commandStr);
            ps.setInt(1, database.getDbid());
            ps.setInt(2, viewid);
            ps.setString(3, parent.getViewName());
            ps.setString(4, parent.getSelectString());
            if (ps.executeUpdate() != 1) {
                XDBServerException ex = new XDBServerException(
                        "Failed to insert row into \"xsysviews\"");
                logger.throwing(ex);
                throw ex;
            }
            if (parent.getColList() != null) {
                int viewcolid = -1;
                rs = meta
                        .executeQuery("SELECT max(viewcolid) FROM xsysviewscolumns");
                try {
                    rs.next();
                    viewcolid = rs.getInt(1) + 1;
                } finally {
                    rs.close();
                }
                int i = 0;
                for (String colName : parent.getColList()) {
                    ExpressionType expType = parent.getColDef().get(i);

                    commandStr = "INSERT INTO xsysviewscolumns"
                            + " (viewcolid, viewid, viewcolseqno, viewcolumn,coltype,collength,colscale,colprecision)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    ps = meta.prepareStatement(commandStr);
                    ps.setInt(1, viewcolid++);
                    ps.setInt(2, viewid);
                    ps.setInt(3, ++i);
                    ps.setString(4, colName);
                    ps.setInt(5, expType.type);
                    ps.setInt(6, expType.length);
                    ps.setInt(7, expType.scale);
                    ps.setInt(8, expType.precision);
                    if (ps.executeUpdate() != 1) {
                        XDBServerException ex = new XDBServerException(
                                "Failed to insert row into \"xsysviewscolumns\"");
                        logger.throwing(ex);
                        throw ex;
                    }
                }
                for (SysColumn col : parent.getDependedSysCol()) {
                    commandStr = "INSERT INTO xsysviewdeps"
                            + " ( viewid, columnid, tableid )"
                            + " VALUES (?, ?, ?)";
                    ps = meta.prepareStatement(commandStr);
                    ps.setInt(1, viewid);
                    ps.setInt(2, col.getColID());
                    ps.setInt(3, col.getSysTable().getTableId());
                    if (ps.executeUpdate() != 1) {
                        XDBServerException ex = new XDBServerException(
                                "Failed to insert row into \"xsysviewdeps\"");
                        logger.throwing(ex);
                        throw ex;
                    }
                }

            }
        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.MetaData.IMetaDataUpdate#refresh()
     */
    public void refresh() throws Exception {
        final String method = "refresh";
        logger.entering(method, new Object[] {});
        try {

            SysView view = new SysView(database, viewid, parent.getViewName(),
                    parent.getSelectString());
            database.addSysView(view);

        } finally {
            logger.exiting(method);
        }
    }

}
