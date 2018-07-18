package com.pay.sms.console.base.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

public class SimpleTest {
	@Test
	public void getSomeDateStr() {
		String startTime = "2017-09-05";
		String endTime = "2017-09-07";
		List<String> dates = new ArrayList<String>();
		try {
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date endDate = sdf.parse(endTime);
			start.setTime(sdf.parse(startTime));
			end.setTime(endDate);
			if (endDate.after(start.getTime())) {
				while (endDate.after(start.getTime())) {
					dates.add(sdf.format(start.getTime()));
					start.add(Calendar.DAY_OF_MONTH, 1);
				}
				dates.add(sdf.format(endDate));
			} else if (startTime.equals(endTime)) {
				dates.add(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(dates);
	}
	
	@Test
	public void getSuccessRateTest() {
		long successNumber = 1;
		long sum = 3;
		float successRate = successNumber * 100 / (float)sum;
		successRate = (float) Math.floor(successRate * 100) / 100;
		System.out.println(successRate);
		
		float successRate1 = (float) Math.floor(successNumber * 10000 / (float) sum) / 100;
		System.out.println(successRate1);
	}
	
	@Test
	public void jsonTest() {
		List<Bean> list = new ArrayList<Bean>();
		Bean b1 = new Bean();
		b1.setDate("2017-09-05");
		b1.setCount(123);
		Bean b2 = new Bean();
		b2.setDate("2017-09-06");
		b2.setCount(111);
		list.add(b1);
		list.add(b2);
		String json = JSONObject.toJSONString(list);
		System.out.println(json);
	}
}

class Bean {
	private String date;
	private int count;
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "Bean [date=" + date + ", count=" + count + "]";
	}
	
	
}
