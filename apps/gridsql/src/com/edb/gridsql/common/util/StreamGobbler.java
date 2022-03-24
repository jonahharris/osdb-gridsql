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
package com.edb.gridsql.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Level;

/**
 *
 *         should read from external process' std out or std err in separate
 *         thread to workaround hanging issue on some platforms. Output can be
 *         optionally redirected to other stream
 */
public class StreamGobbler extends Thread {
    private BufferedReader in;
    private XLogger logger;
    private Level level;

    /**
     *
     */
    public StreamGobbler(InputStream input, XLogger logger, Level level)
            throws IOException {
        // Note: we do not use BufferedInputReader bacause it may have problem
        // if input stream does not have end of line character
        in = new BufferedReader(new InputStreamReader(input));
        this.level = level == null ? Level.INFO : level;
        if (logger != null && logger.isEnabledFor(level)) {
            this.logger = logger;
        }
    }

    @Override
    public void run() {
        try {
            try {
                String read;
                while ((read = in.readLine()) != null) {
                    if (logger != null) {
                        logger.log(level, read, null);
                    }
                }
                in.close();
            } finally {
                in.close();
            }
        } catch (IOException ignore) {
        }
    }
}
