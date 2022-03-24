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
package com.edb.gridsql.parser; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.optimizer.SqlExpression;
import com.edb.gridsql.parser.handler.QueryTreeTracker;

/**
 * 
 */
public class Command {

    public static final int SELECT = 1;

    public static final int UPDATE = 2;

    public static final int INSERT = 3;

    public static final int CREATE = 4;

    public static final int DELETE = 5;

    public static final int DROP = 6;

    public static final int ALTER = 7;

    public static final int MODIFY = 8;
    
    private XDBSessionContext client;

    // Command object
    private Object commandObj;

    // command ID
    private int commandToExecute;

    // This tracks the query trees in this particular command
    private QueryTreeTracker aQueryTreeTracker;

    private ArrayList<SqlExpression> parameters;

    // Other information - which could pertain to
    public Command(int commandToExecute, Object obj,
            QueryTreeTracker aQueryTreeTracker, XDBSessionContext client) {
        this.commandToExecute = commandToExecute;
        this.commandObj = obj;
        this.aQueryTreeTracker = aQueryTreeTracker;
        this.client = client;
    }

    /**
     * This will return the command object
     * 
     * @return
     */
    public Object getCommandObj() {
        return commandObj;
    }
    
    /**
     * This will return the ID to execute
     * 
     * @return
     */
    public int getCommandToExecute() {
        return commandToExecute;
    }

    /**
     * This will return the query tree tracker
     * 
     * @return
     */
    public QueryTreeTracker getaQueryTreeTracker() {
        return aQueryTreeTracker;
    }

    /**
     * Get the client session
     */
    public XDBSessionContext getClientContext() {
        return client;
    }

    /**
     * Get the database name
     */
    public String getDBName() {
        return client.getDBName();
    }

    /**
     * @param index
     * @param sqlExpression
     */
    public void registerParameter(int index, SqlExpression sqlExpression) {
        if (parameters == null) {
            parameters = new ArrayList<SqlExpression>();
        }
        parameters.ensureCapacity(index);
        while (index > parameters.size()) {
            parameters.add(null);
        }
        parameters.set(index - 1, sqlExpression);
    }

    public SqlExpression getParameter(int index) {
        return parameters.get(index - 1);
    }

    public List<SqlExpression> getParameters() {
        return parameters == null ? null
                : Collections.unmodifiableList(parameters);
    }

    public int getParamCount() {
        return parameters == null ? 0 : parameters.size();
    }
}
