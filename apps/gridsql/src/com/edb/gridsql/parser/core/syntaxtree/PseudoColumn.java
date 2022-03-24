//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <PARENTHESIS_START_>
 * f1 -> <SELECT_>
 * f2 -> [ <ALL_> | <DISTINCT_> ]
 * f3 -> SelectList(prn)
 * f4 -> FromClause(prn)
 * f5 -> [ WhereClause(prn) ]
 * f6 -> <PARENTHESIS_CLOSE_>
 */
public class PseudoColumn implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public NodeOptional f2;
   public SelectList f3;
   public FromClause f4;
   public NodeOptional f5;
   public NodeToken f6;

   public PseudoColumn(NodeToken n0, NodeToken n1, NodeOptional n2, SelectList n3, FromClause n4, NodeOptional n5, NodeToken n6) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
   }

   public PseudoColumn(NodeOptional n0, SelectList n1, FromClause n2, NodeOptional n3) {
      f0 = new NodeToken("(");
      f1 = new NodeToken("SELECT");
      f2 = n0;
      f3 = n1;
      f4 = n2;
      f5 = n3;
      f6 = new NodeToken(")");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

