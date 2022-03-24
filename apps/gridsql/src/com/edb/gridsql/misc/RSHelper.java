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
 * RSHelper.java
 *
 *  
 */

package com.edb.gridsql.misc;

import java.sql.*;

import com.edb.gridsql.metadata.SysColumn;

/**
 * 
 * 
 */
public class RSHelper {

    /** Creates a new instance of RSHelper */
    public RSHelper() {
    }

    /**
     * Returns an array of booleans indicating whether or not the column should
     * be quoted when used for inserts (for each item in the ResultSet)
     */
    public static boolean[] getQuoteInfo(ResultSet aResultSet)
            throws SQLException {
        int iColumnCount;
        boolean isQuoted[] = null;

        ResultSetMetaData rsmd = aResultSet.getMetaData();
        iColumnCount = rsmd.getColumnCount();

        // Determine when to quote columns.
        isQuoted = new boolean[iColumnCount];

        for (int i = 1; i <= iColumnCount; i++) {

            switch (rsmd.getColumnType(i)) {
            case java.sql.Types.BOOLEAN:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.DOUBLE:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.BIT:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.SMALLINT:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.INTEGER:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.NUMERIC:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.REAL:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.FLOAT:
                isQuoted[i - 1] = false;
                break;

            case java.sql.Types.DECIMAL:
                isQuoted[i - 1] = false;
                break;

            default:
                isQuoted[i - 1] = true;
                break;
            }
        }

        return isQuoted;
    }

    public static boolean getQuoteInfo(SysColumn tableColumn) {
        boolean isQuoted = false;
        switch (tableColumn.getColType()) {
        case java.sql.Types.BOOLEAN:
            isQuoted = false;
            break;

        case java.sql.Types.DOUBLE:
            isQuoted = false;
            break;

        case java.sql.Types.BIT:
            isQuoted = false;
            break;

        case java.sql.Types.SMALLINT:
            isQuoted = false;
            break;

        case java.sql.Types.INTEGER:
            isQuoted = false;
            break;

        case java.sql.Types.NUMERIC:
            isQuoted = false;
            break;

        case java.sql.Types.REAL:
            isQuoted = false;
            break;

        case java.sql.Types.FLOAT:
            isQuoted = false;
            break;

        case java.sql.Types.DECIMAL:
            isQuoted = false;
            break;

        default:
            isQuoted = true;
            break;
        }

        return isQuoted;
    }

}
