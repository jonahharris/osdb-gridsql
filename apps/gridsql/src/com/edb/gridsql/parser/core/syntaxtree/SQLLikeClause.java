//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( [ <NOT_> ] ( <LIKE_> | <ILIKE_> | <SIMILAR_TO_> ) SQLSimpleExpression(prn) [ <ESCAPE_> SQLSimpleExpression(prn) ] | ( <NOT_BITWISE_> | <REGEX_NOT_MATCHES_> | <REGEX_MATCHES_CASE_INSTV_> | <REGEX_NOT_MATCHES_CASE_INSTV_> | <OVERLAPS_> ) SQLSimpleExpression(prn) )
 */
public class SQLLikeClause implements Node {
   public NodeChoice f0;

   public SQLLikeClause(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

