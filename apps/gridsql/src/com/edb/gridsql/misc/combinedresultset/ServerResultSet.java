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
package com.edb.gridsql.misc.combinedresultset;

/**
 * 
 * 
 */
import java.sql.*;

public interface ServerResultSet {
    public boolean next() throws SQLException;

    public String getString(int position) throws SQLException;

    public byte[] getBytes(int position) throws SQLException;

    public long getLong(int position) throws SQLException;

    public long getLong(String strColumn) throws SQLException;

    public ResultSetMetaData getMetaData() throws SQLException;

    public void close() throws SQLException;

    public void deleteRow() throws SQLException;

    public int findColumn(String strColumn) throws SQLException;
}
