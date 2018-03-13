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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import test.data.Data;
import test.data.MarketData;
import test.data.Player;
import test.data.Transaction;
import test.rawData.Mc;
import test.rawData.RawData;

public class Test {

//	public static final String yourWishMyLord = "New"; // import new data
//	public static final String yourWishMyLord = "Select"; // import new data
	public static final String yourWishMyLord = "Analyse"; // import new data
	public static final String DATA_FILE = "x_1801-1802.txt";
//	public static final String DATA_FILE = "x_1801-1802.buzzers.txt";
	public static final String INDEX_FILE = "y_1801-1802.txt";
	public static final String SELECTED_FILE = "data_1801-1802.txt";
	public static final String RESULTS_FILE = "z_1801-1802.txt";
	
	public static final String DATASOURCEFOLDER = ".";
//	public static final String DATASOURCEFOLDER = "C:\\Users\\Neil Berry\\Downloads\\Coding\\DeveloperHours\\neil";
	public static final String FILETYPESTOREAD = ".bz2";	
//	public static final String FILETYPESTOREAD = ".buzzers"; //a test file
	public static final boolean DEBUG = false;
//	public static final String FORENSIC = "1.139323890"; // CWoz v Halep
//	public static final String FORENSIC = "1.138143588";
	public static final String FORENSIC = "not very likely to match";
	public Test() {
		//no attributes
	} // Constructor

	//Public methods
	@SuppressWarnings("unused") //not all my lordly wishes are executed
	public void run() {

		Data data = new Data();
		FileOutputStream fout;
		FileInputStream fin;

		try {
			//read raw data
			if (yourWishMyLord == "New") {
				fout = new FileOutputStream(DATA_FILE);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));
				walk(DATASOURCEFOLDER, bw, data, 0);
//				Utils.prettilyPrint(data, bw);
				Utils.utilityPrint(data, bw);
				bw.close();
			}
			
			//That's the data created, now select some of it
			else if (yourWishMyLord == "Select") {
				fin = new FileInputStream(DATA_FILE);
				fout = new FileOutputStream(INDEX_FILE);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));

				//read the data that was created
				data = readDataFromFile(br);
				br.close();

				//analyse and output index of results
				Data selectedData = selectData(data, bw);
				bw.close();

				fout = new FileOutputStream(SELECTED_FILE);
				bw = new BufferedWriter(new OutputStreamWriter(fout));
				Utils.utilityPrint(selectedData, bw);
				bw.close();	
			}	
			
			//That's the data created, now analyse it
			else if (yourWishMyLord == "Analyse") {
				fin = new FileInputStream(SELECTED_FILE);
				fout = new FileOutputStream(RESULTS_FILE);
				BufferedReader br = new BufferedReader(new InputStreamReader(fin));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fout));

				//read the data that was created
				data = readDataFromFile(br);
				br.close();

				//analyse and output index of results
				Analyse.analyse(data);
				bw.close();
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
     * analyse the data
     *  - summarise what we have, discard markets not interested in, investigate the rest
     */
    private Data selectData(Data data, BufferedWriter bw) {
    	
    	Data selectedData = new Data();
    	
    	int marketCounter = 0;
    	int transactionCount = 0;
    	List<String> transactionCounts = new ArrayList<>();

    	try {

	    	//Summarise markets and volumes
	    	ArrayList<MarketData> marketDatas = new ArrayList<>();
	    	for (MarketData market : data.getMarketData().values()) {
	
	    		//singles
	    		if (market.getEventName().contains("/")) {
	    			continue;
	    		}
	
	    		System.out.println("Market: " + market.getEventName());
	    		bw.write("Market: " + market.getEventName() + "");
	    		marketCounter++;
	    		for (Player player : market.getPlayers().values()) {
	    			System.out.print("    Player[" + player.getName() + "]: ");
	    			bw.write("    Player[" + player.getName() + "]: ");
	        		for (Transaction transaction : player.getTransactions()) {
	    				System.out.print(transaction.getPrice() + ", ");
	    				bw.write(transaction.getPrice() + ", ");
	        				transactionCount++; 
	    			}
	    			System.out.println("-> [" + transactionCount + "]");
	    			bw.write("-> [" + transactionCount + "]" + "\r\n");
	    			transactionCounts.add(market.getId() + ", \"" + market.getEventName() + "\", " + player.getName() + ", " + transactionCount);

	    			System.out.println("Selecting[" + market.getEventName() + "]: " + transactionCount);
		    		if (transactionCount > 100) {
		    			selectedData.getMarketData().put(market.getId(), market);
		    		}
	    			transactionCount = 0;
	    		}
	    	}
	    	
    	} catch (IOException e) {
    		e.printStackTrace();
    	}

		try {

	    	System.out.println("the number of the markets = " + marketCounter);
				bw.write("the number of the markets = " + marketCounter + "; selected = " + selectedData.getMarketData().size() + "\r\n");
	    	
	    	for (String s : transactionCounts) {
	    		System.out.println(s);
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
