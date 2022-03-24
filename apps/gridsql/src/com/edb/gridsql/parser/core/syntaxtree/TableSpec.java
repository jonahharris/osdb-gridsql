//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> TableName(prn) [ SelectAliasSpec(prn) ]
 *       | <PARENTHESIS_START_> SelectWithoutOrder(prn) <PARENTHESIS_CLOSE_> [ SelectAliasSpec(prn) ] [ <PARENTHESIS_START_> ColumnNameList(prn) <PARENTHESIS_CLOSE_> ]
 */
public class TableSpec implements Node {
   public NodeChoice f0;

   public TableSpec(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}
