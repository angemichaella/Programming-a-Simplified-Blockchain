import java.sql.Timestamp;

public class Block {
	
	private int index;
	private Long timestamp;

	private Transaction transaction;
	private String nonce;
	private String previousHash;
	private String hash;

	// constructor 1
	public Block(){

		this.index = 0;
		timestamp = (new Timestamp(System.currentTimeMillis())).getTime();
		this.transaction = null;
		this.nonce = null;
		this.previousHash = null;

		// get the time when the block is being created
	}

	// constructor 2 - first block
	public Block(
		int index,
		long timestamp,
		Transaction transaction,
		String nonce,
		String previousHash){

		this.index = index;
		this.transaction = transaction;
		this.nonce = nonce;
		this.previousHash = previousHash;
		this.timestamp = timestamp;
		
	}

	

	// getters 
	public int getIndex(){
		return this.index;
	}
	public long getTimestamp(){
		return this.timestamp;
	}
	public Transaction getTransaction(){
		return this.transaction;
	}
	public String getNonce(){
		return this.nonce;
	}
	public String getPreviousHash(){
		return this.previousHash;
	}
	

	// setters
	public void setIndex(int index){
		this.index = index;
	}
	public void setTransaction(Transaction transaction){
		this.transaction = transaction;
	}
	public void setNonce(String nonce){
		this.nonce = nonce;
	}
	public void setPreviousHash(String previousHash){
		this.previousHash = previousHash;
	}
	


	public String toString(){
		//System.out.println("The value of timestamp " + timestamp);
		return new Timestamp(timestamp) + ":" + transaction.toString() + "." + nonce + previousHash;
	}
}