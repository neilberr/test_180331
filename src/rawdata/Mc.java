package rawdata;

import java.util.List;

public class Mc {
	private String id;
	private MarketDefinition marketDefinition;
	private List<Rc> rc;
	public Mc() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public MarketDefinition getMarketDefinition() {
		return marketDefinition;
	}
	public void setMarketDefinition(MarketDefinition marketDefinition) {
		this.marketDefinition = marketDefinition;
	}
	public List<Rc> getRc() {
		return rc;
	}
	public void setRc(List<Rc> rc) {
		this.rc = rc;
	}
} // class Mc	