//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <FIXED_>
 * f1 -> PrecisionSpec()
 * f2 -> UnsignedZeroFillSpecs()
 */
public class FixedDataType implements Node {
   public NodeToken f0;
   public PrecisionSpec f1;
   public UnsignedZeroFillSpecs f2;

   public FixedDataType(NodeToken n0, PrecisionSpec n1, UnsignedZeroFillSpecs n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public FixedDataType(PrecisionSpec n0, UnsignedZeroFillSpecs n1) {
      f0 = new NodeToken("FIXED");
      f1 = n0;
      f2 = n1;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

