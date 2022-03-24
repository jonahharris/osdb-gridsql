//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <INTO_>
 * f1 -> [ <TEMPORARY_> | <TEMP_> ]
 * f2 -> [ <TABLE_> ]
 * f3 -> TableName(prn)
 */
public class IntoClause implements Node {
   public NodeToken f0;
   public NodeOptional f1;
   public NodeOptional f2;
   public TableName f3;

   public IntoClause(NodeToken n0, NodeOptional n1, NodeOptional n2, TableName n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public IntoClause(NodeOptional n0, NodeOptional n1, TableName n2) {
      f0 = new NodeToken("INTO");
      f1 = n0;
      f2 = n1;
      f3 = n2;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

