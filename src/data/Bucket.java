package data;

import java.util.ArrayList;
import java.util.List;

import test.Analyse;

/**
 * class to hold a bucket of results from bottom to top
 *
 */
public class Bucket {
	private double bottom;
	private double top;
	private List<PlayerResult> playerResults;

	//Constructor
	public Bucket() {
		bottom = 0.0;
		top = 0.0;
		playerResults = new ArrayList<PlayerResult>();
	}
	public Bucket(double bottom) {
		this.bottom = bottom;
		this.top = bottom + Analyse.BUCKETSIZE;
		playerResults = new ArrayList<PlayerResult>();
	}

	//public methods
	/**
	 * calculate expected %age for this window
	 *  = 1/odds*100%
	 *   - adjusts for a generous bid-offer spread to be pessimistic about the actual price on offer
	 */
	public double calculateExpectedWinRate() {

		double width = top - bottom;
		double mid = bottom + width/2.0;
		double pessimistic = mid + Analyse.BIDOFFERSPREAD;
		double spanked = pessimistic*(1.0 + Analyse.SPANK);
		
		return 1.0/(spanked);
		
	}// calculateExpectedWinRate()

	/**
	 * calculate win/win+lose
	 * @return
	 */
	public double calculateActualWinRate() {
		int winCount = 0;
		int loseCount = 0;
		for (PlayerResult result : playerResults) {
			if (result.getResult().equals("WINNER")) {
				winCount += 1;
			} else if (result.getResult().equals("LOSER")) {
				loseCount += 1;
			} else {
//				System.out.println("oops(result): " + result.getResult());
			}
		}
		return winCount*1.0 / (winCount + loseCount);
	}// calculateActualWinRate()
	
	//Accessors and Mutators
	public double getBottom() {
		return bottom;
	}

	public void setBottom(double bottom) {
		this.bottom = bottom;
	}

	public double getTop() {
		return top;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public List<PlayerResult> getPlayerResults() {
		return playerResults;
	}

	public void setPlayerResults(List<PlayerResult> playerResults) {
		this.playerResults = playerResults;
	}
} // class Bucket
