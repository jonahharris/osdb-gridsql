//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <ALTER_>
 * f1 -> ( AlterTable(prn) | AlterTableSpace(prn) )
 */
public class Alter implements Node {
   public NodeToken f0;
   public NodeChoice f1;

   public Alter(NodeToken n0, NodeChoice n1) {
      f0 = n0;
      f1 = n1;
   }

   public Alter(NodeChoice n0) {
      f0 = new NodeToken("ALTER");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

