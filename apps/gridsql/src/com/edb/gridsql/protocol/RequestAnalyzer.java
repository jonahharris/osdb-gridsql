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
 * RequestAnalyzer.java
 * 
 *  
 */
package com.edb.gridsql.protocol;

import java.util.LinkedList;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.ExecutableRequest;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.engine.io.MessageTypes;
import com.edb.gridsql.engine.io.XMessage;
import com.edb.gridsql.misc.Timer;
import com.edb.gridsql.parser.IXDBSql;
import com.edb.gridsql.parser.Parser;
import com.edb.gridsql.parser.core.ParseException;

/**
 * To convert protocol request to ExecutableRequest. Protocol
 * request has request type and optional command.
 * 
 *  
 * @see com.edb.gridsql.engine.ExecutableRequest
 */
public class RequestAnalyzer {
    private static final XLogger logger = XLogger
            .getLogger(RequestAnalyzer.class);

    // BUILD_CUT_START
    private static Timer analyzeTimer = new Timer();

    // BUILD_CUT_END

    /**
     * Convert protocol request (request type only) to
     * ExecutableRequest.
     * 
     * @return the ExecutableRequest
     * @param messageType the request type
     * @param client associated session
     * @throws ParseException never thrown
     */
    public static ExecutableRequest getExecutableRequest(int messageType,
            XDBSessionContext client) throws ParseException {
        return getExecutableRequest(messageType, null, client);
    }

    /**
     * Convert protocol request (request type and command) to
     * ExecutableRequest.
     * 
     * @return the ExecutableRequest
     * @param messageType the request type
     * @param cmd the command
     * @param client associated session
     * @throws ParseException failed to parse command
     */
    public static ExecutableRequest getExecutableRequest(int messageType,
            String cmd, XDBSessionContext client) throws ParseException {
        final String method = "getExecutableRequest";
        logger.entering(method, new Object[] { new Integer(messageType), cmd,
                client });
        // BUILD_CUT_START
        analyzeTimer.startTimer();
        // BUILD_CUT_END
        ExecutableRequest request = new ExecutableRequest(cmd);
        Parser parser = new Parser(client);
        try {
            switch (messageType) {
            case MessageTypes.REQ_BULK_INSERT_NEXT:
                parser.parseBulkInsertNext(cmd);
                break;
            case MessageTypes.REQ_BULK_INSERT_START:
                parser.parseBulkInsert(cmd);
                break;
            case MessageTypes.REQ_METADATA:
                parser.parseAddDropNode(cmd);
                break;
            case MessageTypes.REQ_CLOSE_RESULTSET:
                parser.parseCloseResultSet(cmd);
                break;
            case MessageTypes.REQ_BATCH_EXEC:
                String templateStart = "";
                String templateEnd = "";
                int currentPos = 0;
                int endPos = cmd.indexOf(XMessage.ARGS_DELIMITER);
                if (cmd.startsWith("TEMPLATE_START:")) {
                    templateStart = cmd.substring(15, endPos);
                    currentPos = endPos + XMessage.ARGS_DELIMITER.length();
                    endPos = cmd.indexOf(XMessage.ARGS_DELIMITER, currentPos);
                    if (cmd.startsWith("TEMPLATE_END:", currentPos)) {
                        templateStart = cmd.substring(currentPos + 13, endPos);
                        currentPos = endPos + XMessage.ARGS_DELIMITER.length();
                        endPos = cmd.indexOf(XMessage.ARGS_DELIMITER,
                                currentPos);
                    }
                }
                LinkedList<IXDBSql> subRequests = new LinkedList<IXDBSql>();
                while (endPos >= currentPos) {
                    parser.parseStatement(templateStart
                            + cmd.substring(currentPos, endPos) + templateEnd);
                    subRequests.add(parser.getSqlObject());
                    currentPos = endPos + XMessage.ARGS_DELIMITER.length();
                    endPos = cmd.indexOf(XMessage.ARGS_DELIMITER, currentPos);
                }
                parser.parseStatement(templateStart + cmd.substring(currentPos)
                        + templateEnd);
                subRequests.add(parser.getSqlObject());
                request.setSubRequests(subRequests
                        .toArray(new IXDBSql[subRequests.size()]));
                return request;
            default:
                if (cmd != null) {
                    // try and extract fetch size
                    int pos = cmd.lastIndexOf(XMessage.ARGS_DELIMITER);
                    if (pos > 0) {
                        try {
                            int fetchSize = Integer.parseInt(cmd.substring(pos
                                    + XMessage.ARGS_DELIMITER.length()));
                            request.setFetchSize(fetchSize);
                            cmd = cmd.substring(0, pos);
                        } catch (NumberFormatException nfe) {
                            // Ignore error, use default
                        }
                    }
                    parser.parseStatement(cmd);
                }
            }
            IXDBSql sqlObject = parser.getSqlObject();
            request.setSQLObject(sqlObject);
            return request;

        } finally {
            // BUILD_CUT_START
            analyzeTimer.stopTimer();
            // BUILD_CUT_END
            logger.exiting(method);
        }
    }
}
