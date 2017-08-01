package hw4.src.hw4.node;

import hw4.src.hw4.*;
import hw4.src.hw4.node.Candidate;
import hw4.src.hw4.node.Node;
import hw4.src.hw4.node.NodeFactory;
import hw4.src.hw4.node.Transaction;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// This malicious node only broadcasts its transactions once: two rounds before the end
public class MaliciousNode_Zalak_13 implements Node {
   public static final NodeFactory FACTORY = new NodeFactory() {
      @Override
      public Node newNode(Scenario scenario) {
         return new MaliciousNode_Zalak_13(scenario);
      }

      @Override
      public String toString() {
         return "MalZalak13";
      }
   };

   private int round = 0;
   private Set<Transaction> initialTransactions;
   private Scenario scenario;

   public MaliciousNode_Zalak_13(Scenario scenario) {
      this.scenario = scenario;
   }

   @Override
   public void setTrustedNodes(boolean[] trustedNodeFlag) {

   }

   public void setInitialTransactions(Set<Transaction> s) {
      this.initialTransactions = new HashSet(s);
   }

   @Override
   public Set<Transaction> getProposalsForTrustingNodes() {
      return this.initialTransactions;

   }

   public void receiveFromTrustedNodes(Set<Candidate> candidates) {
      for(Candidate candidate : candidates) {
         if (candidate.sender % 2 == 0) {
            if (!this.initialTransactions.contains(candidate.tx))
               this.initialTransactions.add(candidate.tx);
         }
      }


}


}
