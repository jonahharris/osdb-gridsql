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
 * ByteArrayType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.SQLException;

/**
 * handles BINARY, VARBINARY, LONGVARBINARY
 * 
 *  
 */
public class ByteArrayType implements com.edb.gridsql.engine.datatypes.XData {
    public byte[] val = {};

    public int javaType = java.sql.Types.BINARY;

    public boolean isNull = true;

    /** Creates a new instance of ByteArrayType */
    private ByteArrayType() {
    }

    public ByteArrayType(byte[] bs) {
        this.val = bs;
        if (bs != null) {
            isNull = false;
        }
    }

    public int getJavaType() {
        return this.javaType;
    }

    /** returns the java object implemented */
    public Object getObject() throws SQLException {
        return this.val;
    }

    /** is this data value null? */
    public boolean isValueNull() {
        return isNull;
    }

    public void setJavaType(int type) {
        this.javaType = type;
    }

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull) {
        this.isNull = isNull;
    }

    public void setObject(Object o) throws SQLException {
    }

    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return new String(val);
    }

    /** convert the value to string with the specified charset encoding */
    public String toString(String encoding)
            throws java.io.UnsupportedEncodingException {
        if (val == null) {
            return null;
        }
        return new String(val, 0, val.length, encoding);// todo
    }
}
