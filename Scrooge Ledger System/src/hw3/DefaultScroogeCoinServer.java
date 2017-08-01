package hw3;




import java.security.*;
import java.util.*;

//Scrooge creates coins by adding outputs to a transaction to his public key.
//In ScroogeCoin, Scrooge can create as many coins as he wants.
//No one else can create a coin.
//A user owns a coin if a coin is transfer to him from its current owner
public class DefaultScroogeCoinServer implements ScroogeCoinServer {

	private KeyPair scroogeKeyPair;
	private ArrayList<Transaction> ledger = new ArrayList();
    public SecureRandom random;
	private ArrayList<UTXO> UTXOPool;
   //Set scrooge's key pair
	@Override
	public synchronized void init(KeyPair scrooge) {
		scroogeKeyPair = scrooge;
        random = new SecureRandom();
		UTXOPool = new ArrayList<UTXO>();
	}

	//For every 10 minute epoch, this method is called with an unordered list of proposed transactions
	// 		submitted during this epoch.
	//This method goes through the list, checking each transaction for correctness, and accepts as
	// 		many transactions as it can in a "best-effort" manner, but it does not necessarily return
	// 		the maximum number possible.
	//If the method does not accept an valid transaction, the user must try to submit the transaction
	// 		again during the next epoch.
	//Returns a list of hash pointers to transactions accepted for this epoch

	public synchronized List<HashPointer> epochHandler(List<Transaction> txns)  {


		Transaction[] parseTxns = new Transaction[txns.size()];

		for (int ledgerindex = 0; ledgerindex < txns.size(); ledgerindex++) {
			parseTxns[ledgerindex] = txns.get(ledgerindex);
		}
		Transaction[] tempTxns = new Transaction[txns.size()];
		Transaction[] successTxns = new Transaction[txns.size()];
		int tempCounter = 0, successCounter = 0;
		int stuckSize = txns.size();
		while (true) {
			boolean change = false;
			tempCounter = 0;
			for (int i = 0; i < stuckSize; i++) {
				if (inLedger(parseTxns[i])) {
					if (isValid(parseTxns[i])) {
						change = true;
						updateLedger(parseTxns[i]);
						successTxns[successCounter++] = parseTxns[i];
					}
					else System.out.println("THis is the place");
				} else {
					tempTxns[tempCounter++] = parseTxns[i];
				}
			}
			if (change) {
				for (int i = 0; i < tempCounter; i++) {
					parseTxns[i] = tempTxns[i];
				}
				stuckSize = tempCounter;
			} else {
				break;
			}
		}
		Transaction[] result = new Transaction[successCounter];
		List<HashPointer> HashPtrList = new ArrayList<HashPointer>();
		HashPointer hashPointer;
		for (int i = 0; i < successCounter; i++) {
			hashPointer = new HashPointer(successTxns[i].getHash(), i);
			HashPtrList.add(hashPointer);
		}

		return HashPtrList;


	}

	//Returns true if and only if transaction tx meets the following conditions:
	//CreateCoin transaction
	//	(1) no inputs
	//	(2) all outputs are given to Scrooge's public key
	//	(3) all of tx’s output values are positive
	//	(4) Scrooge's signature of the transaction is included

	//PayCoin transaction
	//	(1) all outputs claimed by tx are in the current unspent (i.e. in getUTOXs()),
	//	(2) the signatures on each input of tx are valid,
	//	(3) no UTXO is claimed multiple times by tx,
	//	(4) all of tx’s output values are positive, and
	//	(5) the sum of tx’s input values is equal to the sum of its output values;
	@Override
	public synchronized boolean isValid(Transaction tx) {
        //	(1) no inputs
			if(tx.getType()==Transaction.Type.Create){
			if(!tx.getInputs().isEmpty()){
				return false;
			}
        //	(4) Scrooge's signature of the transaction is included

                if(tx.getSignature().equals(scroogeKeyPair.getPublic()))
                return false;

			for (int i = 0; i < tx.getOutputs().size(); i++) {
				Transaction.Output out = tx.getOutput(i);
        //	(3) all of tx’s output values are positive
                if (out.getValue() <= 0)
					return false;
        //	(2) all outputs are given to Scrooge's public key
        		if (!out.getPublicKey().equals(scroogeKeyPair.getPublic()))
                    return false;
			}
		}else if (tx.getType()==Transaction.Type.Pay){

                //	(1) all outputs claimed by tx are in the current unspent (i.e. in getUTOXs()),
                //	(2) the signatures on each input of tx are valid,
                //	(3) no UTXO is claimed multiple times by tx,
                //	(4) all of tx’s output values are positive, and
                //	(5) the sum of tx’s input values is equal to the sum of its output values;

                double totalInput = 0;


                for (int i = 0; i < tx.numInputs(); i++) {
                    Transaction.Input in = tx.getInput(i);
                    /*try {
                        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
                        signature.sign(in.getRawDataToSign2(),0,in.getSignature().length);
                        if(!signature.verify(in.getSignature()))
                        return false;
                    }
                    catch (NoSuchAlgorithmException x) {
                        throw new RuntimeException(x);
                    } catch (SignatureException e) {
                        e.printStackTrace();
                    }*/
                        HashPointer hashPointer = new HashPointer(tx.getInput(i).getHashOfOutputTx(),tx.getInput(i).getIndexOfTxOutput());
                        UTXO ut = new UTXO(hashPointer,i);
                        if (!UTXOPool.contains(ut))
                            return false;

                    Transaction.Output out  = ledger.get(tx.getIndex(tx.getOutput(in.getIndexOfTxOutput()))).getOutput(in.getIndexOfTxOutput());
                    totalInput +=out.getValue();
//                    for(i=0 ; i<ledger.size();i++){
//                        if(in.getHashOfOutputTx().equals(ledger.get(i))){
//                            Transaction.Output out = ledger.get(i).getOutput(in.getIndexOfTxOutput());
//
//                        }
//                    }
                    //totalInput += op.getValue();
                }
                double totalOutput = 0;
                List<Transaction.Output> txOutputs = tx.getOutputs();
                for (Transaction.Output op : txOutputs) {
                    if (op.getValue() < 0)
                        return false;
                    totalOutput += op.getValue();
                }
                return (totalInput >= totalOutput);

            }
					return true;
	}

	//Returns the complete set of currently unspent transaction outputs on the ledger
	@Override
	public synchronized Set<UTXO> getUTXOs() {
        Set<UTXO> setUTXO = new HashSet<UTXO>(UTXOPool);
		return setUTXO;
	}

	private boolean inLedger(Transaction tx) {
		List<Transaction.Input> inputs = tx.getInputs();
        Transaction.Input in;
		UTXO ut;
		for (int i = 0; i < inputs.size(); i++) {
			in = inputs.get(i);
			if(in==null){
			    return false;
            }
            HashPointer hashPointer = new HashPointer(in.getHashOfOutputTx(),in.getIndexOfTxOutput());
			ut = new UTXO(hashPointer, in.getIndexOfTxOutput());
			if (!UTXOPool.contains(ut))
				return false;
		}
		return true;
	}

	private void updateLedger(Transaction tx) {
		int ledgerIndex =0;
		if (ledger.contains(tx)){
			ledgerIndex = ledger.indexOf(tx);
		}
		for (int i = 0; i < UTXOPool.size(); i++){
			UTXO matchUTXO = UTXOPool.get(i);
			if ((tx.getType() == Transaction.Type.Pay)) {
				if (matchUTXO != null
						& matchUTXO.getOutputIndex() == (tx.getInput(i).getIndexOfTxOutput())) {
					UTXOPool.remove(matchUTXO);
					System.out.println("Removed");
				}
			}
		}
		/*for (int i = 0; i < tx.numInputs(); i++) {
			Transaction.Input in = tx.getInput(i);
			HashPointer hashPointer = new HashPointer(in.getHashOfOutputTx(),in.getIndexOfTxOutput());
			//UTXOPool.remove(new UTXO(hashPointer,tx.getIndex(tx.getOutput(in.getIndexOfTxOutput())) ));
			UTXO removeUTXO = new UTXO(hashPointer,in.getIndexOfTxOutput());
			if (UTXOPool.remove(removeUTXO))
				System.out.println("Removed");
		}*/
		for (int i = 0; i < tx.numOutputs(); i++) {
			Transaction.Output out = tx.getOutput(i);
            HashPointer hashPointer = new HashPointer(tx.getHash(),ledger.size());
            UTXOPool.add(new UTXO(hashPointer, i));
            ledger.add(tx);
		}
	}

}
