package com.pay.sms.console.bean;

import java.util.Date;

/**
 * 
 * @author muya.cao
 *
 */
public class SmsToken {
	
	private int id;
	
	private String appCode;
	
	private String appName;
	
	private String token;
	
	private String operator;
	
	private Date createTime;
	
	private Date modifiedTime;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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

	@Override
	public String toString() {
		return "SmsToken [id=" + id + ", appCode=" + appCode + ", appName="
				+ appName + ", token=" + token + ", operator=" + operator
				+ ", createTime=" + createTime + ", modifiedTime="
				+ modifiedTime + "]";
	}

	
}

