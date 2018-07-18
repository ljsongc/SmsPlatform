package com.pay.sms.console.bean;

import java.util.Date;

/**
 * 成功率统计
 * @author muya.cao
 *
 */
public class SmsRateStatics {
	private int id;
	/**
	 * 短信通道
	 */
	private String channelCode;
	/**
	 * 短信类型
	 */
	private String typeCode;
	/**
	 * 短信应用
	 */
	private String appCode;
	/**
	 * 发送条数
	 */
	private int sendTotal;
	/**
	 * 发送成功条数
	 */
	private int sendSuccessTotal;
	/**
	 * 发送失败条数
	 */
	private int sendFailTotal;
	
	/**
	 * 发送成功率
	 */
	private double sendSuccessRate;
	/**
	 * 接收成功条数
	 */
	private int receiveSuccessTotal;
	/**
	 * 接收失败条数
	 */
	private int receiveFailTotal;
	
	/**
	 * 接收成功率
	 */
	private double receiveSuccessRate;
	
	/**
	 * 计费条数
	 */
	private int feeTotal;
	/**
	 * 统计时间
	 */
	private Date time;
	
	private String typeName;
	
	private Date createTime;
	
	private Date modifiedTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public int getSendTotal() {
		return sendTotal;
	}

	public void setSendTotal(int sendTotal) {
		this.sendTotal = sendTotal;
	}

	public int getSendSuccessTotal() {
		return sendSuccessTotal;
	}

	public void setSendSuccessTotal(int sendSuccessTotal) {
		this.sendSuccessTotal = sendSuccessTotal;
	}

	public int getSendFailTotal() {
		return sendFailTotal;
	}

	public void setSendFailTotal(int sendFailTotal) {
		this.sendFailTotal = sendFailTotal;
	}

	public double getSendSuccessRate() {
		return sendSuccessRate;
	}

	public void setSendSuccessRate(double sendSuccessRate) {
		this.sendSuccessRate = sendSuccessRate;
	}

	public int getReceiveSuccessTotal() {
		return receiveSuccessTotal;
	}

	public void setReceiveSuccessTotal(int receiveSuccessTotal) {
		this.receiveSuccessTotal = receiveSuccessTotal;
	}

	public int getReceiveFailTotal() {
		return receiveFailTotal;
	}

	public void setReceiveFailTotal(int receiveFailTotal) {
		this.receiveFailTotal = receiveFailTotal;
	}

	public double getReceiveSuccessRate() {
		return receiveSuccessRate;
	}

	public void setReceiveSuccessRate(double receiveSuccessRate) {
		this.receiveSuccessRate = receiveSuccessRate;
	}

	public int getFeeTotal() {
		return feeTotal;
	}

	public void setFeeTotal(int feeTotal) {
		this.feeTotal = feeTotal;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	@Override
	public String toString() {
		return "SmsRateStatics [id=" + id + ", channelCode=" + channelCode
				+ ", typeCode=" + typeCode + ", appCode=" + appCode
				+ ", sendTotal=" + sendTotal + ", sendSuccessTotal="
				+ sendSuccessTotal + ", sendFailTotal=" + sendFailTotal
				+ ", sendSuccessRate=" + sendSuccessRate
				+ ", receiveSuccessTotal=" + receiveSuccessTotal
				+ ", receiveFailTotal=" + receiveFailTotal
				+ ", receiveSuccessRate=" + receiveSuccessRate + ", feeTotal="
				+ feeTotal + ", time=" + time + ", typeName=" + typeName
				+ ", createTime=" + createTime + ", modifiedTime="
				+ modifiedTime + "]";
	}

	
	

}

