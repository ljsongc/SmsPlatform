package com.pay.sms.console.bean;

import java.util.Date;

import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;


/**短信类型
 * @author mengjiao.liang
 *
 */
public class SmsType {
	private String typeCode;//类型编码
	private String typeName;//类型名称
	private String remark;//备注说明
	private String operator;//操作人员
	private Date createTime;//创建时间
	private Date updateTime;//修改时间
	private String rateRemark;//查询列表页面使用分流比说明
	private String strLis;//新增修改页面使用拼接参数




	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
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



	public String getRateRemark() {
		return rateRemark;
	}
	public void setRateRemark(String rateRemark) {
		this.rateRemark = rateRemark;
	}
	public String getStrLis() {
		return strLis;
	}
	public void setStrLis(String strLis) {
		this.strLis = strLis;
	}










}

