//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <CURRENTDATE_>
 */
public class Func_PgCurrentDate implements Node {
   public NodeToken f0;

   public Func_PgCurrentDate(NodeToken n0) {
      f0 = n0;
   }

   public Func_PgCurrentDate() {
      f0 = new NodeToken("CURRENT_DATE");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

