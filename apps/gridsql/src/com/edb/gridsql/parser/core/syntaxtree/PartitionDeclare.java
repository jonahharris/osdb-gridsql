//
// Generated by JTB 1.2.2
//

package com.edb.gridsql.parser.core.syntaxtree;

/**
 * Grammar production:
 * f0 -> <PARTITIONINGKEY_> [ Identifier(prn) ] <ON_> PartitionChoice(prn)
 *       | <PARTITION_WITH_> <PARENT_>
 *       | <REPLICATED_>
 *       | <ON_> ( <NODE_> | <NODES_> ) <INT_LITERAL>
 *       | <ROUND_ROBIN_> <ON_> PartitionChoice(prn)
 */
public class PartitionDeclare implements Node {
   public NodeChoice f0;

   public PartitionDeclare(NodeChoice n0) {
      f0 = n0;
   }

   public void accept(com.edb.gridsql.parser.core.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(com.edb.gridsql.parser.core.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

