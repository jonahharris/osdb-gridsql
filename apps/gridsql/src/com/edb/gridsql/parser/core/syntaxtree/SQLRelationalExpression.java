//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( SQLExpressionList(prn) | SQLSimpleExpression(prn) )
 * f1 -> ( SQLRelationalOperatorExpression(prn) | ( SQLInClause(prn) | SQLBetweenClause(prn) | SQLLikeClause(prn) ) | IsNullClause(prn) | IsBooleanClause(prn) )?
 */
public class SQLRelationalExpression implements Node {
   public NodeChoice f0;
   public NodeOptional f1;

   public SQLRelationalExpression(NodeChoice n0, NodeOptional n1) {
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

