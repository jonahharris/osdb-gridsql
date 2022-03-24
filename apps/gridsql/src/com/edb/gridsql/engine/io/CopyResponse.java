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
package com.edb.gridsql.engine.io;

import java.io.InputStream;
import java.io.OutputStream;

import com.edb.gridsql.common.ColumnMetaData;
import com.edb.gridsql.parser.SqlCopyData;

/**
 * Response Message returned on console COPY command (COPY FROM STDIN/COPY TO
 * STDOUT)
 * Contains a reference to SqlCopyData instance and allow to supply console
 * input or output stream to it.
 */
public class CopyResponse extends ResponseMessage {

    private SqlCopyData copyData;

    /**
     * A constructor
     * @param copyData
     */
    public CopyResponse(SqlCopyData copyData) {
        super((byte) (copyData.isCopyIn() ? MessageTypes.RESP_COPY_IN : MessageTypes.RESP_COPY_OUT), 0);
        this.copyData = copyData;
    }

    /**
     * Provide console input stream (STDIN) to SqlCopyData instance
     * @param is
     */
    public void setInputStream(InputStream is) {
        copyData.setStdIn(is);
    }

    /**
     * Provide console output stream (STDOUT) to SqlCopyData instance
     * @param os
     */
    public void setOutputStream(OutputStream os) {
        copyData.setStdOut(os);
    }

    /**
     * Provides description of columns affected by the COPY
     * @return
     */
    public ColumnMetaData[] getColumnMetaData() {
        return copyData.getColumnMeta();
    }

}
