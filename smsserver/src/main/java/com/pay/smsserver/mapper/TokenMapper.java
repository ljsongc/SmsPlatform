package com.pay.smsserver.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * Service/DAO层方法命名规约
 * 	1） 获取单个对象的方法用get做前缀。 
 * 	2） 获取多个对象的方法用list做前缀。 
 * 	3） 获取统计值的方法用count做前缀。
 * 	4） 插入的方法用save/insert做前缀。 
 * 	5） 删除的方法用remove/delete做前缀。 
 * 	6） 修改的方法用update做前缀。
 * @author chenchen.qi
 *
 */
@Component
public interface TokenMapper {

	/**
	 * 通过appCode查询绑定的token
	 * @param appCode 应用标识
	 * @return	token
	 */
	@Select("select token from SMS_TOKEN where appCode = #{appCode}")
	String getTokenByAppCode(@Param(value = "appCode") String appCode);
	
	/**
	 * 根据应用标识获取id
	 * @param appCode
	 * @return
	 */
	@Select("select id from SMS_TOKEN where appCode = #{appCode}")
	Integer getIdByAppCode(@Param(value = "appCode") String appCode);
	
	/**
	 * 保存appCode和token对应关系
	 * @param appCode
	 * @param token
	 */
	@Insert("insert into SMS_TOKEN(appCode,token) values(#{appCode},#{token})")
	void insertAppCodeToken(@Param(value = "appCode") String appCode, @Param(value = "token") String token);
}
