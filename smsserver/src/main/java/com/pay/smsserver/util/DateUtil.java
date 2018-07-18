package com.pay.smsserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String dateToStr(Date date, String pattern){
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Date strToDate(String dateStr, String pattern){
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}
