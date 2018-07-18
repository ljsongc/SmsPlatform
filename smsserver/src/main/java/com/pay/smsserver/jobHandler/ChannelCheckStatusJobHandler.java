package com.pay.smsserver.jobHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.jpush.api.utils.StringUtils;

import com.pay.dsp.common.biz.model.ReturnT;
import com.pay.dsp.core.handler.BaseJobHandler;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.service.ChannelCheckStatusService;
import com.pay.smsserver.service.SmsConfigService;

/**
 * 检测通道发送状态，定时任务类
 * 通道切换
 * @author haoran.liu
 *
 */
public class ChannelCheckStatusJobHandler  extends BaseJobHandler{

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private ChannelCheckStatusService channelCheckStatusService;

	private SmsConfigService smsConfigService;

	private Long recoverTime = null;

	//成功率，低于成功率的通道将变为备通道
	private int successRate;

	//备通道恢复的时间间隔（单位：分）
	private int recoverInterval;

	//查询通道状态的时间间隔（单位：分）
	private int checkInterval;

	@Override
	public ReturnT<String> execute(String... params) throws Exception{
		recoverChannelStatus();
		checkChannelStatus();
		return ReturnT.SUCCESS;
	}

	private void checkChannelStatus(){
		List<SmsConfig> configList = smsConfigService.getMasterList(SmsConfig.SMS_STATE_TRUE, SmsConfig.SMS_TYPE_NOTICE);
		Map<String, String> successInfo = channelCheckStatusService.getSuccessCount(checkInterval);
		Map<String, String> failureInfo = channelCheckStatusService.getFailureCount(checkInterval);
		String maxSuccessChannel = null;
		float maxSuccessRate = 0f;
		int minWeight = Integer.MAX_VALUE;
		List<String> spareList = new ArrayList<String>();
		if(!configList.isEmpty()){
			for(SmsConfig config: configList){
				String channelName = config.getSmsChannel();
				String successCountStr = successInfo.get(channelName);
				String failCountStr = failureInfo.get(channelName);
				float rate = 0f;
				float successCount = 0f;
				float failCount = 0f;
				if(!StringUtils.isEmpty(successCountStr)){
					successCount = Float.valueOf(successCountStr);
				}
				if(!StringUtils.isEmpty(failCountStr)){
					failCount = Float.valueOf(failCountStr);
				}
				float sum = successCount + failCount;
				if(sum != 0){
					rate = successCount * 100 / (successCount + failCount);
					rate = (float) (Math.floor(rate * 100) / 100);
					logger.info("check status: in the past {} minutes, channel {} success: {}, fail: {}, successRate: {}%",
							checkInterval, channelName, successCountStr, failCountStr, rate);
				}else{
					logger.info("check status: in the past {} minutes, channel {} NO sms to send, successRate is: 0.0%", checkInterval, channelName);
				}

				if(rate >= maxSuccessRate){
					if(rate == maxSuccessRate){
						if(config.getWeight() < minWeight){
							minWeight = config.getWeight();
							maxSuccessRate = rate;
							maxSuccessChannel = channelName;
						}
					}else{
						minWeight = config.getWeight();
						maxSuccessRate = rate;
						maxSuccessChannel = channelName;
					}
				}
				if(rate < successRate){
					spareList.add(channelName);
				}
			}

			//全部通道都低于成功率,将成功率最高通道从备用通道list移除
			if(spareList.size() == configList.size()){
				spareList.remove(maxSuccessChannel);
				logger.info("check ALL channel successRate under {}%, choose channel {} to master", successRate, maxSuccessChannel);
			}else{
				logger.info("choose channel {} to master", maxSuccessChannel);
			}

			logger.info("check successRate under {}%, channelList is {}", successRate, spareList);

			for(String spareChannelName: spareList){
				channelCheckStatusService.updateChannelToSpare(spareChannelName, SmsConfig.SMS_STATE_TRUE);
			}
		}else{
			logger.info("check status: channel config list is EMPTY");
		}
	}

	private void recoverChannelStatus(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.SECOND, 0);
		long time = calendar.getTimeInMillis() / 1000;
		if(recoverTime == null){
			recoverTime = time;
		}
		if(time - recoverTime >= recoverInterval * 60){
			recoverTime = time;
			List<SmsConfig> spareConfigList = smsConfigService.getSpareList(SmsConfig.SMS_STATE_TRUE, SmsConfig.SMS_TYPE_NOTICE);
			if(!spareConfigList.isEmpty()){
				logger.info("get spareList {}", spareConfigList);
				Map<String, String> successInfo = channelCheckStatusService.getSuccessCount(recoverInterval);
				Map<String, String> failureInfo = channelCheckStatusService.getFailureCount(recoverInterval);
				for(SmsConfig spareConfig: spareConfigList){
					String channelName = spareConfig.getSmsChannel();
					String successCountStr = successInfo.get(channelName);
					String failCountStr = failureInfo.get(channelName);
					float rate = 0f;
					float successCount = 0f;
					float failCount = 0f;
					if(!StringUtils.isEmpty(successCountStr)){
						successCount = Float.valueOf(successCountStr);
					}
					if(!StringUtils.isEmpty(failCountStr)){
						failCount = Float.valueOf(failCountStr);
					}
					float sum = successCount + failCount;
					if(sum != 0){
						rate = successCount * 100 / (successCount + failCount);
						rate = (float) (Math.floor(rate * 100) / 100);
						logger.info("recover status: in the past {} minutes, channel {} success: {}, fail: {}, successRate: {}%",
								recoverInterval, channelName, successCountStr, failCountStr, rate);
					}else{
						logger.info("recover status: in the past {} minutes, channel {} NO sms to send, can not calculate successRate", recoverInterval, channelName);
					}
					if(rate >= successRate){
						channelCheckStatusService.updateChannelToMaster(channelName, SmsConfig.SMS_STATE_TRUE);
						logger.info("recover Channel {}, rate {}% higher than default rate {}%, update weight = currentWeight", channelName, rate, successRate);
					}
				}
			}else{
				logger.info("recover status: in the past {} minutes, spare channel list(weight != currentWeight) is EMPTY", recoverInterval);
			}
		}
	}

	public void setSuccessRate(int successRate) {
		this.successRate = successRate;
	}

	public void setRecoverInterval(int recoverInterval) {
		this.recoverInterval = recoverInterval;
	}

	public void setChannelCheckStatusService(ChannelCheckStatusService channelCheckStatusService) {
		this.channelCheckStatusService = channelCheckStatusService;
	}

	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	public void setSmsConfigService(SmsConfigService smsConfigService) {
		this.smsConfigService = smsConfigService;
	}

}
