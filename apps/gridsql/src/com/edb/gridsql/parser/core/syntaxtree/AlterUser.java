//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <ALTER_>
 * f1 -> <USER_>
 * f2 -> Identifier(prn)
 * f3 -> [ <PASSWORD_> Identifier(prn) ]
 * f4 -> [ <DBA_> | <RESOURCE_> | <STANDARD_> ]
 */
public class AlterUser implements Node {
   public NodeToken f0;
   public NodeToken f1;
   public Identifier f2;
   public NodeOptional f3;
   public NodeOptional f4;

   public AlterUser(NodeToken n0, NodeToken n1, Identifier n2, NodeOptional n3, NodeOptional n4) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
      f4 = n4;
   }

   public AlterUser(Identifier n0, NodeOptional n1, NodeOptional n2) {
      f0 = new NodeToken("ALTER");
      f1 = new NodeToken("USER");
      f2 = n0;
      f3 = n1;
      f4 = n2;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

