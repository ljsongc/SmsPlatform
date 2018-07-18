package com.pay.smsserver.service;

import java.util.List;

import com.pay.smsserver.bean.SmsEntity;

/**
 * 
 * @author chenchen.qi
 *
 */
public interface SmsFailureService {
	
	/**
	 * 插入失败表
	 * @param smsEntity
	 * @return
	 */
	public boolean insert(SmsEntity smsEntity);

	/**
	 * 根据唯一标识删除失败记录
	 * @param redisKey
	 */
	public boolean delete(SmsEntity smsEntity);
	
	/**
	 * 根据可以重试的错误码，寻找可重试短信
	 * @param total
	 * @param retryCodes
	 * @return
	 */
	public List<SmsEntity> getRetrySmsEntityList(int total, String[] retryCodes);
	
	/**
	 * 增加失败表中，记录的失败次数
	 * @param smsEntity
	 */
	public boolean addSmsFailCount(SmsEntity smsEntity);
	
	/**
	 * 插入失败表
	 * @param smsEntity
	 * @return
	 */
	public void insertHistory(SmsEntity smsEntity);
}

