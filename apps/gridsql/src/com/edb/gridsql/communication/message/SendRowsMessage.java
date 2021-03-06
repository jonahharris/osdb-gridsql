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
 * SendRowsMessage.java
 *
 */

package com.edb.gridsql.communication.message;

/**
 *
 * 
 */
public class SendRowsMessage extends NodeMessage {
    
    /** Parameterless constructor required for serialization */
    public SendRowsMessage() {
    }

    //private static final long serialVersionUID = 2294439851011666205L;

    private boolean isStartMessage = true;
    
    private long numRowsSent = 0;
    
    private int destNodeForRows = -1;

    /**
     * @param messageType
     */
    protected SendRowsMessage(int messageType)
    {
        super(messageType);
    }
   
    public void setIsStartMessage(boolean value)
    {
        isStartMessage = value;
    }  
    
    public boolean getIsStartMessage()
    {
        return isStartMessage;
    }    
    
    public void setNumRowsSent(long value)
    {
        numRowsSent = value;
    }  
    
    public long getNumRowsSent()
    {
        return numRowsSent;
    }       
    
    public void setDestNodeForRows(int value)
    {
        destNodeForRows = value;
    }  
    
    public int getDestNodeForRows()
    {
        return destNodeForRows;
    }      
}

