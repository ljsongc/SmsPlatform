package com.pay.sms.console.mapper.test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.sms.console.base.test.BaseTest;
import com.pay.sms.console.bean.SmsRateStatics;
import com.pay.sms.console.bean.SmsStatisticsChartData;
import com.pay.sms.console.mapper.SmsConsoleMapper;

public class SmsConsoleMapperTest extends BaseTest{
	
	@Autowired
	private SmsConsoleMapper mapper;
	
	private final static Logger logger = LoggerFactory.getLogger(SmsConsoleMapperTest.class);
	
	@Test
	public void mapkeyTest() {
		try {
			logger.info("start================");
			String startTime = "2017-09-01";
			String endTime = "2017-09-20";
			String channels = "'XinGe','ChuangLan','Ali'";
			Map<String, SmsStatisticsChartData> beans = mapper.getNoticeSuccessCount(startTime, endTime, channels);
			Set<Map.Entry<String, SmsStatisticsChartData>> entry = beans.entrySet();
			Iterator<Entry<String, SmsStatisticsChartData>> it = entry.iterator();
			for	(; it.hasNext();) {
				Map.Entry<String, SmsStatisticsChartData> map = it.next();
				System.out.println(map.getKey());
				System.out.println(map.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
