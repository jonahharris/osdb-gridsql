//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <OIDS_>
 */
public class FormatDefOIDS implements Node {
   public NodeToken f0;

   public FormatDefOIDS(NodeToken n0) {
      f0 = n0;
   }

   public FormatDefOIDS() {
      f0 = new NodeToken("OIDS");
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

