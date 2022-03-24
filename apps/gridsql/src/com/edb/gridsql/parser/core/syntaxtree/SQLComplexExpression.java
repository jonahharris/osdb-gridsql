//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> SQLAndExpression(prn)
 * f1 -> ( SQLORExpression(prn) )*
 */
public class SQLComplexExpression implements Node {
   public SQLAndExpression f0;
   public NodeListOptional f1;

   public SQLComplexExpression(SQLAndExpression n0, NodeListOptional n1) {
      f0 = n0;
      f1 = n1;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

