package hw4.src.hw4.node;

final public class Transaction {
    public final int id;

    public Transaction(int id) {
        this.id = id;
    }

    @Override
    /**  @return true if this Transaction has the same id as {@code obj} */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Transaction other = (Transaction) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}