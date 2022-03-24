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
 * SysIndexKey.java
 *
 *  
 */

package com.edb.gridsql.metadata;

/**
 * class SysIndexKey caches data from xsysindexkeys (one record)
 * 
 * 
 */
public class SysIndexKey {
    public int idxkeyid; // unique id for this index key

    public int idxid; // index id - referes to SysIndex.idxid

    public int idxkeyseq; // sequence number of this key in the index

    public int idxascdesc; // ==0 for ascending

    public int colid; // id corresponding to the SysColumn

    public String coloperator;

    public SysColumn sysColumn = null; // corresponding SysColumn object
}
