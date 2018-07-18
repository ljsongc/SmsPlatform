package com.pay.smsserver.bean;

import java.util.Date;

public class SmsConfig {

	public static final String SMS_STATE_TRUE = "TRUE";
	public static final String SMS_STATE_FALSE = "FALSE";
	public static final String SMS_TYPE_NOTICE = "NOTICE";
	public static final String SMS_TYPE_SALE = "SALE";
	public static final String SMS_TYPE_RATE = "RATE_";
	public static final String SMS_IS_OPEN_KEY = "SMS_IS_OPEN_KEY";
	public static final String SMS_IS_OPEN = "true";
	public static final String SMS_IS_OPEN_FALSE = "false";
	private int id; 			// 主键
	private String smsType; 	// 短信类型
	private String smsChannel; 	// 短信通道
	private String smsState; 	// 配置状态
	private Date gmtCreate;
	private Date gmtModified;
	private int weight; 		// 权重值
	private int currentWeight; 	// 当前权重值


	private String channelCode;//通道编号



	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public String getSmsChannel() {
		return smsChannel;
	}

	public void setSmsChannel(String smsChannel) {
		this.smsChannel = smsChannel;
	}

	public String getSmsState() {
		return smsState;
	}

	public void setSmsState(String smsState) {
		this.smsState = smsState;
	}

	@Override
	public String toString() {
		return "SmsConfig [id=" + id + ", smsType=" + smsType + ", smsChannel="
				+ smsChannel + ", smsState=" + smsState + ", gmtCreate="
				+ gmtCreate + ", gmtModified=" + gmtModified + ", weight="
				+ weight + ", currentWeight=" + currentWeight + "]";
	}

}
