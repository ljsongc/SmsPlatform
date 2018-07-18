package com.pay.smsserver.service;

import java.util.Map;

public interface HttpService {

	/**
	 * Post请求
	 * @param param 请求参数
	 * @return 请求结果
	 */
	String post(Map<String, String> param);
	
	/**
	 * Post 传递json数据
	 * @param param 请求参数
	 * @return 请求结果
	 */
	String postJSON(Map<String, String> param);
	
	/**
	 * Get请求
	 * @param param 请求参数
	 * @return 请求结果
	 */
	String get(Map<String, String> param);
}
