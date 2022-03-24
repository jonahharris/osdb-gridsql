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
import com.edb.gridsql.parser.ExpressionType;

/**
 * 
 * 
 */
public class InvalidExpressionException extends XDBServerException {
    /**
     * 
     */
    private static final long serialVersionUID = 3461795006207253787L;

    /**
     * 
     * @param left 
     * @param right 
     * @param Operator 
     */
    public InvalidExpressionException(SqlExpression left, SqlExpression right,
            String Operator) {
        super(ErrorMessageRepository.INVALID_EXPRESSION + " ( "
                + left.rebuildString() + ", " + Operator + " "
                + right.rebuildString() + " )", 0,
                ErrorMessageRepository.INVALID_EXPRESSION_CODE);
    }

    /**
     * 
     * @param leftType 
     * @param operator 
     * @param rightType 
     */
    public InvalidExpressionException(ExpressionType leftType, String operator,
            ExpressionType rightType) {
        super(ErrorMessageRepository.INVALID_EXPRESSION + "( "
                + leftType.getTypeString() + " , " + operator + "  "
                + rightType.getTypeString(), 0,
                ErrorMessageRepository.INVALID_EXPRESSION_CODE);
    }
}
