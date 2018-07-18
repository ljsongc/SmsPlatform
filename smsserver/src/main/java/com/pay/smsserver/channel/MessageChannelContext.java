package com.pay.smsserver.channel;

import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * 通道上下文
 * @author chenchen.qi
 *
 */
public class MessageChannelContext {
	
	/**
	 * 通道名和通道的映射关系
	 */
	private Map<String, MessageChannel> channelMap;
	
	/**
	 * 通过通道名获取通道
	 * @param channelName	通道名称
	 * @return				短信通道
	 */
	public MessageChannel getChannel(String channelName){
		if(StringUtils.isEmpty(channelName)){
			return null;
		}
		return channelMap.get(channelName);
	}

	public Map<String, MessageChannel> getChannelMap() {
		return channelMap;
	}

	public void setChannelMap(Map<String, MessageChannel> channelMap) {
		this.channelMap = channelMap;
	}
	
	
}
