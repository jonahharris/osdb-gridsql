//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( ExistsClause(prn) | [ <NOT_> ] SQLCondResult(prn) | [ <NOT_> ] SQLRelationalExpression(prn) )
 */
public class SQLUnaryLogicalExpression implements Node {
   public NodeChoice f0;

   public SQLUnaryLogicalExpression(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}
