//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <ORDER_BY_>
 * f1 -> OrderByItem(prn)
 * f2 -> ( "," OrderByItem(prn) )*
 */
public class OrderByClause implements Node {
   public NodeToken f0;
   public OrderByItem f1;
   public NodeListOptional f2;

   public OrderByClause(NodeToken n0, OrderByItem n1, NodeListOptional n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public OrderByClause(OrderByItem n0, NodeListOptional n1) {
      f0 = new NodeToken("BY");
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

