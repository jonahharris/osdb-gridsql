//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <REVOKE_>
 * f1 -> PrivilegeList(prn)
 * f2 -> <ON_>
 * f3 -> [ <TABLE_> ]
 * f4 -> TableListForGrant(prn)
 * f5 -> <FROM_>
 * f6 -> GranteeList(prn)
 */
public class Revoke implements Node {
   public NodeToken f0;
   public PrivilegeList f1;
   public NodeToken f2;
   public NodeOptional f3;
   public TableListForGrant f4;
   public NodeToken f5;
   public GranteeList f6;

   public Revoke(NodeToken n0, PrivilegeList n1, NodeToken n2, NodeOptional n3, TableListForGrant n4, NodeToken n5, GranteeList n6) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
      f5 = n5;
      f6 = n6;
   }

   public Revoke(PrivilegeList n0, NodeOptional n1, TableListForGrant n2, GranteeList n3) {
      f0 = new NodeToken("REVOKE");
      f1 = n0;
      f2 = new NodeToken("ON");
      f3 = n1;
      f4 = n2;
      f5 = new NodeToken("FROM");
      f6 = n3;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

