//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <SHOW_INDEXES_>
 * f1 -> <ON_>
 * f2 -> TableName(prn)
 */
public class ShowIndexes implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public TableName f2;

   public ShowIndexes(NodeToken n0, NodeToken n1, TableName n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public ShowIndexes(TableName n0) {
      f0 = new NodeToken("INDEXES");
      f1 = new NodeToken("ON");
      f2 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

