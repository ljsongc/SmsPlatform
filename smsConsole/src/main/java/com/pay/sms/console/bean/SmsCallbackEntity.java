package com.pay.sms.console.bean;

import java.util.Date;

import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

public class SmsCallbackEntity {
	private String id;			//主键
	private String appCode;		//应用编号
	private String phone;		//消息发送目标(接受电话)
	private String content;		//消息内容
	private SmsSendLevel level;	//消息级别(SmsSendLevel类)
	private Date preTime; 		//应用程序发送日期
	private Date time;			//入库日期
	private String memo;		//失败原因
	private String channelNo;	//通道名称
	private SmsSendType type;	//消息类型
	private String ip;			//短信来源
	private String token;		//认证标识
	private String msgid;		//消息id,用于标识本次发送
	private String statusCode;	//状态码
	private String description;	//描述
	private Date createTime;	//创建时间
	private Date receiveTime;	//状态接受时间

	private String channelName;//通道名称
	private String typeName;//短信类型名称
	private String typeCode;//短信类型编号
	private String templateCode;//短信模板
	private String appcode;//应用编号





	public String getAppcode() {
		return appcode;
	}
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTemplateCode() {
		return templateCode;
	}
	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
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
	public SmsSendLevel getLevel() {
		return level;
	}
	public void setLevel(SmsSendLevel level) {
		this.level = level;
	}
	public Date getPreTime() {
		return preTime;
	}
	public void setPreTime(Date preTime) {
		this.preTime = preTime;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getChannelNo() {
		return channelNo;
	}
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
	public SmsSendType getType() {
		return type;
	}
	public void setType(SmsSendType type) {
		this.type = type;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getMsgid() {
		return msgid;
	}
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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

}
