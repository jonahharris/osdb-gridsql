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

import java.sql.ResultSet;
import java.util.Iterator;
import java.util.Map;

import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBGeneratorException;
import com.edb.gridsql.parser.SqlCreateTableColumn;

/**
 * 
 */
public class SysRowIDHandler extends SysSerialGenerator {
    private SysTable table;

    private XDBSessionContext client;

    SysRowIDHandler(SysTable table) {
        this.table = table;
    }

    public long allocateRange(long length, XDBSessionContext client)
            throws XDBGeneratorException {
        this.client = client;
        try {
            return allocateRange(length);
        } finally {
            this.client = null;
        }
    }

    /**
     * @return part of a query that is used to obtain the maximum xrowid 
     * from child tables.
     */
    private String buildChildXrowidQueryPart(SysTable aSysTable) {
        String queryStr = "";
        
        for (Iterator it = aSysTable.getChildrenTables().iterator(); 
                it.hasNext();) {
            SysTable childTable = (SysTable) it.next();
            
            if (queryStr.length() > 0) {
                queryStr += " UNION ";
            }
            queryStr += buildChildXrowidQueryPart(childTable);
        }
        
        if (queryStr.length() == 0) {
            // No child tables, use self
            queryStr = "SELECT MAX(xrowid) FROM " + aSysTable.getTableName();
        }
        
        return queryStr;
    }
    
    
    @Override
    protected void update() throws XDBGeneratorException {
        String rowIDSelect;
        
        if (client == null) {
            throw new XDBGeneratorException(
                    "Can not update RowID generator - client session is not supplied");
        }
        if (table.getChildrenTables() != null) { 
            /* PostgreSQL does a sequential scan on all of the child tables
             * instead of using the index, which takes a long time.
             * As a result, we need to dynamically generate a query 
             * that will be processed more efficiently.
             */
            rowIDSelect = "SELECT MAX(XROWID) FROM (" 
                    + buildChildXrowidQueryPart(table)
                    + ") AS x (XROWID)";
        } else {
            rowIDSelect = "select max(" + SqlCreateTableColumn.XROWID_NAME
                    + ") from " + table.getTableName();            
        }
        Engine engine = Engine.getInstance();
        try {
            long maxValue = 0;
            Map results = engine.executeQueryOnMultipleNodes(rowIDSelect, table
                    .getJoinNodeList(), client);
            for (Iterator it = results.values().iterator(); it.hasNext();) {
                ResultSet rs = (ResultSet) it.next();
                try {
                    if (rs.next()) {
                        long nodeMax = rs.getLong(1);
                        if (nodeMax > maxValue) {
                            maxValue = nodeMax;
                        }
                    }
                } finally {
                    rs.close();
                }
            }
            update(maxValue + 1);
        } catch (Exception ex) {
            throw new XDBGeneratorException("Failed to update RowID generator",
                    ex);
        }
    }
}
