//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <SUM_>
 * f1 -> <PARENTHESIS_START_>
 * f2 -> [ <DISTINCT_> ]
 * f3 -> SQLArgument(prn)
 * f4 -> <PARENTHESIS_CLOSE_>
 */
public class Func_Sum implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeOptional f2;
   public SQLArgument f3;
   public NodeToken f4;

   public Func_Sum(NodeToken n0, NodeToken n1, NodeOptional n2, SQLArgument n3, NodeToken n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public Func_Sum(NodeOptional n0, SQLArgument n1) {
      f0 = new NodeToken("SUM");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = n1;
      f4 = new NodeToken(")");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

