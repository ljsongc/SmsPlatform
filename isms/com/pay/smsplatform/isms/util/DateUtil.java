package com.pay.smsplatform.isms.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 	日期工具类
 * @author zhengzheng.ma
 *
 */
public class DateUtil {
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 	日期转换成指定格式字符串
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date) {
		return dateFormat.format(date);
	}
}
