//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <OR_>
 * f1 -> SQLAndExpression(prn)
 */
public class SQLORExpression implements Node {
   public NodeToken f0;
   public SQLAndExpression f1;

   public SQLORExpression(NodeToken n0, SQLAndExpression n1) {
      f0 = n0;
      f1 = n1;
   }

   public SQLORExpression(SQLAndExpression n0) {
      f0 = new NodeToken("OR");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

