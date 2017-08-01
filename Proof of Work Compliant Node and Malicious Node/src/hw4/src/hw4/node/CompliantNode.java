package hw4.src.hw4.node;

import hw4.src.hw4.Scenario;

import java.util.*;

public class CompliantNode implements Node {
    public static final NodeFactory FACTORY = new NodeFactory() {
        @Override
        public Node newNode(Scenario scenario) {
            return new CompliantNode(scenario);
        }

        @Override
        public String toString() {
            return "CompliantNode";
        }
    };

    boolean[] trustedNodeFlag;
    int numRounds;//
    int currentRound=0;
    Map<Integer,Integer> map=new HashMap<Integer,Integer>();
    Set<Transaction> initialTransactions=new HashSet<>();
    Scenario scenario;

    public CompliantNode(Scenario scenario) {
        this.scenario = scenario;
        this.numRounds= this.scenario.getNumRounds();
    }

    @Override
    public void setTrustedNodes(boolean[] trustedNodeFlag) {
        this.trustedNodeFlag=trustedNodeFlag;
    }

    @Override
    public void setInitialTransactions(Set<Transaction> initialTransactions) {
        for(Transaction tx:initialTransactions){
            this.initialTransactions.add(tx);
            map.put(tx.id, 0);
        }
    }

    @Override
    public Set<Transaction> getProposalsForTrustingNodes() {
        if(currentRound==numRounds){
            Set<Transaction> ret=new  HashSet<>();

            for(Transaction tx:initialTransactions){
                Integer count=map.get(tx.id);
                if(count==null){
                    count=0;
                }
                if(count>=(int)(trustedNodeFlag.length*0.20)){
                    ret.add(tx);
                }
            }
            return ret;
        }
        return initialTransactions;
//        return Collections.EMPTY_SET;
    }

    @Override
    public void receiveFromTrustedNodes(Set<Candidate> candidates) {
        currentRound++;
        // IMPLEMENT THIS
        for(Candidate candidate:candidates){
            Transaction tx=candidate.tx;
            int sender=candidate.sender;

            if(!trustedNodeFlag[sender]){//
                continue;
            }
            initialTransactions.add(tx);

            Integer count=map.get(tx.id);
            if(count==null){
                count=0;
            }
            count++;
            map.put(tx.id,count);

        }

    }
}
