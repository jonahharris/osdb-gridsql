//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <INTERVAL_LITERAL>
 */
public class IntervalLiterals implements Node {
   public NodeToken f0;

   public IntervalLiterals(NodeToken n0) {
      f0 = n0;
   }

   public IntervalLiterals() {
      f0 = new NodeToken("'");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

