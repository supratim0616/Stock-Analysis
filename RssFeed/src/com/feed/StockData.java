package com.feed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

import com.bean.Stock;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class StockData {

	/**
	 * @param args
	 */
	static Logger log = Logger.getLogger(Initiator.class.getName());

	/**
	 * This methods encapsulates all the other other methods defined in the
	 * class and write the json data to appropriate files for uploading on s3
	 * and then delete the local copy of file.
	 * 
	 * @param stockTicker
	 * @param market
	 * @param CompanyName
	 */
	void intiateQuoteUploadProcess(String stockTicker, String market,
			String CompanyName) {
		// Included loop to search for quote feeds till 3 days before
		for (int i = 0; i < 3; i++) {
			Date now = new Date();
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(now);
			gc.add(Calendar.DAY_OF_YEAR, -i);
			Date date = gc.getTime();
			System.out.println(date);
			File file = getNewsFeed(stockTicker, market, date);
			S3FileUpload.uploadFileonS3(file);S3FileUpload.uploadFileonS3(file);
			file.delete();
		}
	}

	private File getNewsFeed(String stockTicker, String market, Date date) {
		URL url;
		String filename = getFilename(stockTicker, market, date);
		File file = new File(filename);
		try {
			url = new URL("http://ichart.yahoo.com/table.csv?s=" + stockTicker
					+ "&g=d&ignore=.csv");
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			CSVReader csvReader = new CSVReader(in, ',', '\'',1);
			String[] row = null;
			Stock stock = new Stock();
			int i = 0 ;
			while ((row = csvReader.readNext()) != null) {
				// bw.write(inputLine);
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				DateFormat df1 = new SimpleDateFormat("HH:mm:ss");
				df1.setTimeZone(TimeZone.getTimeZone("GMT"));
				// get current date time with Date()
				ArrayList<Double> high52week = get52weekHighLow(stockTicker);
				if ( i < 2)
				{
					if (row[0].equals(dateFormat.format(date))) {
						stock.setStock_date(row[0]);
						stock.setOpen(Double.parseDouble(row[1]));
						stock.setHigh(Double.parseDouble(row[2]));
						stock.setLow(Double.parseDouble(row[3]));
						stock.setClose(Double.parseDouble(row[4]));
						stock.setVolume(Double.parseDouble(row[5]));
						stock.setAdj_Close(Double.parseDouble(row[6]));
						stock.setHigh52week(high52week.get(0));
						stock.setLow52week(high52week.get(1));
						stock.setDate(filename.substring(6, 14));
						stock.setTime(df1.format(date));
						stock.setMarket(market);
						stock.setTicker(stockTicker);
						break;
				}}
				else {
				if (row[0].equals(dateFormat.format(date))) {
					stock.setStock_date(row[0]);
					stock.setOpen(Double.parseDouble(row[1]));
					stock.setHigh(Double.parseDouble(row[2]));
					stock.setLow(Double.parseDouble(row[3]));
					stock.setClose(Double.parseDouble(row[4]));
					stock.setVolume(Double.parseDouble(row[5]));
					stock.setAdj_Close(Double.parseDouble(row[6]));
					stock.setHigh52week(high52week.get(0));
					stock.setLow52week(high52week.get(1));
					stock.setDate(filename.substring(6, 14));
					stock.setTime(df1.format(date));
					stock.setMarket(market);
					stock.setTicker(stockTicker);
				} else {
					stock.setStock_date("");
					stock.setOpen(0.0);
					stock.setHigh(0.0);
					stock.setLow(0.0);
					stock.setClose(0.0);
					stock.setVolume(0.0);
					stock.setAdj_Close(0.0);
					stock.setHigh52week(high52week.get(0));
					stock.setLow52week(high52week.get(1));
					stock.setDate(filename.substring(6, 14));
					stock.setTime(df1.format(date));
					stock.setMarket(market);
					stock.setTicker(stockTicker);
				}
				}
				i ++ ;
				if (i == 3)
					break;
			}
			csvReader.close();
			wrteTojson(filename, stock, file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return file;
	}

	/**
	 * This methods create a new file write the data to file in json format
	 * 
	 * @param filename
	 *            - name of the file
	 * @param itemList
	 *            - list of feed items
	 */
	private File wrteTojson(String filename, Stock stock, File file) {
		try {

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			FileWriter writer = new FileWriter(file.getAbsoluteFile(), true);
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			String json = gson.toJson(stock);
			writer.write(json);
			writer.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return file;
	}

	/**
	 * Returns the filename
	 * 
	 * @param stockTicker
	 * @param market
	 * @param date
	 * @return String
	 */
	private String getFilename(String stockTicker, String market, Date date ) {
		String filename = "";
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
			// get current date time with Date()
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			filename = "QUOTE_" + dateFormat.format(date) + market
					+ stockTicker + ".json";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return filename;
	}

	/**
	 * This methods calls the url with parameter g=w (weekly) to get weekly data
	 * and then compare 52 weeks of data to get values.
	 * 
	 * @param stockTicker
	 *            - name of the file
	 * @return ArrayList
	 */
	private ArrayList<Double> get52weekHighLow(String stockTicker) {
		URL url;
		ArrayList<Double> highLowIn52Week = new ArrayList<Double>(10);
		try {
			url = new URL("http://ichart.yahoo.com/table.csv?s=" + stockTicker
					+ "&g=w&ignore=.csv");
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			CSVReader csvReader = new CSVReader(in, ',', '\'', 1);
			ArrayList<Double> high = new ArrayList<Double>();
			ArrayList<Double> low = new ArrayList<Double>();
			String[] row = null;
			int i = 0;
			while ((row = csvReader.readNext()) != null) {
				// bw.write(inputLine);
				high.add(Double.parseDouble(row[2]));
				low.add(Double.parseDouble(row[3]));
				i++;
				if (i == 53) {
					break;
				}
			}
			csvReader.close();
			Collections.sort(high);
			Collections.sort(low);
			highLowIn52Week.add(0, high.get(high.size() - 1));
			highLowIn52Week.add(1, low.get(low.size() - 1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
		}
		return highLowIn52Week;
	}
}
