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
package com.edb.gridsql.metadata.scheduler;

import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.common.util.XLogger;

/**
 * 
 */
public class RequestCost implements Comparable {
    private static final XLogger logger = XLogger.getLogger(RequestCost.class);

    // two different RequestCost objects MUST NOT be equal
    // order number is last criteria in comparison
    private static long ORDER_NUMBER = 0;

    private long orderNumber;

    private long static_cost = 0;

    private long timeInMilliSeconds = 0;

    // This is the Sql.. Object
    private ILockCost sqlObject;

    public RequestCost(ILockCost sqlObject) {
        this.sqlObject = sqlObject;
        if (sqlObject != null) {
            static_cost = sqlObject.getCost();
        }
        timeInMilliSeconds = System.currentTimeMillis();
        orderNumber = ORDER_NUMBER++;
    }

    public ILockCost getSqlObject() {
        return sqlObject;
    }

    public long getDynamicCost() {
        // This is the cost that we get from checking the
        // locking
        return 0;
    }

    /**
     * 
     * @return
     */
    public long getTimeInQueue() {
        long milliSeconds = System.currentTimeMillis() - timeInMilliSeconds;
        return milliSeconds;
    }

    /**
     * TODO: make more sophiticated one
     * 
     * @return
     */
    public long setTotalCost() {
        return (sqlObject == null ? 0L : static_cost + getDynamicCost()
                - getTimeInQueue());
    }

    public boolean isLarge() {
        return static_cost > Props.XDB_LARGE_QUERY_COST;
    }

    /**
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        final String method = "compareTo";
        logger.entering(method, new Object[] {});
        try {

            // Same instance
            if (o == this) {
                return 0;
            }
            RequestCost other = (RequestCost) o;
            long diff = static_cost + timeInMilliSeconds - other.static_cost
                    - other.timeInMilliSeconds;
            if (diff > 0) {
                return 1;
            } else if (diff < 0) {
                return -1;
            } else {
                return orderNumber < other.orderNumber ? 1 : -1;
            }

        } finally {
            logger.exiting(method);
        }
    }
}
