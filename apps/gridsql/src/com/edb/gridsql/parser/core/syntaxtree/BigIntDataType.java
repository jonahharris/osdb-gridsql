//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> ( <BIGINT_> | <BIGINTEGER_> | <INT8_> )
 * f1 -> [ <SERIAL_> ]
 * f2 -> UnsignedZeroFillSpecs()
 */
public class BigIntDataType implements Node {
   public NodeChoice f0;
   public NodeOptional f1;
   public UnsignedZeroFillSpecs f2;

   public BigIntDataType(NodeChoice n0, NodeOptional n1, UnsignedZeroFillSpecs n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

