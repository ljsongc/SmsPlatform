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
import com.pay.smsserver.channel.impl.DHMessageChannelImpl;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;
import com.pay.smsserver.util.DateUtil;

public class DHMessageChannelTest extends SpringBaseTest{

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
	public void dhMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "DaHan";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		DHMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回有成功有失败的分支
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
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "0");
		jsonObject.put("desc", "提交成功");
		jsonObject.put("msgid", "22222");
		jsonObject.put("failPhones", "17601027017,17601027018");
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(2, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交成功]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017,17601027018", messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("18601344991,18601344992", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("22222", messasgeResponses.get(1).getMsgId());
		
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
		
		jsonObject = new JSONObject();
		jsonObject.put("result", "1");
		jsonObject.put("desc", "提交失败");
		jsonObject.put("failPhones", to);
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回都成功
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
		
		jsonObject = new JSONObject();
		jsonObject.put("result", "0");
		jsonObject.put("desc", "提交成功");
		jsonObject.put("msgid", "44444");
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		
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
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void dhSaleMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "DaHanSale";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		DHMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，测试请求返回有成功有失败的分支
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
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "0");
		jsonObject.put("desc", "提交成功");
		jsonObject.put("msgid", "22222");
		jsonObject.put("failPhones", "17601027017,17601027018");
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(2, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交成功]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017,17601027018", messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("18601344991,18601344992", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("22222", messasgeResponses.get(1).getMsgId());
		
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
		
		jsonObject = new JSONObject();
		jsonObject.put("result", "1");
		jsonObject.put("desc", "提交失败");
		jsonObject.put("failPhones", to);
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，测试请求返回都成功
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
		
		jsonObject = new JSONObject();
		jsonObject.put("result", "0");
		jsonObject.put("desc", "提交成功");
		jsonObject.put("msgid", "44444");
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		
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
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void dhSaleMessageChannelPullUplinkMessagesTest() throws Exception{
		
		String channelName = "DaHanSale";
		DHMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONObject json = new JSONObject();
		Date time = new Date();
		json.put("result", "0");
		JSONArray delivers = new JSONArray();
		JSONObject deliver = new JSONObject();
		deliver.put("delivertime", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		deliver.put("content", "这是大汉一条上行"+time);
		deliver.put("phone", "17876545432");
		deliver.put("subcode", "98765456780001");
		delivers.put(deliver);
		deliver = new JSONObject();
		deliver.put("delivertime", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		deliver.put("content", "这是大汉另外一条上行"+time);
		deliver.put("phone", "17876545433");
		deliver.put("subcode", "98765456780002");
		delivers.put(deliver);
		json.put("delivers", delivers);
		
		String appCode = "appCodetest1";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		appCode = "appCodetest2";
		expandCode = "0002";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<UpLinkEntity> upLinkEntitys = channelMapper.listUplinkMessages("17876545433", "这是大汉另外一条上行"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545433", upLinkEntitys.get(0).getPhone());
		upLinkEntitys = channelMapper.listUplinkMessages("17876545432", "这是大汉一条上行"+time, time);
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545432", upLinkEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试三：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		json = new JSONObject();
		time = new Date();
		json.put("result", "0");
		delivers = new JSONArray();
		deliver = new JSONObject();
		deliver.put("delivertime", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		deliver.put("content", "这是一条上行1"+time);
		deliver.put("phone", "17876545432");
		deliver.put("subcode", "98765456780001");
		delivers.put(deliver);
		deliver = new JSONObject();
		deliver.put("delivertime", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		deliver.put("content", "这是另外一条上行2"+time);
		deliver.put("phone", "17876545433");
		deliver.put("subcode", "98765456780002");
		delivers.put(deliver);
		json.put("delivers", delivers);
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		upLinkEntitys = channelMapper.listUplinkMessages("17876545433", "这是另外一条上行2"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545433", upLinkEntitys.get(0).getPhone());
		upLinkEntitys = channelMapper.listUplinkMessages("17876545432", "这是一条上行1"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545432", upLinkEntitys.get(0).getPhone());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void dhMessageChannelPullCallbackStatusesTest() throws Exception{
		String channelName = "DaHanSale";
		DHMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONObject json = new JSONObject();
		Date time = new Date();
		json.put("result", "0");
		JSONArray reports = new JSONArray();
		JSONObject report = new JSONObject();
		report.put("time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		report.put("wgcode", "0001");
		report.put("status", "1");
		report.put("desc", "这是一条任务状态"+time);
		report.put("phone", "17876545432");
		report.put("msgid", "66666");
		reports.put(report);
		report = new JSONObject();
		report.put("time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		report.put("status", "2");
		report.put("wgcode", "0002");
		report.put("desc", "这是另外一条任务状态"+time);
		report.put("phone", "17876545433");
		report.put("msgid", "77777");
		reports.put(report);
		json.put("reports", reports);
		
		messageChannel = (DHMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<SmsStatusEntity> smsStatusEntitys = channelMapper.listMessageStatuses("17876545433", "77777", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545433", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("这是另外一条任务状态"+time, smsStatusEntitys.get(0).getDescription());
		smsStatusEntitys = channelMapper.listMessageStatuses("17876545432", "66666", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545432", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("0001", smsStatusEntitys.get(0).getDescription());
	}
}
