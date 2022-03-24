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
 * BatchInsertGroup.java
 * 
 *  
 */
package com.edb.gridsql.engine;

import java.util.LinkedList;
import java.util.List;

import com.edb.gridsql.parser.SqlModifyTable;

/**
 *  
 */
public class BatchInsertGroup {
    private static final int GROUP_STATUS_CREATED = 0;

    private static final int GROUP_STATUS_EXECUTED = 1;

    private static final int GROUP_STATUS_FAILED = 2;

    private List<SqlModifyTable> members;

    private SqlModifyTable master;

    private int status;

    /**
     * 
     * @param master 
     */
    public BatchInsertGroup(SqlModifyTable master) {
        this.master = master;
        members = new LinkedList<SqlModifyTable>();
        members.add(master);
        status = GROUP_STATUS_CREATED;
    }

    /**
     * 
     * @param member 
     */
    public void addMember(SqlModifyTable member) {
        members.add(member);
    }

    /**
     * 
     * @param insert 
     * @return 
     */
    public boolean isMaster(SqlModifyTable insert) {
        return insert == master;
    }

    /**
     * 
     * @return 
     */
    public SqlModifyTable getMaster() {
        return master;
    }

    /**
     * 
     * @return 
     */
    public List<SqlModifyTable> getMembers() {
        return members;
    }

    /**
     * 
     * @return 
     */
    public boolean executed() {
        return status == GROUP_STATUS_EXECUTED || status == GROUP_STATUS_FAILED;
    }

    public void setExecuted() {
        status = GROUP_STATUS_EXECUTED;
    }

    /**
     * 
     * @return 
     */
    public boolean failed() {
        return status == GROUP_STATUS_FAILED;
    }

    public void setFailed() {
        status = GROUP_STATUS_FAILED;
    }
}
