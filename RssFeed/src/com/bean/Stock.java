/**
 * 
 */
package com.bean;

/**
 * @author pooja.murarka
 * 
 */
public class Stock {

	/**
	 * 
	 */
	// TODO Auto-generated constructor stub
	private String stock_date;
	private Double Open;
	private Double high;
	private Double low;
	private Double close;
	private Double volume;
	private Double adj_Close;
	private Double high52week;
	private Double low52week;
	private String market;
	private String ticker;
	private String today_date;

	public String getStock_date() {
		return stock_date;
	}

	public void setStock_date(String stock_date) {
		this.stock_date = stock_date;
	}

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

	public Double getHigh52week() {
		return high52week;
	}

	public void setHigh52week(Double high52week) {
		this.high52week = high52week;
	}

	public Double getLow52week() {
		return low52week;
	}

	public void setLow52week(Double low52week) {
		this.low52week = low52week;
	}

	public String getDate() {
		return today_date;
	}

	public void setDate(String date) {
		this.today_date = date;
	}

	public Double getOpen() {
		return Open;
	}

	public void setOpen(Double open) {
		Open = open;
	}

	public Double getHigh() {
		return high;
	}

	public void setHigh(Double high) {
		this.high = high;
	}

	public Double getLow() {
		return low;
	}

	public void setLow(Double low) {
		this.low = low;
	}

	public Double getClose() {
		return close;
	}

	public void setClose(Double close) {
		this.close = close;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public Double getAdj_Close() {
		return adj_Close;
	}

	public void setAdj_Close(Double adj_Close) {
		this.adj_Close = adj_Close;
	}
	
	

	@Override
	public String toString() {
		return "{ today_date="+ today_date +", market=" + market + ", ticker=" + ticker +",stock_date=" + stock_date + ", Open=" + Open + ", high =" + high
				+ ", low =" + low + ", close =" + close + ", volume =" + volume
				+ ", adj_Close =" + adj_Close + ", low52week = " + low52week
				+ ", high52week" + high52week + "}";
	}

}
