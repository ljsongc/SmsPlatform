package com.pay.smsserver.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * md5加密工具类
 * @author haoran.liu
 *
 */
public class MD5Util {
	public static String strToHash(String str) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	    byte[] inputByteArray = str.getBytes();
	    messageDigest.update(inputByteArray);
	    byte[] resultByteArray = messageDigest.digest();
	    return byteArrayToHex(resultByteArray);
	}
	
	private static String byteArrayToHex(byte[] byteArray) {
		char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		char[] resultCharArray = new char[byteArray.length * 2];
		int index = 0;
		for (byte b : byteArray) {
			resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
			resultCharArray[index++] = hexDigits[b & 0xf];
		}
		return new String(resultCharArray);
	}
}
