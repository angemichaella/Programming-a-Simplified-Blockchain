public class Transaction {
	
	private String sender;
	private String receiver;
	private int amount;

	// constructor 1
	public Transaction(){
		sender = "";
		receiver = "";
		amount = 0;
	}

	// constructor 2 
	public Transaction(String sender, String receiver, int amount) {
		this.sender = sender;
		this.receiver = receiver;
		this.amount = amount;
	}


	// getters
	public String getSender(){
		return this.sender;
	}
	public String getReceiver(){
		return this.receiver;
	}
	public int getAmount(){
		return this.amount;
	}

	// setters
	public void setSender(String sender){
		this.sender = sender;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}


	public String toString(){
		return sender + ":" + receiver + "=" + amount;
	}
}