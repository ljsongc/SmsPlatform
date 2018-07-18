package com.pay.smsserver.enums;

public enum ChannelCode {


	XinGe("XinGe","XinGe"),		// 信鸽通道
	ChuangLan("ChuangLan","ChuangLan"),	//创蓝通道
	ChuangLanSale("ChuangLanSale","ChuangLanSale"),//创蓝营销通道
	XinGeSale("XinGeSale","XinGeSale"),//信鸽营销
	QiXinTongSale("QiXinTongSale","QiXinTongSale"),//企信通营销
	DaHan("DaHan","DaHan"),//大汉
	DaHanSale("DaHanSale","DaHanSale"),//大汉营销
  ;









	String key;
	String val;

	ChannelCode(String key,String val){
		this.key = key;
		this.val = val;
	}

	public String getCode(){
		return key;
	}

	public String getName(){
		return val;
	}
	@Override
	public String toString() {
		return key;
	}

}
