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
import com.pay.smsserver.channel.impl.XGMessageChannelImpl;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;
import com.pay.smsserver.util.DateUtil;

public class XGTMessageChannelTest extends SpringBaseTest{

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
	public void xgMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "XinGe";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		XGMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，单个手机号返回成功
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017";
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
		jsonObject.put("errmsg", "提交成功");
		jsonObject.put("sid", "33333");
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("33333", messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，单个手机号返回失败
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017";
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
		jsonObject.put("errmsg", "提交失败");
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
//		
		//分支测试三：mock控制http请求结果，部分成功部分失败
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
		jsonObject.put("result", 0);
		jsonObject.put("errmsg", "部分成功");
		JSONArray details = new JSONArray();
		JSONObject detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "44444");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "55555");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 4);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 5);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(4, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("17601027018", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("55555", messasgeResponses.get(1).getMsgId());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(2).getMemo());
		Assert.assertEquals(4, messasgeResponses.get(2).getResult());
		Assert.assertEquals("18601344991", messasgeResponses.get(2).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(2).getMsgId());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(3).getMemo());
		Assert.assertEquals(5, messasgeResponses.get(3).getResult());
		Assert.assertEquals("18601344992", messasgeResponses.get(3).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(3).getMsgId());
		
		//分支测试四：mock控制http请求结果，都成功
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
		jsonObject.put("result", 0);
		jsonObject.put("errmsg", "部分成功");
		details = new JSONArray();
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "44444");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "55555");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "66666");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "77777");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(4, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("17601027018", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("55555", messasgeResponses.get(1).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(2).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(2).getResult());
		Assert.assertEquals("18601344991", messasgeResponses.get(2).getMobile());
		Assert.assertEquals("66666", messasgeResponses.get(2).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(3).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(3).getResult());
		Assert.assertEquals("18601344992", messasgeResponses.get(3).getMobile());
		Assert.assertEquals("77777", messasgeResponses.get(3).getMsgId());
		
		//分支测试五：mock控制http请求结果，都失败
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
		jsonObject.put("result", 1);
		jsonObject.put("errmsg", "全部失败");
		details = new JSONArray();
		detail = new JSONObject();
		detail.put("result", 2);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 3);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 4);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 5);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[全部失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试六：http请求返回失败
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
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void xgSaleMessageChannelSendMessageTest() throws Exception{
		
		String channelName = "XinGeSale";
		SmsEntity smsEntity = null;
		List<MessageResponse> messasgeResponses = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;
		XGMessageChannelImpl messageChannel = null;
		
		//分支测试一：mock控制http请求结果，单个手机号返回成功
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017";
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
		jsonObject.put("errmsg", "提交成功");
		jsonObject.put("sid", "33333");
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("33333", messasgeResponses.get(0).getMsgId());
		
		//分支测试二：mock控制http请求结果，单个手机号返回失败
		appCode = "testAppCode";
		content = new Date() + "这是一条测试短信";
		ip = "10.10.116.35";
		to = "17601027017";
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
		jsonObject.put("errmsg", "提交失败");
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
//		
		//分支测试三：mock控制http请求结果，部分成功部分失败
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
		jsonObject.put("result", 0);
		jsonObject.put("errmsg", "部分成功");
		JSONArray details = new JSONArray();
		JSONObject detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "44444");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "55555");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 4);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 5);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(4, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("17601027018", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("55555", messasgeResponses.get(1).getMsgId());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(2).getMemo());
		Assert.assertEquals(4, messasgeResponses.get(2).getResult());
		Assert.assertEquals("18601344991", messasgeResponses.get(2).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(2).getMsgId());
		Assert.assertEquals("FAILURE[提交失败]", messasgeResponses.get(3).getMemo());
		Assert.assertEquals(5, messasgeResponses.get(3).getResult());
		Assert.assertEquals("18601344992", messasgeResponses.get(3).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(3).getMsgId());
		
		//分支测试四：mock控制http请求结果，都成功
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
		jsonObject.put("result", 0);
		jsonObject.put("errmsg", "部分成功");
		details = new JSONArray();
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "44444");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "55555");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "66666");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 0);
		detail.put("errmsg", "提交成功");
		detail.put("sid", "77777");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(4, messasgeResponses.size());
		Assert.assertEquals("提交成功", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(0).getResult());
		Assert.assertEquals("17601027017", messasgeResponses.get(0).getMobile());
		Assert.assertEquals("44444", messasgeResponses.get(0).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(1).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(1).getResult());
		Assert.assertEquals("17601027018", messasgeResponses.get(1).getMobile());
		Assert.assertEquals("55555", messasgeResponses.get(1).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(2).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(2).getResult());
		Assert.assertEquals("18601344991", messasgeResponses.get(2).getMobile());
		Assert.assertEquals("66666", messasgeResponses.get(2).getMsgId());
		Assert.assertEquals("提交成功", messasgeResponses.get(3).getMemo());
		Assert.assertEquals(0, messasgeResponses.get(3).getResult());
		Assert.assertEquals("18601344992", messasgeResponses.get(3).getMobile());
		Assert.assertEquals("77777", messasgeResponses.get(3).getMsgId());
		
		//分支测试五：mock控制http请求结果，都失败
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
		jsonObject.put("result", 1);
		jsonObject.put("errmsg", "全部失败");
		details = new JSONArray();
		detail = new JSONObject();
		detail.put("result", 2);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "17601027017");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 3);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "17601027018");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 4);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344991");
		details.put(detail);
		detail = new JSONObject();
		detail.put("result", 5);
		detail.put("errmsg", "提交失败");
		detail.put("mobile", "18601344992");
		details.put(detail);
		jsonObject.put("detail", details);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		messageChannel.setHttpService(httpService);
		messasgeResponses = messageChannel.sendMessage(smsEntity);
		Assert.assertEquals(1, messasgeResponses.size());
		Assert.assertEquals("FAILURE[全部失败]", messasgeResponses.get(0).getMemo());
		Assert.assertEquals(1, messasgeResponses.get(0).getResult());
		Assert.assertEquals(to, messasgeResponses.get(0).getMobile());
		Assert.assertEquals(null, messasgeResponses.get(0).getMsgId());
		
		//分支测试六：http请求返回失败
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
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
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
	public void xgSaleMessageChannelPullUplinkMessagesTest() throws Exception{
		
		String channelName = "XinGeSale";
		XGMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONObject json = new JSONObject();
		Date time = new Date();
		json.put("result", "0");
		json.put("errmsg", "ok");
		json.put("count", 2);
		JSONArray datas = new JSONArray();
		json.put("data", datas);
		JSONObject data = new JSONObject();
		data.put("time", System.currentTimeMillis()/1000);
		data.put("text", "这是一条上行1"+time);
		data.put("mobile", "17876545432");
		data.put("extend", "12123120001");
		datas.put(data);
		data = new JSONObject();
		data.put("time", System.currentTimeMillis()/1000);
		data.put("text", "这是另外一条上行2"+time);
		data.put("mobile", "17876545433");
		data.put("extend", "12123120002");
		datas.put(data);
		
		String appCode = "appCodetest1";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		appCode = "appCodetest2";
		expandCode = "0002";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<UpLinkEntity> upLinkEntitys = channelMapper.listUplinkMessages("17876545433", "这是另外一条上行2"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545433", upLinkEntitys.get(0).getPhone());
		upLinkEntitys = channelMapper.listUplinkMessages("17876545432", "这是一条上行1"+time, time);
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545432", upLinkEntitys.get(0).getPhone());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试三：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		json = new JSONObject();
		time = new Date();
		json.put("result", "0");
		json.put("errmsg", "ok");
		json.put("count", 2);
		datas = new JSONArray();
		json.put("data", datas);
		data = new JSONObject();
		data.put("time", System.currentTimeMillis()/1000);
		data.put("text", "这是一条上行11"+time);
		data.put("mobile", "17876545432");
		data.put("extend", "12123120001");
		datas.put(data);
		data = new JSONObject();
		data.put("time", System.currentTimeMillis()/1000);
		data.put("text", "这是另外一条上行22"+time);
		data.put("mobile", "17876545433");
		data.put("extend", "12123120002");
		datas.put(data);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullUplinkMessages();
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		upLinkEntitys = channelMapper.listUplinkMessages("17876545433", "这是另外一条上行22"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545433", upLinkEntitys.get(0).getPhone());
		upLinkEntitys = channelMapper.listUplinkMessages("17876545432", "这是一条上行11"+time, calendar.getTime());
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17876545432", upLinkEntitys.get(0).getPhone());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void xgMessageChannelPullCallbackStatusesTest() throws Exception{
		String channelName = "XinGeSale";
		XGMessageChannelImpl messageChannel = null;
		
		//分支测试一：http请求失败，结果处理是否正常
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = (HttpService)context.getBean("httpService");
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		//分支测试二：mock控制http请求结果，返回一个json数组，保存数据库，发送mq
		JSONObject json = new JSONObject();
		Date time = new Date();
		json.put("result", "0");
		json.put("errmsg", "ok");
		json.put("count", 2);
		JSONArray datas = new JSONArray();
		json.put("data", datas);
		JSONObject data = new JSONObject();
		data.put("user_receive_time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		data.put("errmsg", "00");
		data.put("description", "这是一条上行1"+time);
		data.put("mobile", "17876545432");
		data.put("sid", "99999");
		datas.put(data);
		data = new JSONObject();
		data.put("user_receive_time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		data.put("errmsg", "00");
		data.put("description", "这是另外一条上行2"+time);
		data.put("mobile", "17876545433");
		data.put("sid", "00000");
		datas.put(data);;
		
		String appCode = "appCodetest1";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		appCode = "appCodetest2";
		expandCode = "0002";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		List<SmsStatusEntity> smsStatusEntitys = channelMapper.listMessageStatuses("17876545433", "00000", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545433", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("00000", smsStatusEntitys.get(0).getMsgid());
		smsStatusEntitys = channelMapper.listMessageStatuses("17876545432", "99999", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545432", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("99999", smsStatusEntitys.get(0).getMsgid());
		
		//扫尾
		redisManager.del(appCode);
		redisManager.del(expandCode);
		
		//分支测试三：mock控制http请求结果，返回一个json数组，保存数据库，appCode找不到，不发mq
		json = new JSONObject();
		time = new Date();
		json.put("result", "0");
		json.put("errmsg", "ok");
		json.put("count", 2);
		datas = new JSONArray();
		json.put("data", datas);
		data = new JSONObject();
		data.put("user_receive_time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		data.put("errmsg", "00");
		data.put("description", "这是一条上行11"+time);
		data.put("mobile", "17876545432");
		data.put("sid", "33333");
		datas.put(data);
		data = new JSONObject();
		data.put("user_receive_time", DateUtil.dateToStr(time, "yyyy-MM-dd hh:mm:ss"));
		data.put("errmsg", "00");
		data.put("description", "这是另外一条上行22"+time);
		data.put("mobile", "17876545433");
		data.put("sid", "44444");
		datas.put(data);
		
		messageChannel = (XGMessageChannelImpl)messageChannelContext.getChannel(channelName);
		httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(json.toString());
		messageChannel.setHttpService(httpService);
		messageChannel.pullCallbackStatuses();
		
		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -30);
		smsStatusEntitys = channelMapper.listMessageStatuses("17876545433", "44444", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545433", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("44444", smsStatusEntitys.get(0).getMsgid());
		smsStatusEntitys = channelMapper.listMessageStatuses("17876545432", "33333", calendar.getTime());
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17876545432", smsStatusEntitys.get(0).getPhone());
		Assert.assertEquals("33333", smsStatusEntitys.get(0).getMsgid());
	}
}
