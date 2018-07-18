package com.pay.smsserver.channel.impl;

import java.util.Calendar;

public class QXTSaleMessageChannelImpl extends QXTMessageChannelImpl{

	@Override
	public String getChannelName() {
		return "QiXinTongSale";
	}

	@Override
	public int getSubmitNumber() {
		/**
		 * 白天单次发送不超过50条,夜间不超过20条
		 * 夜间管理从22：30点开始，早7：30点结束，在此期间执行以下策略： 
		 * 1、停用文本、excel群发、通讯录群发； 
		 * 2、接口提交只允许一次提交20个手机号；
		 */
		int submitNumber = 20;
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		if(hour == 7 && minute > 30 || hour > 7 && hour < 22 || hour == 22 && minute < 30){
			submitNumber = 50;
		}
		return submitNumber;
	}

}
