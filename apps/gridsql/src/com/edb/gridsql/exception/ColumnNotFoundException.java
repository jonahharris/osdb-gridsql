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
 * PM 
 */
public class ColumnNotFoundException extends XDBServerException {
    /**
     * 
     */
    private static final long serialVersionUID = 5041676296080719907L;

    String columnName, tableName;

    SqlExpression aAttributeColumnExpression;

    public ColumnNotFoundException(SqlExpression attributeColumnExpression) {
        super(ErrorMessageRepository.UNKOWN_COLUMN_NAME,
                XDBServerException.SEVERITY_LOW,
                ErrorMessageRepository.UNKOWN_COLUMN_NAME_CODE);
        aAttributeColumnExpression = attributeColumnExpression;
        columnName = aAttributeColumnExpression.getColumn().columnName;
        tableName = aAttributeColumnExpression.getColumn().getTableAlias();
    }

    /**
     * 
     * @param columnName 
     * @param tableName 
     */
    public ColumnNotFoundException(String columnName, String tableName) {
        super(ErrorMessageRepository.UNKOWN_COLUMN_NAME,
                XDBServerException.SEVERITY_LOW,
                ErrorMessageRepository.UNKOWN_COLUMN_NAME_CODE);
        this.columnName = columnName;
        this.tableName = tableName;
    }

    /**
     * 
     * @return 
     */
    @Override
    public String getMessage() {
        return super.getMessage() + "(" + columnName + ")";
    }

    /**
     * 
     * @return 
     */
    public SqlExpression getColumnExpression() {
        return aAttributeColumnExpression;
    }

}
