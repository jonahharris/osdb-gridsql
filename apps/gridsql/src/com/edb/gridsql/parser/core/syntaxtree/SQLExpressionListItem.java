//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> SQLSimpleExpression(prn)
 */
public class SQLExpressionListItem implements Node {
   public SQLSimpleExpression f0;

   public SQLExpressionListItem(SQLSimpleExpression n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}
