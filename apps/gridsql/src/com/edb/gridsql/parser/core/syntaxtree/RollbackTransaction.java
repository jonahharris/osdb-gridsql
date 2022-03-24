//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <ROLLBACK_>
 * f1 -> [ ( <TRANSACTION_> | <TRAN_> | <WORK_> ) ]
 */
public class RollbackTransaction implements Node {
   public NodeToken f0;
   public NodeOptional f1;

   public RollbackTransaction(NodeToken n0, NodeOptional n1) {
      f0 = n0;
      f1 = n1;
   }

   public RollbackTransaction(NodeOptional n0) {
      f0 = new NodeToken("ROLLBACK");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

