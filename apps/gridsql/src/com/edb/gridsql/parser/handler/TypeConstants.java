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
 * TypeConstants.java
 * 
 *  
 */
package com.edb.gridsql.parser.handler;

import com.edb.gridsql.common.util.Property;

/**
 * This class defines different constants that are associated with the configuration
 * attributes as specified in the xdb.config file. Each constant has a default value
 * that is applicable in case an attribute is not explicitly set in the configuration file.
 *
 *  
 */
public interface TypeConstants {
    public static final String INTEGER_TEMPLATE = Property.get(
            "xdb.sqltype.integer.map", "INT");

    public static final String BIGINTEGER_TEMPLATE = Property.get(
            "xdb.sqltype.biginteger.map", "BIGINT");

    public static final String SMALLINT_TEMPLATE = Property.get(
            "xdb.sqltype.smallint.map", "SMALLINT");

    public static final String BOOLEAN_TEMPLATE = Property.get(
            "xdb.sqltype.boolean.map", "BOOLEAN");

    public static final String REAL_TEMPLATE = Property.get(
            "xdb.sqltype.real.map", "REAL");

    public static final String FLOAT_TEMPLATE = Property.get(
            "xdb.sqltype.float.map", "FLOAT ({length})");
    
    public static final String FLOAT_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.float.edb.map", "FLOAT");

    public static final String DOUBLE_TEMPLATE = Property.get(
            "xdb.sqltype.double.map", "DOUBLE PRECISION");

    public static final String NUMERIC_TEMPLATE = Property.get(
            "xdb.sqltype.numeric.map", "NUMERIC ({precision}, {scale})");
    
    // EnerpriseDB allows NUMERIC type usage without precision and scale, hence a 
    // new template is defined 
    public static final String NUMERIC_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.numeric.edb.map", "NUMERIC");

    public static final String DECIMAL_TEMPLATE = Property.get(
            "xdb.sqltype.decimal.map", "DEC ({precision}, {scale})");
    
    // EnerpriseDB allows DEC type usage without length, hence a 
    // new template is defined 
    public static final String DECIMAL_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.decimal.edb.map", "DEC");

    public static final String FIXED_TEMPLATE = Property.get(
            "xdb.sqltype.fixed.map", "NUMERIC ({precision}, {scale})");

    public static final String TIMESTAMP_TEMPLATE = Property.get(
            "xdb.sqltype.timestamp.map", "TIMESTAMP");
    
    // EnerpriseDB allows TIMESTAMP type usage with optional length, hence a 
    // new template is defined 
    public static final String TIMESTAMP_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.timestamp.edb.map", "TIMESTAMP ({length})");
    
    public static final String TIMESTAMPWITHOUTLENGTH_WITH_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.timestamp.withoutlength.edb.map", "TIMESTAMP WITH TIME ZONE");
    
    public static final String TIMESTAMPWITHOUTLENGTH_WITHOUT_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.timestamp.withoutlength.edb.map", "TIMESTAMP WITHOUT TIME ZONE");
            
    public static final String TIMESTAMPWITHLENGTH_WITH_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.timestamp.withlength.edb.map", "TIMESTAMP ({length}) WITH TIME ZONE");
    
    public static final String TIMESTAMPWITHLENGTH_WITHOUT_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.timestamp.withlength.edb.map", "TIMESTAMP ({length}) WITHOUT TIME ZONE");

    public static final String TIME_TEMPLATE = Property.get(
            "xdb.sqltype.time.map", "TIME");
    
    // EnerpriseDB allows TIME type usage with optional length, hence a 
    // new template is defined 
    public static final String TIME_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.time.edb.map", "TIME ({length})");
    
    public static final String TIMEWITHOUTLENGTH_WITH_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.time.withoutlength.edb.map", "TIME WITH TIME ZONE");
    
    public static final String TIMEWITHOUTLENGTH_WITHOUT_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.time.withoutlength.edb.map", "TIME WITHOUT TIME ZONE");
            
    public static final String TIMEWITHLENGTH_WITH_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.time.withlength.edb.map", "TIME ({length}) WITH TIME ZONE");
    
    public static final String TIMEWITHLENGTH_WITHOUT_TIMEZONE_TEMPLATE = Property.get(
            "xdb.sqltype.time.withlength.edb.map", "TIME ({length}) WITHOUT TIME ZONE");

    public static final String DATE_TEMPLATE = Property.get(
            "xdb.sqltype.date.map", "DATE");

    public static final String CHAR_TEMPLATE = Property.get(
            "xdb.sqltype.char.map", "CHAR ({length})");

    public static final String VARCHAR_TEMPLATE = Property.get(
            "xdb.sqltype.varchar.map", "VARCHAR ({length})");
    
    // EnerpriseDB allows VARCHAR type usage without length, hence a 
    // new template is defined 
    public static final String VARCHAR_TEMPLATE_EDB_EXTENSION = Property.get(
            "xdb.sqltype.varchar.edb.map", "VARCHAR");

    public static final String VARBIT_TEMPLATE = Property.get(
            "xdb.sqltype.varbit.map", "BIT VARYING ({length})");

    public static final String VARBIT_TEMPLATE_WITHOUT_LENGTH = Property.get(
            "xdb.sqltype.varbit.map", "VARBIT");

    public static final String BIT_TEMPLATE = Property.get(
            "xdb.sqltype.bit.map", "BIT ({length})");

    public static final String INTERVAL_TEMPLATE = Property.get(
            "xdb.sqltype.interval.map", "INTERVAL");

    public static final String INTERVAL_TEMPLATE_QUALIFIED = Property.get(
            "xdb.sqltype.interval.qualified.map", "INTERVAL {from} TO {to}");

    public static final String TEXT_TEMPLATE = Property.get(
            "xdb.sqltype.text.map", "TEXT");

    public static final String BLOB_TEMPLATE = Property.get(
            "xdb.sqltype.blob.map", "BYTEA");

    public static final String MACADDR_TEMPLATE = Property.get(
            "xdb.sqltype.macaddr.map", "MACADDR");
    
    public static final String CIDR_TEMPLATE = Property.get(
            "xdb.sqltype.cidr.map", "CIDR");

    public static final String INET_TEMPLATE = Property.get(
            "xdb.sqltype.inet.map", "INET");    
}
