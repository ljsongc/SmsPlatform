package com.pay.smsserver.service;

import java.util.Map;

import com.pay.smsplatform.isms.bean.SmsBean;

public interface ValidationService {

	/**
	 * 短信信息校验
	 * @param smsBean
	 * @return Map key: flag value: true为校验通过; false为不通过;
	 * 			   key: info value: 为信息说明
	 */
	Map<String, String> validate(SmsBean smsBean);
}
