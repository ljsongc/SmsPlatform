package com.pay.sms.console.service.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pay.sms.console.base.test.BaseTest;
import com.pay.sms.console.service.SmsConsoleService;

public class SmsConsoleServiceTest extends BaseTest{
	@Autowired
	private SmsConsoleService service;
	
	@Test
	public void Test(){
		String startTime = "2017-09-01";
		String endTime = "2017-09-20";
		List<String> channels = new ArrayList<String>();
		channels.add("XinGe");
		channels.add("Ali");
		String jsonStr = service.getSmsStatisticsChartData(startTime, endTime, channels);
		System.out.println(jsonStr);
	}
}
