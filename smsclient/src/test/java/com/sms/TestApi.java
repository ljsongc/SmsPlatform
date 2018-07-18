package com.sms;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.smsclient.SmsClientApi;
import com.pay.smsplatform.smsclient.SmsClientApiImpl;

import junit.framework.TestCase;

public class TestApi extends TestCase {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private SmsClientApi api;
	
	@Before
	public void setUp() {
		api = new SmsClientApiImpl("localhost", 33701);
	}
	
	@Test
	public void testSendAsSync() {
		SmsBean bean = new SmsBean("18601344991", "This is a test message");
		bean.setToken("test");
		bean.setAppCode("sms");
		SmsResponse response = api.send(bean, true);
		log.info("Message sended result {}", response);
	}
	
	@Test
	public void testSendUnSync() throws Exception{
		for(int i = 0; i<1; i++){
			SmsBean bean = new SmsBean("18601344991", "双通道测试短信");
			bean.setToken("wqtnT+zqS8mmkankMN4/1w==");
			bean.setAppCode("sms");
			SmsResponse response = api.send(bean, false);
			log.info("Message sended result {}", response);
		}
	}
	
}
