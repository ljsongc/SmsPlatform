package com.pay.smsserver.web;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.smsserver.channel.MessageChannel;

@Controller
@RequestMapping("qiXinTong")
public class QiXinTongController {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource(name = "qxtMessageChannelImpl")
	private MessageChannel qxtMessageChannelImpl;
	
	@RequestMapping("getMessage")
	public @ResponseBody String getMessage(String phone ,String msgContent,String spNumber,String linkid,String serviceup){
		log.info("getMessage 传值 : phone = {},msgContent={},spNumber={},linkid={},serviceup={}",
				phone, msgContent, spNumber, linkid, serviceup);
		JSONArray uplinkMessages = new JSONArray();
		JSONObject dataJson = new JSONObject();
		dataJson.put("msgContent", msgContent);
		dataJson.put("phone", phone);
		dataJson.put("spNumber", spNumber);
		uplinkMessages.put(dataJson);
		qxtMessageChannelImpl.pushedUplinkMessages(uplinkMessages);
		return "success";
	}
	
	@RequestMapping("getStatus")
	public @ResponseBody String getStatus(String PlatForm ,String FUnikey,String FOrgAddr,String FDestAddr,String FSubmitTime,
			String FFeeTerminal ,String FServiceUPID,String FReportCode,String FLinkID,String FAckStatus){
		log.info(
				"getStutas 传值 : PlatForm = {},FUnikey={},FOrgAddr={},FDestAddr={},FSubmitTime={},"
				+ "FFeeTerminal={},FServiceUPID={},FReportCode={},FLinkID={},FAckStatus={}",
				PlatForm, FUnikey, FOrgAddr, FDestAddr, FSubmitTime,FFeeTerminal,FServiceUPID,FReportCode,FLinkID,FAckStatus);
		JSONArray callbackStatuses = new JSONArray();
		JSONObject dataJson = new JSONObject();
		dataJson.put("FSubmitTime", FSubmitTime);
		dataJson.put("FDestAddr", FDestAddr);
		dataJson.put("FReportCode", FReportCode);
		dataJson.put("FAckStatus", FAckStatus);
		dataJson.put("FLinkID", FLinkID);
		callbackStatuses.put(dataJson);
		qxtMessageChannelImpl.pushedCallbackStatuses(callbackStatuses);
		return "success";
	}
}
