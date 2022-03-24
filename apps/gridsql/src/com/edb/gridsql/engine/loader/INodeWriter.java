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
 * INodeWriter.java
 *
 *
 */
package com.edb.gridsql.engine.loader;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 */
public interface INodeWriter {
    public void start() throws IOException;

    public void writeRow(byte[] row) throws IOException;

    public void writeRow(byte[] row, int offset, int length) throws IOException;

    public void finish(boolean success) throws IOException;

    public void commit() throws SQLException;

    public void rollback() throws SQLException;

    public void close() throws SQLException;

    public String getStatistics();

    public long getRowCount();
}
