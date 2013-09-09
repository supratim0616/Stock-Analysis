package com.bean;

/**
 * @param args
 */

public class Item {

	private String title;
	private String description;
	private String guid;
	private String pubDate;
	private String pubTime;
	private String market;
	private String ticker;
	private String today_date;

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}

	public String getDate() {
		return today_date;
	}

	public void setDate(String date) {
		this.today_date = date;
	}

	// getter and setter methods

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getPubTime() {
		return pubTime;
	}

	public void setPubTime(String pubTime) {
		this.pubTime = pubTime;
	}

	@Override
	public String toString() {
		return "{ today_date =" + today_date + ", market=" + market
				+ ", ticker=" + ticker + ", title=" + title + ", description="
				+ description + ", pubDate =" + pubDate + ", pubTime =" + pubTime +"}";
	}

}
