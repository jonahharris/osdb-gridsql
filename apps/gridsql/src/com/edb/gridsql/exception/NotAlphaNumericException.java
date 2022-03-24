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
package com.edb.gridsql.exception;

import com.edb.gridsql.optimizer.SqlExpression;

/**
 * 
 */
public class NotAlphaNumericException extends XDBServerException {
    /**
     * 
     */
    private static final long serialVersionUID = 1101122668936886445L;

    /**
     * 
     * @param aSqlExpression 
     */
    public NotAlphaNumericException(SqlExpression aSqlExpression) {
        super(ErrorMessageRepository.EXPRESSION_NOT_ALPHANUMERIC
                + aSqlExpression.rebuildString(), 0,
                ErrorMessageRepository.EXPRESSION_NOT_ALPHANUMERIC_CODE);
    }

    /**
     * 
     * @param aParameter 
     * @param function 
     */
    public NotAlphaNumericException(SqlExpression aParameter,
            SqlExpression function) {

        super(ErrorMessageRepository.EXPRESSION_NOT_ALPHANUMERIC + " ( "
                + aParameter.rebuildString() + ", " + function.rebuildString()
                + " )", 0,
                ErrorMessageRepository.EXPRESSION_NOT_ALPHANUMERIC_CODE);
    }

}
