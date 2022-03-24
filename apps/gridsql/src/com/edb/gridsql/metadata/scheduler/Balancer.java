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
 * Balancer.java
 *
 *  
 */

package com.edb.gridsql.metadata.scheduler;

import com.edb.gridsql.metadata.DBNode;
import com.edb.gridsql.metadata.SysDatabase;

import java.util.Vector;

/**
 * 
 * 
 * 
 * We just use round-robin for now. Now that the rest of the work has been done,
 * we can change it later to least pending requests. Not sure if it makes sense
 * to tie this to the DB. It should probably be system wide, but I wanted to
 * keep it together with the Scheduler, in case it is more tightly integrated.
 */
public class Balancer {

    Vector nodeList;

    /** Creates a new instance of Balancer */
    public Balancer(SysDatabase sysDatabase) {

        nodeList = new Vector(sysDatabase.getDBNodeList());
    }

    /** Get next node id to use */
    public int getNextNodeId() {

        DBNode aDBNode = (DBNode) nodeList.remove(0);
        nodeList.add(aDBNode);

        return aDBNode.getNodeId();
    }
}
