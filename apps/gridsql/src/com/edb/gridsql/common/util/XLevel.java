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
 * XLevel.java
 *
 *  
 */

package com.edb.gridsql.common.util;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;

/**
 * define XDB specific debug levels
 * 
 *  
 */
public class XLevel extends org.apache.log4j.Level {
    /*
     * syslogEquivalent - Debug levels in the standard UNIX applications go like
     * this: 0: no debug info 1: more 2: even more 3: even MORE 4: even *MORE*
     * 5: lots... [...] 9: gadzillions...
     */
    public static final String TRACE_STR = "TRACE";

    public static final int TRACE_INT = Priority.DEBUG_INT - 100;

    public static final XLevel TRACE = new XLevel(TRACE_INT, TRACE_STR, 7);

    public static final String SUPERTRACE_STR = "SUPERTRACE";

    public static final int SUPERTRACE_INT = Priority.DEBUG_INT - 150;

    public static final XLevel SUPERTRACE = new XLevel(SUPERTRACE_INT,
            SUPERTRACE_STR, 9);

    protected XLevel(int level, String strLevel, int syslogEquivalent) {
        super(level, strLevel, syslogEquivalent);
    }

    public static Level toLevel(final String name, final Level defaultLevel) {
        if (name == null) {
            return defaultLevel;
        }

        String upper = name.toUpperCase();
        if (upper.equals(TRACE_STR)) {
            return TRACE;
        }
        if (upper.equals(SUPERTRACE_STR)) {
            return SUPERTRACE;
        }
        return Level.toLevel(name, defaultLevel);
    }

    public static Level toLevel(final String name) {
        return Level.toLevel(name, SUPERTRACE);
    }

    public static Level toLevel(final int i, final Level defaultLevel) {
        Level p;
        if (i == TRACE_INT) {
            p = TRACE;
        } else if (i == SUPERTRACE_INT) {
            p = SUPERTRACE;
        } else {
            p = Level.toLevel(i);
        }
        return (p == null) ? defaultLevel : p;
    }
}
