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
 * IMetaDataUpdate.java
 *
 *  
 */

package com.edb.gridsql.metadata;

import com.edb.gridsql.engine.XDBSessionContext;

/**
 * Interface IMetaDataUpdate provides the methods that that are called to update
 * the MetaData DB and refresh the cache from
 * Engine.executeDDLOnMultipleNodes().
 * 
 * RESTRICTIONS - Never call beginTransaction(), commitTransaction() or
 * rollbackTransaction() on MetaData, from callses implementing this interface.
 * 
 * This interface has been created solely for executing DDL commands on MetaData
 * DB.
 * 
 */
public interface IMetaDataUpdate {
    public void execute(XDBSessionContext client) throws Exception;

    public void refresh() throws Exception;
}
