package com.pay.smsserver.handler.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.handler.MessageHandler;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.SmsFailureService;
import com.pay.smsserver.util.CommonUtil;

public class DefaultMessageHandler implements MessageHandler {


	private Logger logger = LoggerFactory.getLogger(getClass());
	private ChannelService channelService;
	private SmsFailureService smsFailureService;
	private RedisManager redisManager;

	@Override
	public void handle(SmsBean smsBean) {

		//数据填充
		SmsEntity smsEntity = channelService.fillData(smsBean);
		logger.info("fillData smsBean={},smsEntity={}",smsBean,smsEntity);

		//通道筛选
		MessageChannel messageChannel = null;
		//如果isOpen为true 新逻辑

		String isOpenValue= redisManager.get(SmsConfig.SMS_IS_OPEN_KEY);
		if(org.apache.commons.lang3.StringUtils.isBlank(isOpenValue)){
			isOpenValue = SmsConfig.SMS_IS_OPEN_FALSE;
		}
		logger.info("SMS_IS_OPEN={},UK={}",isOpenValue,smsBean.getUniqueKey());
		if(SmsConfig.SMS_IS_OPEN.equals(isOpenValue)){
			if(org.apache.commons.lang3.StringUtils.isNotBlank(smsBean.getTemplateCode())){
				//根据短信类型筛选通道（有短信模板时使用）
				 messageChannel = channelService.filterChannelBySmsType(smsBean.getTypeCode(),null,smsBean.getUniqueKey());
			}
		}else{
			messageChannel = channelService.filterChannel(SmsConfig.SMS_STATE_TRUE, smsEntity.getType().name(), null, smsEntity.getAppCode());
		}

		logger.info("handle messageChannel is {},UK={} ",messageChannel,smsBean.getUniqueKey());

		if(messageChannel == null){
			this.logger.info("filter channel is null by status={} type={}", SmsConfig.SMS_STATE_TRUE, smsEntity.getType().name());
			return;
		}
		smsEntity.setChannelNo(messageChannel.getChannelName());

		//内容清洗
		messageChannel.cleanContent(smsEntity);

		//重复拦截
		messageChannel.interceptRepeatPhone(smsEntity);

		//按通道要求条数提交发送
		int submitNumber = messageChannel.getSubmitNumber();
		try {
			StringBuffer submitPhones = new StringBuffer();//提交手机号码
			int count = 0;
			String[] phones = smsEntity.getTo().split(",");
			for(String phone: phones){
				if(StringUtils.isEmpty(phone)){
					continue;
				}
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher match = pattern.matcher(phone);
				if(!match.matches() || (phone.length() != 11)){
					continue;
				}
				submitPhones.append(phone).append(",");
				count++;
				if(count == submitNumber){
					count = 0;
					submitPhones.deleteCharAt(submitPhones.length() - 1);
					smsEntity.setTo(submitPhones.toString());
					submitPhones.setLength(0);

					//短信下发
					List<MessageResponse> messasgeResponses = messageChannel.sendMessage(smsEntity);


					this.logger.info("default message handler handle channel={} to={} content={}, sendMessage result={},UK={}",
							smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), messasgeResponses,smsEntity.getUniqueKey());

					//短信发送结果处理
					if(messasgeResponses != null){
						channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
					}
				}
			}
			if(count > 0){
				submitPhones.deleteCharAt(submitPhones.length() - 1);
				smsEntity.setTo(submitPhones.toString());
				submitPhones.setLength(0);

				List<MessageResponse> messasgeResponses = messageChannel.sendMessage(smsEntity);
				this.logger.info("default message handler handle channel={} to={} content={}, sendMessage result={},UK={}",
						smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), messasgeResponses,smsEntity.getUniqueKey());

				if(messasgeResponses != null){
					channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("default message hander handle to={} content={}, UK={},error={}", smsEntity.getTo(),smsEntity.getUniqueKey(), smsEntity.getContent(), e);
		}
	}


	@Override
	public void failHandle(SmsEntity smsEntity) {

		smsFailureService.insertHistory(smsEntity);
		MessageChannel messageChannel =null;
		String isOpenValue= redisManager.get(SmsConfig.SMS_IS_OPEN_KEY);
		if(org.apache.commons.lang3.StringUtils.isBlank(isOpenValue)){
			isOpenValue = SmsConfig.SMS_IS_OPEN_FALSE;
		}
		this.logger.info("default message handler failHandle isOpenValue={},uk={}",isOpenValue,smsEntity.getUniqueKey());
		if(SmsConfig.SMS_IS_OPEN.equals(isOpenValue)){
			if(org.apache.commons.lang3.StringUtils.isNotBlank(smsEntity.getTemplateCode())){
				//根据短信类型筛选通道（有短信模板时使用）
				 messageChannel = channelService.filterChannelBySmsType(smsEntity.getTypeCode(),null,smsEntity.getUniqueKey());
			}

		}else{
	     messageChannel = channelService.filterChannel(SmsConfig.SMS_STATE_TRUE, smsEntity.getType().name(), smsEntity.getChannelNo(), smsEntity.getAppCode());
		}
		this.logger.info("default message handler failHandle smsEntity={}",smsEntity);



		if(messageChannel == null){
			this.logger.info("filter channel is null by status={} type={}", SmsConfig.SMS_STATE_TRUE, smsEntity.getType().name());
			return;
		}
		smsEntity.setChannelNo(messageChannel.getChannelName());

		try {
			//短信下发
			List<MessageResponse> messasgeResponses = messageChannel.sendMessage(smsEntity);

			this.logger.info("default message handler failHandle channel={} to={} content={}, sendMessage result={}",
					smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), messasgeResponses);

			//短信发送结果处理
			if(messasgeResponses != null){
				channelService.failHandleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("default message hander failHandle to={} content={}, error={}", smsEntity.getTo(), smsEntity.getContent(), e);
		}
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}



	public void setSmsFailureService(SmsFailureService smsFailureService) {
		this.smsFailureService = smsFailureService;
	}


	public RedisManager getRedisManager() {
		return redisManager;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}





}
