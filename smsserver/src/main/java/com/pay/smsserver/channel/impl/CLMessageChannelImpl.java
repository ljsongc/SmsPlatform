package com.pay.smsserver.channel.impl;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsserver.astrotrain.AstrotrainProducer;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsStatusEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;
import com.pay.smsserver.util.DateUtil;

public class CLMessageChannelImpl implements MessageChannel {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected String sendUrl; // 发送Url
	protected String userName; // 账号
	protected String passWord; // 密码
	protected String rd; // 是否需要状态报告
	protected String signature;// 短信里公司签名
	protected int connectionTimeOut;
	protected int socketTimeout;
	protected String requestCharset;//请求编码
	protected String responseCharset;//响应编码
	protected int singleMaxNum;//短信不拆分单条最大字数
	protected int multiMaxNum;//短信拆分单条最大字数
	
	protected boolean syncToRocketMQ = true;//是否同步到rocketmq，默认为true
	protected String uplinkUrl;//上行地址
	protected String uplinkKey;//获取上行短信的key
	protected int timeInterval;//时间间隔   单位:秒
	protected String statusUrl;//上行地址
	protected int pullStatusCount;//上行拉取条数
	protected int submitNumber = 1;//单次提交条数
	
	protected ChannelService channelService;
	protected HttpService httpService;
	protected AstrotrainProducer astrotrainProducer;
	
	@Override
	public String getChannelName() {
		return "ChuangLan";
	}

	@Override
	public void interceptRepeatPhone(SmsEntity smsEntity) {
		return;
	}

	@Override
	public void cleanContent(SmsEntity smsEntity) {
		String content = smsEntity.getContent();
		content = content.replace("【", "『");
		content = content.replace("】", "』");
		content = content.trim();
		smsEntity.setContent(content);
	}

	@Override
	public int getSubmitNumber() {
		return submitNumber;
	}

	/**
	 * 创蓝通道群发提交时按照整体为单位，要么都成功，要么都失败，故这里messageResponses集合元素个数为1
	 */
	@Override
	public List<MessageResponse> sendMessage(SmsEntity smsEntity) {
		
		MessageResponse messageResponse = new MessageResponse();
		List<MessageResponse> messageResponses = new ArrayList<MessageResponse>();
		
		try{
			if(!StringUtils.isEmpty(smsEntity.getTo()) && smsEntity.getTo().length() > 0){
				Map<String,String> paraMap = new HashMap<String,String>();
				paraMap.put("un", userName);
				paraMap.put("pw", passWord);
				paraMap.put("phone", smsEntity.getTo());
				paraMap.put("msg", URLEncoder.encode(smsEntity.getContent(), "UTF-8"));
				paraMap.put("rd", rd);
				if(!StringUtils.isEmpty(smsEntity.getAppCode())){
					String expandCode = channelService.getExpandCodeByAppCode(smsEntity.getAppCode());
					paraMap.put("ex", expandCode);
				}
				paraMap.put(SmsConstants.SEND_URL, sendUrl);
				paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
				paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
				paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
				paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
				
				String responseStr = httpService.post(paraMap);
				this.logger.info("{} sendMessage to={} content={}, param={} result={}", smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), paraMap, responseStr);
				
				if(StringUtils.isEmpty(responseStr)){
					messageResponse = new MessageResponse();
					messageResponse.setMemo("FAILURE[http请求失败]");
					messageResponse.setResult(-1);
					messageResponse.setMobile(smsEntity.getTo());
					messageResponses.add(messageResponse);
					return messageResponses;
				}
				
				String[] strArray = responseStr.split("\n");
				String status = strArray[0].split(",")[1];
				String msgId = null;
				if("0".equals(status) && strArray.length > 1){//发送成功才会有msgId
					msgId = strArray[1];
				}
				if ("0".equals(status)) {
					messageResponse.setMemo(status);
					messageResponse.setResult(0);
					messageResponse.setMobile(smsEntity.getTo());
					messageResponse.setMsgId(msgId);
				} else {
					messageResponse.setMemo("FAILURE[" + status + "]");
					messageResponse.setResult(1);
					messageResponse.setMobile(smsEntity.getTo());
				}
			}
		} catch (Exception e) {
			messageResponse.setMemo("FAILURE[" + e.getMessage() + "]");
			messageResponse.setResult(-1);
			messageResponse.setMobile(smsEntity.getTo());
			this.logger.error("{} sendMessage to={} content={} error", smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), e);
		}
		messageResponses.add(messageResponse);
		return messageResponses;
	}

	@Override
	public void numberStatistics(SmsEntity smsEntity) {
		channelService.numberStatistics(smsEntity, signature, singleMaxNum, multiMaxNum);
	}
	
	@Override
	public void pullUplinkMessages() {
		String startTime = null;
		String endTime = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.set(Calendar.SECOND, 0);
			endTime = sdf.format(calendar.getTime());
			startTime = sdf.format(calendar.getTime().getTime() - (timeInterval - 1) * 1000);
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("account", userName);
			paraMap.put("key", uplinkKey);
			paraMap.put("startTime", startTime);
			paraMap.put("endTime", endTime);
			paraMap.put(SmsConstants.SEND_URL, uplinkUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			
			String responseStr = httpService.post(paraMap);
			this.logger.info("{} get uplink messages startTime={} endTime={}, result={}", getChannelName(), startTime, endTime, responseStr);
			
			if(!StringUtils.isEmpty(responseStr)){
				JSONArray jsonArray = new JSONArray(responseStr); 
				for(int i = 0; i < jsonArray.length(); i++){
					UpLinkEntity upLinkEntity = null;
					try {
						JSONObject object = (JSONObject) jsonArray.get(i);
						upLinkEntity = new UpLinkEntity();
						upLinkEntity.setContent(object.isNull("content") ? null : object.getString("content"));
						upLinkEntity.setPhone(object.isNull("mobile") ? null : object.getString("mobile"));
						upLinkEntity.setCreateTime(new Date());
						upLinkEntity.setReceiveTime(new Date(object.getLong("create_time")));
						upLinkEntity.setType(getChannelName());
						String appCode = channelService.getAppCodeByExpandCode(object.isNull("msg_src_code") ? null : object.getString("msg_src_code"));
						channelService.insertUplinkMessage(upLinkEntity);
						logger.info("{} insert uplinkMessage={}", getChannelName(), upLinkEntity);
						if(syncToRocketMQ &&!StringUtils.isEmpty(appCode)){
							JSONObject uplinkJson = new JSONObject(upLinkEntity);
							uplinkJson.put("createTime", DateUtil.dateToStr(upLinkEntity.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
							uplinkJson.put("receiveTime", DateUtil.dateToStr(upLinkEntity.getReceiveTime(), "yyyy-MM-dd HH:mm:ss"));
							uplinkJson.put("uniqueKey", UUID.randomUUID().toString());
							logger.info("{} send to rocketMQ  appCode={} uplinkSms={}", getChannelName(), appCode, uplinkJson);
							astrotrainProducer.sendStringMessage(uplinkJson.toString(), appCode);
						}
					} catch (Exception e) {
						this.logger.error("{} handle uplink messages upLinkEntity={} error", getChannelName(), upLinkEntity, e);
					} 
				}
			}
		} catch (Exception e) {
			this.logger.error("{} pull uplink messages startTime={} endTime={} error", getChannelName(), startTime, endTime, e);
		}
	}
	
	@Override
	public void pushedUplinkMessages(JSONArray uplinkMessages) {
		
	}

	@Override
	public void pullCallbackStatuses() {
		try {
			Map<String, String> paraMap = new HashMap<String, String>();
			paraMap.put("account", userName);
			paraMap.put("key", uplinkKey);
			paraMap.put("count", String.valueOf(pullStatusCount));
			paraMap.put(SmsConstants.SEND_URL, statusUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			
			String responseStr = httpService.post(paraMap);
			this.logger.info("{} get callback statuses pullCount={}, result={}", getChannelName(), pullStatusCount, responseStr);
			
			if(!StringUtils.isEmpty(responseStr)){
				JSONObject json = new JSONObject(responseStr);
				Integer ret = json.isNull("ret") ? null : json.getInt("ret");
				JSONArray jsonArray = json.isNull("result") ? null : json.getJSONArray("result");
				if(ret == 0 && jsonArray != null){
					for(int i = 0; i < jsonArray.length(); i++){
						SmsStatusEntity smsStatusEntity = null;
						try {
							JSONObject object = (JSONObject) jsonArray.get(i);
							smsStatusEntity = new SmsStatusEntity();
							String receiveTimeStr = object.isNull("reportTime") ? null : object.getString("reportTime");
							Date receiveDate = receiveTimeStr == null ? null : DateUtil.strToDate(receiveTimeStr, "yyMMddHHmm");
							smsStatusEntity.setReceiveDate(receiveDate);
							smsStatusEntity.setPhone(object.isNull("mobile") ? null : object.getString("mobile"));
							smsStatusEntity.setErrmsg(object.isNull("status") ? null : object.getString("status"));
							smsStatusEntity.setDescription(object.isNull("statusDesc") ? null : object.getString("statusDesc"));
							smsStatusEntity.setMsgid(object.isNull("msgid") ? null : object.getString("msgid"));
							smsStatusEntity.setCreateDate(new Date());
							smsStatusEntity.setChannelNo(getChannelName());
							channelService.insertMessageStatus(smsStatusEntity);
							this.logger.info("{} get callback statuses insert db smsStatusEntity={}", getChannelName(), smsStatusEntity);
						} catch (Exception e) {
							this.logger.error("{} handle callback statuses smsStatusEntity={} error", getChannelName(), smsStatusEntity, e);
						}
					}
				}
			}
		} catch (Exception e) {
			this.logger.error("{} pull callback statuses pullStatusCount={} error", getChannelName(), pullStatusCount, e);
		}
	}

	@Override
	public void pushedCallbackStatuses(JSONArray callbackStatuses) {
		
	}

	public void setSendUrl(String sendUrl) {
		this.sendUrl = sendUrl;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public void setRd(String rd) {
		this.rd = rd;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setConnectionTimeOut(int connectionTimeOut) {
		this.connectionTimeOut = connectionTimeOut;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public void setRequestCharset(String requestCharset) {
		this.requestCharset = requestCharset;
	}

	public void setResponseCharset(String responseCharset) {
		this.responseCharset = responseCharset;
	}
	
	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public void setSingleMaxNum(int singleMaxNum) {
		this.singleMaxNum = singleMaxNum;
	}

	public void setMultiMaxNum(int multiMaxNum) {
		this.multiMaxNum = multiMaxNum;
	}

	public void setUplinkUrl(String uplinkUrl) {
		this.uplinkUrl = uplinkUrl;
	}

	public void setUplinkKey(String uplinkKey) {
		this.uplinkKey = uplinkKey;
	}

	public void setTimeInterval(int timeInterval) {
		this.timeInterval = timeInterval;
	}

	public void setAstrotrainProducer(AstrotrainProducer astrotrainProducer) {
		this.astrotrainProducer = astrotrainProducer;
	}
	
	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public void setPullStatusCount(int pullStatusCount) {
		this.pullStatusCount = pullStatusCount;
	}

	public void setSubmitNumber(int submitNumber) {
		this.submitNumber = submitNumber;
	}

	public boolean isSyncToRocketMQ() {
		return syncToRocketMQ;
	}

	public void setSyncToRocketMQ(boolean syncToRocketMQ) {
		this.syncToRocketMQ = syncToRocketMQ;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
}
