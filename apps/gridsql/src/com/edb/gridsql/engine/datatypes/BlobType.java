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
 * BlobType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

/**
 * 
 *  
 */
public class BlobType implements com.edb.gridsql.engine.datatypes.XData {
    public java.sql.Blob val = null;

    public boolean isNull = true;

    public int javaType = java.sql.Types.BLOB;

    /** Creates a new instance of BlobType */
    public BlobType(byte[] bs) {
        val = new XBlob(bs);
        if (bs != null) {
            isNull = false;
        }
    }

    public int getJavaType() {
        return javaType;
    }

    /** returns the java object implemented */
    public Object getObject() throws SQLException {
        return val;
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
    }

    public void setObject(Object o) throws SQLException {
    }

    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    /** convert the value to string with the specified charset encoding */
    public String toString(String encoding)
            throws java.io.UnsupportedEncodingException {
        if (val == null) {
            return null;
        }
        try {
            return new String(val.getBytes(1, (int) val.length()), encoding);
        } catch (Exception e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }
}
