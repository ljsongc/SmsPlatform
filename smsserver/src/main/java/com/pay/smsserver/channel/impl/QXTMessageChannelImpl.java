package com.pay.smsserver.channel.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
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

public class QXTMessageChannelImpl implements MessageChannel {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected String sendUrl; // 发送Url
	protected String userName; // 账号
	protected String passWord; // 密码
	protected String epid;//企业 id 
	protected String signature;// 短信里公司签名
	protected int connectionTimeOut;//连接超时时间
	protected int socketTimeout;//通信超时时间
	protected String proxyIp;//代理ip
	protected int proxyPort;//代理端口
	protected String requestCharset;//请求编码
	protected String responseCharset;//响应编码
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
		return "QiXinTong";
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

	/**
	 * 企信通通道群发提交时按照整体为单位，要么都成功，要么都失败，故这里messageResponses集合元素个数为1
	 */
	@Override
	public List<MessageResponse> sendMessage(SmsEntity smsEntity) {
		
		MessageResponse messageResponse = new MessageResponse();
		List<MessageResponse> messageResponses = new ArrayList<MessageResponse>();
		
		String expandCode = null;
		try {
			if(!StringUtils.isEmpty(smsEntity.getTo()) && smsEntity.getTo().length() > 0){
				if(!StringUtils.isEmpty(smsEntity.getAppCode())){
					expandCode = channelService.getExpandCodeByAppCode(smsEntity.getAppCode());
				}
				Map<String,String> paraMap = new HashMap<String,String>();
				String content = URLEncoder.encode(smsEntity.getContent(), "gb2312");
				String linkid = String.valueOf((UUID.randomUUID().toString() + new Date().getTime()).hashCode());
				paraMap.put("username", userName);
				paraMap.put("password", passWord);
				paraMap.put("phone", smsEntity.getTo());
				paraMap.put("message", content);
				paraMap.put("epid", epid);
				paraMap.put("linkid", linkid);
				paraMap.put("subcode", expandCode);
				paraMap.put(SmsConstants.SEND_URL, sendUrl);
				paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
				paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
				paraMap.put(SmsConstants.PROXY_IP, proxyIp);
				paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
				paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
				paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
				
				String responseStr = httpService.get(paraMap);
				this.logger.info("{} sendMessage to={} content={},msgid={}, result={}", smsEntity.getChannelNo(), smsEntity.getTo(), content,linkid, responseStr);
				
				if(StringUtils.isEmpty(responseStr)){
					messageResponse = new MessageResponse();
					messageResponse.setMemo("FAILURE[http请求失败]");
					messageResponse.setResult(-1);
					messageResponse.setMobile(smsEntity.getTo());
					messageResponses.add(messageResponse);
					return messageResponses;
				}
				
				if (responseStr != null && "00".equals(responseStr)) {
					messageResponse.setMemo(responseStr);
					messageResponse.setResult(0);
					messageResponse.setMobile(smsEntity.getTo());
					messageResponse.setMsgId(linkid);
				} else {
					messageResponse.setMemo("FAILURE[" + responseStr + "]");
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
		try {
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("username", userName);
			paraMap.put("password", passWord);
			paraMap.put("epid", epid);
			paraMap.put(SmsConstants.SEND_URL, upLinkUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.PROXY_IP, proxyIp);
			paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			String responseStr = httpService.get(paraMap);
			logger.info("{} response uplinkMessage={}", getChannelName(), responseStr);
			//epid+,+号码+,+linkid+,+上行地址码+,+上行内容+,+上行时间||
			//String responseStr = "企业ID,13800138000,1234567897,106900951234,你好,2014-09-18 15:09:55.693||企业ID,13800138000,1234567898,106900951234,你好,2014-09-18 15:09:59.573||企业ID,13800138000,1234567899,106900951234,你好,2014-09-18 15:10:02.653||";
			if (!StringUtils.isEmpty(responseStr)
					&& !("1".equals(responseStr)|| "epid error".equals(responseStr)
							|| "username or password error".equals(responseStr) 
							|| "No data!".equals(responseStr))) {
				String[] split = responseStr.split("\\|\\|");
				UpLinkEntity upLinkEntity = null;
				for (int i = 0; i < split.length; i++) {
					String[] message = split[i].split(",");
					try {
						upLinkEntity = new UpLinkEntity();
						upLinkEntity.setContent(message[4]);
						upLinkEntity.setPhone(message[1]);
						upLinkEntity.setCreateTime(new Date());
						upLinkEntity.setReceiveTime(DateUtil.strToDate(message[5], "yyyy-MM-dd HH:mm:ss"));
						upLinkEntity.setType(getChannelName());
						String appCode = channelService.getAppCodeByExpandCode(message[3]);
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
			this.logger.error("{} pushed uplink error", getChannelName(), e);
		}
	}

	
	@Override
	public void pushedUplinkMessages(JSONArray uplinkMessages) {
		try {
			if(uplinkMessages != null){
				for(int i=0; i< uplinkMessages.length(); i++){
					UpLinkEntity upLinkEntity = null;
					try {
						JSONObject dataJson = (JSONObject) uplinkMessages.get(i);
						upLinkEntity = new UpLinkEntity();
						
						String encode = URLEncoder.encode(dataJson.getString("msgContent"), "ISO8859-1");
						encode = URLDecoder.decode(encode, "gb2312");
						upLinkEntity.setContent(encode);
						
						upLinkEntity.setPhone(dataJson.getString("phone"));
						upLinkEntity.setCreateTime(new Date());
						upLinkEntity.setReceiveTime(new Date());
						upLinkEntity.setType(getChannelName());
						String appCode = channelService.getAppCodeByExpandCode(dataJson.getString("spNumber"));
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
			this.logger.error("{} pushed uplink messages={} error", getChannelName(), uplinkMessages, e);
		}
	}
	
	@Override
	public void pullCallbackStatuses() {
		try {
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("username", userName);
			paraMap.put("password", passWord);
			paraMap.put("epid", epid);
			paraMap.put(SmsConstants.SEND_URL, statusUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, requestCharset);
			paraMap.put(SmsConstants.RESPONSE_CHARSET, responseCharset);
			paraMap.put(SmsConstants.PROXY_IP, proxyIp);
			paraMap.put(SmsConstants.PROXY_PORT, String.valueOf(proxyPort));
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(connectionTimeOut));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(socketTimeout));
			String responseStr = httpService.get(paraMap);
			logger.info("{} callback status={}", getChannelName(), responseStr);
			//epid+,+号码+,+linkid+,+状态报告+,+时间||
			//String responseStr = "122759,13800130809,1234567897,DELIVRD,2014-09-18 15:09:55.693||122759,13800130809,1234567898,DELIVRD,2014-09-18 15:09:59.573||企业ID,13800130809,1234567899,DELIVRD,2014-09-18 15:10:02.653||";
			if (!StringUtils.isEmpty(responseStr)
					&& !("1".equals(responseStr)|| "password error".equals(responseStr)
							|| "username or password error".equals(responseStr))) {
				String[] split = responseStr.split("\\|\\|");
				for (int i = 0; i < split.length; i++) {
					String[] message = split[i].split(",");
					SmsStatusEntity smsStatusEntity = null;
					try {
						String id = message[0];
						if(epid.equals(id)){
							
						}
						smsStatusEntity = new SmsStatusEntity();
						smsStatusEntity.setReceiveDate(DateUtil.strToDate(message[4], "yyyy-MM-dd HH:mm:ss"));
						smsStatusEntity.setPhone(message[1]);
						smsStatusEntity.setErrmsg(message[3]);
						smsStatusEntity.setDescription(null);
						smsStatusEntity.setMsgid(message[2]);
						smsStatusEntity.setCreateDate(new Date());
						smsStatusEntity.setChannelNo(getChannelName());
						channelService.insertMessageStatus(smsStatusEntity);
						this.logger.info("{} get callback statuses insert db smsStatusEntity={}", getChannelName(), smsStatusEntity);
					} catch (Exception e) {
						this.logger.error("{} handle callback statuses smsStatusEntity={} error", getChannelName(), smsStatusEntity, e);
					}
				}
			}
		} catch (Exception e) {
			this.logger.error("{} callback statuses error", getChannelName(), e);
		}
	}
	
	@Override
	public void pushedCallbackStatuses(JSONArray callbackStatuses) {
		try {
			if(callbackStatuses != null){
				for(int i=0; i< callbackStatuses.length(); i++){
					JSONObject dataJson = (JSONObject) callbackStatuses.get(i);
					SmsStatusEntity smsStatusEntity = null;
					try {
						smsStatusEntity = new SmsStatusEntity();
						smsStatusEntity.setReceiveDate(DateUtil.strToDate(dataJson.getString("FSubmitTime"), "yyyyMMddhhmmss"));
						smsStatusEntity.setPhone(dataJson.getString("FDestAddr"));
						smsStatusEntity.setErrmsg(dataJson.getString("FReportCode"));
						smsStatusEntity.setDescription(dataJson.getString("FAckStatus"));
						smsStatusEntity.setMsgid(dataJson.getString("FLinkID"));
						smsStatusEntity.setCreateDate(new Date());
						smsStatusEntity.setChannelNo(getChannelName());
						channelService.insertMessageStatus(smsStatusEntity);
						this.logger.info("{} get callback statuses insert db smsStatusEntity={}", getChannelName(), smsStatusEntity);
					} catch (Exception e) {
						this.logger.error("{} handle callback statuses smsStatusEntity={} error", getChannelName(), smsStatusEntity, e);
					}
				}
			}
		} catch (Exception e) {
			this.logger.error("{} pushed callback statuses={} error", getChannelName(), callbackStatuses, e);
		}
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

	public void setEpid(String epid) {
		this.epid = epid;
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

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
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
	
	public void setSubmitNumber(int submitNumber) {
		this.submitNumber = submitNumber;
	}
	
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}
	
	public void setAstrotrainProducer(AstrotrainProducer astrotrainProducer) {
		this.astrotrainProducer = astrotrainProducer;
	}

	public void setStatusUrl(String statusUrl) {
		this.statusUrl = statusUrl;
	}

	public void setUpLinkUrl(String upLinkUrl) {
		this.upLinkUrl = upLinkUrl;
	}
	
}
