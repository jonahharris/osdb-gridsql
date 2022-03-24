//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <NOW_>
 * f1 -> <PARENTHESIS_START_>
 * f2 -> <PARENTHESIS_CLOSE_>
 */
public class Func_Now implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeToken f2;

   public Func_Now(NodeToken n0, NodeToken n1, NodeToken n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public Func_Now() {
      f0 = new NodeToken("NOW");
      f1 = new NodeToken("(");
      f2 = new NodeToken(")");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}
