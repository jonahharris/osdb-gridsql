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

package com.edb.gridsql.parser;

import java.sql.Types;

import com.edb.gridsql.common.util.Property;
import com.edb.gridsql.optimizer.QueryCondition;
import com.edb.gridsql.optimizer.SqlExpression;
import com.edb.gridsql.parser.core.syntaxtree.ColumnDeclare;
import com.edb.gridsql.parser.core.syntaxtree.DefaultSpec;
import com.edb.gridsql.parser.core.syntaxtree.Node;
import com.edb.gridsql.parser.core.syntaxtree.NodeChoice;
import com.edb.gridsql.parser.core.syntaxtree.NodeSequence;
import com.edb.gridsql.parser.core.visitor.ObjectDepthFirst;
import com.edb.gridsql.parser.handler.DataTypeHandler;
import com.edb.gridsql.parser.handler.IdentifierHandler;
import com.edb.gridsql.parser.handler.QueryConditionHandler;
import com.edb.gridsql.parser.handler.RawImageHandler;

// -----------------------------------------------------------------
public class SqlCreateTableColumn extends ObjectDepthFirst {
    public static String IDX_SERIAL_NAME = "IDX_SERIAL";

    public static final String XROWID_NAME = "xrowid";

    // Ingres must use decimal, max of 31 digits
    // Allow override with xdb.xrowid.type
    public static final SqlCreateTableColumn ROW_ID_COLUMN = new SqlCreateTableColumn(
            XROWID_NAME, new DataTypeHandler(Property.getInt(
                    "xdb.xrowid.SQLtype", Types.DECIMAL), Property.getInt(
                    "xdb.xrowid.length", 0), Property.getInt(
                    "xdb.xrowid.precision", 31), Property.getInt(
                    "xdb.xrowid.scale", 0)), false, null);

    private Command commandToExecute;

    // Name of the column
    public String columnName;

    private DataTypeHandler typeHandler;

    // If the type is null able
    public int isnullable = 1;

    // For "ALTER TABLE " USING exp
    // To indicate whether this column is a primary key or not.
    // Please note that this variable only gets set if the user
    // specifies that the primary key in the column specification
    // Todo- check if the user has specified any Primary key specification
    // Todo- at the global level specs.In that case we should throw a Illegal
    // Todo- argument function.
    protected boolean isPrimaryKey;

    // This variable will be used for holding reference information.
    protected SqlForeignReference aSqlForeignReference = null;

    // Query Condition for check condition
    protected QueryCondition checkCondition;

    // Default value
    String defaultValue;

    private String columnDef;

    // This class is a informaion class
    class SqlForeignReference {
        String tableName;

        String columnName;

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public SqlForeignReference(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }
    };

    /**
     * @param columnName
     * @param typeHandler
     * @param isnullable
     */
    SqlCreateTableColumn(String columnName, DataTypeHandler typeHandler,
            boolean isnullable, String defaultValue) {
        this.columnName = columnName;
        this.typeHandler = typeHandler;
        this.isnullable = isnullable ? 1 : 0;
        this.defaultValue = defaultValue;
    }

    public SqlCreateTableColumn(SqlExpression sqlEx) {
    	this.columnName = sqlEx.getAlias();
        if (this.columnName == null || this.columnName.length() == 0) {
            if (sqlEx.isColumn()) {
                this.columnName = sqlEx.getColumn().getColumnName();
            }
        }
    	ExpressionType type = sqlEx.getExprDataType();
        this.typeHandler = new DataTypeHandler(type.type, type.length,
        		type.precision, type.scale);
    }

    // Commands to execute
    public SqlCreateTableColumn(Command commandToExecute) {
        this.commandToExecute = commandToExecute;
    }

    /**
     * The below function is responsible for building individual coulmns. each
     * column also has the original Definition and therefore should be remade
     * from the tokens.The approach taken here is to have each token here attach
     * its own string to the originalDefinition. Mind IT!! the original
     * definition is used by the Engine to run the query with out modification
     * .There fore this field needs to be worked on well--Later this might
     * change but at present we are going to do it this way
     */

    /**
     * Grammar production:
     * f0 -> Identifier(prn)
     * f1 -> types()
     * f2 -> [ <NOT_> <NULL_> | <NULL_> ]
     * f3 -> [ DefaultSpec(prn) ]
     * f4 -> [ <PRIMARYKEY_> ]
     * f5 -> [ <CHECK_> "(" SQLComplexExpression(prn) ")" ]
     */
    @Override
    public Object visit(ColumnDeclare n, Object argu) {
        Object _ret = null;
        // get the column name
        columnName = (String) n.f0.accept(new IdentifierHandler(), argu);
        typeHandler = new DataTypeHandler();
        n.f1.accept(typeHandler, argu);
        // Call being intercepted in "NodeToken"
        if (n.f2.present()) {
            NodeChoice aNodeChoice = (NodeChoice) n.f2.node;
            switch (aNodeChoice.which) {
            case 0:
                this.isnullable = 0;
                break;
            case 1:
                this.isnullable = 1;
                break;
            }
        }
        // Call intercepted @ DefaultSpec()
        n.f3.accept(this, argu);
        if (n.f4.present()) {
            isPrimaryKey = true;
        }
        if (n.f5.present()) {
            NodeSequence aNodeSequence = (NodeSequence) n.f5.node;
            Node node = aNodeSequence.elementAt(2);
            QueryConditionHandler aQueryConditionHandler = new QueryConditionHandler(
                    commandToExecute);
            node.accept(aQueryConditionHandler, argu);
            checkCondition = aQueryConditionHandler.aRootCondition;
        }
        return _ret;
    }

    public String getTypeHandlerString() {
        return typeHandler.getTypeString();
    }

    public DataTypeHandler getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(DataTypeHandler typeHandler) {
        this.typeHandler = typeHandler;
    }

    public String getColumnAttrString() {
        String columnAttr = "";
        // Impying that it is true
        if (isnullable != 1) {
            columnAttr += " NOT NULL";
        }

        if (defaultValue != null) {
            columnAttr += " DEFAULT " + defaultValue;
        }

        /*
         * if (isPrimaryKey == true) { columnDef += " PRIMARY KEY "; }
         */

        if (aSqlForeignReference != null) {
            columnAttr += " " + " REFERENCES "
                    + aSqlForeignReference.getTableName() + " ( "
                    + aSqlForeignReference.getColumnName() + " ) ";
        }

        if (checkCondition != null) {
            columnAttr += " " + " CHECK " + " ( "
                    + checkCondition.rebuildString() + " ) ";
        }
        return columnAttr;
    }

    /**
     *
     * @return This function recreates the create table statement using the
     *         information contained in the information class
     */
    public String rebuildString() {
        if (columnDef == null) {
            columnDef = IdentifierHandler.quote(columnName) + " " + typeHandler.getTypeString();
            // Impying that it is true
            if (isnullable != 1) {
                columnDef += " NOT NULL";
            }

            if (defaultValue != null) {
                columnDef += " DEFAULT " + defaultValue;
            }

            if (aSqlForeignReference != null) {
                columnDef += " " + " REFERENCES "
                        + IdentifierHandler.quote(aSqlForeignReference.getTableName()) + " ( "
                        + IdentifierHandler.quote(aSqlForeignReference.getColumnName()) + " ) ";
            }

            if (checkCondition != null) {
                columnDef += " " + " CHECK " + " ( "
                        + checkCondition.rebuildString() + " ) ";
            }
        }
        return columnDef;
    }

    /**
     * Grammar production: f0 -> <DEFAULT_> f1 -> SQLSimpleExpression(prn)
     *
     * @param n
     * @param argu
     * @return
     */

    @Override
    public Object visit(DefaultSpec n, Object argu) {
        Object _ret = null;
        RawImageHandler imageHandler = new RawImageHandler();
        n.f1.accept(imageHandler, argu);
        defaultValue = imageHandler.getImage();
        return _ret;
    }

    // Get the default value for this , if set
    public String getDefaultValue() {

        return defaultValue;
    }

    // Get tge check condition as a string
    public String getcheckConditionString() {
        return checkCondition != null ? checkCondition.rebuildString() : null;
    }

    public boolean canBePartitioningKey() {
        return typeHandler.canBePartitioningKey();
    }

    public boolean isSerial() {
        return typeHandler.isSerial();
    }

    public int getColumnType() {
        return typeHandler.getSqlType();
    }

    public int getColumnLength() {
        return typeHandler.getLength();
    }

    public int getColumnPrecision() {
        return typeHandler.getPrecision();
    }

    public int getColumnScale() {
        return typeHandler.getScale();
    }
}
