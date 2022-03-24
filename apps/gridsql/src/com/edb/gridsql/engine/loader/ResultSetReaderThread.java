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
package com.edb.gridsql.engine.loader;

import java.sql.ResultSet;
import java.util.concurrent.Callable;

import com.edb.gridsql.exception.XDBDataReaderException;

/**
 * Purpose of Data Reader is to split data source on rows
 * ResultSetReaderThread assumes the data source is a java.sql.ResultSet
 *
 * @author amart
 */
public class ResultSetReaderThread implements Callable<Boolean> {

    private DataReaderAndProcessorBuffer<String[]> loadBuffer;

    private int columns;

    private ResultSet dataSourceRS;

    private int[] groupHashList;

    /**
     * Creates a new instance of ResultSetReaderThread
     * @param rs
     * @param buffer
     * @param groupHashList List of expression strings to use to create
     *        a hashable String on. Used for GROUP BY processing
     * @throws XDBDataReaderException
     */
    public ResultSetReaderThread(ResultSet rs,
            DataReaderAndProcessorBuffer<String[]> buffer,
            int[] groupHashList)
            throws XDBDataReaderException {
        try {
            dataSourceRS = rs;
            columns = dataSourceRS.getMetaData().getColumnCount();
            loadBuffer = buffer;
            this.groupHashList = groupHashList;
        } catch (Exception ex) {
            throw new XDBDataReaderException(ex);
        }
    }

    /**
     * Closes data source.
     *
     */
    public void close() {
        try {
            if (dataSourceRS != null) {
                dataSourceRS.close();
            }
        } catch (Exception ex) {
            // Ignore exception message.
        }
    }

    /**
     * Read current row from the data source into string array
     * @return
     * @throws XDBDataReaderException
     */
    private String[] readLineFromResultSet() throws XDBDataReaderException {
        try {
            if (!dataSourceRS.next()) {
                return null;
            }
            String[] colsValue = new String[columns];
            for (int i = 0; i < columns; i++) {
                colsValue[i] = dataSourceRS.getString(i + 1);
            }
            return colsValue;
        } catch (Exception ex) {
            throw new XDBDataReaderException(ex);
        }
    }

    /**
     * @see java.util.concurrent.Callable#call()
     */
    public Boolean call() throws XDBDataReaderException {
        String[] rowColsValue;
        try {
            while ((rowColsValue = readLineFromResultSet()) != null) {
                loadBuffer.putRowValue(rowColsValue, getGroupByHashString());
            }
            return true;
        } catch (Exception ex) {
            throw new XDBDataReaderException(ex);
        } finally {
            close();
            if (loadBuffer != null) {
                loadBuffer.markFinished();
            }
        }
    }

    /**
     * @return a String to use for group by hashing purposes
     */
    private String getGroupByHashString() throws java.sql.SQLException {

        if (groupHashList == null || dataSourceRS.isAfterLast()) {
            return null;
        }

        StringBuffer sbHash = new StringBuffer();
        for (int element : groupHashList) {
            sbHash.append(dataSourceRS.getString(element));
        }
        return sbHash.toString();
    }
}
