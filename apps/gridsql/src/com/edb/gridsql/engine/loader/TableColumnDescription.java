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
 * TableColumnDescription.java
 *
 * 
 *
 * 
 * 
 */

package com.edb.gridsql.engine.loader;

import com.edb.gridsql.optimizer.SqlExpression;

/**
 *
 * 
 */
public class TableColumnDescription {

    private String name;

    private int length;

    private boolean serial;

    private boolean nullable;

    private SqlExpression defaultExpr;

    /**
     * Creates a new instance of TableColumnDescription
     */
    public TableColumnDescription() {
    }

    public void setName(String n) {
        this.name = n ;
    }

    public String getName() {
        return this.name ;
    }

    public void setLength(int l) {
        this.length = l ;
    }

    public int getLength() {
        return this.length ;
    }

    public void setSerial(boolean v) {
        this.serial = v ;
    }

    public boolean isSerial() {
        return this.serial ;
    }

    public void setNullable(boolean v) {
        this.nullable = v ;
    }

    public boolean isNullable() {
        return this.nullable ;
    }

    public void setDefault(SqlExpression defaultExpr) {
        this.defaultExpr = defaultExpr;
    }

    public SqlExpression getDefault() {
        return defaultExpr;
    }
}
