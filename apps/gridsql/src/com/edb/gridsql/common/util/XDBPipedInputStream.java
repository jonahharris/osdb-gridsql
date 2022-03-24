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

package com.edb.gridsql.common.util;

/**
 * Puropose:
 *
 * The main purpose of this class is to let us create a PipedInputStream instance with *custome size*;
 * It will be useful when EDBWriter instance wants to load data (into some database table) using EDB JDBC
 * copy command.
 *
 * 
 */
import java.io.*;

public class XDBPipedInputStream extends PipedInputStream {

    public XDBPipedInputStream(int bufferSize) {
        super();
        buffer = new byte[bufferSize];
    }

    public XDBPipedInputStream(PipedOutputStream out, int bufferSize) throws IOException {
        super(out);
        buffer = new byte[bufferSize];
    }

    public int getLength() {
        return in + 1;
    }
}    
    
