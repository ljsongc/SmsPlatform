package com.pay.smsserver.bean;

import java.util.Date;

import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

/**
 * 消息实体
 * @author chenchen.qi
 *
 */
public class SmsEntity {

	/**
	 * 主键
	 */
	private Long id;
	/**
	 * 应用标识
	 */
	private String appCode;
	/**
	 * 手机号
	 */
	private String to;
	/**
	 * 短信内容
	 */
	private String content;
	/**
	 * 短信级别
	 */
	private SmsSendLevel level;
	/**
	 * 业务端发送时间
	 */
	private Date preTime;
	/**
	 * 入库时间
	 */
	private Date time;
	/**
	 * 通道名称
	 */
	private String channelNo;
	/**
	 * 消息类型
	 */
	private SmsSendType type;
	/**
	 * 业务端ip
	 */
	private String ip;
	/**
	 * 认证标识token
	 */
	private String token;
	/**
	 * 失败次数
	 */
	private int failCount;
	/**
	 * 失败原因
	 */
	private String memo;
	/**
	 * 下发标识
	 */
	private String msgid;
	/**
	 * 消息唯一标识
	 */
	private String uniqueKey;

	private Date createTime;

	private Date modifiedTime;


	private String templateCode; //模板编号
	private String typeCode;  //短信类型编号

	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
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
	public String getUniqueKey() {
		return uniqueKey;
	}
	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
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
		return "SmsEntity [id=" + id + ", appCode=" + appCode + ", to=" + to
				+ ", content=" + content + ", level=" + level + ", preTime="
				+ preTime + ", time=" + time + ", channelNo=" + channelNo
				+ ", type=" + type + ", ip=" + ip + ", token=" + token
				+ ", failCount=" + failCount + ", memo=" + memo + ", msgid="
				+ msgid + ", uniqueKey=" + uniqueKey + ", createTime="
				+ createTime + ", modifiedTime=" + modifiedTime
				+ ", templateCode=" + templateCode + ", typeCode=" + typeCode
				+ "]";
	}



}

