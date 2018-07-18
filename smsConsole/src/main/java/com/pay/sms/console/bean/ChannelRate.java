package com.pay.sms.console.bean;

import java.util.Date;

import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;


/**短信类型
 * @author mengjiao.liang
 *
 */
public class ChannelRate {
	private int id;			//主键
	private String typeCode;//类型编码
	private String channelCode;//类型编号
	private int rate;
	private String remark;//备注说明
	private String operator;//操作人员
	private Date createTime;//创建时间
	private Date updateTime;//修改时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getChannelCode() {
		return channelCode;
	}
	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ChannelRate [id=" + id + ", typeCode=" + typeCode
				+ ", channelCode=" + channelCode + ", rate=" + rate
				+ ", remark=" + remark + ", operator=" + operator
				+ ", createTime=" + createTime + ", updateTime=" + updateTime
				+ "]";
	}














}

