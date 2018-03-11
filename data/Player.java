package data;

import java.util.ArrayList;
import java.util.List;

import rawdata.Rc;
import rawdata.Runners;

/**
 * Class for one player
 *  - id seems to be unique for his lifetime
 *  - eventually he will be crowned winner or banished as loser
 */
public class Player {
	private int id;
	private String name;
	private String status;
	private List<Transaction> transactions;
	
	//Constructor
	public Player() {
		id = 0;
		name = "Player Name";
		status = "indeterminate";
		transactions = new ArrayList<Transaction>();
	} // Constructor

	//Constructor from runner
	public Player(Runners runner) {
		id = runner.getId();
		name = runner.getName();
		status = runner.getStatus();
		transactions = new ArrayList<Transaction>();
	} // Constructor(Runner)

	//Constructor from runner change
	public Player(Rc rc, long timeStamp) {
		id = rc.getId();
		name = "created from rc";
		status = "still playing";
		transactions = new ArrayList<Transaction>();
		transactions.add(new Transaction(rc, timeStamp));
	} // Constructor

	//Public methods
	/**
	 * add new transaction
	 *  - transaction is inner so noone can create a new one.
	 */
	public void addNewTransaction(Rc rc, long timeStamp) {
		transactions.add(new Transaction(rc, timeStamp));
	} // addNewTransaction()

	//Accessors and Mutators
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public List<Transaction> getTransactions() {return transactions;}
	public void setTransactions(List<Transaction> transactions) {this.transactions = transactions;}
	public String getStatus() {return status;}
	public void setStatus(String status) {this.status = status;}

} // class Player
