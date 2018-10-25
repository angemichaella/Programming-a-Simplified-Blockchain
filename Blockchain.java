import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.lang.*;
import java.util.Random;
import java.sql.Timestamp;

public class Blockchain{

	// arrayList that will be used as the blockchain
	// by default, this blockchain is assumed to handle 10 blocks of transaction
	private ArrayList<Block> chain;
	private String[] hashCode;
	private int size;
	private int hashTrials = 0;
	
	public static void main(String[] args){

		// 1. read from a file and add to arrayList
		Scanner in = new Scanner(System.in);
		System.out.println("Please, enter the name of the file: ");
		String filename = in.nextLine().trim() + ".txt";
		// this will throw an error if the file doesn't exist in the directory
		Blockchain myBlockchain = Blockchain.fromFile(filename);

		// 2. validate the blockchain
		boolean isValid =myBlockchain.validateBlockchain();


		boolean enterTransaction = true;

		while(enterTransaction==true){
			// 3. get the sender, the receiver and the balance of the sender
			System.out.println("Please enter the name of the sender");
			String sender = in.nextLine().trim();
			System.out.println("Please enter the name of the receiver");
			String receiver = in.nextLine().trim();
			System.out.println("Please enter the amount of bitcoins to be sent");
			int amount = Integer.valueOf(in.nextLine().trim());

			if (myBlockchain.getBalance(sender) >= amount){
				String nonce = "";
				Block tempBlock;
				String previousHash = "";
				int index = myBlockchain.size(); // the new index for the new block

				// getting the right previousHash 
				if(myBlockchain.size() == 0){
					previousHash = "00000";
				} else {
					try{
						previousHash = Sha1.hash(myBlockchain.chain.get(myBlockchain.size()-1).toString());
					} catch (Exception e){
						System.out.println("Could not get previous hash");
					}
					
				}

				Long timestamp = (new Timestamp(System.currentTimeMillis())).getTime(); // the long value of the timestamp

				Transaction tempT = new Transaction(sender, receiver, amount); // the transaction to be inserted into the new block 

				
				
				char[] randChar = new char[6];

				String potHash = "                                 ";
				String potString = "";
				
				myBlockchain.hashTrials = 0;

				while (!potHash.substring(0,5).equals("00000")){
					myBlockchain.hashTrials ++;
					System.out.println("in the while loop");
					nonce = "";

					// generate new value for the nonce
					for(int i = 0; i<randChar.length; i++){
						int randInt = (int)(Math.random()*((126-33)+1))+33;
						randChar[i] = (char)randInt;
						nonce+=randChar[i];
					}

					System.out.println("nonce: " + nonce);
					potString = new Timestamp(timestamp) + ":" + tempT.toString() + "." + nonce + previousHash;

					
					try{
						potHash = Sha1.hash(potString);
						System.out.println("Potential hash " + potHash);
					} catch (Exception e){
						System.out.println("Couldn't convert to hash code in the main method to get the potHash");
					}
				}
				
				
				// tempBlock = new Block(index, timestamp, tempTransaction, nonce, hash(myBlockchain.chain[index-1]))
				
				tempBlock = new Block (index, timestamp, tempT, nonce, previousHash);
				System.out.println("The block to be added is " + tempBlock.toString());
				myBlockchain.add(tempBlock);

			}
			System.out.println("Hast trials: " + myBlockchain.hashTrials);
			System.out.println("Enter 1 if you want to add another transaction to the block, enter anything else if this was your last transaction");
			String userChoice = in.nextLine();
			if(!userChoice.equals("1")){
				enterTransaction = false;
			}

		}
		myBlockchain.toFile("blockchain_aniyo066");
		
	}

	// blockchain initializer 
	public Blockchain(int size){
		chain = new ArrayList<Block>(size);
		size = 0;
		hashCode = new String[1];
	}
	// method to read from a file
	public static Blockchain fromFile(String fileName){

		// get the number of lines in the fileName
		int lines = 0;
		try{
			BufferedReader iterator = new BufferedReader(new FileReader(fileName));
			while (iterator.readLine() != null){
				lines++;
			}
		} catch (Exception e) {
			System.out.println("Could not get the number of lines");
		}

		// create the new empty blockchain
		int blockNbr = lines/7;
		Blockchain myBlockchain = new Blockchain(blockNbr);
		myBlockchain.hashCode = new String[blockNbr];


		// this method will read the file line by line
		try{
			BufferedReader iterator = new BufferedReader(new FileReader(fileName)); // this one will act as an iterator checking if the end of the text file hasn't been reached yet
			BufferedReader reader = new BufferedReader(new FileReader(fileName)); // this is the real reader that will go through the text file returning each line

			try {
				while(iterator.readLine() != null){ // while we haven't reached the end of the file
					int index = 0;
					long timestamp = 0;
					String sender = "";
					String receiver = "";
					int amount = 0;
					String nonce = "";
					String hash = "";
					try{
						index = Integer.valueOf(reader.readLine()); // get index of block from line 1
						timestamp = Long.valueOf(reader.readLine()); // get timestamp of block from line 2
						sender = reader.readLine(); // get sender name of transaction from line 3
						receiver = reader.readLine(); // get receive rname of transaction from line 4
						amount = Integer.valueOf(reader.readLine()); // get amount of transaction from line 5
						nonce = reader.readLine(); // get previous nonce from line 6
						hash = reader.readLine(); // get previous hash from line 7
					} catch (Exception e){
						System.out.println("An exception has been caught while reading from the file");
					}
					

					Transaction tempTransaction = new Transaction(sender, receiver, amount);

					Block tempBlock = null;

					if(index == 0){
						tempBlock = new Block(index, timestamp, tempTransaction, nonce, "00000");
					}
					else{
						tempBlock = new Block(index, timestamp, tempTransaction, nonce, myBlockchain.hashCode[index-1]);
					}
					
					myBlockchain.add(tempBlock);
					myBlockchain.hashCode[index] = hash;


					for (int i = 1; i<7; i++){ // make the "iterator" skip 6 lines
						iterator.readLine();
					}
				}

			} catch (Exception e) {
				System.out.println("Could not go through the entire file");
			}

		} catch (Exception e) {
			System.out.println("Could not initialize the buffered readers");
		}

		return myBlockchain;
	}

	// method to write to a file
	public void toFile(String fileName){

		if (this.size() != 0 ){
			try{
				FileWriter wr = new FileWriter(fileName+".txt", true);
				// write the lines
				for (int i = 0; i<this.size(); i++){
					// get a block 
					Block tempB = this.chain.get(i);
					wr.write(tempB.getIndex() + "\n");
					wr.write(String.valueOf(tempB.getTimestamp()) + "\n");
					wr.write(tempB.getTransaction().getSender() + "\n");
					wr.write(tempB.getTransaction().getReceiver() + "\n");
					wr.write(tempB.getTransaction().getAmount() + "\n");
					wr.write(tempB.getNonce() + "\n");
					wr.write(Sha1.hash(tempB.toString()) + "\n");
					System.out.println("Completed writing block at index " + i + " on the file");
				}
				// close the writer
				try{
					wr.close();
				} catch (Exception e) {
					System.out.println("Couldn't close the file writer");
				}
			} catch (Exception e){
				System.out.println("Couldn't write all the lines to the file");
			}
		}
	}

	// method to validate this blockchain
	public boolean validateBlockchain(){

		// 1. Validate the hash codes making sure they correspond to the values in the corresponding block and check that all the index and previous hash attributes are consistent
		
		// check if the previous hash attribute for the first block is = "00000"
		
		// chech if the hash codes in the file correspond to those generated by the Sha1 class and checking the previous hash attributes from index 1

		System.out.println("Validating the blockchain...");

		for (int i =0; i<this.size(); i++){
			System.out.println("Block at index " + i + " is " + this.chain.get(i).toString());
			String hashCodeFromSha = "";
			try{
				hashCodeFromSha = Sha1.hash(this.chain.get(i).toString());
				System.out.println("Hash code from sha of bitcoin at index " + i + " is " + hashCodeFromSha);
				System.out.println("The hash code from the file is " + hashCode[i]);
			} catch (Exception e) {
				System.out.println("Couldn't get the hash value in the validateBlockchain() method");
			}
			if (!hashCode[0].substring(0,5).equals("00000")){
				System.out.println("failed: first characters: " + hashCode[0].substring(0,5));
				return false;
			}
			if (!hashCodeFromSha.equals(hashCode[i])){
				System.out.println("The hash generated by the Sha1 class does not correspond to the one in the file for index " + i);
				return false;
			}
			if (i == 0) {
				if (!this.chain.get(0).getPreviousHash().equals("00000")) {
					System.out.println("The previous has for the first block is not equal to 00000");
					return false;
				}
			} else if (i>0 && i<this.size()-1) {
				if(!hashCode[i].equals(this.chain.get(i+1).getPreviousHash())){
					System.out.println("The previous hash in index " + (i+1) + " is not equal to the hash code in index " + i);
					return false;
				}
			}

			// 2. Validate all the transactions
			// check if no user has spent more than he owns. The bitcoin user starts with 50 bitcoin
			if (this.getBalance(this.chain.get(i).getTransaction().getSender()) < 0){
				System.out.println("The amount to be sent is greater than the amount the sender owns at block " + i );
				return false;
			}
		}

		System.out.println("Successfully validated the blockchain!");
		return true;
	}

	// method to get a specific user's balance
	public int getBalance(String username){
		int bal = 0; // b for balance
		System.out.println("username is " + username);
		if (username.trim().equals("bitcoin")){
			System.out.println("setting bitcoin balance to 50");
			bal = 50;
		}
		for (int i = 0; i< this.size(); i++){
			if (this.chain.get(i).getTransaction().getSender().equals(username)){
				System.out.println("username " + username + " is a sender in block " + i);
				bal -= this.chain.get(i).getTransaction().getAmount();
				System.out.println("new bal: " + bal);
			} else if (this.chain.get(i).getTransaction().getReceiver().equals(username)){
				System.out.println("username " + username + " is a receiver in block " + i);
				bal += this.chain.get(i).getTransaction().getAmount();
				System.out.println("new bal: " + bal);
			}
		}
		System.out.println("bal is " + bal);
		return bal;
	}

	// method to create the new block to be added to the blockchain
	public void add(Block block){
		this.chain.add(block);
		this.size++;

	}

	// method to get the size of chain
	public int size(){
		return this.size;
	}

}