package com.pay.smsserver.constants;

/**
 * 常量
 * @author chenchen.qi
 *
 */
public class SmsConstants {

	/**
	 * redis中token存储的key前缀
	 */
	public static String REDIS_SMS_TOKEN = "SMS_TOKEN:";
	
	public static String REDIS_RULE_KEY = "SMS_RULE:";
	
	public static String REDIS_SMS_CHUANGLAN_REPEAT_SET = "CHUANGLAN_REPEAT";
	
	/**
	 * redis中短信计数的key前缀
	 */
	public static String REDIS_SMS_COUNT_KEY = "SMS_COUNT:";
	
	public static final String SEND_URL = "SEND_URL";
	public static final String PROXY_IP = "PROXY_IP";
	public static final String PROXY_PORT = "PROXY_PORT";
	public static final String REQUEST_CHARSET = "REQUEST_CHARSET";
	public static final String RESPONSE_CHARSET = "RESPONSE_CHARSET";
	public static final String CONNECT_TIME_OUT = "CONNECT_TIME_OUT";
	public static final String SOCKET_TIME_OUT = "SOCKET_TIME_OUT";
	public static final String JSON = "JSON";
}
