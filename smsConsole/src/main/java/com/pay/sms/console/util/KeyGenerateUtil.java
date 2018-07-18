package com.pay.sms.console.util;

import com.pay.des.util.DesUtil;

public class KeyGenerateUtil {

	public static String generateKey(String appCode){
		return DesUtil.desEncrypt(appCode);
	}
}
