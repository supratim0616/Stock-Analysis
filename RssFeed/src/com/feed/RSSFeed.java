package com.feed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bean.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.*;

public class RSSFeed {

	/**
	 * @param args
	 * @throws IOException
	 */
	static Logger log = Logger.getLogger(Initiator.class.getName());

	void intiateFeedUploadProcess(String stockTicker, String market,
			String CompanyName) {
		long bytesCountfortitle = 0;
		long bytesCountfordesc = 0;
		try {
			File file = getRssFeed(stockTicker, market);
			ArrayList<ArrayList<Item>> finallist = ModifyXMLFile(
					file.getName(), stockTicker, market, CompanyName);
			for (int i = 0; i < finallist.size(); i++) {
				ArrayList<Item> itemList = finallist.get(i);
				String filename = getFilename(stockTicker, market);
				
				if (i == 0) {
					wrteTojson(filename, itemList);
					for (int j=0; j< itemList.size(); j++)
					{
						System.out.println("title is " + itemList.get(j).getTitle());
					bytesCountfortitle = bytesCountfortitle + itemList.get(j).getTitle().length();
					bytesCountfordesc = bytesCountfordesc + itemList.get(j).getDescription().length();
					}
					File file1 = new File(filename);
					S3FileUpload.uploadFileonS3(file1);
					file1.delete();
				} else {
					if (itemList.size() > 0) {
						filename = filename.replace(".json", "_reject.json");
						wrteTojson(filename, itemList);
						File file1 = new File(filename);
						S3FileUpload.uploadFileonS3(file1);
						file1.delete();
					}
				}
				if (i == 0)
					file.delete();
				
			}
			log.info("Total title bytes is : " + bytesCountfortitle + " and for description is :" + bytesCountfordesc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void wrteTojson(String filename, ArrayList<Item> itemList) {
		try {
			FileWriter writer = new FileWriter(filename, true);
			writer.write("{ \"" + filename + " \" : [ ");
			Gson gson = new GsonBuilder().disableHtmlEscaping().create();
			for (int j = 0; j < itemList.size(); j++) {
				String json = gson.toJson(itemList.get(j));
				try {
					// write converted json data to a file named "file.json"

					writer.write(json);
					if (j < itemList.size() - 1) {
						writer.write(",\n");
					}

				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			writer.write("]}");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private File getRssFeed(String stockTicker, String market) {
		URL url;
		File file = new File(getFilename(stockTicker, market).replace("json",
				"xml"));
		try {
			if (market.equals("NASDAQ")) {
				url = new URL(
						"http://articlefeeds.nasdaq.com/nasdaq/symbols?symbol="
								+ stockTicker);
			} else
				url = new URL(
						"http://feeds.finance.yahoo.com/rss/2.0/headline?s="
								+ stockTicker + "&region=US&lang=en-US");
			log.info("URL is : " + url);

			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				bw.write(inputLine);

			}
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	private String getFilename(String stockTicker, String market) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MMddyyyy");
		// get current date time with Date()
		Date date = new Date();
		String filename = "NEWS_" + dateFormat.format(date) + market
				+ stockTicker + ".json";
		return filename;
	}

	private ArrayList<ArrayList<Item>> ModifyXMLFile(String filename,
			String stockTicker, String market, String CompanyName) {
		ArrayList<Item> itemList = new ArrayList<Item>();
		ArrayList<Item> rejectitemList = new ArrayList<Item>();
		ArrayList<ArrayList<Item>> finalList = new ArrayList<ArrayList<Item>>();
		try {
			String filepath = filename;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			NodeList nList = doc.getElementsByTagName("item");
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat(
					"E, dd MMM yyyy");
			dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

			// Time in GMT
			String today = dateFormatGmt.format(new Date());
			log.info("today is :" + today);
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					if (eElement.getElementsByTagName("pubDate").item(0)
							.getTextContent().contains(today)
							&& eElement.getElementsByTagName("description")
									.item(0).getTextContent().length() > 0) {
						Item item = new Item();
						item.setTitle(eElement.getElementsByTagName("title")
								.item(0).getTextContent());
						item.setDescription(eElement
								.getElementsByTagName("description").item(0)
								.getTextContent());
						item.setPubDate(eElement
								.getElementsByTagName("pubDate").item(0)
								.getTextContent());
						item.setDate(filename.substring(5, 13));
						item.setTicker(stockTicker);
						item.setMarket(market);
						String[] compName = CompanyName.split(" ");
						boolean contains = false;
						for (int i = 0; i < compName.length; i++) {
							if (eElement.getElementsByTagName("description")
									.item(0).getTextContent()
									.contains(compName[i])
									|| eElement.getElementsByTagName("title")
											.item(0).getTextContent()
											.contains(compName[i])) {
								contains = true;
								break;
							}
						}
						if (!contains) {
							if (eElement.getElementsByTagName("description")
									.item(0).getTextContent()
									.contains(stockTicker)
									|| eElement.getElementsByTagName("title")
											.item(0).getTextContent()
											.contains(stockTicker)) {
								contains = true;
							}

						}

						if (!(itemList.contains(item)) && contains) {
							itemList.add(item);
						} else {
							rejectitemList.add(item);
						}

					}

				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finalList.add(0, itemList);
		finalList.add(1, rejectitemList);
		return finalList;
	}

}
