package com.pay.smsserver.channel.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
import com.pay.smsserver.util.MD5Util;

public class XGMessageChannelImpl implements MessageChannel {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected String sendUrl;
	protected String appkey;
	protected int sdkappid;
	protected String signature;// 短信里公司签名
	protected int sendType;//0为通知类，1为营销类
	protected int connectionTimeOut;
	protected int socketTimeout;
	protected String requestCharset;//请求编码
	protected String responseCharset;//响应编码
	protected int singleMaxNum;//短信不拆分单条最大字数
	protected int multiMaxNum;//短信拆分单条最大字数
	protected String upLinkUrl;//上行地址
	protected int pullUplinkCount;//上行拉取条数
	protected String statusUrl;//上行地址
	protected int pullStatusCount;//上行拉取条数
	protected boolean syncToRocketMQ = true;//是否同步到rocketmq，默认为true
	protected int submitNumber = 1;//单次提交条数
	
	protected Random random = new Random();
	protected ChannelService channelService;
	protected HttpService httpService;
	protected AstrotrainProducer astrotrainProducer;
	
	@Override
	public String getChannelName() {
		return "XinGe";
	}

	@Override
	public void interceptRepeatPhone(SmsEntity smsEntity) {
		return;
	}

	@Override
	public void cleanContent(SmsEntity smsEntity) {
		return;
	}

	@Override
	public int getSubmitNumber() {
		return 1;
	}

	@Override
	public List<MessageResponse> sendMessage(SmsEntity smsEntity) {
		
		MessageResponse messageResponse = null;
		List<MessageResponse> messageResponses = new ArrayList<MessageResponse>();
		
		String expandCode = null;
		try{
			if(!StringUtils.isEmpty(smsEntity.getAppCode())){
				expandCode = channelService.getExpandCodeByAppCode(smsEntity.getAppCode());
			}
			String nationCode = "86";
	        long rnd = random.nextInt(999999) % (999999 - 100000 + 1) + 100000;
	        long curTime = System.currentTimeMillis()/1000;
	        String url = String.format("%s?sdkappid=%d&random=%d", sendUrl, sdkappid, rnd);
	        
	        JSONObject data = new JSONObject();
			data.put("type", sendType);
			data.put("msg", smsEntity.getContent());
			if(!StringUtils.isEmpty(expandCode)){
				data.put("extend", expandCode);
			}else{
				data.put("extend", "");
			}
			data.put("ext", "");
			data.put("time", curTime);
			String sig = MD5Util.strToHash(String.format("appkey=%s&random=%d&time=%d&mobile=%s", appkey, rnd, curTime, smsEntity.getTo()));
			data.put("sig", sig);
			
			boolean isMultiPhones = false;
			String[] multiPhones = smsEntity.getTo().split(",");
			if(multiPhones.length > 1){
				isMultiPhones = true;
				JSONArray tel = new JSONArray();
				for(String p: multiPhones){
					if(!StringUtils.isEmpty(p)){
						JSONObject telElement = new JSONObject();
						telElement.put("nationcode", nationCode);
						telElement.put("mobile", p);
						tel.put(telElement);
					}
				}
				data.put("tel", tel);
			}else{
				JSONObject tel = new JSONObject();
				tel.put("nationcode", nationCode);
				tel.put("mobile", smsEntity.getTo());
				data.put("tel", tel);
			}
			
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, url);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			paraMap.put(SmsConstants.JSON, data.toString());
			
			String responseStr = httpService.postJSON(paraMap);
			this.logger.info("{} sendMessage to={} content={}, result={}", smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), responseStr);
			
			if(StringUtils.isEmpty(responseStr)){
				messageResponse = new MessageResponse();
				messageResponse.setMemo("FAILURE[http请求失败]");
				messageResponse.setResult(-1);
				messageResponse.setMobile(smsEntity.getTo());
				messageResponses.add(messageResponse);
				return messageResponses;
			}
			
			JSONObject responseJson = new JSONObject(responseStr);
			Integer topResult = responseJson.isNull("result") ? null : responseJson.getInt("result");
			String topErrMsg = responseJson.isNull("errmsg") ? null : responseJson.getString("errmsg");
			String msgId = null;
			if(topResult != null && topResult == 0){
				if(!isMultiPhones){
					msgId = responseJson.getString("sid");
					messageResponse = new MessageResponse();
					messageResponse.setMemo(topErrMsg);
					messageResponse.setResult(0);
					messageResponse.setMobile(smsEntity.getTo());
					messageResponse.setMsgId(msgId);
					messageResponses.add(messageResponse);
				}else{
					JSONArray array = responseJson.getJSONArray("detail");//这一批提交有成功有失败
					if(array != null){
						for(int i = 0; i < array.length(); i++){
							JSONObject responseDetailJson = array.getJSONObject(i);
							int result = responseDetailJson.getInt("result");
							String errMsg = responseDetailJson.getString("errmsg");
							if(result == 0){
								msgId = responseDetailJson.getString("sid");
							}
							String mobile = responseDetailJson.getString("mobile");
							if(result == 0){
								messageResponse = new MessageResponse();
								messageResponse.setMemo(errMsg);
								messageResponse.setResult(0);
								messageResponse.setMobile(mobile);
								messageResponse.setMsgId(msgId);
							} else {
								messageResponse = new MessageResponse();
								messageResponse.setMemo("FAILURE[" + errMsg + "]");
								messageResponse.setResult(result);
								messageResponse.setMobile(mobile);
							}
							messageResponses.add(messageResponse);
						}
					}
				}
			}else{//这一批提交失败了
				messageResponse = new MessageResponse();
				messageResponse.setMemo("FAILURE[" + topErrMsg + "]");
				messageResponse.setResult(topResult);
				messageResponse.setMobile(smsEntity.getTo());
				messageResponses.add(messageResponse);
			}
		}catch(Exception e) {
			messageResponse = new MessageResponse();
			messageResponse.setMemo("FAILURE[" + e.getMessage() + "]");
			messageResponse.setResult(-1);
			messageResponse.setMobile(smsEntity.getTo());
			messageResponses.add(messageResponse);
			this.logger.error("{} sendMessage to={} content={} error", smsEntity.getChannelNo(), smsEntity.getTo(), smsEntity.getContent(), e);
		}
		return messageResponses;
	}

	@Override
	public void numberStatistics(SmsEntity smsEntity) {
		channelService.numberStatistics(smsEntity, signature, singleMaxNum, multiMaxNum);
	}

	@Override
	public void pullUplinkMessages() {
		try {
			long rand = random.nextInt(999999) % (999999 - 100000 + 1) + 100000;
			long time = System.currentTimeMillis()/1000;
			String url = String.format("%s?sdkappid=%d&random=%d", upLinkUrl, sdkappid, rand);
			JSONObject data = new JSONObject();
			data.put("sig",MD5Util.strToHash((String.format("appkey=%s&random=%d&time=%d", appkey, rand, time))));
			data.put("time", time);
			data.put("type", 1);////0 1分别代表 短信下发状态，短信回复
			data.put("max", pullUplinkCount);//最多100
			
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, url);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			paraMap.put(SmsConstants.JSON, data.toString());
			
			String responseStr = httpService.postJSON(paraMap);
			this.logger.info("{} get uplink messages pullCount={}, result={}", getChannelName(), pullUplinkCount, responseStr);
			
			if(!StringUtils.isEmpty(responseStr)){
				JSONObject responseJson = new JSONObject(responseStr);
				Integer result = responseJson.isNull("result") ? null : responseJson.getInt("result");
				String errMsg = responseJson.isNull("errmsg") ? null : responseJson.getString("errmsg");
				Integer count = responseJson.isNull("count") ? null : responseJson.getInt("count");
				if(result != null && result == 0 && "ok".equals(errMsg) && count != null && count > 0){
					JSONArray dataArray = responseJson.getJSONArray("data");
					for(int i=0; i< dataArray.length(); i++){
						UpLinkEntity upLinkEntity = null;
						try {
							JSONObject dataJson = (JSONObject) dataArray.get(i);
							upLinkEntity = new UpLinkEntity();
							upLinkEntity.setContent(dataJson.getString("text"));
							upLinkEntity.setPhone(dataJson.getString("mobile"));
							upLinkEntity.setCreateTime(new Date());
							upLinkEntity.setReceiveTime(new Date(dataJson.getLong("time")*1000));
							upLinkEntity.setType(getChannelName());
							String appCode = channelService.getAppCodeByExpandCode(dataJson.getString("extend"));
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
			}
		} catch (Exception e) {
			this.logger.error("{} pull uplink messages pullUplinkCount={}, error", getChannelName(), pullUplinkCount, e);
		}
	}
	
	@Override
	public void pushedUplinkMessages(JSONArray uplinkMessages) {
		
	}
	
	@Override
	public void pullCallbackStatuses() {
		try {
			long rand = random.nextInt(999999) % (999999 - 100000 + 1) + 100000;
			long time = System.currentTimeMillis()/1000;
			String url = String.format("%s?sdkappid=%d&random=%d", statusUrl, sdkappid, rand);
			JSONObject data = new JSONObject();
			data.put("sig",MD5Util.strToHash((String.format("appkey=%s&random=%d&time=%d", appkey, rand, time))));
			data.put("time", time);
			data.put("type", 0);////0 1分别代表 短信下发状态，短信回复
			data.put("max", pullStatusCount);//最多100
			
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, url);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			paraMap.put(SmsConstants.JSON, data.toString());
			
			String responseStr = httpService.postJSON(paraMap);
			this.logger.info("{} get callback statuses pullCount={}, result={}", getChannelName(), pullStatusCount, responseStr);
			
			if(!StringUtils.isEmpty(responseStr)){
				JSONObject responseJson = new JSONObject(responseStr);
				int result = responseJson.getInt("result");
				String errMsg = responseJson.getString("errmsg");
				int count = responseJson.getInt("count");
				if(result == 0 && "ok".equals(errMsg) && count > 0){
					JSONArray dataArray = responseJson.getJSONArray("data");
					for(int i=0; i< dataArray.length(); i++){
						SmsStatusEntity smsStatusEntity = null;
						try {
							JSONObject dataJson = (JSONObject) dataArray.get(i);
							smsStatusEntity = new SmsStatusEntity();
							String receiveTimeStr = dataJson.getString("user_receive_time");
							smsStatusEntity.setReceiveDate(DateUtil.strToDate(receiveTimeStr, "yyyy-MM-dd hh:mm:ss"));
							smsStatusEntity.setPhone(dataJson.getString("mobile"));
							smsStatusEntity.setErrmsg(dataJson.getString("errmsg"));
							smsStatusEntity.setDescription(dataJson.getString("description"));
							smsStatusEntity.setMsgid(dataJson.getString("sid"));
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

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public void setSdkappid(int sdkappid) {
		this.sdkappid = sdkappid;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public void setSendType(int sendType) {
		this.sendType = sendType;
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

	public void setUpLinkUrl(String upLinkUrl) {
		this.upLinkUrl = upLinkUrl;
	}

	public void setPullUplinkCount(int pullUplinkCount) {
		this.pullUplinkCount = pullUplinkCount;
	}

	public void setSyncToRocketMQ(boolean syncToRocketMQ) {
		this.syncToRocketMQ = syncToRocketMQ;
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
	
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
}
