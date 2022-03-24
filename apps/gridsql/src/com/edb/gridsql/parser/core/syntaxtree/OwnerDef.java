//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <OWNER_TO_>
 * f1 -> ( <PUBLIC_> | Identifier(prn) )
 */
public class OwnerDef implements Node {
   public NodeToken f0;
   public NodeChoice f1;

   public OwnerDef(NodeToken n0, NodeChoice n1) {
      f0 = n0;
      f1 = n1;
   }

   public OwnerDef(NodeChoice n0) {
      f0 = new NodeToken("TO");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

