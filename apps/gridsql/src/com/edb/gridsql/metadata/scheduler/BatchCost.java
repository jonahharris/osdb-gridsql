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
 * BatchCost.java
 * 
 *  
 */
package com.edb.gridsql.metadata.scheduler;

import com.edb.gridsql.metadata.SysTable;

/**
 *  
 */
public class BatchCost implements ILockCost {
    private long totalCost = 0;

    private LockSpecification<SysTable> lockSpec = new LockSpecification<SysTable>();

    /**
     * 
     */
    public BatchCost() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    public long getCost() {
        return totalCost;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs(java.lang.Object)
     */
    public LockSpecification<SysTable> getLockSpecs() {
        return lockSpec;
    }

    public void addElement(ILockCost sqlObject) {
        if (sqlObject != null) {
            totalCost += sqlObject.getCost();
            lockSpec.addAll(sqlObject.getLockSpecs());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    public boolean needCoordinatorConnection() {
        return true;
    }
}
