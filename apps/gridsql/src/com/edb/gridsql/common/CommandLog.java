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
 * commandLog.java
 *
 *  
 */

package com.edb.gridsql.common;

import org.apache.log4j.Logger;

import com.edb.gridsql.common.util.Property;
import com.edb.gridsql.engine.XDBSessionContext;

/**
 * 
 * Just puts command-related loggers in one place
 */
public class CommandLog {

    /** logs SELECT statements */
    public static final Logger queryLogger = Logger.getLogger("query");

    /** logs other commands */
    public static final Logger cmdLogger = Logger.getLogger("command");

    /** logs long SELECT statements */
    public static final Logger longQueryLogger = Logger.getLogger("longquery");

    public static final long longQuerySeconds = Property.getLong(
            "xdb.longQuerySeconds", 300);

    /** Creates a new instance of commandLog */
    public CommandLog() {
    }

    /**
     * 
     * @param value 
     */
    public static void setAdditivity(boolean value) {
        queryLogger.setAdditivity(value);
        cmdLogger.setAdditivity(value);
        longQueryLogger.setAdditivity(value);
    }

    /**
     * 
     * @param cmd 
     * @param duration 
     * @param client 
     */
    public static void checkLongQuery(String cmd, long duration,
            XDBSessionContext client) {
        if (duration >= longQuerySeconds) {
            longQueryLogger.info("Session: " + client.getSessionID()
                    + "; Time: " + duration + "s; Command: " + cmd);
        }
    }
}
