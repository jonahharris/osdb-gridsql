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
 * DateType.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Types;

/**
 * 
 *  
 */
public class DateType implements XData {
    public Date val = null;

    protected DateType() {
    }

    /** Creates a new instance of DateType */
    public DateType(Date val) {
        this.val = val;
    }

    public Date getDate() {
        return val;
    }

    public int getJavaType() {
        return Types.DATE;
    }

    /** is this data value null? */
    public boolean isValueNull() {
        return val == null;
    }

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull) {
        // this.isNull = isNull;
    }

    /** get the current data type value as string */
    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return val.toString();
    }

    public void setJavaType(int type) {
    }

    /** returns the java object implemented */
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
