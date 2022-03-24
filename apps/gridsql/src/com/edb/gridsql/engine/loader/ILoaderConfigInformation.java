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

package com.edb.gridsql.engine.loader;

import java.util.List;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.partitions.PartitionMap;
import com.edb.gridsql.parser.ExpressionType;

/**
 * Puropose:
 *
 * The main purpose of this interface is to able to easily obtain
 * data loader's configuration information so that we can launch mutiple data-processor threads
 * with the same configuration settings.
 *
 * (for further information see DataProcessorThread class).
 *
 *
 */
public interface ILoaderConfigInformation {

    public Loader               getLoaderInstance() ;
    public XDBSessionContext    getClient() ;
    public PartitionMap         getPartitionMap() ;
    public SysTable             getTableInformation() ;
    public List<TableColumnDescription> getTableColumnsInformation() ;
    public INodeWriterFactory   getWriterFactory() ;
    public IUniqueValueProvider getRowIdProvider() ;
    public IUniqueValueProvider getSerialProvider() ;

    public char getSeparator() ;

    public boolean noParse() ;
    public boolean destinationTypeNodeId() ;
    public boolean suppressSendingNodeId() ;

    public int getTableColumnsCount() ;
    public int getSerialColumnPosition() ;
    public int getSerialColumnSequence() ;
    public int getPartitionColumnSequence() ;
    public ExpressionType getHashDataType() ;
    public int getXRowidColumnSequence() ;

    public String getNULLValue();
    public char getQuoteChar();
    public char getQuoteEscape();
}
