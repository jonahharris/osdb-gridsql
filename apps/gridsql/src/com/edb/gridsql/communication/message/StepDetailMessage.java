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
package com.edb.gridsql.communication.message;

import com.edb.gridsql.planner.StepDetail;

/**
 *
 *
 */
public class StepDetailMessage extends NodeMessage {
    private static final long serialVersionUID = -1816110899868328424L;

    /**
     * Non-null only when msgType = MSG_EXECUTE_STEP
     */
    private StepDetail aStepDetail;

    /** Parameterless constructor required for serialization */
    public StepDetailMessage() {
    }

    /**
     * @param messageType
     */
    protected StepDetailMessage(int messageType) {
        super(messageType);
    }

    @Override
    public void setStepDetail(StepDetail stepDetails) {
        aStepDetail = stepDetails;
        if (targetNodeIDs == null) {
            setTargetFromSD();
        }
    }

    @Override
    public StepDetail getStepDetail() {
        return aStepDetail;
    }

    /**
     * Setter for property targetNodeID.
     *
     * @param targetNodeID
     *            New value of property targetNodeID.
     */
    @Override
    public void setTargetNodeID(Integer targetNodeID) {
        if (targetNodeID == null) {
            setTargetFromSD();
        } else {
            super.setTargetNodeID(targetNodeID);
        }
    }

    private void setTargetFromSD() {
        if (aStepDetail == null) {
            targetNodeIDs = null;
        } else {
            boolean addCoord = aStepDetail.getDestType() == StepDetail.DEST_TYPE_BROADCAST_AND_COORD
                    || aStepDetail.combineOnCoordFirst;
            if (aStepDetail.consumerNodeList == null) {
                targetNodeIDs = addCoord ? new Integer[] { new Integer(0) }
                        : null;
            } else {
                targetNodeIDs = new Integer[aStepDetail.consumerNodeList.size()
                        + (addCoord ? 1 : 0)];
                aStepDetail.consumerNodeList.toArray(targetNodeIDs);
                if (addCoord) {
                    targetNodeIDs[targetNodeIDs.length - 1] = new Integer(0);
                }
            }
        }
    }
}
