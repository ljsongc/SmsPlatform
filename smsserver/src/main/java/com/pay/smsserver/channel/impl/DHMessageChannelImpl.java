package com.pay.smsserver.channel.impl;

import java.security.MessageDigest;
import java.util.ArrayList;
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

/**
 *	大汉三通短信通道接口实现
 * @author muya.cao
 *
 */
public class DHMessageChannelImpl implements MessageChannel {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String url;//
	protected String account;// 用户名（必填）
	protected String password;// 密码（必填）
	protected String signature; // 短信签名（必填）
	protected int connectionTimeOut;
	protected int socketTimeout;
	protected String requestCharset;//请求编码
	protected String responseCharset;//响应编码
	protected String proxyIp;
	protected int proxyPort;
	protected int singleMaxNum;//短信不拆分单条最大字数
	protected int multiMaxNum;//短信拆分单条最大字数
	protected boolean syncToRocketMQ = true;//是否同步到rocketmq，默认为true
	protected int submitNumber = 1;//单次提交条数
	protected String statusUrl;//状态拉取地址
	protected String upLinkUrl;//上行短信拉取地址

	protected ChannelService channelService;
	protected HttpService httpService;
	protected AstrotrainProducer astrotrainProducer;

	@Override
	public String getChannelName() {
		return "DaHan";
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
		content = content.replace("[", "『");
		content = content.replace("]", "』");
		content = content.trim();
		smsEntity.setContent(content);
	}

	@Override
	public int getSubmitNumber() {
		return 1;
	}

	@Override
	public List<MessageResponse> sendMessage(SmsEntity smsEntity) {

		List<MessageResponse> messageResponses = new ArrayList<MessageResponse>();
		MessageResponse messageResponse = null;
		String expandCode = null;
		try{
			if(!StringUtils.isEmpty(smsEntity.getAppCode())){
				expandCode = channelService.getExpandCodeByAppCode(smsEntity.getAppCode());
			}
			String msgid = UUID.randomUUID().toString().replace("-", ""); // 短信id，查询短信状态报告时需要
			JSONObject data = new JSONObject();
			data.put("account", account);
			//MD5加密(32位小写)
			data.put("password", MD5Encode(password));
			data.put("msgid", msgid);
			data.put("phones", smsEntity.getTo());
			data.put("content", smsEntity.getContent());
			data.put("sign", signature);
			data.put("subcode", expandCode);
			data.put("sendtime", "");

			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, url);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.PROXY_IP, proxyIp);
			paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
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
			String result = responseJson.isNull("result") ? null : responseJson.getString("result");
			String errMsg = responseJson.isNull("desc") ? null : responseJson.getString("desc");
			String msgId = responseJson.isNull("msgid") ? null : responseJson.getString("msgid");
			if("0".equals(result)){
				//result为0 不表示所有手机号都提交成功,failPhones为失败手机号
				String failPhones = responseJson.isNull("failPhones") ? null : responseJson.getString("failPhones");
				String successPhones = null;
				boolean isPartFail = false;
				if(!StringUtils.isEmpty(failPhones)){
					isPartFail = true;
					StringBuilder successPhonesTemp = new StringBuilder();
					for(String phone : smsEntity.getTo().split(",")){
						if(!failPhones.contains(phone)){
							successPhonesTemp.append(phone).append(",");
						}
					}
					if(successPhonesTemp.indexOf(",") != -1){
						successPhonesTemp.deleteCharAt(successPhonesTemp.length() - 1);
						successPhones = successPhonesTemp.toString();
					}
				}else{
					successPhones = smsEntity.getTo();
				}
				if(isPartFail){
					messageResponse = new MessageResponse();
					messageResponse.setMemo("FAILURE[" + errMsg + "]");
					messageResponse.setResult(Integer.parseInt(result));
					messageResponse.setMobile(failPhones);
					messageResponses.add(messageResponse);
				}
				if(!StringUtils.isEmpty(successPhones)){
					messageResponse = new MessageResponse();
					messageResponse.setMemo(errMsg);
					messageResponse.setResult(0);
					messageResponse.setMobile(successPhones);
					messageResponse.setMsgId(msgId);
					messageResponses.add(messageResponse);
				}
			}else{//这一批提交失败了
				messageResponse = new MessageResponse();
				messageResponse.setMemo("FAILURE[" + errMsg + "]");
				messageResponse.setResult(Integer.parseInt(result));
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

	/**
	 * 密码MD5加密(小写)
	 * @param sourceString
	 * @return
	 */
	public String MD5Encode(String sourceString) {
		String resultString = null;
		try {
			resultString = new String(sourceString);
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] bytes =md.digest(resultString.getBytes());

			StringBuffer bf = new StringBuffer(bytes.length * 2);
			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xFF) < 16) {
					bf.append("0");
				}
				bf.append(Long.toString(bytes[i] & 0xFF, 16));
			}

			resultString= bf.toString();
		} catch (Exception e) {
			logger.error("password md5 error",e);
		}
		return resultString;
	}

	@Override
	public void numberStatistics(SmsEntity smsEntity) {
		channelService.numberStatistics(smsEntity, signature, singleMaxNum, multiMaxNum);
	}

	@Override
	public void pullUplinkMessages() {

		try {
			JSONObject param = new JSONObject();
			param.put("account", account);
			param.put("password", MD5Encode(password));//MD5加密(32位小写)
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, upLinkUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.PROXY_IP, proxyIp);
			paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			paraMap.put(SmsConstants.JSON, param.toString());
			String responseStr = httpService.postJSON(paraMap);
			this.logger.info("{} get uplink result={}", getChannelName(), responseStr);
			if(responseStr != null){
				JSONObject json = new JSONObject(responseStr);
				if ("0".equals(json.get("result"))) {
					JSONArray jsonArray = json.isNull("delivers") ? null : json.getJSONArray("delivers");
					if(jsonArray == null){
						return;
					}
					for (int i = 0; i < jsonArray.length(); i++) {
						UpLinkEntity upLinkEntity = null;
						try {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							upLinkEntity = new UpLinkEntity();
							upLinkEntity.setReceiveTime(DateUtil.strToDate(jsonObject.getString("delivertime"), "yyyy-MM-dd hh:mm:ss"));
							upLinkEntity.setContent(jsonObject.getString("content"));
							upLinkEntity.setPhone(jsonObject.getString("phone"));
							upLinkEntity.setType(getChannelName());
							upLinkEntity.setCreateTime(new Date());
							String subcode = jsonObject.getString("subcode");
							String appCode = channelService.getAppCodeByExpandCode(subcode);
							channelService.insertUplinkMessage(upLinkEntity);
							logger.info("{} insert uplinkMessage={}", getChannelName(), upLinkEntity);
							if (!StringUtils.isEmpty(appCode)) {
								org.json.JSONObject uplinkJson = new org.json.JSONObject(upLinkEntity);
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
			this.logger.error("{} pull uplink messages error", getChannelName(), e);
		}
	}

	@Override
	public void pushedUplinkMessages(JSONArray uplinkMessages) {

	}

	@Override
	public void pullCallbackStatuses() {
		try {
			JSONObject data = new JSONObject();
			data.put("account", account);
			data.put("password", MD5Encode(password));//MD5加密(32位小写)

			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put(SmsConstants.SEND_URL, statusUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.PROXY_IP, proxyIp);
			paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			paraMap.put(SmsConstants.JSON, data.toString());
			String responseStr = httpService.postJSON(paraMap);
			this.logger.info("{} get callback statuses result={}", getChannelName(), responseStr);
			if(responseStr != null){
				JSONObject json = new JSONObject(responseStr);
				if("0".equals(json.get("result"))){
					JSONArray jsonArray = json.isNull("reports") ? null : json.getJSONArray("reports");
					if(jsonArray == null){
						return;
					}
					for (int i = 0; i < jsonArray.length(); i++) {
						Object Object = jsonArray.get(i);
						SmsStatusEntity smsStatusEntity = null;
						try {
							JSONObject jsonObject = (JSONObject)Object;
							smsStatusEntity = new SmsStatusEntity();
							smsStatusEntity.setReceiveDate(DateUtil.strToDate(jsonObject.get("time").toString(), "yyyy-MM-dd hh:mm:ss"));
							smsStatusEntity.setPhone(jsonObject.get("phone").toString());
							if("1".equals(jsonObject.get("status").toString())){
								smsStatusEntity.setErrmsg(jsonObject.get("desc").toString());
								smsStatusEntity.setDescription(jsonObject.get("wgcode").toString());
							}else{
								smsStatusEntity.setErrmsg(jsonObject.get("wgcode").toString());
								smsStatusEntity.setDescription(jsonObject.get("desc").toString());
							}
							smsStatusEntity.setMsgid(jsonObject.get("msgid").toString());
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
			this.logger.error("{} pull callback statuses error", getChannelName(), e);
		}
	}

	@Override
	public void pushedCallbackStatuses(JSONArray callbackStatuses) {

	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public void setSingleMaxNum(int singleMaxNum) {
		this.singleMaxNum = singleMaxNum;
	}

	public void setMultiMaxNum(int multiMaxNum) {
		this.multiMaxNum = multiMaxNum;
	}

	public void setSyncToRocketMQ(boolean syncToRocketMQ) {
		this.syncToRocketMQ = syncToRocketMQ;
	}

	public void setSubmitNumber(int submitNumber) {
		this.submitNumber = submitNumber;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public void setUpLinkUrl(String upLinkUrl) {
		this.upLinkUrl = upLinkUrl;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	public void setAstrotrainProducer(AstrotrainProducer astrotrainProducer) {
		this.astrotrainProducer = astrotrainProducer;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
}
