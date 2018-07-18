package com.pay.smsserver.service;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.channel.MessageChannelContext;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.enums.ChannelCode;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.util.DateUtil;

/**
 * 单元测试遵守BCDE原则
 * B：Border，边界值测试，包括循环、特殊取时间点数据顺序等。 
 * C：Correct，正确的输入，并得到预期结果。 ，正确的输入并得到预期结果。 
 * D：Design，与设计文档相结合，来编写单元测试。 ，与设计文档相结合来编写单元测试。 
 * E：Error，强制错误信息输入（如：非法数据、异常流程业务允许等），并得 到预期的结果。
 * @author chenchen.qi
 *
 */
public class ChannelServiceTest extends SpringBaseTest{

	@Autowired
	private ChannelService channelService;
	@Autowired
	private ChannelMapper channelMapper;
	@Autowired
	private RedisManager redisManager;
	@Autowired
	private MessageChannelContext messageChannelContext;

	@Test
	public void testCode() throws ParseException{
		Date beginTime = DateUtils.parseDate("2018-05-20", new String[]{"yyyy-MM-dd"});
		Date endTime = DateUtils.parseDate("2018-05-30", new String[]{"yyyy-MM-dd"});
		channelService.StatisticalSuccessRate(beginTime, endTime);
	}

	@Test
	@Transactional
	public void testGetExpandCodeByAppCode(){
		//分支测试1、从redis中根据appCode获取到expandCode
		String appCode = "testAppCode";
		String expandCode = "0001";
		redisManager.set(appCode, expandCode);
		redisManager.set(expandCode, appCode);
		String expandCodeFromService = channelService.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(expandCode, expandCodeFromService);
		redisManager.del(appCode);
		redisManager.del(expandCode);

		//分支测试2、redis中获取不到，从db中获取到，并更新到redis中
		appCode = "testAppCode2";
		expandCode = "0002";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		expandCodeFromService = channelService.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(expandCode, expandCodeFromService);
		String expandCodeRedis = redisManager.get(appCode);
		Assert.assertEquals(expandCode, expandCodeRedis);
		redisManager.del(appCode);
		redisManager.del(expandCode);

		//分支测试3、redis中获取不到，db中获取不到，根据appCode在SMS_TOKEN表id作为expandCode
		appCode = "CustomerFundBoss-timer";
		expandCode = "0017";
		expandCodeFromService = channelService.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(expandCode, expandCodeFromService);
		String expandCodeDB = channelMapper.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(expandCode, expandCodeDB);
		expandCodeRedis = redisManager.get(appCode);
		Assert.assertEquals(expandCode, expandCodeRedis);
		redisManager.del(appCode);
		redisManager.del(expandCode);

		//分支测试4、redis中获取不到，db中获取不到，根据appCode在SMS_TOKEN表找不到id
		appCode = "testAppCode3";
		expandCode = "0002";
		expandCodeFromService = channelService.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(null, expandCodeFromService);
	}

	@Test
	@Transactional
	public void testGetAppCodeByExpandCode(){
		//分支测试1、portNumber超过四位，redis和db里都没有
		String portNumber = "1069056380103461";
		String appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(null, appCodeFromService);

		//分支测试2、portNumber超过四位，redis有
		portNumber = "1069056380103461";
		String expandCode = portNumber.substring(portNumber.length() - 4, portNumber.length());
		String appCode = "testAppCode2";
		redisManager.set(expandCode, appCode);
		appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(appCode, appCodeFromService);
		redisManager.del(expandCode);

		//分支测试3、portNumber超过四位，redis没有，db里有
		appCode = "testAppCode3";
		portNumber = "1069056380103461";
		expandCode = portNumber.substring(portNumber.length() - 4, portNumber.length());
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(appCode, appCodeFromService);
		redisManager.del(expandCode);

		//分支测试4、portNumber小于四位，redis和db里都没有
		portNumber = "460";
		appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(null, appCodeFromService);

		//分支测试5、portNumber小于四位，redis有
		portNumber = "460";
		expandCode = "0" + portNumber;
		appCode = "testAppCode5";
		redisManager.set(expandCode, appCode);
		appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(appCode, appCodeFromService);
		redisManager.del(expandCode);

		//分支测试6、portNumber小于四位，redis没有，db里有
		appCode = "testAppCode6";
		portNumber = "460";
		expandCode = "0" + portNumber;
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		appCodeFromService = channelService.getAppCodeByExpandCode(portNumber);
		Assert.assertEquals(appCode, appCodeFromService);
		redisManager.del(expandCode);
	}

	@Test
	public void testFillData(){
		SmsBean smsBean = new SmsBean();
		String appCode = "testAppCode";
		smsBean.setAppCode(appCode);
		String content = "test...";
		smsBean.setContent(content);
		String ip = "10.10.116.35";
		smsBean.setIp(ip);
		Date time = new Date();
		smsBean.setTime(time);
		String to = "18909090808";
		smsBean.setTo(to);
		String token = "token";
		smsBean.setToken(token);
		SmsEntity smsEntity = channelService.fillData(smsBean);
		Assert.assertEquals(smsBean.getAppCode(), smsEntity.getAppCode());
		Assert.assertEquals(smsBean.getContent(), smsEntity.getContent());
		Assert.assertEquals(smsBean.getIp(), smsEntity.getIp());
		Assert.assertEquals(SmsSendLevel.NORMAL, smsEntity.getLevel());
		Assert.assertEquals(smsBean.getTime(), smsEntity.getPreTime());
		Assert.assertEquals(smsBean.getTo(), smsEntity.getTo());
		Assert.assertEquals(smsBean.getToken(), smsEntity.getToken());
		Assert.assertEquals(SmsSendType.NOTICE, smsEntity.getType());
	}

	@Test
	@Transactional
	public void testFilterChannel(){
		//分支测试一：status为TRUE，type为NOTICE，failChannelName为null，两个通道(weight=currentWeight)下选权值最小的
		channelMapper.deleteChannelConfig();
		MessageChannel messageChannel = channelService.filterChannel("TRUE", "NOTICE", null, null);
		String smsStatus = "TEST";
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
		messageChannel = channelService.filterChannel(smsStatus, smsType, null, null);
		Assert.assertEquals("XinGe", messageChannel.getChannelName());

		//分支测试二：status为TRUE，type为NOTICE，failChannelName为null，两个通道下(一个weight=currentWeight，一个不等)选权值最小的
		channelMapper.deleteChannelConfig();
		messageChannel = channelService.filterChannel("TRUE", "NOTICE", null, null);
		Assert.assertEquals(null, messageChannel);

		smsStatus = "TEST";
		smsType = "NOTICE";
		channelName = "XinGe";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(1);
		smsConfig.setWeight(2);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		channelName = "ChuangLan";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(3);
		smsConfig.setWeight(3);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		messageChannel = channelService.filterChannel(smsStatus, smsType, null, null);
		Assert.assertEquals("ChuangLan", messageChannel.getChannelName());

		//分支测试三：status为TRUE，type为NOTICE，failChannelName为null，两个通道(weight=currentWeight)下选权值最小的
		channelMapper.deleteChannelConfig();
		messageChannel = channelService.filterChannel("TRUE", "NOTICE", null,null);
		Assert.assertEquals(null, messageChannel);

		smsStatus = "TEST";
		smsType = "NOTICE";
		channelName = "XinGe";
		smsConfig = new SmsConfig();
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

		List<MessageChannel> messageChannels = new ArrayList<MessageChannel>();
		for(int i=0; i<10; i++){
			messageChannel = channelService.filterChannel(smsStatus, smsType, null, null);
			messageChannels.add(messageChannel);
		}
		Assert.assertEquals("XinGe", messageChannels.get(0).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(1).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(2).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(3).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(4).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(5).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(6).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(7).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(8).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(9).getChannelName());

		//分支测试四：status为TRUE，type为SALE，failChannelName为null，两个通道(weight=currentWeight)下选权值最小的
		channelMapper.deleteChannelConfig();
		messageChannel = channelService.filterChannel("TRUE", "NOTICE", null, null);
		Assert.assertEquals(null, messageChannel);

		smsStatus = "TEST";
		smsType = "SALE";
		channelName = "XinGe";
		smsConfig = new SmsConfig();
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

		messageChannels = new ArrayList<MessageChannel>();
		for(int i=0; i<10; i++){
			messageChannel = channelService.filterChannel(smsStatus, smsType, null, null);
			messageChannels.add(messageChannel);
		}
		Assert.assertEquals("XinGe", messageChannels.get(0).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(1).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(2).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(3).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(4).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(5).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(6).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(7).getChannelName());
		Assert.assertEquals("XinGe", messageChannels.get(8).getChannelName());
		Assert.assertEquals("ChuangLan", messageChannels.get(9).getChannelName());

		//分支测试四：status为TRUE，type为NOTICE，failChannelName为ChuangLan，多个通道(weight=currentWeight)下排除失败的随机选一个
		channelMapper.deleteChannelConfig();
		messageChannel = channelService.filterChannel("TRUE", "NOTICE", null, null);
		Assert.assertEquals(null, messageChannel);

		smsStatus = "TEST";
		smsType = "SALE";
		channelName = "XinGe";
		smsConfig = new SmsConfig();
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
		channelName = "QiXinTong";
		smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(3);
		smsConfig.setWeight(3);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);

		for(int i=0; i<10; i++){
			messageChannel = channelService.filterChannel(smsStatus, smsType, "ChuangLan", null);
		}
	}

	@Test
	@Transactional
	public void testHandleMessageResponses(){

		String channelName = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;

		Calendar calendar = Calendar.getInstance();
		Date startTime = calendar.getTime();
		String startTimeStr = DateUtil.dateToStr(startTime, "yyyyMMdd");
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date endTime = calendar.getTime();
		String endTimeStr = DateUtil.dateToStr(endTime, "yyyyMMdd");
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		String beforeTimeStr = DateUtil.dateToStr(calendar.getTime(), "yyyyMMdd");

		SmsEntity smsEntity = null;
		try {

			//分支测试1、创蓝通知通道，通知类消息结果为成功保存到通知成功表，群发两人，长短信总长度为142，进行计数，前一天统计条数为120。

			channelName = "ChuangLan";
			appCode = "testAppCode";
			content = "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.NOTICE;
			token = "token";

			smsEntity = new SmsEntity();
			smsEntity.setAppCode(appCode);
			smsEntity.setChannelNo(channelName);
			smsEntity.setContent(content);
			smsEntity.setIp(ip);
			smsEntity.setLevel(level);
			smsEntity.setPreTime(new Date());
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);

			MessageChannel messageChannel = messageChannelContext.getChannel(channelName);
			//准备响应结果
			List<MessageResponse> messasgeResponses = new ArrayList<MessageResponse>();
			MessageResponse messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("1234567890");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);
			//准备昨天的计数数据
			redisManager.set(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + beforeTimeStr, String.valueOf(120l));
			channelMapper.insertStatisticsCount(beforeTimeStr, startTimeStr, channelName, 4l);
			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			//验证成功表是否有刚插入的数据
			List<SmsEntity> smsEntitys = null;
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("1234567890", smsEntitys.get(0).getMsgid());
			//验证redis和db中的计数值
			Long count = 6l;
			Long countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			String countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(count), countStr);
			//验证redis中前一天计数结果是否更新到db中
			Long beforeCount = 120l;
			countDB = channelMapper.countNumberStatistics(beforeTimeStr, startTimeStr, channelName);
			Assert.assertEquals(beforeCount, countDB);


			//分支测试2、创蓝营销通道，营销类消息结果为成功保存到营销成功表，群发两人，长短信总长度为142，进行计数，前一天统计条数为2000。

			channelName = "ChuangLanSale";
			appCode = "testAppCode";
			content = "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991,1876765432";
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
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("0987654321");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);
			//准备昨天的计数数据
			redisManager.set(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + beforeTimeStr, String.valueOf(2000l));
			channelMapper.insertStatisticsCount(beforeTimeStr, startTimeStr, channelName, 0l);
			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("0987654321", smsEntitys.get(0).getMsgid());

			//验证redis和db中的计数值
			count = 9l;
			countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(count), countStr);
			//验证redis中前一天计数结果是否更新到db中
			beforeCount = 2000l;
			countDB = channelMapper.countNumberStatistics(beforeTimeStr, startTimeStr, channelName);
			Assert.assertEquals(beforeCount, countDB);


			//分支测试3、信鸽通知通道，通知类消息结果为成功保存到通知成功表，群发两人，长短信总长度为142，进行计数，本次非当天第一次计数，已经计数了2000，不去更新数据库。

			channelName = "XinGe";
			appCode = "testAppCode";
			content = "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991,1876765432,1876545432";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.NOTICE;
			token = "token";

			smsEntity = new SmsEntity();
			smsEntity.setAppCode(appCode);
			smsEntity.setChannelNo(channelName);
			smsEntity.setContent(content);
			smsEntity.setIp(ip);
			smsEntity.setLevel(level);
			smsEntity.setPreTime(new Date());
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("1111111");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);
			//准备今天的计数数据
			redisManager.set(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr, String.valueOf(2000l));
			channelMapper.insertStatisticsCount(startTimeStr, endTimeStr, channelName, 2000l);
			//调用方法
			channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("1111111", smsEntitys.get(0).getMsgid());

			//验证redis和db中的计数值
			count = 2000l;
			countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(2012l), countStr);

			//分支测试4、信鸽营销通道，营销类消息结果为成功保存到营销成功表，群发两人，长短信总长度为142，进行计数，本次为当天第一次计数，前一天redis里计数值找不到。

			channelName = "XinGeSale";
			appCode = "testAppCode";
			content = "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991,1876765432,18765434321,1909878654";
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
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("2222222");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);
			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("2222222", smsEntitys.get(0).getMsgid());

			//验证redis和db中的计数值
			count = 15l;
			countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(count), countStr);
			//验证redis中前一天计数结果是否更新到db中
			countDB = channelMapper.countNumberStatistics(beforeTimeStr, startTimeStr, channelName);
			Assert.assertEquals(null, countDB);

			//分支测试5、企信通通知通道，通知类消息结果为失败，保存失败表，不去统计。

			channelName = "QiXinTong";
			appCode = "testAppCode";
			content = "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991,1876765432,1876545432，18766765786,16535425262";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.NOTICE;
			token = "token";

			smsEntity = new SmsEntity();
			smsEntity.setAppCode(appCode);
			smsEntity.setChannelNo(channelName);
			smsEntity.setContent(content);
			smsEntity.setIp(ip);
			smsEntity.setLevel(level);
			smsEntity.setPreTime(new Date());
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("333333");
			messasgeResponse.setResult(1);
			messasgeResponses.add(messasgeResponse);
			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.handleMessageResponses(smsEntity, messageChannel, messasgeResponses);
			//验证失败表是否有刚插入的数据
			smsEntitys = channelMapper.listFailRecords(to, content, startTime, endTime);
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals(null, smsEntitys.get(0).getMsgid());

			//验证redis和db中的计数值
			countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(null, countDB);
			countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(null, countStr);

		} finally{
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLan" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLan" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLanSale" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLanSale" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGe" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGe" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGeSale" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGeSale" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "QiXinTong" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "QiXinTong" + beforeTimeStr);
		}
	}

	@Test
	@Transactional
	public void testFailHandleMessageResponses(){
		String channelName = null;
		String appCode = null;
		String content = null;
		String ip = null;
		String to = null;
		SmsSendLevel level = null;
		SmsSendType type = null;
		String token = null;

		Calendar calendar = Calendar.getInstance();
		Date startTime = calendar.getTime();
		String startTimeStr = DateUtil.dateToStr(startTime, "yyyyMMdd");
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Date endTime = calendar.getTime();
		String endTimeStr = DateUtil.dateToStr(endTime, "yyyyMMdd");
		calendar.add(Calendar.DAY_OF_MONTH, -2);
		String beforeTimeStr = DateUtil.dateToStr(calendar.getTime(), "yyyyMMdd");

		SmsEntity smsEntity = null;
		Date endTimeDB = null;
		Date startTimeDB = null;
		List<SmsEntity> failSmsEntitys = null;
		MessageChannel messageChannel = null;
		List<SmsEntity> smsEntitys = null;
		List<MessageResponse> messasgeResponses = null;
		MessageResponse messasgeResponse = null;
		try {

			//分支测试1、创蓝通知通道，失败的通知类短信发送成功，保存到通知成功表，从失败表中删除该记录，群发两人，长短信总长度为142，进行计数，前一天统计条数为120。
			channelName = "ChuangLan";
			appCode = "testAppCode";
			content = new Date() + "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.NOTICE;
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
			smsEntity.setFailCount(1);

			//准备一条失败记录
			calendar = Calendar.getInstance();
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			channelMapper.insertFailRecord(smsEntity);
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(1, failSmsEntitys.get(0).getFailCount());
			smsEntity.setId(failSmsEntitys.get(0).getId());

			messageChannel = messageChannelContext.getChannel(channelName);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("1234567890");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);

			//准备昨天的计数数据
			redisManager.set(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + beforeTimeStr, String.valueOf(120l));
			channelMapper.insertStatisticsCount(beforeTimeStr, startTimeStr, channelName, 4l);

			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.failHandleMessageResponses(smsEntity, messageChannel, messasgeResponses);

			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("1234567890", smsEntitys.get(0).getMsgid());

			//验证失败表记录是否删除
			calendar = Calendar.getInstance();
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(0, failSmsEntitys.size());

			//验证redis和db中的计数值
			Long count = 6l;
			Long countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			String countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(count), countStr);
			//验证redis中前一天计数结果是否更新到db中
			Long beforeCount = 120l;
			countDB = channelMapper.countNumberStatistics(beforeTimeStr, startTimeStr, channelName);
			Assert.assertEquals(beforeCount, countDB);


			//分支测试2、创蓝营销通道，失败的营销类短信发送成功，保存到营销成功表，从失败表中删除该记录，群发三人，长短信总长度为142，进行计数，前一天统计条数为120。
			channelName = "ChuangLanSale";
			appCode = "testAppCode";
			content = new Date() + "这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信这是一个测试短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991,19876765434";
			level = SmsSendLevel.ERROR;
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
			smsEntity.setFailCount(2);
			smsEntity.setMemo("手机号码格式错误");

			//准备一条失败记录
			calendar = Calendar.getInstance();
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			channelMapper.insertFailRecord(smsEntity);
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(2, failSmsEntitys.get(0).getFailCount());
			smsEntity.setId(failSmsEntitys.get(0).getId());

			messageChannel = messageChannelContext.getChannel(channelName);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("0");
			messasgeResponse.setMobile(to);
			messasgeResponse.setMsgId("7777777777");
			messasgeResponse.setResult(0);
			messasgeResponses.add(messasgeResponse);

			//准备昨天的计数数据
			redisManager.set(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + beforeTimeStr, String.valueOf(120l));
			channelMapper.insertStatisticsCount(beforeTimeStr, startTimeStr, channelName, 4l);

			//调用方法
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			channelService.failHandleMessageResponses(smsEntity, messageChannel, messasgeResponses);

			//验证成功表是否有刚插入的数据
			smsEntitys = null;
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(1, smsEntitys.size());
			Assert.assertEquals(appCode, smsEntitys.get(0).getAppCode());
			Assert.assertEquals("7777777777", smsEntitys.get(0).getMsgid());

			//验证失败表记录是否删除
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(0, failSmsEntitys.size());

			//验证redis和db中的计数值
			count = 9l;
			countDB = channelMapper.countNumberStatistics(startTimeStr, endTimeStr, channelName);
			Assert.assertEquals(count, countDB);
			countStr = redisManager.get(SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTimeStr);
			Assert.assertEquals(String.valueOf(count), countStr);
			//验证redis中前一天计数结果是否更新到db中
			beforeCount = 120l;
			countDB = channelMapper.countNumberStatistics(beforeTimeStr, startTimeStr, channelName);
			Assert.assertEquals(beforeCount, countDB);


			//分支测试3、信鸽通知通道，失败的通知类短信发送依旧失败，保存到通知失败表，更新失败表失败次数
			channelName = "XinGe";
			appCode = "testAppCode";
			content = new Date() + "这是一条失败短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.NOTICE;
			token = "token";

			Date time = new Date();
			smsEntity = new SmsEntity();
			smsEntity.setAppCode(appCode);
			smsEntity.setChannelNo(channelName);
			smsEntity.setContent(content);
			smsEntity.setIp(ip);
			smsEntity.setLevel(level);
			smsEntity.setPreTime(time);
			smsEntity.setTime(time);
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);
			smsEntity.setFailCount(1);

			//准备一条失败记录
			calendar = Calendar.getInstance();
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			channelMapper.insertFailRecord(smsEntity);
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(1, failSmsEntitys.get(0).getFailCount());
			smsEntity.setId(failSmsEntitys.get(0).getId());

			messageChannel = messageChannelContext.getChannel(channelName);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("提交失败");
			messasgeResponse.setMobile(to);
			messasgeResponse.setResult(1);
			messasgeResponses.add(messasgeResponse);

			//调用方法
			channelService.failHandleMessageResponses(smsEntity, messageChannel, messasgeResponses);

			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(0, smsEntitys.size());

			//验证失败表记录是否删除
			calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 1);
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(2, failSmsEntitys.get(0).getFailCount());
			Assert.assertEquals("提交失败", failSmsEntitys.get(0).getMemo());

			//分支测试4、信鸽营销通道，失败的营销类短信发送依旧失败，并且超过最大次数，从失败表里移除到失败历史表
			channelName = "XinGeSale";
			appCode = "testAppCode";
			content = new Date() + "这是一条失败历史短信";
			ip = "10.10.116.35";
			to = "17601027017,18601344991";
			level = SmsSendLevel.NORMAL;
			type = SmsSendType.SALE;
			token = "token";

			time = new Date();
			smsEntity = new SmsEntity();
			smsEntity.setAppCode(appCode);
			smsEntity.setChannelNo(channelName);
			smsEntity.setContent(content);
			smsEntity.setIp(ip);
			smsEntity.setLevel(level);
			smsEntity.setPreTime(time);
			smsEntity.setTime(time);
			smsEntity.setTo(to);
			smsEntity.setToken(token);
			smsEntity.setType(type);
			smsEntity.setFailCount(2);

			//准备一条失败记录
			calendar = Calendar.getInstance();
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			channelMapper.insertFailRecord(smsEntity);
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(2, failSmsEntitys.get(0).getFailCount());
			smsEntity.setId(failSmsEntitys.get(0).getId());

			messageChannel = messageChannelContext.getChannel(channelName);

			//准备响应结果
			messasgeResponses = new ArrayList<MessageResponse>();
			messasgeResponse = new MessageResponse();
			messasgeResponse.setMemo("提交失败了");
			messasgeResponse.setMobile(to);
			messasgeResponse.setResult(1);
			messasgeResponses.add(messasgeResponse);

			//调用方法
			channelService.failHandleMessageResponses(smsEntity, messageChannel, messasgeResponses);

			//验证成功表是否有刚插入的数据
			if(type == SmsSendType.NOTICE){
				smsEntitys = channelMapper.listNoticeSuccessRecords(to, content, startTime, endTime);
			}else{
				smsEntitys = channelMapper.listSaleSuccessRecords(to, content, startTime, endTime);
			}
			Assert.assertEquals(0, smsEntitys.size());

			//验证失败表记录是否删除
			calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, 1);
			endTimeDB = calendar.getTime();
			calendar.add(Calendar.HOUR, -1);
			startTimeDB = calendar.getTime();
			failSmsEntitys = channelMapper.listFailRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(0, failSmsEntitys.size());

			failSmsEntitys = channelMapper.listFailHistoryRecords(to, content, startTimeDB, endTimeDB);
			Assert.assertEquals(1, failSmsEntitys.size());
			Assert.assertEquals(content, failSmsEntitys.get(0).getContent());
			Assert.assertEquals(0, failSmsEntitys.get(0).getFailCount());
			Assert.assertEquals("提交失败了", failSmsEntitys.get(0).getMemo());
			Assert.assertEquals(channelName, failSmsEntitys.get(0).getChannelNo());

		} finally{
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLan" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLan" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLanSale" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "ChuangLanSale" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGe" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGe" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGeSale" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "XinGeSale" + beforeTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "QiXinTong" + startTimeStr);
			redisManager.del(SmsConstants.REDIS_SMS_COUNT_KEY + "QiXinTong" + beforeTimeStr);
		}
	}
}
