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
 * ShortType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.SQLException;
import java.sql.Types;

/**
 * 
 *  
 */
public class ShortType implements XData {
    public Short val = null;

    private boolean isNull = true;

    /** Creates a new instance of ShortType */
    public ShortType(String str) {
        if (str != null) {
            val = new Short(str);
            isNull = false;
        }
    }

    public ShortType(short s) {
        val = new Short(s);
        isNull = false;
    }

    public int getJavaType() {
        return Types.SMALLINT;
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
        return isNull;
    }

    public void setJavaType(int type) {
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
