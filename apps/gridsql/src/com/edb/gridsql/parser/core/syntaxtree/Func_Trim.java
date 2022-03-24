//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <TRIM_>
 * f1 -> <PARENTHESIS_START_>
 * f2 -> ( <BOTH> | <LEADING> | <TRAILING> )
 * f3 -> [ SQLArgument(prn) ]
 * f4 -> <FROM_>
 * f5 -> SQLArgument(prn)
 * f6 -> <PARENTHESIS_CLOSE_>
 */
public class Func_Trim implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeChoice f2;
   public NodeOptional f3;
   public NodeToken f4;
   public SQLArgument f5;
   public NodeToken f6;

   public Func_Trim(NodeToken n0, NodeToken n1, NodeChoice n2, NodeOptional n3, NodeToken n4, SQLArgument n5, NodeToken n6) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
   }

   public Func_Trim(NodeChoice n0, NodeOptional n1, SQLArgument n2) {
      f0 = new NodeToken("TRIM");
      f1 = new NodeToken("(");
      f2 = n0;
      f3 = n1;
      f4 = new NodeToken("FROM");
      f5 = n2;
      f6 = new NodeToken(")");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

