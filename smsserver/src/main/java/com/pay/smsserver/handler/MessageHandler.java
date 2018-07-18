package com.pay.smsserver.handler;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsserver.bean.SmsEntity;

public interface MessageHandler {

	/**
	 * 处理短信发送
	 * @param smsBean 客户端短信实体
	 */
	void handle(SmsBean smsBean);
	
	/**
	 * 处理失败短信发送
	 * @param smsEntity 服务端短信实体
	 */
	void failHandle(SmsEntity smsEntity);
}
