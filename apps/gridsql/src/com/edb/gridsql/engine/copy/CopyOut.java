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
package com.edb.gridsql.engine.copy;

import java.sql.SQLException;

/**
 * Objects implementing this interface are used to control running COPY TO command
 */
public interface CopyOut {

    /**
     * Returns number of fields in the data set
     * @return field count
     */
    int getFieldCount();

    /**
     * Get one row of data from the COPY command
     * @return byte array containing the data
     * @throws SQLException
     *          if SQL error occurs
     */
    byte[] readFromCopy() throws SQLException;

    /**
     * Cancel the COPY command
     * @throws SQLException
     *          if SQL error occurs
     */
    void cancelCopy() throws SQLException;
}
