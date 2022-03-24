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
/**
 *
 */
package com.edb.gridsql.engine.loader;

import java.io.IOException;
import java.util.Map;

import com.edb.gridsql.common.util.Props;
import com.edb.gridsql.engine.loader.Loader.DATA_SOURCE;
import com.edb.gridsql.metadata.NodeDBConnectionInfo;

/**
 * Derived from PgWriterFactory
 */
public class PostgresWriterFactory implements INodeWriterFactory {
    private NodeDBConnectionInfo[] nodeDBConnectionInfos;

    private String template;

    private Map<String,String> params;

    /**
     *
     */
    public PostgresWriterFactory() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Util.loader.INodeWriterFactory#createWriter(int,
     *      java.lang.String, boolean)
     */
    public INodeWriter createWriter(int nodeID) throws IOException {
        if (nodeDBConnectionInfos == null) {
            throw new IOException(
                    "Can not create Writer: no database connection info");
        }
        for (NodeDBConnectionInfo element : nodeDBConnectionInfos) {
            if (element.getNodeID() == nodeID) {
                return new PostgresWriter(element, template, params);
            }
        }
        throw new IOException(
                "Can not create Writer: no database connection info for Node "
                        + nodeID);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Util.loader.INodeWriterFactory#close()
     */
    public void close() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Util.loader.INodeWriterFactory#getNodeConnectionInfos()
     */
    public NodeDBConnectionInfo[] getNodeConnectionInfos() {
        return nodeDBConnectionInfos;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.edb.gridsql.Util.loader.INodeWriterFactory#setNodeConnectionInfos(com.edb.gridsql..MetaData.NodeDBConnectionInfo[])
     */
    public void setNodeConnectionInfos(
            NodeDBConnectionInfo[] nodeDBConnectionInfos) {
        this.nodeDBConnectionInfos = nodeDBConnectionInfos;
    }

    /* (non-Javadoc)
     * @see com.edb.gridsql.engine.loader.INodeWriterFactory#setParams(java.util.Map)
     */
    public void setParams(DATA_SOURCE ds, Map<String, String> params) {
        if (ds == DATA_SOURCE.CSV) {
            template = Props.XDB_LOADER_EDBWRITER_CSV_TEMPLATE;
        } else {
            template = Props.XDB_LOADER_EDBWRITER_TEMPLATE;
        }
        this.params = params;
    }
}
