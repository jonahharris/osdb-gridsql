//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <DROP_>
 * f1 -> <TABLESPACE_>
 * f2 -> Identifier(prn)
 */
public class DropTablespace implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Identifier f2;

   public DropTablespace(NodeToken n0, NodeToken n1, Identifier n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public DropTablespace(Identifier n0) {
      f0 = new NodeToken("DROP");
      f1 = new NodeToken("TABLESPACE");
      f2 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

