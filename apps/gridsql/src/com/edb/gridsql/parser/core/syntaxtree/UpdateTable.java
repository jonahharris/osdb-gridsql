//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <UPDATE_>
 * f1 -> TableName(prn)
 * f2 -> <SET_>
 * f3 -> SetUpdateClause(prn)
 * f4 -> ( "," SetUpdateClause(prn) )*
 * f5 -> [ WhereClause(prn) ]
 */
public class UpdateTable implements Node {
   public NodeToken f0;
   public TableName f1;
   public NodeToken f2;
   public SetUpdateClause f3;
   public NodeListOptional f4;
   public NodeOptional f5;

   public UpdateTable(NodeToken n0, TableName n1, NodeToken n2, SetUpdateClause n3, NodeListOptional n4, NodeOptional n5) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
   }

   public UpdateTable(TableName n0, SetUpdateClause n1, NodeListOptional n2, NodeOptional n3) {
      f0 = new NodeToken("UPDATE");
      f1 = n0;
      f2 = new NodeToken("SET");
      f3 = n1;
      f4 = n2;
      f5 = n3;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

