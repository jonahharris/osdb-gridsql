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
 */
public class SortCriteria {
    public static final int ASCENDING = 1;

    public static final int DESCENDING = 2;

    private int columnPosition;

    private boolean includeInResult;

    int columnType;

    public int getDirection() {
        return direction;
    }

    private int direction;

    public SortCriteria(int columnPosition, boolean includeInResult,
            int direction, int columnType) {
        this.columnPosition = columnPosition;
        this.includeInResult = includeInResult;
        this.direction = direction;
        this.columnType = columnType;
    }

    public int getColumnPosition() {
        return columnPosition;
    }

    public boolean isIncludeInResult() {
        return includeInResult;
    }

    public int getColumnType() {
        return columnType;
    }
}
