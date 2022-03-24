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
 * BooleanType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.SQLException;
import java.sql.Types;

/**
 * true value is "T" or "TRUE" (case insensitive) false otherwise
 * 
 *  
 */
public class BooleanType implements XData {
    private boolean isNull = true;

    public Boolean val = null;

    /** Creates a new instance of BooleanType */
    private BooleanType() {
    }

    public BooleanType(String val) throws SQLException {
        if (val != null) {
            if (val.equalsIgnoreCase("T") || val.equalsIgnoreCase("true")) {
                this.val = Boolean.TRUE;
            } else {
                this.val = Boolean.FALSE;
            }
            isNull = false;
        }
    }

    public BooleanType(boolean v) {
        val = new Boolean(v);
        isNull = false;
    }

    public int getJavaType() {
        return Types.BOOLEAN;
    }

    /** is this data value null? */
    public boolean isValueNull() {
        return isNull;
    }

    public void setJavaType(int type) {
    }

    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull) {
        this.isNull = isNull;
    }

    /** returns the java */
    public Object getObject() throws SQLException {
        return val;
    }

    public void setObject(Object o) throws SQLException {
    }

    /** convert the value to string with the specified charset encoding */
    public String toString(String encoding)
            throws java.io.UnsupportedEncodingException {
        if (val == null) {
            return null;
        }
        return new String(val.toString().getBytes(encoding));
    }

}
