//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <INT_LITERAL>
 * f1 -> ( "," <INT_LITERAL> )+
 */
public class NodePartitionList implements Node {
   public NodeToken f0;
   public NodeList f1;

   public NodePartitionList(NodeToken n0, NodeList n1) {
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

