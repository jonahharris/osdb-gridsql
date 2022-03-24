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
 * 
 *
 */
package com.edb.gridsql.metadata;

import java.util.HashSet;

import com.edb.gridsql.common.util.XLogger;


public class SysUser {
    private static final XLogger logger = XLogger.getLogger(SysUser.class);

    private int id;

    private SysLogin login;

    private SysDatabase database;

    private HashSet ownerOf;

    private HashSet permissionsOn;

    /**
     * 
     */
    public SysUser(int id, SysLogin login, SysDatabase database) {
        this.id = id;
        this.login = login;
        this.database = database;
        ownerOf = new HashSet();
        permissionsOn = new HashSet();
    }

    public int getUserID() {
        return id;
    }

    void setUserID(int userID) {
        if (id == -1) {
            id = userID;
        }
    }

    public SysLogin getLogin() {
        return login;
    }

    public String getName() {
        return login.getName();
    }

    public int getUserClass() {
        return login.getUserClass();
    }

    void addOwned(Object obj) {
        ownerOf.add(obj);
    }

    void removeOwned(Object obj) {
        ownerOf.remove(obj);
    }

    public String getOwnedStr() {
        if (ownerOf.isEmpty()) {
            return null;
        }
        StringBuffer owners = new StringBuffer();
        for (Object obj : ownerOf) {
            owners.append(obj).append(", ");
        }
        return owners.substring(0, owners.length() - 2);
    }

    void addGranted(Object obj) {
        permissionsOn.add(obj);
    }

    void removeGranted(Object obj) {
        permissionsOn.remove(obj);
    }

    public String getGrantedStr() {
        if (permissionsOn.isEmpty()) {
            return null;
        }
        StringBuffer permissions = new StringBuffer();
        for (Object obj : permissionsOn) {
            permissions.append(obj).append(", ");
        }
        return permissions.substring(0, permissions.length() - 2);
    }

    @Override
    public String toString() {
        return login + "@" + database.getDbname();
    }
}
