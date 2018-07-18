package com.pay.smsserver.astrotrain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.pay.astrotrain.client.ATMessage;
import com.pay.astrotrain.client.ATProducer;
import com.pay.astrotrain.client.message.StringMessage;
import com.pay.astrotrain.client.producer.DefaultATProducer;
import com.pay.smsserver.util.DateUtil;

public class CamelAstrotrainProducer {

	private DefaultATProducer atProducer;
	
	private String topic;
	
	private String appId;
	
	private String groupName;
	
	private String instanceName;
	
	private String namesrvAddr;
	
	private ATProducer producer;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void start() {
		this.logger.info("camel astrotrain producer has start");
		this.atProducer = new DefaultATProducer();
		this.atProducer.setAppId(appId);
		this.atProducer.setGroupName(groupName);
		this.atProducer.setInstanceName(instanceName);
		this.atProducer.setNamesrvAddr(namesrvAddr);
		try {
			this.atProducer.start();
			this.producer = this.atProducer.createProducer(topic);
		} catch (Exception e) {
			this.logger.error("camel astrotrain producer start error {}", e);
		}
	}
	
	
	public void shutdown() {
		this.logger.info("astrotrain producer has shutdown");
		if(this.atProducer != null){
			this.atProducer.shutdown();
		}
	}
	
	
	
	public void sendStringMessage(String uniqueKey, Date receiveTime){
		JSONObject json = new JSONObject();
		json.put("uniqueKey", uniqueKey);
		String dateToStr = DateUtil.dateToStr(receiveTime, "yyyy-MM-dd HH:mm:ss");
		json.put("receiveTime",dateToStr);
		try {
			
			StringMessage message = new StringMessage(json.toString());
			this.logger.info("astrotrain producer send {}", message);
			message.setProperty(ATMessage.MSG_KEYS, UUID.randomUUID().toString());
			this.producer.send(message);
		} catch (Exception e) {
			this.logger.error("astrotrain producer sendStringMessage msg={}", json.toString(), e);
		}
	}
	

	public void setTopic(String topic) {
		this.topic = topic;
	}


	public void setAppId(String appId) {
		this.appId = appId;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}


	public void setNamesrvAddr(String namesrvAddr) {
		this.namesrvAddr = namesrvAddr;
	}

	
}
