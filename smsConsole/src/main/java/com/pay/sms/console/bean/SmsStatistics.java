package com.pay.sms.console.bean;

/**
 * 短信统计实体类
 * @author haoran.liu
 *
 */
public class SmsStatistics {
	//发送日期
	private String date;
	
	//发送条数
	private long number;
	
	//成功条数
	private long successNumber;
	
	//拆分后计费条数
	private long countNumber;
	
	//成功率
	private float successRate;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public long getNumber() {
		return number;
	}
	
	/**
	 * 设置发送条数
	 * @param number
	 */
	public void setNumber(long number) {
		this.number = number;
	}

	public long getSuccessNumber() {
		return successNumber;
	}
	
	/**
	 * 设置成功条数
	 * @param successNumber
	 */
	public void setSuccessNumber(long successNumber) {
		this.successNumber = successNumber;
	}

	public long getCountNumber() {
		return countNumber;
	}
	
	/**
	 * 设置计费条数
	 * @param countNumber
	 */
	public void setCountNumber(long countNumber) {
		this.countNumber = countNumber;
	}

	public float getSuccessRate() {
		return successRate;
	}

	public void setSuccessRate(float successRate) {
		this.successRate = successRate;
	}
}
