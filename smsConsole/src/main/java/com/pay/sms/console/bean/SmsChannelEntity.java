package com.pay.sms.console.bean;

import java.util.Date;

public class SmsChannelEntity {
	private long id;
	private String name;//名
	private String state;//状态
	private String type;
	private int weight;//权重
	private int currentWeight;//当前通道发送权重
	private Date gmtCreate;//创建时间
	private Date gmtModified;//修改时间
	private String channelCode;//通道编号
	private String channelCost;//成本说明
	private String remark;//备注
	private String channelContact;//联系方式




	public int getCurrentWeight() {
		return currentWeight;
	}

	public void setCurrentWeight(int currentWeight) {
		this.currentWeight = currentWeight;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public String getChannelCost() {
		return channelCost;
	}

	public void setChannelCost(String channelCost) {
		this.channelCost = channelCost;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getChannelContact() {
		return channelContact;
	}

	public void setChannelContact(String channelContact) {
		this.channelContact = channelContact;
	}



}
