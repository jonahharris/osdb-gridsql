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
 * This is a wrapper class for CopyIn interface of the Postgres driver
 * Delegates respective method calls
 */
public class PgCopyOut implements CopyOut {

    private org.postgresql.copy.CopyOut copyOut;

    /**
     * Constructs new instance of PgCopyOut
     * @param copyOut
     *          Postgres driver's CopyOut object
     */
    PgCopyOut(org.postgresql.copy.CopyOut copyOut) {
        this.copyOut = copyOut;
    }

    /**
     * Delegate the call to Postgres CopyOut
     * @return field count
     */
    public int getFieldCount() {
        return copyOut.getFieldCount();
    }

    /**
     * Delegate the call to Postgres CopyOut
     * @return byte array containing the data
     * @throws SQLException
     *          if SQL error occurs
     */
    public byte[] readFromCopy() throws SQLException {
        return copyOut.readFromCopy();
    }

    /**
     * Delegate the call to Postgres CopyOut
     * @throws SQLException
     *          if SQL error occurs
     */
    public void cancelCopy() throws SQLException {
        copyOut.cancelCopy();
    }
}
