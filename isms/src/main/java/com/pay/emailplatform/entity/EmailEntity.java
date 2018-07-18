package com.pay.emailplatform.entity;

import java.io.Serializable;
import java.util.Date;

import com.pay.smsplatform.isms.enums.EmailType;

public class EmailEntity implements Serializable{

	private static final long serialVersionUID = -7630442373044316305L;
	
	private int id;
	private String to; // 收件人
	private String from; // 发件人
	private String subject; // 主题
	private String content; // 正文
	private String info; // 结果
	private Date appDate; // 应用发送日期
	private Date dbDate;	//入库日期
	private EmailType emailType; //邮件类型
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getAppDate() {
		return appDate;
	}

	public void setAppDate(Date appDate) {
		this.appDate = appDate;
	}

	public Date getDbDate() {
		return dbDate;
	}

	public void setDbDate(Date dbDate) {
		this.dbDate = dbDate;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public EmailType getEmailType() {
		return emailType;
	}

	public void setEmailType(EmailType emailType) {
		this.emailType = emailType;
	}

	@Override
	public String toString() {
		return "EmailEntity [id=" + id + ", to=" + to + ", from=" + from
				+ ", subject=" + subject + ", content=" + content + ", info="
				+ info + ", appDate=" + appDate + ", dbDate=" + dbDate
				+ ", emailType=" + emailType + "]";
	}
}
