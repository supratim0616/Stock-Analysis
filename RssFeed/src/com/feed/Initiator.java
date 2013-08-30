package com.feed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import org.apache.log4j.Logger;


public class Initiator {

	/**
	 * @param args
	 */
	final static File folder = new File("config/");
	static Logger log = Logger.getLogger(Initiator.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		RSSFeed rss = new RSSFeed();
		StockData st = new StockData();
		String[] tickerSymbol = getTickerSymbolsList();
		// for (int j = 0; j < tickerSymbol.length; j++) {
		// String[] tickerList = getTickers(tickerSymbol[j]);
		// for (int i = 0; i < tickerList.length; i++) {
		//rss.intiateFeedUploadProcess("SIRI", "NASDAQ");
		// st.intiateQuoteUploadProcess(tickerList[i], tickerSymbol[j]);
		// }

		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			ArrayList<HashMap<String, String>> tickerCompList =gettickerComapnyNameList(file);
			for(int i=0;i<tickerCompList.size();i++)
			{
				HashMap<String, String> tickerCompName = tickerCompList.get(i);
				for ( String key : tickerCompName.keySet() ) {
				    System.out.println( key );
				    log.info("Ticker is " + key + " market is " + file.getName().replace(".txt", "") + " company name is" + tickerCompName.get(key));
				    rss.intiateFeedUploadProcess(key,file.getName().replace(".txt", ""),tickerCompName.get(key));	
				    //st.intiateQuoteUploadProcess(key,file.getName().replace(".txt", ""),tickerCompName.get(key));
				}
			}

		}
	}

	// Method for retrieving Tickers corresponding to a ticker symbol.
	private static String[] getTickers(String tickerSymbol) {
		Properties prop = new Properties();
		String[] tickerList = {};
		try {
			// load a properties file
			prop.load(new FileInputStream("ticker.properties"));
			// get the property value and print it out
			String val = prop.getProperty(tickerSymbol);
			tickerList = val.split(",");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tickerList;

	}

	// Method for retrieving ticker symbols.
	private static String[] getTickerSymbolsList() {
		Properties prop = new Properties();
		String[] tickerSymbolList = {};
		try {
			// load a properties file
			prop.load(new FileInputStream("ticker.properties"));
			// get the property value and print it out
			String val = prop.getProperty("tickerlist");
			tickerSymbolList = val.split(",");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return tickerSymbolList;

	}

	private static ArrayList<HashMap<String, String>> gettickerComapnyNameList(
			File file) {
		ArrayList<HashMap<String, String>> tickerCompNameList = new ArrayList<HashMap<String, String>>();
		try {
			if (file.isFile()) {
				System.out.println("file is" + file);
				File path = file;
				// String content = FileUtils.readFileToString(file);
				// System.out.println(content);
				FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr);
				while (br.ready()) {
					// System.out.println( br.readLine());
					String[] tickerCompName = br.readLine().split(",");
					HashMap<String, String> ticker = new HashMap<String, String>();
					ticker.put(tickerCompName[0], tickerCompName[1]);
					tickerCompNameList.add(ticker);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return tickerCompNameList;
	}

	private static void listFilesForFolder(File folder) {
		try {
			File[] listOfFiles = folder.listFiles();
			
			for (File file : listOfFiles) {
				if (file.isFile()) {
					log.info("File to be processed is " + file);
					File path = file;
					FileReader fr = new FileReader(path);
					BufferedReader br = new BufferedReader(fr);
					ArrayList<HashMap<String, String>> tickerCompNameList = new ArrayList<HashMap<String, String>>();
					while (br.readLine() != null) {
						String[] tickerCompName = br.readLine().split(",");
						HashMap<String, String> ticker = new HashMap<String, String>();
						ticker.put(tickerCompName[0], tickerCompName[1]);
						tickerCompNameList.add(ticker);
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}

	}
}
