//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <BIT_>
 * f1 -> [ LengthSpec() ]
 */
public class BitDataType implements Node {
   public NodeToken f0;
   public NodeOptional f1;

   public BitDataType(NodeToken n0, NodeOptional n1) {
      f0 = n0;
      f1 = n1;
   }

   public BitDataType(NodeOptional n0) {
      f0 = new NodeToken("BIT");
      f1 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

