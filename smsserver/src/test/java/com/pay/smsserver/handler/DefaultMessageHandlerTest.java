package com.pay.smsserver.handler;

import static org.mockito.ArgumentMatchers.anyMapOf;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.channel.MessageChannelContext;
import com.pay.smsserver.channel.impl.QXTMessageChannelImpl;
import com.pay.smsserver.channel.impl.XGMessageChannelImpl;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.service.HttpService;

public class DefaultMessageHandlerTest extends SpringBaseTest{

	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private MessageHandler messageHandler;
	@Autowired
	private MessageChannelContext messageChannelContext;

	@Test
	public void testTempleant(){
		SmsBean smsBean = new SmsBean();
		Date time = new Date();
		String content = "正常发送测试"+time;
		smsBean.setAppCode("testAppCode");
		smsBean.setContent(content);
		smsBean.setIp("10.10.116.35");
		smsBean.setTo("10090909090,10898989898");
		smsBean.setToken("testToken");
		smsBean.setTemplateCode("BBXWCAPHQN63ED7NWDE0");
		smsBean.setTypeCode("NOTICE");
		messageHandler.handle(smsBean);
	}

	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void handleTest(){
		//分支测试一：通知类，两个手机号，发送成功，保存到成功表两条记录
		channelMapper.deleteChannelConfig();
		String smsStatus = "TRUE";
		String smsType = "NOTICE";
		String channelName = "XinGe";
		SmsConfig smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(1);
		smsConfig.setWeight(1);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		channelName = "ChuangLan";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(2);
		smsConfig.setWeight(2);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("result", "0");
		jsonObject.put("errmsg", "提交成功");
		jsonObject.put("sid", "33333");

		XGMessageChannelImpl xgChannel = (XGMessageChannelImpl)messageChannelContext.getChannel("XinGe");
		HttpService httpService = Mockito.mock(HttpService.class);
		when(httpService.postJSON(anyMapOf(String.class, String.class))).thenReturn(jsonObject.toString());
		xgChannel.setHttpService(httpService);
		Map<String,MessageChannel> channelMap = messageChannelContext.getChannelMap();
		channelMap.put("XinGe", xgChannel);
		messageChannelContext.setChannelMap(channelMap);

		SmsBean smsBean = new SmsBean();
		Date time = new Date();
		String content = "正常发送测试"+time;
		smsBean.setAppCode("testAppCode");
		smsBean.setContent(content);
		smsBean.setIp("10.10.116.35");
		smsBean.setTo("10090909090,10898989898");
		smsBean.setToken("testToken");
		messageHandler.handle(smsBean);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 10);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listNoticeSuccessRecords("10090909090", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode", smsEntitys.get(0).getAppCode());
		Assert.assertEquals("33333", smsEntitys.get(0).getMsgid());
		smsEntitys = channelMapper.listNoticeSuccessRecords("10898989898", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode", smsEntitys.get(0).getAppCode());
		Assert.assertEquals("33333", smsEntitys.get(0).getMsgid());

		//分支测试二：营销类， 110个手机号，企信通通道，50一次，分三次发送，都成功，成功表三条记录
		channelMapper.deleteChannelConfig();
		smsStatus = "TRUE";
		smsType = "SALE";
		channelName = "QiXinTongSale";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(1);
		smsConfig.setWeight(1);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);

		QXTMessageChannelImpl qxtChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel("QiXinTongSale");
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("00");
		qxtChannel.setHttpService(httpService);
		channelMap = messageChannelContext.getChannelMap();
		channelMap.put("QiXinTongSale", qxtChannel);
		messageChannelContext.setChannelMap(channelMap);

		smsBean = new SmsBean();
		time = new Date();
		content = "正常发送测试"+time;
		smsBean.setAppCode("testAppCode");
		smsBean.setContent(content);
		smsBean.setIp("10.10.116.35");
		smsBean.setTo("18601344991,15712941735,15712941735,15712941735,15712941735,13994918110,17600655801,18937858589,18563955521,15369290929,13585727210,13919130555,18909170102,15801062706,15935675865,13274915436,18936383840,15152855873,15825312780,18931111982,18004435997,18609510319,15355823083,18873975012,13613645764,13679304404,13804657942,13500607950,13930315298,13501177055,15869880273,15160107050,18671507567,13501155003,13890175070,17701088627,18872653997,13801021540,18307001312,15098073627,18673307891,15971211216,15810277550,13810347441,13934045363,13115386817,18543571166,13423121901,13546265037,13148994344,15336021026,15877886365,13833332646,13510930771,13578883433,18047456327,15812214927,13391887225,13163530000,15925198291,17625758825,15252572128,13972127449,13912729785,18794350972,18346555921,18808995269,13722119878,18623288900,13905616306,18552568999,13127871210,13546057558,18608775563,15997243118,15086185809,13150102960,17640679699,15974955527,13854191120,18247424440,18926026886,15035458685,18699472083,18600881786,15717263305,15736693430,15635008080,13835830113,13809654402,13452148496,15855729095,18187821137,18991722787,15811006812,17625919488,15292869625,18620636103,13713140618,18259683251,13501155006,15712941735,15254388507,18759522450,13881578292,15049882416,15531060955,18220756590,15904073458,15132325805");
		smsBean.setToken("testToken");
		smsBean.setType(SmsSendType.SALE);
		messageHandler.handle(smsBean);

		calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 10);
		endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		startTime = calendar.getTime();
		smsEntitys = channelMapper.listSaleSuccessRecords("18601344991,15712941735,15712941735,15712941735,15712941735,13994918110,17600655801,18937858589,18563955521,15369290929,13585727210,13919130555,18909170102,15801062706,15935675865,13274915436,18936383840,15152855873,15825312780,18931111982,18004435997,18609510319,15355823083,18873975012,13613645764,13679304404,13804657942,13500607950,13930315298,13501177055,15869880273,15160107050,18671507567,13501155003,13890175070,17701088627,18872653997,13801021540,18307001312,15098073627,18673307891,15971211216,15810277550,13810347441,13934045363,13115386817,18543571166,13423121901,13546265037,13148994344", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode", smsEntitys.get(0).getAppCode());
		Assert.assertNotEquals(null, smsEntitys.get(0).getMsgid());
		smsEntitys = channelMapper.listSaleSuccessRecords("15336021026,15877886365,13833332646,13510930771,13578883433,18047456327,15812214927,13391887225,13163530000,15925198291,17625758825,15252572128,13972127449,13912729785,18794350972,18346555921,18808995269,13722119878,18623288900,13905616306,18552568999,13127871210,13546057558,18608775563,15997243118,15086185809,13150102960,17640679699,15974955527,13854191120,18247424440,18926026886,15035458685,18699472083,18600881786,15717263305,15736693430,15635008080,13835830113,13809654402,13452148496,15855729095,18187821137,18991722787,15811006812,17625919488,15292869625,18620636103,13713140618,18259683251", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode", smsEntitys.get(0).getAppCode());
		Assert.assertNotEquals(null, smsEntitys.get(0).getMsgid());
		smsEntitys = channelMapper.listSaleSuccessRecords("13501155006,15712941735,15254388507,18759522450,13881578292,15049882416,15531060955,18220756590,15904073458,15132325805", "正常发送测试"+time, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode", smsEntitys.get(0).getAppCode());
		Assert.assertNotEquals(null, smsEntitys.get(0).getMsgid());

	}

	@SuppressWarnings("deprecation")
	@Test
	@Transactional
	public void failHandleTest(){
		//分支测试一：通知类，失败通道为信鸽，目前两个通道，企信通和信鸽，再次发送用企信通成功，从失败表移除，插入成功表，保存到成功表两条记录
		channelMapper.deleteChannelConfig();
		String smsStatus = "TRUE";
		String smsType = "NOTICE";
		String channelName = "QiXinTong";
		SmsConfig smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(1);
		smsConfig.setWeight(1);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		channelName = "XinGe";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(2);
		smsConfig.setWeight(2);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);

		QXTMessageChannelImpl qxtChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel("QiXinTong");
		HttpService httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("00");
		qxtChannel.setHttpService(httpService);
		Map<String, MessageChannel> channelMap = messageChannelContext.getChannelMap();
		channelMap.put("QiXinTong", qxtChannel);
		messageChannelContext.setChannelMap(channelMap);

		SmsEntity smsEntity = new SmsEntity();
		Date time = new Date();
		smsEntity.setAppCode("testAppCode1");
		String content = "失败短信测试"+time;
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		String to = "18601344991,15132325805";
		smsEntity.setTo(to);
		smsEntity.setToken("testToken");
		smsEntity.setType(SmsSendType.NOTICE);
		smsEntity.setChannelNo("XinGe");
		smsEntity.setMemo("号码异常");
		smsEntity.setFailCount(1);
		smsEntity.setTime(time);
		channelMapper.insertFailRecord(smsEntity);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 10);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();

		List<SmsEntity> failSmsEntitys = channelMapper.listFailRecords(to, content, startTime, endTime);
		Assert.assertNotEquals(null, failSmsEntitys.get(0).getId());
		smsEntity.setId(failSmsEntitys.get(0).getId());

		messageHandler.failHandle(smsEntity);

		List<SmsEntity> smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("testAppCode1", smsEntitys.get(0).getAppCode());
		Assert.assertNotEquals(null, smsEntitys.get(0).getMsgid());
		smsEntitys = channelMapper.listFailRecords(to, content, startTime, endTime);
		Assert.assertEquals(0, smsEntitys.size());

		//分支测试二：营销类，失败通道为信鸽，目前两个通道，创蓝和信鸽，再次发送用创蓝依旧失败，更新失败表失败次数
		channelMapper.deleteChannelConfig();
		smsStatus = "TRUE";
		smsType = "NOTICE";
		channelName = "QiXinTong";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(1);
		smsConfig.setWeight(1);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		channelName = "XinGe";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(2);
		smsConfig.setWeight(2);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);

		qxtChannel = (QXTMessageChannelImpl)messageChannelContext.getChannel("QiXinTong");
		httpService = Mockito.mock(HttpService.class);
		when(httpService.get(anyMapOf(String.class, String.class))).thenReturn("11");
		qxtChannel.setHttpService(httpService);
		channelMap = messageChannelContext.getChannelMap();
		channelMap.put("QiXinTong", qxtChannel);
		messageChannelContext.setChannelMap(channelMap);

		smsEntity = new SmsEntity();
		time = new Date();
		smsEntity.setAppCode("testAppCode3");
		content  = "呵呵3"+time;
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		to = "18601344991,15132325805,877878888";
		smsEntity.setTo(to);
		smsEntity.setToken("testToken");
		smsEntity.setType(SmsSendType.NOTICE);
		smsEntity.setChannelNo("XinGe");
		smsEntity.setMemo("号码异常");
		smsEntity.setFailCount(1);
		smsEntity.setTime(time);
		channelMapper.insertFailRecord(smsEntity);

		failSmsEntitys = channelMapper.listFailRecords(to, content, startTime, endTime);
		Assert.assertNotEquals(null, failSmsEntitys.get(0).getId());
		smsEntity.setId(failSmsEntitys.get(0).getId());

		messageHandler.failHandle(smsEntity);

		calendar = Calendar.getInstance();
		time = calendar.getTime();
		calendar.add(Calendar.MINUTE, 10);
		endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		startTime = calendar.getTime();
		smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
		Assert.assertEquals(0, smsEntitys.size());
		smsEntitys = channelMapper.listFailRecords(to, content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals(2, smsEntitys.get(0).getFailCount());
		Assert.assertEquals("testAppCode3", smsEntitys.get(0).getAppCode());
	}
}


