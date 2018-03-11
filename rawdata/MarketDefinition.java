package rawdata;

import java.util.List;

public class MarketDefinition {
	boolean bspMarket; 
	boolean turnInPlayEnabled;
	boolean persistenceEnabled;
	double marketBaseRate;
	String eventId;
	String eventTypeId;
	int numberOfWinners;
	String bettingType;
	String marketType;
	String marketTime;
	String suspendTime;
	boolean bspReconciled;
	boolean complete;
	boolean inPlay;
	boolean crossMatching;
	boolean runnersVoidable;
	int numberOfActiveRunners;
	int betDelay;
	String status;
	List<Runners> runners;
	String settledTime;
	List<String> regulators;
	String countryCode;
	boolean discountAllowed;
	String timezone;
	String openDate;
	long version;
	String name;
	String eventName;

	//Constructor
	public MarketDefinition() {
	}

	//Accessors and Mutators
	public boolean isBspMarket() {return bspMarket;}
	public void setBspMarket(boolean bspMarket) {this.bspMarket = bspMarket;}
	public boolean isTurnInPlayEnabled() {return turnInPlayEnabled;}
	public void setTurnInPlayEnabled(boolean turnInPlayEnabled) {this.turnInPlayEnabled = turnInPlayEnabled;}
	public boolean isPersistenceEnabled() {return persistenceEnabled;}
	public void setPersistenceEnabled(boolean persistenceEnabled) {this.persistenceEnabled = persistenceEnabled;}
	public double getMarketBaseRate() {return marketBaseRate;}
	public void setMarketBaseRate(double marketBaseRate) {this.marketBaseRate = marketBaseRate;}
	public String getEventId() {return eventId;}
	public void setEventId(String eventId) {this.eventId = eventId;}
	public String getEventTypeId() {return eventTypeId;}
	public void setEventTypeId(String eventTypeId) {this.eventTypeId = eventTypeId;}
	public int getNumberOfWinners() {return numberOfWinners;}
	public void setNumberOfWinners(int numberOfWinners) {this.numberOfWinners = numberOfWinners;}
	public String getBettingType() {return bettingType;}
	public void setBettingType(String bettingType) {this.bettingType = bettingType;}
	public String getMarketType() {return marketType;}
	public void setMarketType(String marketType) {this.marketType = marketType;}
	public String getMarketTime() {return marketTime;}
	public void setMarketTime(String marketTime) {this.marketTime = marketTime;}
	public String getSuspendTime() {return suspendTime;}
	public void setSuspendTime(String suspendTime) {this.suspendTime = suspendTime;}
	public boolean isBspReconciled() {return bspReconciled;}
	public void setBspReconciled(boolean bspReconciled) {this.bspReconciled = bspReconciled;}
	public boolean isComplete() {return complete;}
	public void setComplete(boolean complete) {this.complete = complete;}
	public boolean isInPlay() {return inPlay;}
	public void setInPlay(boolean inPlay) {this.inPlay = inPlay;}
	public boolean isCrossMatching() {return crossMatching;}
	public void setCrossMatching(boolean crossMatching) {this.crossMatching = crossMatching;}
	public boolean isRunnersVoidable() {return runnersVoidable;}
	public void setRunnersVoidable(boolean runnersVoidable) {this.runnersVoidable = runnersVoidable;}
	public int getNumberOfActiveRunners() {return numberOfActiveRunners;}
	public void setNumberOfActiveRunners(int numberOfActiveRunners) {this.numberOfActiveRunners = numberOfActiveRunners;}
	public int getBetDelay() {return betDelay;}
	public void setBetDelay(int betDelay) {this.betDelay = betDelay;}
	public String getStatus() {return status;}
	public void setStatus(String status) {this.status = status;}
	public String getSettledTime() {return settledTime;}
	public void setSettledTime(String settledTime) {this.settledTime = settledTime;}
	public List<Runners> getRunners() {return runners;}
	public void setRunners(List<Runners> runners) {this.runners = runners;}
	public List<String> getRegulators() {return regulators;}
	public void setRegulators(List<String> regulators) {this.regulators = regulators;}
	public String getCountryCode() {return countryCode;}
	public void setCountryCode(String countryCode) {this.countryCode = countryCode;}
	public boolean isDiscountAllowed() {return discountAllowed;}
	public void setDiscountAllowed(boolean discountAllowed) {this.discountAllowed = discountAllowed;}
	public String getTimezone() {return timezone;}
	public void setTimezone(String timezone) {this.timezone = timezone;}
	public String getOpenDate() {return openDate;}
	public void setOpenDate(String openDate) {this.openDate = openDate;}
	public long getVersion() {return version;}
	public void setVersion(long version) {this.version = version;}
	public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	public String getEventName() {return eventName;}
	public void setEventName(String eventName) {this.eventName = eventName;}
} // class MarketDefinition
