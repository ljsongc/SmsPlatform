package com.pay.smsplatform.isms.enums;

/**
 * 	消息发送标志常量类
 * @author zhengzheng.ma
 *
 */
public enum SmsSendFlag {	
	SEND,		//已发送（发送成功与否暂不可知）
	TEMPSTOP,	//暂时不发送
	SUCCESS,	//发送成功
	FAIL		//发送失败
}
