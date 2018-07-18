package com.pay.smsserver.service;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsserver.context.SpringBaseTest;
import com.pay.smsserver.mapper.TokenMapper;

/**
 * 单元测试遵守BCDE原则
 * B：Border，边界值测试，包括循环、特殊取时间点数据顺序等。 ，边界值测试包括循环、特殊取时间点数据顺序等。  
 * C：Correct，正确的输入，并得到预期结果。 ，正确的输入并得到预期结果。  
 * D：Design，与设计文档相结合，来编写单元测试。 ，与设计文档相结合来编写单元测试。  
 * E：Error，强制错误信息输入（如：非法数据、异常流程业务允许等），并得 到预期的结果。 
 * @author chenchen.qi
 *
 */
public class ValidationServiceTest extends SpringBaseTest{

	@Autowired
	private ValidationService validationService;
	
	@Autowired
	private TokenMapper tokenMapper;
	
	/**
	 * 测试边界值(validate方法没有此种情况)
	 */
	@Test
	public void testValidateBorder(){
	}
	
	/**
	 * 测试正确输入
	 */
	@Test
	@Transactional
	public void testValidateCorret(){
		String appCode = "testAppCode";
		String token = "testToken";
		tokenMapper.insertAppCodeToken(appCode, token);
		SmsBean smsBean = new SmsBean();
		smsBean.setAppCode(appCode);
		smsBean.setToken(token);
		Map<String, String> result = validationService.validate(smsBean);
		Assert.assertEquals("true", result.get("flag"));
		Assert.assertEquals("token validate successfully", result.get("info"));
		
		smsBean.setAppCode(appCode + "2");
		smsBean.setToken(token);
		result = validationService.validate(smsBean);
		Assert.assertEquals("fail", result.get("flag"));
		Assert.assertEquals("token validate unsuccessfully", result.get("info"));
	}
	
	/**
	 * 测试空值
	 */
	@Test
	public void testValidateNull(){
		SmsBean smsBean = new SmsBean();
		Map<String, String> result = validationService.validate(smsBean);
		Assert.assertEquals("fail", result.get("flag"));
		Assert.assertEquals("appCode is null", result.get("info"));
		smsBean.setAppCode("appCode");
		result= validationService.validate(smsBean);
		Assert.assertEquals("fail", result.get("flag"));
		Assert.assertEquals("token is null", result.get("info"));
	}
	
	/**
	 * 测试非法值
	 */
	@Test
	public void testValidateDataError(){
		SmsBean smsBean = new SmsBean();
		String appCode = "哈哈哈";
		String token = "呵呵呵";
		tokenMapper.insertAppCodeToken(appCode, token);
		smsBean.setAppCode(appCode);
		smsBean.setToken(token);
		Map<String, String> result= validationService.validate(smsBean);
		Assert.assertEquals("true", result.get("flag"));
		Assert.assertEquals("token validate successfully", result.get("info"));
		
		result= validationService.validate(smsBean);
		Assert.assertEquals("true", result.get("flag"));
		Assert.assertEquals("token validate successfully", result.get("info"));
	}
}
