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
package com.edb.gridsql.engine.io;

/**
 * 
 *  
 */
public class RequestMessage extends com.edb.gridsql.engine.io.XMessage {
    // id specific to a connection, thus not a unique id for the system. (?)
    // this should be used with the connection id to determine uniqueness
    private static int nextRequestId = 0;

    public synchronized static int getNextRequestId() {
        if (nextRequestId >= Integer.MAX_VALUE) {
            System.out
                    .println("request id has reached max int value, recycling");
            nextRequestId = 0;
        }
        return ++nextRequestId;
    }

    /**
     * Reconstruct received request
     */
    public RequestMessage(byte[] header) {
        super();
        setHeaderBytes(header);
    }

    public RequestMessage(byte type, String cmd) {
        super();
        setType(type);
        setRequestId(getNextRequestId());
        storeString(cmd);
    }

    public RequestMessage(int type, String cmd) {
        this((byte) type, cmd);
    }
}
