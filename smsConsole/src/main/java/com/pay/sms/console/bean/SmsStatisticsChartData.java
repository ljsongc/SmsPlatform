package com.pay.sms.console.bean;

public class SmsStatisticsChartData {
	
	//时间
	private String date;
	
	//条数
	private long number;

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "SmsStatisticsChartData [date=" + date + ", number=" + number
				+ "]";
	}
	
}
