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
 * XData.java
 *
 *  
 */

package com.edb.gridsql.engine.datatypes;

import java.sql.SQLException;

/**
 * All XDB datatype should implement this
 * 
 *  
 */
public interface XData {

    public int getJavaType();

    public void setJavaType(int type);// useful for some classes

    /** is this data value null? */
    public boolean isValueNull();

    /** indicate whether the current data value is null */
    public void setNull(boolean isNull);

    /**
     * get the current data type value as string - default encoding or use the
     * last known type
     */
    public String toString();

    /** convert the value to string with the specified charset encoding */
    public String toString(String encoding)
            throws java.io.UnsupportedEncodingException;

    /** returns the java object implemented */
    public Object getObject() throws SQLException;

    public void setObject(Object o) throws SQLException;

}
