package com.pay.smsserver.channel;

import java.util.List;

import org.json.JSONArray;

import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsEntity;

public interface MessageChannel {
	
	/**
	 * 获取通道名称
	 * @return
	 */
	String getChannelName();

	/**
	 * 拦截手机号码重复
	 * @param smsEntity
	 */
	void interceptRepeatPhone(SmsEntity smsEntity);
	
	/**
	 * 清洗短信内容
	 * @param smsEntity
	 */
	void cleanContent(SmsEntity smsEntity);
	
	/**
	 * 通道允许单次提交手机号个数
	 * 注意：如果通道允许条数超过200呢，不能单次提交太多，否则数据库存储也不方便
	 * @return
	 */
	int getSubmitNumber();
	
	/**
	 * 消息下发
	 * @param smsEntity
	 * @return
	 */
	List<MessageResponse> sendMessage(SmsEntity smsEntity);

	/**
	 * 条数统计
	 * @param smsEntity
	 */
	void numberStatistics(SmsEntity smsEntity);
	
	/**
	 * 主动拉取上行短信
	 */
	void pullUplinkMessages();
	
	/**
	 * 被动接收上行短信
	 * @param uplinkMessages JSONArray类型
	 */
	void pushedUplinkMessages(JSONArray uplinkMessages);
	
	/**
	 * 主动拉取短信状态
	 */
	void pullCallbackStatuses();
	
	/**
	 * 被动接收短信状态
	 * @param callbackStatuses JSONArray类型
	 */
	void pushedCallbackStatuses(JSONArray callbackStatuses);
}
