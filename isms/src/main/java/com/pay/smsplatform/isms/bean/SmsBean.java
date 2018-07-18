package com.pay.smsplatform.isms.bean;

import java.io.Serializable;
import java.util.Date;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

/**
 * 	消息类（请求类）
 * @author zhengzheng.ma
 *
 */
public class SmsBean implements Serializable{
	private static final long serialVersionUID = 4624051572793804086L;
	
	private String appCode;		//应用编号（所属应用）
	private String to;			//消息发送目标（谁接收）
	private String content;		//消息内容（发送实体）
	private SmsSendLevel level;	//消息级别(枚举类已实现Serializable接口)
	private Date time;			//发送日期(Date类已实现Serializable接口)
	private SmsSendType type;	//消息类型
	private String token;		//认证标识
	private String ip;			//发送方ip
	private String uniqueKey;   //消息唯一标识
	private String templateCode; //模板编号
	private String typeCode;  //短信类型编号
	
	public SmsBean(){
		
	}
	
	public SmsBean(String to,String content){
		this.to=to;
		this.content=content;
	}
	
	public SmsBean(String to,String content,SmsSendType type){
		this.to=to;
		this.content=content;
		this.type = type;
	}
	
	public SmsBean(String appCode,String to,String content){
		this.appCode=appCode;
		this.to=to;
		this.content=content;
	}
	
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

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public SmsSendType getType() {
		return type;
	}

	public void setType(SmsSendType type) {
		this.type = type;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	@Override
	public String toString() {
		return "SmsBean [appCode=" + appCode + ", to=" + to + ", content="
				+ content + ", level=" + level + ", time=" + time + ", type="
				+ type + ", token=" + token + ", ip=" + ip + ", uniqueKey="
				+ uniqueKey + ", templateCode=" + templateCode + ", typeCode="
				+ typeCode + "]";
	}
	
	
	
}
