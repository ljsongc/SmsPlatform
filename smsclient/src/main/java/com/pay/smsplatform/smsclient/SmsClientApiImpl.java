package com.pay.smsplatform.smsclient;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;

public class SmsClientApiImpl implements SmsClientApi{
	private String host;
	private int port;
	
	public SmsClientApiImpl(String host,int port){
		this.host=host;
		this.port=port;
	}

	@Override
	public SmsResponse send(SmsBean smsBean, boolean synFlag) {
		return SmsHandle.getInstance(host, port).send(smsBean, synFlag);
	}
	
	@Override
	public void close(){
		SmsHandle.getInstance(host, port).close();
	}

}
