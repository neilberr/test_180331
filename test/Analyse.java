package test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rosuda.JRI.Rengine;

import data.Bucket;
import data.Data;
import data.MarketData;
import data.Player;
import data.PlayerResult;
import data.Transaction;

public class Analyse {

	public static final double BUCKETSIZE = 0.05;
	public static final double BIDOFFERSPREAD = 0.02; // worst case
	public static final double SPANK = 0.04; // normal case

	public static void analyse(Data data, BufferedWriter bw) {

		List<PlayerResult> playerResults = new ArrayList<PlayerResult>();
		
	    //Start Rengine.
	    Rengine engine = new Rengine(new String[] { "--no-save" }, false, null);

	    //get result of each player - win or lose and starting price
	    int marketCount = 0;
	    for (MarketData market : data.getMarketData().values()) {
	    	if(marketCount % 500 == 0) {
	    		System.out.println("[" + marketCount + "]: " + market.getEventName());
	    	}
	    	for (Player player : market.getPlayers().values()) {
	    		playerResults.add(result(player, market, engine));
	    	}
	    	marketCount += 1;
	    }

	    //now have all results win or lose and the price, put in a bucket
	    List<Bucket> resultVector = new ArrayList<Bucket>();
	    for (double bucketSize = 1.01; bucketSize < 2.0; bucketSize += Analyse.BUCKETSIZE) {
	    	resultVector.add(new Bucket(bucketSize));
	    }

	    //titles
	    System.out.println("Bucket[from; to; size; expected; actal; difference ");

	    //fill those buckets
	    for (Bucket bucket : resultVector) {
	    	for (PlayerResult result : playerResults) {
	    		if (result.getStartingPrice() > bucket.getBottom() && result.getStartingPrice() < bucket.getTop()) {
	    			bucket.getPlayerResults().add(result);
	    		}
	    	}
	    	
	    	//bucket is full
	    	System.out.println(bucket.getBottom() + ";" + 
	    			bucket.getTop() + ";" + 
	    			bucket.getPlayerResults().size() + ";" +
	    			bucket.calculateExpectedWinRate() + ";" +
	    	    	bucket.calculateActualWinRate() + ";"
	    	);
	    }

	    //now have a bucket of results, calculate significance
	    //TODO: :-)
	    
	    //recast buckets into players
	    Map<String, List<PlayerResult>> resultsPerPlayer = new HashMap<String, List<PlayerResult>>();
	    for (PlayerResult result : playerResults) {
	    	String playerName = result.getPlayerName();
	    	List<PlayerResult> resultList = resultsPerPlayer.get(playerName);
	    	
	    	//player not yet on the map
	    	if (resultList == null) {
	    		resultList = new ArrayList<PlayerResult>();
	    	}

	    	//add new result
	    	resultList.add(result);

	    	//add to perPlayer data
	    	resultsPerPlayer.put(playerName, resultList);	    	
	    }

	    System.out.println("Number of players: " + resultsPerPlayer.keySet().size());
	    
	    //print out summary

	    for (String playerName : resultsPerPlayer.keySet()) {
	    	try {
				bw.write("Player{"+ playerName + "}: " + resultsPerPlayer.get(playerName).size() + "\r\n");

	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    }

	    //print out details
	    for (String playerName : resultsPerPlayer.keySet()) {
	    	try {
				bw.write("Player{"+ playerName + "}: ");
	    	for (PlayerResult result : resultsPerPlayer.get(playerName)) {
	    		bw.write("Opponent: " + result.getOpponentName() + ": " + result.getResult() + "/" + result.getStartingPrice() + "\r\n");
	    	}

	    	} catch (IOException e) {
				e.printStackTrace();
			}
	    }
/*
  	    //have a go at alignment

	    String pad1 = "";
	    for (int i = 0; i < (25 - market.getEventName().length()); i++) {
	    	pad1 += " ";
	    }

	    String pad2 = "";
	    for (int i = 0; i < (50 - market.getEventName().length() - player.getName().length() - pad1.length()); i++) {
	    	pad2 += " ";
	    }

	    //Print output values
	    System.out.println(market.getEventName() + ":" + pad1 + 
	    		player.getName() + ":" + 
	    		"(" + player.getStatus() + ")" + pad2 + 
	    		"pm(" + Utils.dp3(preMatchMean) + ", " + Utils.dp3(preMatchStdDev) + ")" +
	    		" ip(" + Utils.dp3(inPlayMean) + ", " + Utils.dp3(inPlayStdDev) + ")");

//    	    System.out.println("Mean of given vector is=" + Utils.dp3(mean));	    	
//    	    System.out.println("Standard deviation of given vector is=" + Utils.dp3(stdDev));	    	

	    System.out.println("");
*/	    
	} // analyse()

	/**
	 * evaluate whether this player winned or losed
	 *  - and their starting price
	 */
	private static PlayerResult result(Player player, MarketData market, Rengine engine) {
	
		PlayerResult playerResult = new PlayerResult();

		String preMatchTransactionVector = "c(";
	    String inPlayTransactionVector = "c(";
	    String pmAppendComma = "";
	    String ipAppendComma = "";

	    //use market time to distinguish pre match from in play
	    for (Transaction transaction : player.getTransactions()) {
//    	    	System.out.print(transaction.getPrice() + "|");
	    	if (transaction.getTime() < Utils.dateToMillis(market.getTime())) {
    			preMatchTransactionVector += pmAppendComma + transaction.getPrice();
    			pmAppendComma = ",";
	    	} else {
	    		inPlayTransactionVector += ipAppendComma + transaction.getPrice();
	    		ipAppendComma = ",";
	    	}
	    }
	    preMatchTransactionVector += ")";
	    inPlayTransactionVector += ")";

//    	    System.out.println("ipvector = " + inPlayTransactionVector);
//    	    System.out.println("pmvector = " + preMatchTransactionVector);

	    //The vector that was created in JAVA context is stored in 'rVector' which is a variable in R context.
	    engine.eval("rVector=" + preMatchTransactionVector);
	    engine.eval("meanValr=mean(rVector)");
	    engine.eval("sdValr=sd(rVector)");
    	    
	    engine.eval("tVector=" + inPlayTransactionVector);
	    engine.eval("meanValt=mean(tVector)");
	    engine.eval("sdValt=sd(tVector)");
    	    
	    //Retrieve mean and std dev values
	    double preMatchMean = engine.eval("meanValr").asDouble();
	    double preMatchStdDev = engine.eval("sdValr").asDouble();
	    double inPlayMean = engine.eval("meanValt").asDouble();
	    double inPlayStdDev = engine.eval("sdValt").asDouble();

	    //stack result
	    playerResult.setPlayerName(player.getName());
	    playerResult.setOpponentName(market.getEventName());
	    playerResult.setResult(player.getStatus());
	    playerResult.setStartingPrice(preMatchMean);
	    playerResult.setStdDev(preMatchStdDev);

	    return playerResult;
    } // result()
} // class Analyse
