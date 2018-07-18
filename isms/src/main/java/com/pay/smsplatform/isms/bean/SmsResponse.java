package com.pay.smsplatform.isms.bean;

import java.io.Serializable;
import com.pay.smsplatform.isms.enums.SmsSendFlag;

/**
 * 	响应类
 * @author zhengzheng.ma
 *
 */
public class SmsResponse implements Serializable{
	private static final long serialVersionUID = 4784732273354877782L;
	
	private SmsSendFlag responseFlag;	//响应标志
	private String responseContent;		//响应内容
	
	public SmsSendFlag getResponseFlag() {
		return responseFlag;
	}
	
	public void setResponseFlag(SmsSendFlag responseFlag) {
		this.responseFlag = responseFlag;
	}
	
	public String getResponseContent() {
		return responseContent;
	}
	
	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}

	@Override
	public String toString() {
		return "SmsResponse [responseFlag=" + responseFlag
				+ ", responseContent=" + responseContent + "]";
	}
	
}
