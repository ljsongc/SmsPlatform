package com.pay.smsserver.service;

import java.util.List;

import com.pay.smsserver.bean.SmsConfig;

/**
 * 
 * @author chenchen.qi
 *
 */
public interface SmsConfigService {
	
	/**
	 * 查询全部配置信息
	 */
	public List<SmsConfig> list();
	
	/**
	 * 根据短信类型获取通道信息
	 * @param state		通道状态:true:启用;flase:禁用
	 * @param type		短信类型
	 * @return			通道信息
	 */
	public List<SmsConfig> list(String state, String type);
	
	/**
	 * 根据短信类型获取通备用通道信息
	 * @param state		通道状态:true:启用;flase:禁用
	 * @param type		短信类型
	 * @return			通道信息
	 */
	public List<SmsConfig> getSpareList(String state, String type);
	
	/**
	 * 根据短信类型获取通非备用通道信息
	 * @param state		通道状态:true:启用;flase:禁用
	 * @param type		短信类型
	 * @return			通道信息
	 */
	public List<SmsConfig> getMasterList(String state, String type);
	
	/**
	 * 更新配置状态
	 * @param smsConfig
	 */
	public void update(SmsConfig smsConfig);

}

