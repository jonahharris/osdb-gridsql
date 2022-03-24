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
 * SortedVector.java
 *
 *
 */

package com.edb.gridsql.misc;

import java.util.*;

/**
 * Quick and dirty class for keeping things sorted in a Vector Note that it
 * sorts in descending order (biggest first)
 */
public class SortedVector extends Vector {

    /**
     * 
     */
    private static final long serialVersionUID = -8588925912565673414L;
    private Vector keyVector;

    /** Creates a new instance of SortedVector */
    public SortedVector() {
        super();

        keyVector = new Vector();
    }

    // addElement, but with a key.
    // This Vector will contain a small number of elements,
    // so just iterate to find position.

    public void blah() {

    }

    public synchronized void addElement(int key, Object anObject) {
        int i;

        for (i = 0; i < keyVector.size(); i++) {
            if (key < ((Integer) keyVector.elementAt(i)).intValue()) {
                // i--;
                break;
            }
        }

        keyVector.insertElementAt(new Integer(key), i);

        super.insertElementAt(anObject, i);
    }

    // Get the key for the specified postion
    public int getKeyAt(int position) {
        return ((Integer) keyVector.elementAt(position)).intValue();
    }

}
