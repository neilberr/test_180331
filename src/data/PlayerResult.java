package data;

/**
 * class for player result
 *  - win or lose
 *  - starting price
 *  - std dev of prematch prices
 */
public class PlayerResult {

	private String playerName;
	private String result;
	private String opponentName;
	private double startingPrice;
	private double stdDev;
	
	//Constructor
	public PlayerResult() {
		playerName = "";
		result = "undetermined";
		opponentName = "";
		startingPrice = 0.0;
		stdDev = 0.0;
	}

	//Accessors and Mutators
	public String getResult() {return result;}
	public void setResult(String result) {this.result = result;}
	public double getStartingPrice() {return startingPrice;}
	public void setStartingPrice(double startingPrice) {this.startingPrice = startingPrice;}
	public double getStdDev() {return stdDev;}
	public void setStdDev(double stdDev) {this.stdDev = stdDev;}
	public String getPlayerName() {return playerName;}
	public void setPlayerName(String playerName) {this.playerName = playerName;}
	public String getOpponentName() {return opponentName;}
	public void setOpponentName(String opponentName) {this.opponentName = opponentName;}
} // class playerResult

