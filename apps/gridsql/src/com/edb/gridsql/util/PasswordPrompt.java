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
package com.edb.gridsql.util;

import java.io.IOException;

/**
 * 
 * 
 */
public class PasswordPrompt implements Runnable {

    // Quick test
    /**
     * 
     * @param args 
     * @throws java.lang.Exception 
     */
    public static void main(String[] args) throws Exception {
        String password = getPassword("Password: ");
        System.out.println("Password entered: \"" + password + "\"");
        password = getPassword("Password: ");
        System.out.println("Password entered: \"" + password + "\"");
        password = getPassword("Password: ");
        System.out.println("Password entered: \"" + password + "\"");
    }

    /**
     * 
     * @param prompt 
     * @throws java.io.IOException 
     * @return 
     */
    public static String getPassword(String prompt) throws IOException {
    	String lineEnd = System.getProperty("line.separator");
        System.err.print(prompt);
        StringBuffer sb = new StringBuffer(32);
        PasswordPrompt instance = new PasswordPrompt();
        new Thread(instance).start();
        try {
            while (true) {
                int c = System.in.read();
                if (c == -1) {
                    return sb.toString();
                } else {
                    sb.append((char) c);
                    if (sb.toString().endsWith(lineEnd)) {
                    	return sb.substring(0, sb.length() - lineEnd.length());
                    }
                }
            }
        } finally {
            instance.stop();
        }
    }

    private volatile boolean stop = false;

    /**
     * 
     */
    private PasswordPrompt() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */

    public void run() {
        int priority = Thread.currentThread().getPriority();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        try {
            while (!stop) {
                System.err.print("\010 ");
                System.err.flush();
                try {
                    // attempt masking at this rate
                    Thread.sleep(1);
                } catch (InterruptedException ignore) {
                }
            }
            System.out.print("\010");
        } finally {
            // restore the original priority
            Thread.currentThread().setPriority(priority);
        }
    }

    private void stop() {
        stop = true;
    }
}
