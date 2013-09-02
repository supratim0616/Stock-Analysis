package com.feed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Initiator {

	/**
	 * @param args
	 */
	final static File folder = new File("config/");
	static Logger log = Logger.getLogger(Initiator.class.getName());

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			RSSFeed rss = new RSSFeed();
			StockData st = new StockData();
			File[] listOfFiles = folder.listFiles();
			for (File file : listOfFiles) {
				ArrayList<HashMap<String, String>> tickerCompList = gettickerComapnyNameList(file);
				for (int i = 0; i < tickerCompList.size(); i++) {
					HashMap<String, String> tickerCompName = tickerCompList
							.get(i);
					for (String key : tickerCompName.keySet()) {
						System.out.println(key);
						log.info("==================Started collection of feeds for===============================");
						log.info(" ticker : " + key + " market : "
								+ file.getName().replace(".txt", "")
								+ " company name : " + tickerCompName.get(key));
						rss.intiateFeedUploadProcess(key, file.getName()
								.replace(".txt", ""), tickerCompName.get(key));
						log.info("Started collection of price quotes ");
						st.intiateQuoteUploadProcess(key, file.getName()
								.replace(".txt", ""), tickerCompName.get(key));
						log.info("==============Finsihed=====================");
					}
				}

			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static ArrayList<HashMap<String, String>> gettickerComapnyNameList(
			File file) {
		ArrayList<HashMap<String, String>> tickerCompNameList = new ArrayList<HashMap<String, String>>();
		try {
			if (file.isFile()) {
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
				br.close();
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return tickerCompNameList;
	}

}
