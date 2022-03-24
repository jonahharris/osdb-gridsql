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
 */
package com.edb.gridsql.engine;

import com.edb.gridsql.communication.message.NodeMessage;
import com.edb.gridsql.exception.XDBServerException;

/**
 *  
 * 
 */
public class NodeThreadPool extends ObjectPool<NodeThread> {
    private int nodeID;

    /**
     * 
     * @param nodeID 
     * @param minSize 
     * @param maxSize 
     */
    public NodeThreadPool(int nodeID, int minSize, int maxSize) {
        super(minSize, maxSize);
        this.nodeID = nodeID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.server.ObjectPool#createEntry()
     */
    /**

     * 

     * @throws com.edb.gridsql.exception.XDBServerException 

     * @return 

     */

    @Override
    protected NodeThread createEntry() throws XDBServerException {
        NodeThread nt = new NodeThread(nodeID);
        new Thread(nt).start();
        return nt;
    }

    /**
     * 
     * @param entry 
     * @see com.edb.gridsql.server.ObjectPool#destroyEntry(java.lang.Object)
     */

    @Override
    protected void destroyEntry(NodeThread entry) {
        entry.processMessage(NodeMessage
                .getNodeMessage(NodeMessage.MSG_STOP_THREAD));
    }

    /**
     * 
     * @throws com.edb.gridsql.exception.XDBServerException 
     * @return 
     */

    public NodeThread getNodeThread() throws XDBServerException {
        return getObject();
    }

    /**
     * 
     * @param thread 
     */

    public void releaseNodeThread(NodeThread thread) {
        if (thread.isAlive()) {
            releaseObject(thread);
        } else {
            // If thread is not alive no need to stop it - it is already stopped
            destroyObject(thread, false);
        }
    }
}
