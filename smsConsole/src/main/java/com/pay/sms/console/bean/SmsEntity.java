package com.pay.sms.console.bean;

import java.util.Date;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

/**
 * 
 * @author chenchen.qi
 *
 */
public class SmsEntity {
	
	private Long id;			//主键
	private String appCode;		//应用编号
	private String to;			//消息发送目标(接受电话)
	private String content;		//消息内容
	private SmsSendLevel level;	//消息级别(SmsSendLevel类)
	private Date preTime; 		//应用程序发送日期
	private Date time;			//入库日期
	private String memo;		//失败原因
	private String channelNo;
	private SmsSendType type;	//消息类型
	private String ip;			//短信来源	
	private String token;		//认证标识
	private String msgid;		//消息id,用于标识本次发送
	
	public String getTo() {
		return to;
	}
	public void setTo(String to) {
		this.to = to;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public SmsSendLevel getLevel() {
		return level;
	}
	public void setLevel(SmsSendLevel level) {
		this.level = level;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Date getPreTime() {
		return preTime;
	}
	public void setPreTime(Date preTime) {
		this.preTime = preTime;
	}
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	
}

