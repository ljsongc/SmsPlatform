package com.pay.smsserver.service;

import java.util.Map;

/**
 * 检测通道发送状态接口类
 * @author haoran.liu
 *
 */
public interface ChannelCheckStatusService {
	
	/**
	 * 查询各通道成功表数量
	 * @param intervalTime 间隔时间
	 * @return
	 */
	public Map<String, String> getSuccessCount(int intervalTime);
	
	/**
	 * 查询各通道失败表数量
	 * @param intervalTime 间隔时间
	 * @return
	 */
	public Map<String, String> getFailureCount(int intervalTime);
	
	/**
	 * 通道状态更新为主通道
	 * @param channelName 通道名
	 * @param channelName 通道状态
	 */
	public void updateChannelToMaster(String channelName, String status);
	
	/**
	 * 通道状态更新为备通道
	 * @param channelName 通道名
	 * @param channelName 通道状态
	 */
	public void updateChannelToSpare(String channelName, String status);
}
