package data;

import java.util.HashMap;
import java.util.Map;

import rawdata.Mc;
import rawdata.RawData;
import rawdata.Rc;
import rawdata.Runners;
import test.Utils;
import test.Test;

/**
 * class for all the markets
 *  - array of MarketData, one record for each market
 *  - MarketData contains a map of market records
 *  - nonMarketData is an index of ids for markets in which we are not interested in
 */
public class Data {

	private Map<String, MarketData> marketData;
	private Map<String, String> nonMarketData;

	//Constructor
	public Data() {
		marketData = new HashMap<String, MarketData>();
		nonMarketData = new HashMap<String, String>();
	} // Constructor

	//Public methods
	/**
	 * add a new record to MarketData array from market change message
	 *  - or update an existing record if market already in
	 *  - some mcs have market definition, some market definitions have runners
	 *    - if market definition is in blacklist, return
	 *  - some mcs have rcs
	 *   - if mc has rc only then no way to know if it is match_odds so add a new market data record anyway
	 *    - if mc has market definition and it is not match_odds then remove it again and add id to blacklist
	 * @param rawData - market change
	 */
	public void putMarketData(RawData rawData) {
		
		//raw data can have multiple market change messages (must have at least one)
		if (rawData.getMc().size() < 1) {
			System.out.println("ERROR: no mc in " + rawData.getOp() + 
					", pt = " + rawData.getPt() + 
					", clk = " + rawData.getClk());
			Runtime.getRuntime().halt(0);
		}

		//not all have marketDefinition but all have id, check it against blacklist
		for (Mc mc : rawData.getMc()) {
			if (!nonMarketData.containsKey(mc.getId())) {
				
				//copy non-null fields to attributes
				MarketData newMarketData = copyFrom(marketData.get(mc.getId()), mc, rawData.getPt());

				if (mc.getId().contains(Test.FORENSIC)) {
					Utils.prettilyPrint(newMarketData, null);
				}

				//if not a market in which we are not interested in
				if (newMarketData != null) {
					marketData.put(mc.getId(), newMarketData);
				} else {
					marketData.remove(mc.getId());
					nonMarketData.put(mc.getId(), mc.getMarketDefinition().getMarketType());
				}
			}
		}
	} // putMarketData()
	
	//Private methods
	/**
	 * copy non-null fields only from mc to market data
	 *  - pass me null if you want me to create a new market data
	 *  - creates a newMarket data record from mc
	 *   - then uses statuses in mc to decide if to copy data from the newMarket to the given market data
	 *   - if runner change has only one runner, update the other as a counter point 
	 *  - if this mc is not for our sort of thing then return null
	 */
	private MarketData copyFrom(MarketData marketData, Mc mc, long timeStamp) {

		//convert from rawdata to data format
		MarketData mcData = new MarketData(mc, timeStamp);

		if (mc.getId().contains(Test.FORENSIC)) {
//			Utils.prettilyPrint(mcData, null);
		}

		//get given null if market data is to be created
		if (marketData == null) {
			marketData = new MarketData();
		}

		//if there is market definition, copy market definition
		if (mc.getMarketDefinition() != null &&
				mc.getMarketDefinition().getMarketType() != null) {
			
			//one track mind and singles
			if (!mc.getMarketDefinition().getMarketType().equals("MATCH_ODDS") ||
					mc.getMarketDefinition().getRunners().get(0).getName().contains("/")) {
				return null;
			} else {
				
				//check if going in play
				marketData.setId(mc.getId());
/*				System.out.println("timestatus: " + 
						Utils.millisToDateTime(timeStamp) + ": " + mcData.getStatus() + "/" + 
						mc.getMarketDefinition().isInPlay());
*/
				if (marketData.getTime().equals("")) {
					if (mc.getMarketDefinition().isInPlay()) {
						marketData.setTime(Utils.millisToDateTime(timeStamp));
					}
				}
				if (mcData.getName() != null) {
					marketData.setName(mcData.getName());
				}
				if (mcData.getMarketType() != null) {
					marketData.setMarketType(mcData.getMarketType());
				}
				if (mcData.getEventName() != null) {
					marketData.setEventName(mcData.getEventName());
				}
				
				//if there is runners, update or create players
				if (mc.getMarketDefinition().getRunners() != null &&
						mc.getMarketDefinition().getRunners().size() > 0) {
					for (Runners runner : mc.getMarketDefinition().getRunners()) {
						Player existingPlayer = marketData.getPlayers().get(runner.getId());
						
						//update existing player
						if (existingPlayer != null) {
							if (runner.getName() != null) {
								existingPlayer.setName(runner.getName());
							}
							if (runner.getStatus() != null) {
								existingPlayer.setStatus(runner.getStatus());
							}
						}
						
						//create new player
						else {
							existingPlayer = new Player(runner);
						}
						
						//overwrite/create
						marketData.getPlayers().put(runner.getId(), existingPlayer);
					} 
				}
			}
		}

		//if there is runner change (can occur with or without market definition) update players
		if (mc.getRc() != null &&
				mc.getRc().size() > 0) {
			for (Rc rc : mc.getRc()) {
				
				//update player from runner
				Player existingPlayer = marketData.getPlayers().get(rc.getId());
				if (existingPlayer != null) {
					existingPlayer.addNewTransaction(rc, timeStamp);
				}
				
				//create new player
				else {
					marketData.addNewPlayer(rc, timeStamp);
				}
				
				//a transaction for only one player is present so the other player needs a counter point
				if (mc.getRc().size() < 2 && marketData.getPlayers().size() > 1) { //check if other player exists yet
					for (Player otherPlayer : marketData.getPlayers().values()) {
						if (otherPlayer != null && existingPlayer != null) {
							if (!(otherPlayer.getId() == existingPlayer.getId())) {
								Transaction counterTransaction = new Transaction(
										otherPlayer.getId(), timeStamp, Utils.counterPoint(rc.getLtp()), true);
								marketData.getPlayers().get(otherPlayer.getId()).getTransactions().add(counterTransaction);
							}
						}
					}
				}
			}
		}

		//TODO:
		// - probably should store and check timestamps to only overwrite older updates.
		// - don't just add runner change, check if a previous update with same timestamp is already there and overwrite it
			
		return marketData;
		
	} // copyFrom()

	//Accessors and Mutators
	public Map<String, MarketData> getMarketData() {return marketData;}
	public void setMarketData(Map<String, MarketData> marketData) {this.marketData = marketData;}	
} // class Data
