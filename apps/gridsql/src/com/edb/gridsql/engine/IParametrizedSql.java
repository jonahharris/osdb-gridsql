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
/**
 * 
 */
package com.edb.gridsql.engine;

import com.edb.gridsql.exception.XDBServerException;

/**
 * The interface should be implemented by SQL statement that supports parameters
 * 
 * 
 * @version draft
 */
public interface IParametrizedSql // ?? extends IPreparable
{
    /**
     * Returns number of parameters found in the statement
     * 
     * @return the number of parameters
     * @throws XDBServerException
     *                 if error occurs, e.g. statement has not been parsed
     */
    int getParamCount() throws XDBServerException;

    /**
     * Get value for specific parameter
     * 
     * @param index
     *                Zero-based index (0 - first parameter, 1 - second, etc.)
     * @return Value for the parameter
     * @throws ArrayIndexOutOfBoundsException
     *                 if parameter with specified index does not exist
     * @throws XDBServerException
     *                 if error occurs
     * 
     */
    String getParamValue(int index) throws ArrayIndexOutOfBoundsException,
            XDBServerException;

    /**
     * Get values for all parameters
     * 
     * @return the values
     * @throws ArrayIndexOutOfBoundsException
     *                 if supplied array is longer than number of parameters
     * @throws XDBServerException
     *                 if error occurs
     */
    String[] getParamValues() throws ArrayIndexOutOfBoundsException,
            XDBServerException;

    /**
     * Set value for specific parameter
     * 
     * @param index
     *                Zero-based index (0 - first parameter, 1 - second, etc.)
     * @param value
     *                Value for the parameter
     * @throws ArrayIndexOutOfBoundsException
     *                 if parameter with specified index does not exist
     * @throws XDBServerException
     *                 if error occurs
     */
    void setParamValue(int index, String value)
            throws ArrayIndexOutOfBoundsException, XDBServerException;

    /**
     * Set value for all parameters
     * 
     * @param values
     *                the values
     * @throws ArrayIndexOutOfBoundsException
     *                 if supplied array is longer than number of parameters
     * @throws XDBServerException
     *                 if error occurs
     */
    void setParamValues(String[] values) throws ArrayIndexOutOfBoundsException,
            XDBServerException;

    /**
     * Get data type for specific parameter
     * 
     * @param index
     *                Zero-based index (0 - first parameter, 1 - second, etc.)
     * @return Data type of the parameter
     * @throws ArrayIndexOutOfBoundsException
     *                 if parameter with specified index does not exist
     * @throws XDBServerException
     *                 if error occurs
     * 
     */
    int getParamDataType(int index) throws ArrayIndexOutOfBoundsException,
            XDBServerException;

    /**
     * Get data types for all parameters
     * 
     * @return the data types
     * @throws ArrayIndexOutOfBoundsException
     *                 if supplied array is longer than number of parameters
     * @throws XDBServerException
     *                 if error occurs
     */
    int[] getParamDataTypes() throws ArrayIndexOutOfBoundsException,
            XDBServerException;

    /**
     * Set data type for specific parameter
     * 
     * @param index
     *                Zero-based index (0 - first parameter, 1 - second, etc.)
     * @param dataType
     *                Data Type for the parameter
     * @throws ArrayIndexOutOfBoundsException
     *                 if parameter with specified index does not exist
     * @throws XDBServerException
     *                 if error occurs
     */
    void setParamDataType(int index, int dataType)
            throws ArrayIndexOutOfBoundsException, XDBServerException;

    /**
     * Set data types for all parameters
     * 
     * @param data
     *                types the data types
     * @throws ArrayIndexOutOfBoundsException
     *                 if supplied array is longer than number of parameters
     * @throws XDBServerException
     *                 if error occurs
     */
    void setParamDataTypes(int[] dataTypes)
            throws ArrayIndexOutOfBoundsException, XDBServerException;
}
