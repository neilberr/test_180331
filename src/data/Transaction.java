package data;

import rawdata.Rc;

public class Transaction {
	private int playerId;
	private long time;
	private double price;
	private double volume;
	boolean countered; //calculated from the other player
	
	//Constructor
	public Transaction() {
		time = 0l;
		playerId = 0;
		price = 0.0;
		volume = 0.0;
		countered = false;
	} // Constructor

	public Transaction(Rc rc, long timeStamp) {
		this.time = timeStamp;
		this.playerId = rc.getId();
		this.price = rc.getLtp();
		this.volume = 0.0;
		this.countered = false;
	} // Constructor(Rc)

	public Transaction(int playerId, long time, double price, boolean countered) {
		this.time = time;
		this.playerId = playerId;
		this.price = price;
		this.volume = 0.0;
		this.countered = countered;
	} // Constructor(attributes)

	//Accessors and Mutators
	public int getPlayerId() {return playerId;}
	public void setPlayerId(int playerId) {this.playerId = playerId;}
	public double getPrice() {return price;}
	public void setPrice(double price) {this.price = price;}
	public double getVolume() {return volume;}
	public void setVolume(double volume) {this.volume = volume;}
	public long getTime() {return time;}
	public void setTime(long time) {this.time = time;}
	public boolean isCountered() {return countered;}
	public void setCountered(boolean countered) {this.countered = countered;}
} // class Transaction
