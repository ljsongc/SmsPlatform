package com.pay.smsplatform.smsclient;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;

public interface SmsClientApi {
	public SmsResponse send(SmsBean smsBean,boolean synFlag);
	public void close();
}
