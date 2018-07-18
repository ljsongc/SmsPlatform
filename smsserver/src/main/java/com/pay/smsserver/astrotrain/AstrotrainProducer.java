package com.pay.smsserver.astrotrain;

import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.astrotrain.client.ATMessage;
import com.pay.astrotrain.client.ATProducer;
import com.pay.astrotrain.client.message.ObjectMessage;
import com.pay.astrotrain.client.message.StringMessage;
import com.pay.astrotrain.client.producer.DefaultATProducer;

public class AstrotrainProducer {

	private DefaultATProducer atProducer;
	
	private String topic;
	
	private String appId;
	
	private String groupName;
	
	private String instanceName;
	
	private String namesrvAddr;
	
	private ATProducer producer;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void start() {
		this.logger.info("astrotrain producer has start");
		this.atProducer = new DefaultATProducer();
		this.atProducer.setAppId(appId);
		this.atProducer.setGroupName(groupName);
		this.atProducer.setInstanceName(instanceName);
		this.atProducer.setNamesrvAddr(namesrvAddr);
		try {
			this.atProducer.start();
			this.producer = this.atProducer.createProducer(topic);
		} catch (Exception e) {
			this.logger.error("astrotrain producer start error {}", e);
		}
	}
	
	
	public void shutdown() {
		this.logger.info("astrotrain producer has shutdown");
		if(this.atProducer != null){
			this.atProducer.shutdown();
		}
	}
	
	public void sendObjectMessage(Serializable entity, String appCode){
		try {
			ObjectMessage message = new ObjectMessage(entity);
			this.logger.info("astrotrain producer send {}", message);
			message.setProperty(ATMessage.MSG_KEYS, UUID.randomUUID().toString());
			if(!StringUtils.isEmpty(appCode)){
				message.setProperty(ATMessage.TAGS_KEY, appCode);
			}
			this.producer.send(message);
		} catch (Exception e) {
			this.logger.error("astrotrain producer sendObjectMessage entity={} error", entity, e);
		}
	}
	
	public void sendStringMessage(String msg, String appCode){
		try {
			StringMessage message = new StringMessage(msg);
			this.logger.info("astrotrain producer send {}", message);
			message.setProperty(ATMessage.MSG_KEYS, UUID.randomUUID().toString());
			if(!StringUtils.isEmpty(appCode)){
				message.setProperty(ATMessage.TAGS_KEY, appCode);
			}
			this.producer.send(message);
		} catch (Exception e) {
			this.logger.error("astrotrain producer sendStringMessage msg={}", msg, e);
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
