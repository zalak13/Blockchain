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
public class MaliciousNode_Zalak_23 implements Node {
   public static final NodeFactory FACTORY = new NodeFactory() {
      @Override
      public Node newNode(Scenario scenario) {
         return new MaliciousNode_Zalak_23(scenario);
      }

      @Override
      public String toString() {
         return "MalZalak23";
      }
   };

   private int round = 0;
   private Set<Transaction> initialTransactions;
   private Scenario scenario;

   public MaliciousNode_Zalak_23(Scenario scenario) {
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
      this.round++;

      HashSet<Transaction> randomTransactions = new HashSet<Transaction>();
      double p = .7; // take 70% of transactions
      for (Transaction tx : initialTransactions) {
         if (Math.random() < p)
            randomTransactions.add(tx);
      }
      return randomTransactions;

   }

   public void receiveFromTrustedNodes(Set<Candidate> candidates) {
      for(Candidate candidate : candidates) {
         if(!this.initialTransactions.contains(candidate.tx))
            this.initialTransactions.add(candidate.tx);
   }


}


}
