package com.pay.smsserver.mapper;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsRateStatics;
import com.pay.smsserver.bean.SmsStatusEntity;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.util.DateUtil;

public class ChannelMapperTest extends SpringBaseTest {

	@Autowired
	private ChannelMapper channelMapper;
	
	@Test
	@Transactional
	public void testInsertSmsRateStatics(){
		SmsRateStatics rate = new SmsRateStatics();
		rate.setAppCode("test");
		rate.setChannelCode("DaHan");
		rate.setTypeCode("Sale");
		rate.setSendSuccessTotal(8);
		rate.setSendSuccessRate(80.00);
		rate.setSendFailTotal(2);
		rate.setReceiveFailTotal(2);
		rate.setReceiveSuccessRate(6);
		rate.setTime(new Date());
		channelMapper.insertRateStatics(rate);
	}
	
	@Test
	public void testUpdateSmsRateStatics(){
		SmsRateStatics rate = new SmsRateStatics();
		rate.setId(539);
		rate.setAppCode("test");
		rate.setChannelCode("DaHan");
		rate.setTypeCode("Sale");
		rate.setSendSuccessTotal(8);
		rate.setSendFailTotal(2);
		rate.setReceiveFailTotal(5);
		rate.setReceiveSuccessRate(6);
		rate.setFeeTotal(6);
		double successRate = (double) 1 * 100/ 3;
		//发送成功率
		rate.setSendSuccessRate(successRate);
		channelMapper.updateRateStatics(rate);
	}
	
	@Test
	public void testFindSuccess() throws ParseException{
		Date parseDate = DateUtils.parseDate("2018-05-20", new String[]{"yyyy-MM-dd"});
		List<SmsRateStatics> findSuccessTotal = channelMapper.findNotiveReceiveSuccessTotal(parseDate, new Date());
		for (SmsRateStatics smsRateStatics : findSuccessTotal) {
			System.out.println(smsRateStatics);
		}
	}
	

	@Test
	public void testSelectSmsRateStatics() throws ParseException{
		Date parseDate = DateUtils.parseDate("2018-05-28", new String[]{"yyyy-MM-dd"});
		SmsRateStatics rateStatics = channelMapper.findSmsRateStaticsByCodeAndTime("testAppCode", null, "DaHan", parseDate);
		System.out.println(rateStatics);
		rateStatics = channelMapper.findSmsRateStaticsByCodeAndTime("testAppCode", "Sale", "DaHan", parseDate);
		System.out.println(rateStatics);
		Date parseDate2 = DateUtils.parseDate("2018-06-03", new String[]{"yyyy-MM-dd"});
		rateStatics = channelMapper.findSmsRateStaticsByCodeAndTime("test", "Sale", "DaHan", parseDate2);
		System.out.println(rateStatics);
	}
	
	@Test
	@Transactional
	public void testListChannelsByStatusType(){
		String smsStatus = "TRUE";
		String smsType = "YZM";
		String channelName = "testChannel";
		SmsConfig smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(0);
		smsConfig.setWeight(0);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		List<SmsConfig> smsConfigs = channelMapper.listChannelsByStatusType(smsStatus, smsType);
		Assert.assertEquals(1, smsConfigs.size());
		Assert.assertEquals(channelName, smsConfigs.get(0).getSmsChannel());
	}
	
	@Test
	@Transactional
	public void testInsertNoticeSuccessRecord(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试成功短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setMsgid(UUID.randomUUID().toString());
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.NOTICE);
		channelMapper.insertNoticeSuccessRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listNoticeSuccessRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
	}
	
	@Test
	@Transactional
	public void testInsertSaleSuccessRecord(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试成功短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setMsgid(UUID.randomUUID().toString());
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.SALE);
		channelMapper.insertSaleSuccessRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listSaleSuccessRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
	}
	
	@Test
	@Transactional//采用事务环境，默认rollback为true，也就是test方法执行后，所有对数据库的操作都会自动回滚，也就是不对数据库进行侵入
	public void testInsertFailRecord(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试失败短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.SALE);
		smsEntity.setMemo("手机号是黑名单");
		smsEntity.setFailCount(2);
		channelMapper.insertFailRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listFailRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
	}
	
	@Test
	@Transactional
	public void testDeleteFailRecord(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试失败短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.SALE);
		smsEntity.setMemo("手机号是黑名单");
		smsEntity.setFailCount(1);
		channelMapper.insertFailRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listFailRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
		Assert.assertEquals(1, smsEntitys.get(0).getFailCount());
		
		smsEntity = smsEntitys.get(0);
		channelMapper.deleteFailRecord(smsEntity);
		smsEntitys = channelMapper.listFailRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(0, smsEntitys.size());
	}
	
	@Test
	@Transactional
	public void testUpdateFailInfos(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试失败短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.SALE);
		smsEntity.setMemo("手机号是黑名单");
		smsEntity.setFailCount(1);
		channelMapper.insertFailRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listFailRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
		Assert.assertEquals(1, smsEntitys.get(0).getFailCount());
		Assert.assertEquals("TestChannel", smsEntitys.get(0).getChannelNo());
		
		smsEntity = smsEntitys.get(0);
		smsEntity.setFailCount(2);
		smsEntity.setMemo("手机号非法");
		smsEntity.setChannelNo("TestChannel2");
		channelMapper.updateFailInfos(smsEntity);
		smsEntitys = channelMapper.listFailRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals(2, smsEntitys.get(0).getFailCount());
		Assert.assertEquals("TestChannel2", smsEntitys.get(0).getChannelNo());
		Assert.assertEquals("手机号非法", smsEntitys.get(0).getMemo());
	}
	
	@Test
	@Transactional
	public void testInsertFailHistoryRecord(){
		Calendar calendar = Calendar.getInstance();
		Date time = calendar.getTime();
		String content = "这是一条测试失败短信" + time;
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("smsTest");
		smsEntity.setChannelNo("TestChannel");
		smsEntity.setContent(content);
		smsEntity.setIp("10.10.116.35");
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		smsEntity.setTime(new Date());
		smsEntity.setPreTime(new Date());
		smsEntity.setTo("17601027017");
		smsEntity.setToken("smsTestToken");
		smsEntity.setType(SmsSendType.SALE);
		smsEntity.setMemo("三次都失败了");
		channelMapper.insertFailHistoryRecord(smsEntity);
		Date endTime = calendar.getTime();
		calendar.add(Calendar.HOUR, -1);
		Date startTime = calendar.getTime();
		List<SmsEntity> smsEntitys = channelMapper.listFailHistoryRecords("17601027017", content, startTime, endTime);
		Assert.assertEquals(1, smsEntitys.size());
		Assert.assertEquals("smsTest", smsEntitys.get(0).getAppCode());
		Assert.assertEquals("三次都失败了", smsEntitys.get(0).getMemo());
	}
	
	@Test
	@Transactional
	public void testInsertStatisticsCount(){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, 1);
		String beginTime = DateUtil.dateToStr(cal.getTime(), "yyyyMMdd");
		cal.add(Calendar.DAY_OF_MONTH, 1);
		String endTime = DateUtil.dateToStr(cal.getTime(), "yyyyMMdd");
		String channelName = "QXT";
		Long count = 20l;
		channelMapper.insertStatisticsCount(beginTime, endTime, channelName, count);
		Long dbCount = channelMapper.countNumberStatistics(beginTime, endTime, channelName);
		Assert.assertEquals(count, dbCount);
	}
	
	@Test
	@Transactional
	public void testInsertUplinkMessage(){
		Date time = new Date();
		UpLinkEntity upLinkEntity = new UpLinkEntity();
		String content = "这是一条上行短信"+time;
		String phone = "17601027017";
		String type = "QXT";
		upLinkEntity.setContent(content);
		upLinkEntity.setPhone(phone);
		upLinkEntity.setType(type);
		upLinkEntity.setReceiveTime(time);
		upLinkEntity.setCreateTime(time);
		channelMapper.insertUplinkMessage(upLinkEntity);
		List<UpLinkEntity> upLinkEntitys = channelMapper.listUplinkMessages(phone, content, time);
		Assert.assertEquals(1, upLinkEntitys.size());
		Assert.assertEquals("17601027017", upLinkEntitys.get(0).getPhone());
	}
	
	@Test
	@Transactional
	public void testInsertMessageStatus(){
		Date time = new Date();
		SmsStatusEntity smsStatusEntity = new SmsStatusEntity();
		String content = "这是一条短信状态"+time;
		String phone = "17601027017";
		String msgId = "0987654321";
		smsStatusEntity.setChannelNo("TETTC");
		smsStatusEntity.setCreateDate(time);
		smsStatusEntity.setPhone(phone);
		smsStatusEntity.setDescription(content);
		smsStatusEntity.setErrmsg("00");
		smsStatusEntity.setMsgid(msgId);
		smsStatusEntity.setReceiveDate(time);
		channelMapper.insertMessageStatus(smsStatusEntity);
		List<SmsStatusEntity> smsStatusEntitys = channelMapper.listMessageStatuses(phone, msgId, time);
		Assert.assertEquals(1, smsStatusEntitys.size());
		Assert.assertEquals("17601027017", smsStatusEntitys.get(0).getPhone());
	}
	
	@Test
	@Transactional
	public void testInsertAppCodeExpandCode(){
		String appCode = "appCodetest";
		String expandCode = "0001";
		channelMapper.insertAppCodeExpandCode(appCode, expandCode);
		String dbExpandCode = channelMapper.getExpandCodeByAppCode(appCode);
		Assert.assertEquals(expandCode, dbExpandCode);
	}
	
	@Test
	@Transactional
	public void testCountSuccessMessages(){
		for(int i = 0; i < 20; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试成功短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel1");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertNoticeSuccessRecord(smsEntity);
		}
		for(int i = 0; i < 30; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试成功短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel2");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertNoticeSuccessRecord(smsEntity);
		}
		List<Map<String, String>> successMessages = channelMapper.countSuccessMessages(SmsSendType.NOTICE.name(), 1);
		Assert.assertEquals(2, successMessages.size());
		for(Map<String, String> map : successMessages){
			if("TestChannel1".equals(map.get("channelName"))){
				Assert.assertEquals(20l, map.get("count"));
			}else if("TestChannel2".equals(map.get("channelName"))){
				Assert.assertEquals(30l, map.get("count"));
			}
		}
	}
	
	@Test
	@Transactional
	public void testCountFailMessages(){
		for(int i = 0; i < 12; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试失败短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel3");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertFailRecord(smsEntity);
		}
		for(int i = 0; i < 23; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试失败短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel4");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertFailRecord(smsEntity);
		}
		List<Map<String, String>> failMessages = channelMapper.countFailMessages(SmsSendType.NOTICE.name(), 1);
		Assert.assertEquals(2, failMessages.size());
		for(Map<String, String> map : failMessages){
			if("TestChannel3".equals(map.get("channelName"))){
				Assert.assertEquals(12l, map.get("count"));
			}else if("TestChannel4".equals(map.get("channelName"))){
				Assert.assertEquals(23l, map.get("count"));
			}
		}
	}
	
	@Test
	@Transactional
	public void testCountFailHistoryMessages(){
		for(int i = 0; i < 12; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试失败短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel5");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertFailHistoryRecord(smsEntity);
		}
		for(int i = 0; i < 23; i++){
			Calendar calendar = Calendar.getInstance();
			Date time = calendar.getTime();
			String content = "这是一条测试失败短信" + time;
			SmsEntity smsEntity = new SmsEntity();
			smsEntity.setAppCode("smsTest");
			smsEntity.setChannelNo("TestChannel6");
			smsEntity.setContent(content);
			smsEntity.setIp("10.10.116.35");
			smsEntity.setLevel(SmsSendLevel.NORMAL);
			smsEntity.setMsgid(UUID.randomUUID().toString());
			smsEntity.setTime(new Date());
			smsEntity.setPreTime(new Date());
			smsEntity.setTo("17601027017");
			smsEntity.setToken("smsTestToken");
			smsEntity.setType(SmsSendType.NOTICE);
			channelMapper.insertFailHistoryRecord(smsEntity);
		}
		List<Map<String, String>> failMessages = channelMapper.countFailHistoryMessages(SmsSendType.NOTICE.name(), 1);
		Assert.assertEquals(2, failMessages.size());
		for(Map<String, String> map : failMessages){
			if("TestChannel5".equals(map.get("channelName"))){
				Assert.assertEquals(12l, map.get("count"));
			}else if("TestChannel6".equals(map.get("channelName"))){
				Assert.assertEquals(23l, map.get("count"));
			}
		}
	}
	
	@Test
	@Transactional
	public void testListMasterSmsConfigs(){
		String smsStatus = "TRUE";
		String smsType = "YZM";
		String channelName = "testChannel";
		SmsConfig smsConfig = new SmsConfig();
		smsConfig.setCurrentWeight(0);
		smsConfig.setWeight(0);
		smsConfig.setSmsChannel(channelName);
		smsConfig.setSmsState(smsStatus);
		smsConfig.setSmsType(smsType);
		channelMapper.insertSmsConfig(smsConfig);
		List<SmsConfig> smsConfigs = channelMapper.listMasterSmsConfigs(smsStatus, smsType);
		Assert.assertEquals(1, smsConfigs.size());
		Assert.assertEquals(channelName, smsConfigs.get(0).getSmsChannel());
		channelMapper.updateChannelToSpare(channelName, smsStatus);
		smsConfigs = channelMapper.listMasterSmsConfigs(smsStatus, smsType);
		Assert.assertEquals(0, smsConfigs.size());
	}
}
