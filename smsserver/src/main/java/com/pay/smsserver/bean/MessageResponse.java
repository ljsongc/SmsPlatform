package com.pay.smsserver.bean;

public class MessageResponse {

	private String mobile;//发送手机号码
	
	private String msgId;//发送成功的唯一标识
	
	private String memo;//发送结果说明
	
	private int result;//发送结果，0表示成功，1表示外部错误，-1表示内部错误

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "MessageResponse [mobile=" + mobile + ", msgId=" + msgId
				+ ", memo=" + memo + ", result=" + result + "]";
	}

}
