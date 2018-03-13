package rawdata;

import java.util.ArrayList;
import java.util.List;

public class RawData {
	
	private String op;
	private String clk;
	private long pt;
	private List<Mc> mc;
	
	//Constructor
	public RawData() {
		op = "op";
		clk = "clk";
		pt = 0l;
		mc = new ArrayList<Mc>();
	} // Constructor

	//Methods
	public String toString() {
		String s = "";
		int mcCount = 0;
		for (Mc i : mc) {
			if (i.getMarketDefinition() != null &&
					i.getMarketDefinition().getMarketTime() != null &&
					i.getMarketDefinition().getMarketType() != null &&
					i.getMarketDefinition().getMarketType().equals("MATCH_ODDS")) {
				s += "mc" + mcCount +": id = "+ safeString(i.getId()) + ", " +
						"time: " + safeString(i.getMarketDefinition().getMarketTime()) + ", " +
						"type: " + safeString(i.getMarketDefinition().getMarketType()) + ", " +
						"event: " + safeString(i.getMarketDefinition().getEventName()) + ", " +
						"name: " + safeString(i.getMarketDefinition().getName()) + 
	 					"\n"; 
				mcCount++;
			}
		}
		return s;
	} // toString
	
	//private methods
	private String safeString(String s) {
		if (s == null) {
			return "";
		} else {
			return s;
		}
	} // safeString()
	public String getOp() {return op;}
	public void setOp(String op) {this.op = op;}
	public String getClk() {return clk;}
	public void setClk(String clk) {this.clk = clk;}
	public long getPt() {return pt;}
	public void setPt(long pt) {this.pt = pt;}	
	public List<Mc> getMc() {return mc;}
	public void setMc(List<Mc> mc) {this.mc = mc;}
} // class RawData
