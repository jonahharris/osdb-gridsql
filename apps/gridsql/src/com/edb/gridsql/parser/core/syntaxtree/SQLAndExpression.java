//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> SQLUnaryLogicalExpression(prn)
 * f1 -> [ <LEFTOUTER_> ]
 * f2 -> ( SQLAndExp(prn) )*
 */
public class SQLAndExpression implements Node {
   public SQLUnaryLogicalExpression f0;
   public NodeOptional f1;
   public NodeListOptional f2;

   public SQLAndExpression(SQLUnaryLogicalExpression n0, NodeOptional n1, NodeListOptional n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

