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
 * BigDecimalType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.math.*;
import java.sql.*;

/**
 * 
 *  
 */
public class BigDecimalType implements XData {
    public BigDecimal val = null;

    private int javaType = Integer.MIN_VALUE;

    protected BigDecimalType() {
    }

    /** Creates a new instance of BigDecimalType */
    public BigDecimalType(String str) {
        if (str != null) {
            val = new BigDecimal(str);
        }
    }

    public int getJavaType() {
        return this.javaType;
    }

    /** get the current data type value as string */
    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    /** is this data value null? */
    public boolean isValueNull() {
        return val == null;
    }

    public void setJavaType(int type) {
        this.javaType = type;
    }

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull) {
    }

    public Object getObject() throws SQLException {
        return val;
    }

    public void setObject(Object o) throws SQLException {
        //
    }

    /** convert the value to string with the specified charset encoding */
    public String toString(String encoding)
            throws java.io.UnsupportedEncodingException {
        if (val == null) {
            return null;
        }
        return new String(val.toString().getBytes(), encoding);
    }

}
