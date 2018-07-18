package com.pay.smsserver.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.mapper.TokenMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ValidationService;

public class ValidationServiceImpl implements ValidationService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private RedisManager redisManager;
	private TokenMapper tokenMapper;
	
	@Override
	public Map<String, String> validate(SmsBean smsBean) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		if (StringUtils.isEmpty(smsBean.getAppCode())) {
			result.put("flag", "fail");
			result.put("info", "appCode is null");
			this.logger.info("validate to={} content={}, appCode is null ", smsBean.getTo(), smsBean.getContent());
			return result;
		}
		if (StringUtils.isEmpty(smsBean.getToken())) {
			result.put("flag", "fail");
			result.put("info", "token is null");
			this.logger.info("validate to={} content={}, token is null ", smsBean.getTo(), smsBean.getContent());
			return result;
		}
		String sourceToken = this.redisManager.get(SmsConstants.REDIS_SMS_TOKEN + smsBean.getAppCode());
		if (StringUtils.isEmpty(sourceToken)) {
			sourceToken = tokenMapper.getTokenByAppCode(smsBean.getAppCode());
			if (!StringUtils.isEmpty(sourceToken)) {
				this.logger.info("validate appCode={} sourceToken={}, sourceToken in DB, synchronize to redis", smsBean.getAppCode(), sourceToken);
				this.redisManager.set(SmsConstants.REDIS_SMS_TOKEN + smsBean.getAppCode(), sourceToken);
			}
		}
		if (smsBean.getToken().equals(sourceToken)) {
			result.put("flag", "true");
			result.put("info", "token validate successfully");
			return result;
		} else {
			result.put("flag", "fail");
			result.put("info", "token validate unsuccessfully");
			this.logger.info("validate appCode={} token={} fail, sourceToken={}", smsBean.getAppCode(), smsBean.getToken(), sourceToken);
			return result;
		}
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	public void setTokenMapper(TokenMapper tokenMapper) {
		this.tokenMapper = tokenMapper;
	}
	
	
}
