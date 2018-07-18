package com.pay.smsserver.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
public class CommonUtil {
	private static int no = 0;
		public static String genRandomCode(int fix) {
			String chars = "0123456789876543210";
			char[] rands = new char[fix];
			int a = 0;
			for (int i = 0; i < 1000; i++) {
				int rand = (int) (Math.random() * 19);
				int temp = 0;
				for (int j = 0; j < a; j++) {
					if (String.valueOf(rands[j]).endsWith(String.valueOf(chars.charAt(rand)))) {
						temp = 1;
					}
				}
				if (temp == 1) {
					continue;
				} else {
					rands[a] = chars.charAt(rand);
					a++;
				}
				if (a >= fix) {
					break;
				}
			}
			return new String(rands);
		}

}
