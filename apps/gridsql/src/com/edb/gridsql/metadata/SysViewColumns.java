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
package com.edb.gridsql.metadata;

import com.edb.gridsql.common.util.XLogger;

public class SysViewColumns {
    private static final XLogger logger = XLogger
            .getLogger(SysViewColumns.class);

    private int viewColumnid;

    private int viewColSeqno;

    private String viewColumn;

    private int coltype;

    private int collength;

    private int colscale;

    private int colprecision;

    // ----------------------------------------------------------------
    public SysViewColumns(int aViewColumnID, int aViewID, int aViewColSeqno,
            String aViewColumn, int colType, int colLength, int colScale,
            int colPrecision) {
        this.viewColumnid = aViewColumnID;
        this.viewColSeqno = aViewColSeqno;
        this.viewColumn = aViewColumn;
        this.coltype = colType;
        this.collength = colLength;
        this.colscale = colScale;
        this.colprecision = colPrecision;

    }

    /**
     * @return Returns the viewColumn.
     */
    public String getViewColumn() {
        return viewColumn;
    }

    /**
     * @return Returns the viewColumnid.
     */
    public int getViewColumnid() {
        return viewColumnid;
    }

    /**
     * @return Returns the viewColSeqno.
     */
    public int getViewColSeqno() {
        return viewColSeqno;
    }

    public int getCollength() {
        return collength;
    }

    public void setCollength(int collength) {
        this.collength = collength;
    }

    public int getColprecision() {
        return colprecision;
    }

    public void setColprecision(int colprecision) {
        this.colprecision = colprecision;
    }

    public int getColscale() {
        return colscale;
    }

    public void setColscale(int colscale) {
        this.colscale = colscale;
    }

    public int getColtype() {
        return coltype;
    }

    public void setColtype(int coltype) {
        this.coltype = coltype;
    }
}