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
 * AbstractConstraintChecker.java
 * 
 *  
 */
package com.edb.gridsql.constraintchecker;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.edb.gridsql.common.util.XLogger;
import com.edb.gridsql.engine.Engine;
import com.edb.gridsql.engine.ExecutionResult;
import com.edb.gridsql.engine.IExecutable;
import com.edb.gridsql.engine.XDBSessionContext;
import com.edb.gridsql.exception.XDBServerException;
import com.edb.gridsql.metadata.SysColumn;
import com.edb.gridsql.metadata.SysTable;
import com.edb.gridsql.metadata.scheduler.ILockCost;
import com.edb.gridsql.metadata.scheduler.LockSpecification;

/**
 *  
 */
public abstract class AbstractConstraintChecker implements IConstraintChecker,
        ILockCost {
    private static final XLogger logger = XLogger
            .getLogger(AbstractConstraintChecker.class);

    protected static final int VIOLATE_IF_EMPTY = 1;

    protected static final int VIOLATE_IF_NOT_EMPTY = 2;

    protected XDBSessionContext client;

    protected SysTable targetTable;

    protected SysTable tempTable;

    private Collection keysToCheck;

    private Map<IExecutable, ViolationCriteria> validateSQL;

    /**
     * 
     * @param targetTable
     * @param client
     */
    public AbstractConstraintChecker(SysTable targetTable,
            XDBSessionContext client) {
        this.client = client;
        this.targetTable = targetTable;
    }

    /**
     * 
     * @param columnsInvolved
     * @return
     */
    public final Collection<SysColumn> scanConstraints(
            Collection<SysColumn> columnsInvolved) {
        keysToCheck = new LinkedList();
        return scanConstraints(columnsInvolved, keysToCheck);
    }

    /**
     * 
     * @return
     * @param columnsInvolved
     * @param keysToCheck
     */
    protected abstract Collection<SysColumn> scanConstraints(
            Collection<SysColumn> columnsInvolved, Collection keysToCheck);

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.ConstraintChecker.IConstraintChecker#setTempTable(com.edb.gridsql..MetaData.SysTable)
     */
    /**
     * 
     * @param tempTable
     */
    public void setTempTable(SysTable tempTable) {
        this.tempTable = tempTable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Engine.IPreparable#isPrepared()
     */
    /**
     * 
     * @return
     */
    public boolean isPrepared() {
        return validateSQL != null;
    }

    /**
     * 
     * @throws java.lang.Exception
     */
    public void prepare() throws Exception {
        final String method = "prepare";
        logger.entering(method, new Object[] {});
        try {

            Collection<SysColumn> empty = Collections.emptyList();
            if (keysToCheck == null) {
                scanConstraints(empty);
            }
            validateSQL = new HashMap<IExecutable, ViolationCriteria>();
            try {
                for (Iterator it = keysToCheck.iterator(); it.hasNext();) {
                    validateSQL.putAll(prepareConstraint(it.next()));
                }
            } catch (Exception e) {
                logger.catching(e);
                validateSQL = null;
                logger.throwing(e);
                throw e;
            }
        } finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     * @param constraint
     * @throws java.lang.Exception
     * @return
     */
    protected abstract Map<IExecutable, ViolationCriteria> prepareConstraint(
            Object constraint) throws Exception;

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.Engine.IExecutable#execute(com.edb.gridsql..Engine.Engine)
     */
    /**
     * 
     * @param engine
     * @throws java.lang.Exception
     * @return
     */
    public ExecutionResult execute(Engine engine) throws Exception {
        final String method = "execute";
        logger.entering(method, new Object[] {});
        try {

            if (!isPrepared()) {
                prepare();
            }
            for (Object element : validateSQL.entrySet()) {
                Map.Entry entry = (Map.Entry) element;
                IExecutable query = (IExecutable) entry.getKey();
                ViolationCriteria criteria = (ViolationCriteria) entry
                        .getValue();
                checkViolation(engine, query, criteria);
            }
            return getReport();

        } finally {
            logger.exiting(method);
        }
    }

    /**
     * 
     * @param engine
     * @param query
     * @param criteria
     * @throws java.lang.Exception
     */
    protected void checkViolation(Engine engine, IExecutable query,
            ViolationCriteria criteria) throws Exception {
        boolean violation = true;
        ResultSet rs = query.execute(engine).getResultSet();
        try {
            boolean hasRows = rs.next();
            if (criteria.violationType == VIOLATE_IF_EMPTY) {
                violation = !hasRows;
            } else if (criteria.violationType == VIOLATE_IF_NOT_EMPTY) {
                violation = hasRows;
            }

        } finally {
            rs.close();
        }
        if (violation) {
            throw new XDBServerException("Constraint violation: "
                    + criteria.message);
        }
    }

    /**
     * 
     * @throws java.lang.Exception
     * @return
     */
    protected ExecutionResult getReport() throws Exception {
        return null;
    }

    /**
     * 
     * @return
     */
    public boolean isEmpty() {
        return keysToCheck == null || keysToCheck.isEmpty();
    }

    protected class ViolationCriteria {
        public int violationType;

        public String message;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getCost()
     */
    /**
     * 
     * @return
     */
    public long getCost() {
        final String method = "getCost";
        logger.entering(method, new Object[] {});
        try {

            try {
                if (!isPrepared()) {
                    prepare();
                }
            } catch (Exception e) {
                logger.catching(e);
            }
            long cost = LOW_COST;
            for (Object query : validateSQL.keySet()) {

                if (query instanceof ILockCost) {
                    cost += ((ILockCost) query).getCost();
                }
            }
            return cost;

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#getLockSpecs(java.lang.Object)
     */
    /**
     * 
     * @return
     */
    public LockSpecification<SysTable> getLockSpecs() {
        final String method = "getLockSpecs";
        logger.entering(method);
        try {

            try {
                if (!isPrepared()) {
                    prepare();
                }
            } catch (Exception e) {
                logger.catching(e);
            }
            LockSpecification<SysTable> lSpec = new LockSpecification<SysTable>();
            for (Object query : validateSQL.keySet()) {

                if (query instanceof ILockCost) {
                    lSpec.addAll(((ILockCost) query).getLockSpecs());
                }
            }
            return lSpec;

        } finally {
            logger.exiting(method);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.edb.gridsql.MetaData.Scheduler.ILockCost#needCoordinatorConnection()
     */
    /**
     * 
     * @return
     */
    public boolean needCoordinatorConnection() {
        return true;
    }
}
