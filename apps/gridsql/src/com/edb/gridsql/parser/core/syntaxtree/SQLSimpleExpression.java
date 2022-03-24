//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> SQLMultiplicativeExpression(prn)
 * f1 -> ( SimpleExpressionOperand(prn) )*
 */
public class SQLSimpleExpression implements Node {
   public SQLMultiplicativeExpression f0;
   public NodeListOptional f1;

   public SQLSimpleExpression(SQLMultiplicativeExpression n0, NodeListOptional n1) {
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

