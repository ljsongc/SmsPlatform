package com.pay.smsserver.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsStatusEntity;
import com.pay.smsserver.channel.MessageChannel;

/**
 * Service/DAO层方法命名规约
 * 	1） 获取单个对象的方法用get做前缀。
 * 	2） 获取多个对象的方法用list做前缀。
 * 	3） 获取统计值的方法用count做前缀。
 * 	4） 插入的方法用save/insert做前缀。
 * 	5） 删除的方法用remove/delete做前缀。
 * 	6） 修改的方法用update做前缀。
 * @author chenchen.qi
 *
 */
public interface ChannelService {

	/**
	 * 统计各通道指定时间间隔内成功总数6
	 * @param smsSendType 消息类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(分钟)
	 * @return key为通道名称，value为成功总数
	 */
	List<Map<String, String>> countSuccessMessages(String smsSendType, int minuteIntervalTime);

	/**
	 * 统计各通道指定时间间隔内失败总数
	 * @param smsSendType 消息类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(分钟)
	 * @return key为通道名称，value为成功总数
	 */
	List<Map<String, String>> countFailMessages(String smsSendType, int minuteIntervalTime);

	/**
	 * 统计各通道指定时间间隔内失败总数
	 * @param smsSendType 消息类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(分钟)
	 * @return key为通道名称，value为成功总数
	 */
	List<Map<String, String>> countFailHistoryMessages(String smsSendType, int minuteIntervalTime);

	/**
	 * 根据通道状态和类型筛选通道
	 * @param status 通道状态
	 * @param smsSendType	通道类型
	 * @param failChannelName 失败通道名
	 * @return	发送使用的通道
	 */
	List<SmsConfig> listMasterSmsConfigs(String status, String smsSendType);

	/**
	 * 更新指定通道为备用通道
	 * @param channelName 通道名称
	 * @param status 通道状态
	 */
	void updateChannelToSpare(String channelName, String status);

	/**
	 * 保存上行消息
	 * @param upLinkEntity 上行短信实体
	 */
	void insertUplinkMessage(UpLinkEntity upLinkEntity);

	/**
	 * 查询指定上行短信
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param createTime 创建时间
	 * @return 上行短信集合
	 */
	List<UpLinkEntity> listUplinkMessages(String phone, String content, Date createTime);

	/**
	 * 保存消息状态
	 * @param smsStatusEntity 消息状态实体
	 */
	void insertMessageStatus(SmsStatusEntity smsStatusEntity);

	/**
	 * 查询指定消息状态
	 * @param phone 手机号码
	 * @param msgid 下行标识
	 * @param createTime 创建时间
	 * @return 消息状态集合
	 */
	List<SmsStatusEntity> listMessageStatuses(String phone, String msgid, Date createTime);

	/**
	 * 根据应用标识获取扩展码
	 * @param appCode 应用标识
	 * @return 扩展码
	 */
	String getExpandCodeByAppCode(String appCode);

	/**
	 * 根据扩展码获取应用标识
	 * @param portNumber 通道返回扩展码
	 * @return appCode 应用标识
	 */
	String getAppCodeByExpandCode(String portNumber);

	/**
	 * 数据填充
	 * @param smsBean 客户端bean
	 * @return 服务端bean
	 */
	SmsEntity fillData(SmsBean smsBean);

	/**
	 * <p>根据通道状态类型和appCode筛选通道</p>
	 * <br/>获取通道的规则如下：
	 * <br/>1、指定失败通道，则从剩下的通道里随机选择一个
	 * <br/>2、不指定失败通道，通知类选择weight=currentWeight里权值最小的；营销类选择轮询
	 * @param status 通道状态
	 * @param type	通道类型
	 * @param failChannelName 失败通道名
	 * @return	发送使用的通道
	 */
	MessageChannel filterChannel(String status, String type, String failChannelName, String appCode);

	/**
	 * <p>处理消息结果</p>
	 * 成功结果：<br/>
	 * a、保存消息到成功表<br/>
	 * b、优先进行redis的条数计数，当天第一次计数会更新到数据库，并将昨天的计数值更新到数据库<br/>
	 * 失败结果：<br/>
	 * a、保存消息到失败表
	 * @param smsEntity 短信实体
	 * @param messageChannel 消息通道
	 * @param messasgeResponses 消息响应
	 */
	void handleMessageResponses(SmsEntity smsEntity, MessageChannel messageChannel, List<MessageResponse> messasgeResponses);

	/**
	 * 失败重试处理消息结果
	 * @param smsEntity 短信实体
	 * @param messageChannel 消息通道
	 * @param messasgeResponses 消息响应
	 */
	void failHandleMessageResponses(SmsEntity smsEntity, MessageChannel messageChannel, List<MessageResponse> messasgeResponses);

	/**
	 * 条数统计
	 * @param smsEntity 短信对象
	 * @param signature 签名
	 * @param singleMaxNum 单发单条最大长度
	 * @param multiMaxNum 群发单条最大长度
	 */
	void numberStatistics(SmsEntity smsEntity, String signature, int singleMaxNum, int multiMaxNum);

	/**
	 * @param typeCode短信类型编号
	 * @param type 短信类型
	 * @return
	 */
	MessageChannel filterChannelBySmsType(String typeCode,String filterChannelBySmsType,String uk);
	
	void StatisticalSuccessRate(Date beginTime, Date endTime);
	

}
