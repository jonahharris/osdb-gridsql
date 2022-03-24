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
/**
 * This interface defines two methods to get LOCK/ UNLOCK queries
 * The classes implementing this interface must return
 * the actaul syntax for LOCK / UNLOCK command for the particular database
 */

package com.edb.gridsql.util;

public interface LockUnlockTable {
    /**
     * 
     * @param tableName 
     * @return 
     */
    public String getLockString(String tableName);

    /**
     * 
     * @param tableName 
     * @return 
     */
    public String getUnlockString(String tableName);
}
