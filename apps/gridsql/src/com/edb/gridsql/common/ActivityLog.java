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
 * ActivityLog.java
 *
 */

package com.edb.gridsql.common;

import org.apache.log4j.Logger;

import com.edb.gridsql.common.util.Property;
import com.edb.gridsql.engine.XDBSessionContext;

/**
 *
 * 
 */
public class ActivityLog {
    
    /** Creates a new instance of ActivityLog */
    public ActivityLog() {
    }
    
    /** grid activity logger */
    public static final Logger activityLogger = Logger.getLogger("activity");
    
    public static void setAdditivity (boolean value)
    {
        activityLogger.setAdditivity (value);   
    }
    
    public static void startRequest (long requestId, String statement)
    {
        activityLogger.info(requestId + ",B," + statement);
    }

    public static void endRequest (long requestId)
    {
        activityLogger.info(requestId + ",E");
    }

    public static void startStep (long requestId, int sourceNodeId)
    {
        activityLogger.info(requestId + ",Q," + sourceNodeId);
    }

    public static void endStep (long requestId, int sourceNodeId)
    {
        activityLogger.info(requestId + ",E," + sourceNodeId);
    }
    
    public static void startShipRows (long requestId, int sourceNodeId, int destNodeId)
    {
        activityLogger.info(requestId + ",S," + sourceNodeId + "," + destNodeId);
    }  
    
    public static void endShipRows (long requestId, int sourceNodeId, 
            int destNodeId, long numShippedRows)
    {
        activityLogger.info(requestId + ",F," + sourceNodeId + "," + destNodeId + "," + numShippedRows);
    }      
}
