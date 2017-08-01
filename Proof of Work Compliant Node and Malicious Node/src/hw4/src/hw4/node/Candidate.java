package hw4.src.hw4.node;

public class Candidate {
	public final Transaction tx;
	public final int sender;
	
	public Candidate(Transaction tx, int sender) {
		this.tx = tx;
		this.sender = sender;
	}
}