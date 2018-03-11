package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import data.Data;
import data.MarketData;
import data.Player;
import data.Transaction;
import rawdata.Mc;
import rawdata.RawData;

/**
 * test
 *  - proceeds in three phases, any one can be ordered, or all of them
 *   - first read all the raw data and store match odds only in new file (New)
 *   - then open new file, apply the selection criteria and create data file and index to the data file (Select)
 *   - then open data file and analyse the heck out of it (Analyse)
 */
public class Test {

//	public static final String yourWishMyLord = "All"; // import select and analyse

//Import new data from data source folder into new file
//	public static final String yourWishMyLord = "New";
	public static final String DATASOURCEFOLDER = "C:\\Users\\MyLatop\\Downloads\\xdata"; // non-1.1*.bz files
	public static final String FILETYPESTOREAD = ".bz2";
	public static final String NEW_FILE = "new_1801-1802.txt";
//	public static final String NEW_FILE = "x_1801-1802.buzzers.txt"; // small test

//Select markets from new file and write index file and data file
	public static final String yourWishMyLord = "Select"; // select markets based on 5 rules
	public static final String INDEX_FILE = "select_index.txt"; // lists markets in or out
	public static final String DATA_FILE = "data_yymmdd.txt"; // lists markets in or out

//	public static final String yourWishMyLord = "Analyse"; // analyse data

//	public static final String DATASOURCEFOLDER = ".";
//	public static final String DATASOURCEFOLDER = "C:\\Users\\MyLatop\\Downloads\\xdata";
//	public static final String DATASOURCEFOLDER = "C:\\Users\\Neil Berry\\Downloads\\Coding\\DeveloperHours\\neil"; // work
//	public static final String DATASOURCEFOLDER = "C:\\Users\\MyLatop\\Downloads\\old\\AWealth\\bf\\data\\test\\files\\data"; // 1.1*.bz files
//	public static final String FILETYPESTOREAD = ".buzzers"; //a test file
	public static final boolean DEBUG = false;
//	public static final String FORENSIC = "1.139323890"; // CWoz v Halep
//	public static final String FORENSIC = "1.138143588";
//	public static final String FORENSIC = "1.122430932"; // Rafa v Ferrer
	public static final String FORENSIC = "not very likely to match";

	//Constructor
	public Test() {
		//no attributes
	} // Constructor

	//Public methods
	@SuppressWarnings("unused") //not all my lordly wishes are executed
	public void run() {

		Data data = new Data();
		FileOutputStream fout;
		FileInputStream fin;

		System.out.println("Your wish *" + yourWishMyLord + "* is my command:");

		try {
			//read raw data
			if (yourWishMyLord == "New" || yourWishMyLord == "All") {
				System.out.println("I renew...");
				fout = new FileOutputStream(NEW_FILE);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
				walk(DATASOURCEFOLDER, bw, data, 0);
//				Utils.prettilyPrint(data, bw);
				Utils.utilityPrint(data, bw);
				bw.close();
			}
			
			//That's the data created, now select the best bits
			if (yourWishMyLord == "Select" || yourWishMyLord == "All") {
				System.out.println("I select...");

				//read all the data
				System.out.println("Reading... " + NEW_FILE);
				fin = new FileInputStream(NEW_FILE);
				BufferedReader dataFile = new BufferedReader(new InputStreamReader(fin));
				data = readDataFromFile(dataFile);
				dataFile.close();

				//select and write index file of what got selected and excluded and why
				System.out.println("Selecting... " + INDEX_FILE);
				fout = new FileOutputStream(INDEX_FILE);
				BufferedWriter indexFile = new BufferedWriter(new OutputStreamWriter(fout));
				Data dataSet = selectData(data, indexFile);
				indexFile.close();

				//write data file of selected data only
				System.out.println("Writing... " + DATA_FILE);
				fout = new FileOutputStream(DATA_FILE);
				BufferedWriter selectedFile = new BufferedWriter(new OutputStreamWriter(fout));
				Utils.utilityPrint(dataSet, selectedFile);
				selectedFile.close();
			}
			//That's the data selected, now analyse the pants off it
			if (yourWishMyLord == "Analyse" || yourWishMyLord == "All") {
				System.out.println("I analyse...");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	} // run()

	//Private Methods
	/**
	 * read one file, if a directory, step in, if a file, read the raw data
	 */
    private int walk(String path, BufferedWriter bw, Data data, int fileNumber) {
    	
		File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return fileNumber;
        
        for ( File f : list ) {
            if ( f.isDirectory() ) {
                fileNumber = walk(f.getAbsolutePath(), bw, data, fileNumber);
//	                System.out.println( "Dir:" + f.getAbsoluteFile() );
            }
            else {
            	fileNumber += 1;
                if (f.getAbsolutePath().contains(FILETYPESTOREAD)) {
                    System.out.println( "File[" + fileNumber + "]: " + f.getAbsoluteFile().getPath());
                	List<RawData> rawDataList = readRawDataFromFile(f.getAbsoluteFile().getPath(), bw);
                	for (RawData i : rawDataList) {
                		data.putMarketData(i);
                	}
                }
            }
        }
        return fileNumber;
    } // walk ()

	/**
	 * read all the data records from the file into data
	 *  - this file would have been created by running with my lordly wish = new
	 */
	private Data readDataFromFile(BufferedReader br) {

		Data data = new Data();
		String lineFromFile;
		MarketData marketData;
			
		try {
			while ((lineFromFile = br.readLine()) != null) {
					
				if (DEBUG || lineFromFile.contains(FORENSIC)) {
					System.out.println("lineFromDataFile: " + lineFromFile);
				}

				//read one Data record from one line of the file
				marketData = readDataFromJson(lineFromFile);
				data.getMarketData().put(marketData.getId(), marketData);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
			
		return data;
	} // readDataFromFile()
    
    /**
     * select the data according to the criteria
     *  - summarise what we have, discard markets not interested in
     *   - and return the rest
     *  - write a file of which were selected and rejected, and why
     */
    private Data selectData(Data data, BufferedWriter bw) {
    	
    	Data selectedData = new Data();

    	int marketCounter = 0;
    	int transactionCount = 0;
    	double marketMinutes = 0.0;

    	//composed of elements for printing out market summary
    	List<String> marketSummaryMessages = new ArrayList<>();

    	try {

	    	//Summarise markets and volumes
	    	for (MarketData market : data.getMarketData().values()) {
	
	    		//singles
	    		if (market.getEventName().contains("/")) {
	    			continue;
	    		}
	
//	    		System.out.println("Market[" + market.getId() + "]: " + market.getEventName() + " (" + market.getTime() + ")");
//	    		bw.write(market.getId() + ", " + market.getEventName() + ", " + market.getTime());
	    		marketCounter++;
	    		long marketStartTime = Utils.dateToMillis(market.getTime());
	    		long marketEndTime = marketStartTime;
	    		
	    		//title for each player
	    		for (Player player : market.getPlayers().values()) {
//	    			System.out.println("    Player[" + player.getName() + "]:");
//	    			bw.write("    Player[" + player.getName() + "], ");

        			//reset transaction count because both players have the same number
        			// - so only need to remember the second one
        			transactionCount = 0;

	    			//tag in-play
	        		for (Transaction transaction : player.getTransactions()) {


	        			//move end time on so it ends up as the last transaction for either player
	        			if (transaction.getTime() > marketEndTime) {
	        				marketEndTime = transaction.getTime();
	        			}

	        			String countered = "";
    					if (transaction.isCountered()) {
    						countered = ", countered";
    					}

    					//check if in play yet (0 means never went in play
	    				if (marketStartTime == 0 || transaction.getTime() < marketStartTime) {
	        				
/*		    				System.out.println("        " + 
		    						Utils.millisToDateTime(transaction.getTime()) + ", " + 
		    						transaction.getPrice() + ", " + 
		    						"pre-match" + countered);
*/	    				} else {

							//very important, only care about in-play
							transactionCount++;
/*		    				System.out.println("        " + 
		    						Utils.millisToDateTime(transaction.getTime()) + ", " + 
		    						transaction.getPrice() + ", " + 
		    						"in-play" + countered);
*/	    				}	
//	    				bw.write(transaction.getTime() + ", " + transaction.getPrice() + ", ");
	    				
    				} // that's all the transactions for one player
//	    			System.out.println("-> [" + transactionCount + "]" + " Coverage: " + "91%");
//	    			bw.write("-> [" + transactionCount + "]" + "\r\n");
	    			
	    		} //that's all the players for this market

	    		//market summary string (csv)
        		String startTime;
        		if (market.getTime().equals("")) {
        			startTime = "never in-play";
        		} else {
        			startTime = market.getTime();
        			marketMinutes = (marketEndTime - marketStartTime)/1000/60;
        		}
        		
        		//decide in our out
        		double coverage = 0.0;
        		String selected = "";
        		String reason = "";

        		//Rule 1
        		if (startTime.equals("never in-play")) {
        			selected = "No";
        			reason = "never in-play";
        		} 
        		
        		//Rule 2
        		else if (marketMinutes > 300.0) {
        			selected = "No";
        			reason = "longer than 300mins";        			
        		}

        		//Rule 3
        		else if (marketMinutes < 40.0) {
        			selected = "No";
        			reason = "less than 40mins";
        		}

        		//Rule 4
        		else if (transactionCount > (marketMinutes - 5)) {
        			selected = "Yes";
        			selectedData.getMarketData().put(market.getId(), market);
        			reason = "less than 5 mins missing";
        			coverage = 1;
        		}

        		//Rule 5
        		else {
        			coverage = transactionCount/marketMinutes;
        			if (coverage >= 0.9) {
            			selected = "Yes";
            			selectedData.getMarketData().put(market.getId(), market);
            			reason = "coverage > 90%";
        			} else {
        				selected = "No";
            			reason = "coverage < 90%";        				
        			}
        		}

        		//comprise the string which summarises this market to write to file as csv
    			marketSummaryMessages.add(
    					market.getId() + ", " + 
    					startTime + ", " + 
    					"\"" + market.getEventName() + "\", " + 
    					transactionCount + ", " +
    					Utils.millisToDateTime(marketEndTime) + ", " +
    					marketMinutes + ", " +
    					Utils.formatPercent(coverage) + ", " +
    					selected + ", " +
    					reason);
    			transactionCount = 0;
	    	} // that's all the markets

	    	bw.write(""); //allow us to have the catch block if there is no other bw.writing

    	} catch (IOException e) {
    		e.printStackTrace();
    	}

    	//write the final totals
		try {
	    	System.out.println("the number of the markets = " + marketCounter + "; " +
	    			"specially selected = " + selectedData.getMarketData().size());
			bw.write("the number of the markets = " + marketCounter + "\r\n");
	    	
	    	for (String s : marketSummaryMessages) {
	//	    		System.out.println(s);
	    		bw.write(s + "\r\n");
	    	}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return selectedData;
    } // selectData()
    
    /**
     * just open a .bz2 file
     */
	private BufferedReader getBufferedReaderForCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
//		System.out.println("getBufferedReaderFor: " + fileIn);
	    FileInputStream fin = new FileInputStream(fileIn);
	    BufferedInputStream bis = new BufferedInputStream(fin);
	    CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
	    BufferedReader br = new BufferedReader(new InputStreamReader(input));
	    return br;
	} // getBuff...()

	/**
	 * open a not .bz2 file
	 */
	private BufferedReader getBufferedReaderForUnCompressedFile(String fileIn) throws FileNotFoundException, CompressorException {
//		System.out.println("getBufferedReaderFor: " + fileIn);
	    FileInputStream fin = new FileInputStream(fileIn);
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
	    return br;
	} // getBuffUn...()

	/**
	 * read all the transactions from one file into raw data
	 */
	private List<RawData> readRawDataFromFile(String fileName, BufferedWriter bw) {
		BufferedReader br;
		List<RawData> rawDataList = new ArrayList<RawData>();

		try {
			if (fileName.contains(".bz2")) {
			br = getBufferedReaderForCompressedFile(fileName);
		} else {
			br = getBufferedReaderForUnCompressedFile(fileName);
		}
			String lineFromFile;
			
			while ((lineFromFile = br.readLine()) != null) {
				
				if (DEBUG || lineFromFile.contains(FORENSIC)) {
					System.out.println("lineFromRawDataFile: " + lineFromFile);
				}

				//read one RawData record from one line of the file
				RawData rawData = readRawDataFromJson(lineFromFile);
	    		rawDataList.add(rawData);
	    		
	    		for (Mc mc : rawData.getMc()) {
	    			if (mc.getId().contains(FORENSIC)) {
//	    				Utils.prettilyPrint(mc, null);
	    			}
	    		}
	
	    		//decide if anything interesting to print
		    	String outP = rawData.toString();
		    	if (!outP.equals("")) {
//		    		bw.write(outP + "\r\n");
		    	}
//				bw.write(data.toString() + "\r\n");
			}
			
			br.close();

		} catch (IOException | CompressorException e) {
			e.printStackTrace();
		}
		return rawDataList;
	} // readRawDataFromFile()

	/**
	 * read all the json from one transaction into raw data
	 */
	private RawData readRawDataFromJson(String json) {
		RawData rawData = new RawData();
		ObjectMapper mapper = new ObjectMapper();

		try {
			rawData = mapper.readValue(
					json, new TypeReference<RawData>() { });
	         
		    mapper.enable(SerializationFeature.INDENT_OUTPUT);
		    if (DEBUG) {
			    String jsonString = mapper.writeValueAsString(rawData);
		    	System.out.println(jsonString);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}

	    return rawData;
	} // readRawDataFromJson()	
	
	/**
	 * read all the json from one transaction into market data
	 */
	private MarketData readDataFromJson(String json) {
		MarketData data = null;
		ObjectMapper mapper = new ObjectMapper();

		try {
			data = mapper.readValue(
					json, new TypeReference<MarketData>() { });
	         
		    mapper.enable(SerializationFeature.INDENT_OUTPUT);
		    if (DEBUG) {
			    String jsonString = mapper.writeValueAsString(data);
		    	System.out.println(jsonString);
		    }
		} catch (IOException e) {
			e.printStackTrace();
		}

	    return data;
	} // readDataFromJson()	

} // class Test
