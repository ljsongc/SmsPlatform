package com.pay.smsplatform.isms.bean;

import java.io.Serializable;
import java.util.Date;

public class UpLinkEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4287142083523246439L;

	private String phone;
	
	private String content;
	
	private Date createTime;
	
	private Date receiveTime;

	private String type;//通道标识
	
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "UpLinkEntity [phone=" + phone + ", content=" + content
				+ ", createTime=" + createTime + ", receiveTime=" + receiveTime
				+ ", type=" + type + "]";
	}
}
