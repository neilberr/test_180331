package test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import data.Data;
import data.MarketData;

public class Utils {

	/**
	 * use Json to print out data
	 */
	public static void prettilyPrint(Object o, BufferedWriter bw) {
		ObjectMapper mapper = new ObjectMapper();
	    mapper.enable(SerializationFeature.INDENT_OUTPUT);
	    try {
			String jsonString = mapper.writeValueAsString(o);
	    	System.out.println(jsonString);
	    	if (bw != null) {
	    		bw.write(jsonString);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	} // prettilyPrint()
	
	/**
	 * use Json to print out data as one line per market data
	 */
	public static void utilityPrint(Data data, BufferedWriter bw) {
		ObjectMapper mapper = new ObjectMapper();
	    try {
	    	for (MarketData market : data.getMarketData().values()) {
				String jsonString = mapper.writeValueAsString(market);
//		    	System.out.println(jsonString);
		    	if (bw != null) {
		    		bw.write(jsonString + "\r\n");
		    	}
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	} // utilityPrint()

	/**
	 * transform milliseconds as yyyy-mm-dd HH:mm:ss.nnn
	 */
	public static String millisToDateTime(long millis) {
		Date date=new Date(millis);
		String dateFormat = "yyyy-MM-dd HH:mm:ss.SSS";
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
	    
//	    System.out.println("date[" + millis + "]: " + simpleDateFormat.format(date));
	    
		return simpleDateFormat.format(date);
	} // millisToDateTime

	/**
	 * transform date as yyyy-mm-ddThh:mm:ss.nnn to millis
	 *  - return 0 if blank (indicates never went in play)
	 */
	public static long dateToMillis(String dateInput) {
		String date1 = dateInput.replace('T', ' ');
		String date2 = date1.split("Z")[0];
		long timeInMillis = 0l;

		if (dateInput.equals("")) {
			return 0l;
		}

		//creates a formatter that parses the date in the given format
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date;
		try {
			date = sdf.parse(date2);
			timeInMillis = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//	    System.out.println("date[" + millis + "]: " + simpleDateFormat.format(date));
	    
		return timeInMillis;
	} // dateToMillis()

	/**
	 * calculate counter point
	 */
	public static double counterPoint(double point) {
		
		if (point <= 0.0) {
			return 0.0;
		} else {
			return (1.0 + 1.0/(point - 1.0));
		}
	} // counterPoint()

	/**
	 * format a double as a percentage pp.ppp%
	 */
	public static String formatPercent(double percentValue) {
		
		return (String.format("%.2f%%", percentValue * 100.0));
			
	} // formatPercent()

	/**
	 * format a double as 3 dp
	 */
	public static String dp3(double doubleValue) {
		
		DecimalFormat df2 = new DecimalFormat("0.##");

		return df2.format(doubleValue);
			
	} // formatPercent()

} // class Utils
