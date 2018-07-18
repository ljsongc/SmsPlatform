package com.pay.smsserver.bean;

import java.util.Date;

/**
 * 短信送达状态实体类
 * @author haoran.liu
 *
 */
public class SmsStatusEntity {
	
	private String phone;
	
	private String msgid;
	
	private String errmsg;
	
	private String description;
	
	private String channelNo;
	
	private Date createDate;
	
	private Date receiveDate;
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}
	
	public String getChannelNo() {
		return channelNo;
	}

	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
	
	@Override
	public String toString() {
		return "SmsStatusEntity [phone=" + phone + ", msgid=" + msgid
				+ ", errmsg=" + errmsg + ", description=" + description
				+ ", channelNo=" + channelNo + ", createDate=" + createDate
				+ ", receiveDate=" + receiveDate + "]";
	}
	
}
