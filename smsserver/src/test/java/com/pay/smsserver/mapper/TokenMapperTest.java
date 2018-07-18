package com.pay.smsserver.mapper;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.TokenMapper;

public class TokenMapperTest extends SpringBaseTest {

	@Autowired
	private TokenMapper tokenMapper;
	
	@Test
	@Transactional
	public void testInsertAppCodeExpandCode(){
		String appCode = "testAppCode";
		String token = "testToken";
		tokenMapper.insertAppCodeToken(appCode, token);
		tokenMapper.getIdByAppCode(appCode);
		String dbToken = tokenMapper.getTokenByAppCode(appCode);
		Assert.assertEquals(token, dbToken);
	}
}
