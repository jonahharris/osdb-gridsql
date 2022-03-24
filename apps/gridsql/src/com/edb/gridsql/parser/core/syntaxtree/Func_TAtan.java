//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <ATAN_>
 * f1 -> <PARENTHESIS_START_>
 * f2 -> SQLArgument(prn)
 * f3 -> <PARENTHESIS_CLOSE_>
 */
public class Func_TAtan implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public SQLArgument f2;
   public NodeToken f3;

   public Func_TAtan(NodeToken n0, NodeToken n1, SQLArgument n2, NodeToken n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public Func_TAtan(SQLArgument n0) {
      f0 = new NodeToken("ATAN");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = new NodeToken(")");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

