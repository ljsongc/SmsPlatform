package com.pay.smsserver.channel;

import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.Mockito.when;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsStatusEntity;
import com.pay.smsserver.channel.impl.QXTMessageChannelImpl;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;
import com.pay.smsserver.util.DateUtil;

public class QXTMessageChannelTest extends SpringBaseTest{

	@Autowired
	private MessageChannelContext messageChannelContext;
	@Autowired
	private ChannelService channelService;
	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private RedisManager redisManager;
	@Autowired
	private ApplicationContext context;
	
	private HttpService httpService;
	
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void qxtMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "QiXinTong";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		QXTMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回成功
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,17601027018,18601344991,18601344992";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("00");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("00", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertNotEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回都失败的分支
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,17601027018,18601344991,18601344992";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("02");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[02]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试三：http请求返回失败
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,18601344991";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[http请求失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(-1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void qxtSaleMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "QiXinTongSale";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		QXTMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回成功
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,17601027018,18601344991,18601344992";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("00");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("00", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertNotEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回都失败的分支
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,17601027018,18601344991,18601344992";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("02");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[02]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试三：http请求返回失败
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017,18601344991";
		level = SmsSendLevel.NORMAL;
		type = SmsSendType.SALE;
		token = "token";
		smsEntity = new SmsEntity();
		smsEntity.setAppCode(appCode);
		smsEntity.setChannelNo(channelName);
		smsEntity.setContent(content);
		smsEntity.setIp(ip);
		smsEntity.setLevel(level);
		smsEntity.setPreTime(new Date());
		smsEntity.setTime(new Date());
		smsEntity.setTo(to);
		smsEntity.setToken(token);
		smsEntity.setType(type);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[http请求失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(-1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
	}
	
	@Test
	@Transactional
	public void qxtSaleMessageChannelPushedUplinkMessagesTest() throws Exception{
		
		String channelName = "QiXinTongSale";
		QXTMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedUplinkMessages(null);
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		Date time = new Date();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("msgContent", URLDecoder.decode(URLEncoder.encode("呵呵"+time, "gb2312"), "ISO8859-1"));
		jsonObject.put("phone", "17876545432");
		jsonObject.put("spNumber", "98765456780001");
		jsonArray.put(jsonObject);
		
		String appCode = "appCodetest1";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedUplinkMessages(jsonArray);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<UpLinkEntity> upLinkEntitys = channelMapper.listUplinkMessages("17876545432", "呵呵"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545432", upLinkEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试三：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		time = new Date();
		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		jsonObject.put("msgContent", URLDecoder.decode(URLEncoder.encode("呵呵888"+time, "gb2312"), "ISO8859-1"));
		jsonObject.put("phone", "17876545430");
		jsonObject.put("spNumber", "98765456780001");
		jsonArray.put(jsonObject);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedUplinkMessages(jsonArray);
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		upLinkEntitys = channelMapper.listUplinkMessages("17876545430", "呵呵888"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545430", upLinkEntitys.get(0).getPhone());
	}
	
	@Test
	@Transactional
	public void clMessageChannelPushedCallbackStatusesTest() throws Exception{
		String channelName = "QiXinTongSale";
		QXTMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedUplinkMessages(null);
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		Date time = new Date();
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("FSubmitTime", DateUtil.dateToStr(time, "yyyyMMddhhmmss"));
		jsonObject.put("FDestAddr", "17876545432");
		jsonObject.put("FReportCode", "000");
		jsonObject.put("FAckStatus", "上行");
		jsonObject.put("FLinkID", "986780001");
		jsonArray.put(jsonObject);
		
		String appCode = "appCodetest1";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedCallbackStatuses(jsonArray);
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<SmsStatusEntity> smsStatusEntitys = channelMapper.listMessageStatuses("17876545432", "986780001", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545432", smsStatusEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试三：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		time = new Date();
		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		jsonObject.put("FSubmitTime", DateUtil.dateToStr(time, "yyyyMMddhhmmss"));
		jsonObject.put("FDestAddr", "17876545433");
		jsonObject.put("FReportCode", "000");
		jsonObject.put("FAckStatus", "上行");
		jsonObject.put("FLinkID", "986780002");
		jsonArray.put(jsonObject);
		
		messageChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel(channelName);
		messageChannel.pushedCallbackStatuses(jsonArray);
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		smsStatusEntitys = channelMapper.listMessageStatuses("17876545433", "986780002", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545433", smsStatusEntitys.get(0).getPhone());
	}
}
