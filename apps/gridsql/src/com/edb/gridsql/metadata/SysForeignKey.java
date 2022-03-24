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
 * SysForeignKey.java
 *
 *  
 */

package com.edb.gridsql.metadata;

import java.util.Enumeration;

/**
 * class SysForeignKeys caches data from xsysforeignkeys (one record)
 * 
 * 
 */

public class SysForeignKey {
    public int fkeyid;

    public int refid;

    public int fkeyseq;

    public int colid;

    public int refcolid;

    // Sends back the refering . syscolummn
    public SysColumn getReferringSysColumn(SysDatabase database) {
        for (Enumeration e = database.getAllTables(); e.hasMoreElements();) {
            SysTable table = (SysTable) e.nextElement();
            SysColumn col = table.getSysColumn(colid);
            if (col != null) {
                return col;
            }
        }
        return null;
    }

    // Sends back the referenced syscolumn
    public SysColumn getReferencedSysColumn(SysDatabase database) {
        for (Enumeration e = database.getAllTables(); e.hasMoreElements();) {
            SysTable table = (SysTable) e.nextElement();
            SysColumn col = table.getSysColumn(refcolid);
            if (col != null) {
                return col;
            }
        }
        return null;
    }

    // Get the column ID
    public int getColid() {
        return colid;
    }

    public int getRefcolid() {
        return refcolid;
    }

}