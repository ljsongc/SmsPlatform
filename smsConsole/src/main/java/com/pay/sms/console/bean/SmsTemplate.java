package com.pay.sms.console.bean;

import java.util.Date;

/**
 * 
 * @author muya.cao
 *
 */
public class SmsTemplate {
	
	/**
	 * 模板编号
	 */
	private String templateCode;
	
	/**
	 * 短信类型编码
	 */
	private String typeCode;
	
	/**
	 * 应用编码
	 */
	private String appCode;
	
	/**
	 * 短信标题
	 */
	private String title;
	/**
	 * 短信内容
	 */
	private String content;
	/**
	 * 操作员
	 */
	private String operator;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 修改时间
	 */
	private Date modifiedTime;
	
	private String createTimeStr;
	/**
	 * 短信类型
	 */
	private String typeName;
	private String appName;
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
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getCreateTimeStr() {
		return createTimeStr;
	}
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	@Override
	public String toString() {
		return "SmsTemplate [templateCode=" + templateCode + ", typeCode="
				+ typeCode + ", appCode=" + appCode + ", title=" + title
				+ ", content=" + content + ", operator=" + operator
				+ ", createTime=" + createTime + ", modifiedTime="
				+ modifiedTime + ", createTimeStr=" + createTimeStr
				+ ", typeName=" + typeName + ", appName=" + appName + "]";
	}
	
	

	
}

