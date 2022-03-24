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
 * TimeType.java
 *
 *
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

/**
 *
 *
 */
public class TimeType implements XData {
    private Time val = null;
    private String timezone = null;

    protected TimeType() {
    }

    /** Creates a new instance of TimeType */
    public TimeType(Time t, String timezone) {
        this.val = t;
        this.timezone = timezone == null ? "" : timezone;
    }

    public int getJavaType() {
        return Types.TIME;
    }

    /** get the current data type value as string */
    @Override
    public String toString() {
        if (val == null) {
            return null;
        }
        return val.toString() + timezone;
    }

    /** is this data value null? */
    public boolean isValueNull() {
        return val == null;
    }

    public void setJavaType(int type) {
    }

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull) {
    }

    /** returns the java object implemented */
    public Object getObject() throws SQLException {
        return timezone == null || timezone.length() == 0 ? val : this;
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
