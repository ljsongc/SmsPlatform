package com.sms;


public class Test {
	
	public static void main(String[] args) {
		for(int i=0;i<1;i++){
			new Thread(new Th()).start();
		}
		
	}

}

class Th implements Runnable{

	@Override
	public void run() {
//		while(true){
//			SmsUtilTest.sendContentSms("13520161736", "hello,11测试！");
//		}
		SmsUtilTest.sendContentSms("13520161736", "hello,11测试！");
	}
	
}

