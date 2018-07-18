package com.sms;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.isms.enums.SmsSendFlag;
import com.pay.smsplatform.smsclient.SmsClientApiImpl;

public class SmsUtilTest {
	private static final Logger log = LoggerFactory.getLogger(SmsUtilTest.class);
	
	public static boolean send(SmsBean smsBean,boolean synFlag){
		SmsResponse smsResponse=new SmsClientApiImpl("192.168.200.140", 33701).send(smsBean,synFlag);
		if(smsResponse!=null && (SmsSendFlag.SUCCESS.equals(smsResponse.getResponseFlag()) || SmsSendFlag.TEMPSTOP.equals(smsResponse.getResponseFlag()))){
			return true;
		}
		return false;
	}
	
	public static boolean sendContentSms(String to, String content){
		SmsBean smsBean=new SmsBean();
		smsBean.setTo(to);
		smsBean.setContent(content+"[lepay]");
		smsBean.setAppCode("app1");
		SmsResponse smsResponse=new SmsClientApiImpl("127.0.0.1", 33701).send(smsBean,true);
		if(smsResponse!=null && (SmsSendFlag.SUCCESS.equals(smsResponse.getResponseFlag()) || SmsSendFlag.TEMPSTOP.equals(smsResponse.getResponseFlag()))){
			return true;
		}
		return false;
	}
	
	public static boolean sendSmsAsyn(final String to, final String content, boolean addHostName){
		String hostName="";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.error("", e);
		}
		SmsBean smsBean=new SmsBean();
		smsBean.setTo(to);
		smsBean.setContent(hostName + ":" + content);
		new SmsClientApiImpl("192.168.200.140", 33701).send(smsBean,false);
		return true;
	}
	
}
