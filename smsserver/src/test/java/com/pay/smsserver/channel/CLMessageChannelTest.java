package com.pay.smsserver.channel;

import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.Mockito.when;

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
import com.pay.smsserver.channel.impl.CLMessageChannelImpl;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;

public class CLMessageChannelTest extends SpringBaseTest{

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
	public void clMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "ChuangLan";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		CLMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回成功的分支
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn("123456,0\n11111");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("0", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals("11111", messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回失败的分支
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn("123456,19");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[19]", messasgeResponses.get(0).getMemo());
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void clSaleMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "ChuangLanSale";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		CLMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回成功的分支
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn("123456,0\n11111");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("0", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals("11111", messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回失败的分支
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn("123456,19");
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[19]", messasgeResponses.get(0).getMemo());
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
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void clSaleMessageChannelPullUplinkMessagesTest() throws Exception{
		
		String channelName = "ChuangLanSale";
		CLMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		Date time = new Date();
		jsonObject.put("content", "收到了"+time);
		jsonObject.put("mobile", "17010909876");
		jsonObject.put("create_time", time.getTime());
		jsonObject.put("msg_src_code", "098887660001");
		jsonArray.put(jsonObject);
		
		String appCode = "appCodetest";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn(jsonArray.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		List<UpLinkEntity> upLinkEntitys = channelMapper.listUplinkMessages("17010909876", "收到了"+time, time);
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17010909876", upLinkEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		jsonArray = new JSONArray();
		jsonObject = new JSONObject();
		time = new Date();
		jsonObject.put("content", "我收到了"+time);
		jsonObject.put("mobile", "17010909876");
		jsonObject.put("create_time", time.getTime());
		jsonObject.put("msg_src_code", "0988876601234");
		jsonArray.put(jsonObject);
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn(jsonArray.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		upLinkEntitys = channelMapper.listUplinkMessages("17010909876", "我收到了"+time, time);
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17010909876", upLinkEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void clMessageChannelPullCallbackStatusesTest() throws Exception{
		String channelName = "ChuangLanSale";
		CLMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONObject jsonObject = new JSONObject("{\"ret\":0,\"result\":[{\"msgid\":\"17120111331734234\",\"reportTime\":\"1712011133\",\"mobile\":\"18601344991\",\"status\":\"DISTURB\",\"statusDesc\":null},{\"msgid\":\"17120111331734234\",\"reportTime\":\"1712011133\",\"mobile\":\"17601027017\",\"status\":\"DISTURB\",\"statusDesc\":null}]}");
		String appCode = "appCodetest";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (CLMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.post(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR, -1);
		List<SmsStatusEntity> smsStatusEntitys = channelMapper.listMessageStatuses("18601344991", "17120111331734234", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("18601344991", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals(null, smsStatusEntitys.get(0).getDescription());
		Assert.assertEquals(channelName, smsStatusEntitys.get(0).getChannelNo());
	}
}
