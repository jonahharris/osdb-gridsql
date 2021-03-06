//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <OFFSET_>
 * f1 -> <INT_LITERAL>
 */
public class OffsetClause implements Node {
   public NodeToken f0;
   public NodeToken f1;

   public OffsetClause(NodeToken n0, NodeToken n1) {
      f0 = n0;
      f1 = n1;
   }

   public OffsetClause(NodeToken n0) {
      f0 = new NodeToken("OFFSET");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

