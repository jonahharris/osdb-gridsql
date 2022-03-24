//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> [ <NOT_> ]
 * f1 -> "EXISTS"
 * f2 -> <PARENTHESIS_START_>
 * f3 -> SubQuery(prn)
 * f4 -> <PARENTHESIS_CLOSE_>
 */
public class ExistsClause implements Node {
   public NodeOptional f0;
   public NodeToken f1;
   public NodeToken f2;
   public SubQuery f3;
   public NodeToken f4;

   public ExistsClause(NodeOptional n0, NodeToken n1, NodeToken n2, SubQuery n3, NodeToken n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public ExistsClause(NodeOptional n0, SubQuery n1) {
      f0 = n0;
      f1 = new NodeToken("EXISTS");
      f2 = new NodeToken("(");
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
