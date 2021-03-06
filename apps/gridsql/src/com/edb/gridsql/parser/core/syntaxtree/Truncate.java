//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <TRUNCATE_>
 * f1 -> [ <TABLE_> ]
 * f2 -> TableName(prn)
 */
public class Truncate implements Node {
   public NodeToken f0;
   public NodeOptional f1;
   public TableName f2;

   public Truncate(NodeToken n0, NodeOptional n1, TableName n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public Truncate(NodeOptional n0, TableName n1) {
      f0 = new NodeToken("TRUNCATE");
      f1 = n0;
      f2 = n1;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

