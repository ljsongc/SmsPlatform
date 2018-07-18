package com.pay.sms.console.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import com.pay.sms.console.bean.ChannelRate;
import com.pay.sms.console.bean.SmsCallbackEntity;
import com.pay.sms.console.bean.SmsChannelEntity;
import com.pay.sms.console.bean.SmsEntity;
import com.pay.sms.console.bean.SmsRateStatics;
import com.pay.sms.console.bean.SmsSaleCallbackChartData;
import com.pay.sms.console.bean.SmsStatisticsChartData;
import com.pay.sms.console.bean.SmsToken;
import com.pay.sms.console.bean.SmsType;
import com.pay.sms.console.sql.DynamicSqlHandle;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

@Component
public interface SmsConsoleMapper {

	//通知类
	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` "
			+ " from `SMS_SUCCESS` where `TIME` >=#{startTime} and `TIME` <=#{endTime} "
			+ " order by `TIME` desc limit #{start}, #{size}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findSuccessSms(@Param("startTime") String startTime,
			@Param("endTime") String endTime, @Param("start") int start, @Param("size") int size);

	@Select("select count(*) from `SMS_SUCCESS` where `TIME`>=#{startTime} and `TIME`<=#{endTime}")
	public Integer findSuccessSmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` " +
			"from `SMS_SUCCESS` where id = #{id}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public SmsEntity findSmsById(@Param("id") int id);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findSuccessSmsByCondition")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findSuccessSmsByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime,
			@Param("endTime") String endTime, @Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findSuccessSmsSizeByCondition")
	public int findSuccessSmsSizeByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime);

	//营销类
	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` "
			+ " from `SMS_SALE_SUCCESS` where `TIME` >=#{startTime} and `TIME` <=#{endTime} "
			+ " order by `TIME` desc limit #{start}, #{size}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findSaleSuccessSms(@Param("startTime") String startTime,
			@Param("endTime") String endTime, @Param("start") int start, @Param("size") int size);

	@Select("select count(*) from `SMS_SALE_SUCCESS` where `TIME`>=#{startTime} and `TIME`<=#{endTime}")
	public Integer findSaleSuccessSmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` " +
			"from `SMS_SALE_SUCCESS` where id = #{id}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public SmsEntity findSaleSmsById(@Param("id") int id);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findSaleSuccessSmsByCondition")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findSaleSuccessSmsByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findSaleSuccessSmsSizeByCondition")
	public int findSaleSuccessSmsSizeByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `id`,`appCode`,`token` from `SMS_TOKEN` limit #{start}, #{size}")
	public List<Map<String, String>> findSmsToken(@Param("start") int start, @Param("size") int size);

	@Select("select count(*) from `SMS_TOKEN`")
	public int findSmsTokenSize();

	@Delete("delete from `SMS_TOKEN` where id = #{id}")
	public void deleteToken(@Param("id") Integer id);

	@Select("select `id`,`appCode`,`token` from `SMS_TOKEN` where id = #{id}")
	public Map<String, String> findSmsTokenById(@Param("id") Integer id);

	@Insert("insert into `SMS_TOKEN`(`appCode`,`token`) values(#{appCode},#{token})")
	public void saveToken(@Param("appCode") String appCode, @Param("token") String token);

	@Select("select `token` from `SMS_TOKEN` where appCode = #{appCode}")
	public String findSmsTokenByAppCode(@Param("appCode") String appCode);

	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN limit #{start},#{size}")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public List<SmsToken> tokenPageList(@Param("start") int start, @Param("size") int size);


	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN where gmt_create >= #{startTime} and gmt_create <= #{endTime} and appCode like  CONCAT('%','${appCode}','%' )  "
			+ "limit #{start},#{size}")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public List<SmsToken> tokenSearch(@Param("start") int start, @Param("size") int size,
			@Param("startTime")String startTime,@Param("endTime")String endTime, @Param("appCode")String appCode);

	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN where appCode like  CONCAT('%','${appCode}','%' )  "
			+ "limit #{start},#{size}")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public List<SmsToken> tokenSearchByAppCode(@Param("start") int start, @Param("size") int size, @Param("appCode")String appCode);


	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN where appCode =  #{appCode}")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public SmsToken findTokenByAppCode(@Param("appCode")String appCode);

	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN ")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public List<SmsToken> findAllToken();

	@Select("select count(*) from SMS_TOKEN where gmt_create >= #{startTime} and gmt_create <= #{endTime} and appCode like  CONCAT('%','${appCode}','%' ) ")
	public int findTokenCount(@Param("startTime")String startTime,@Param("endTime")String endTime, @Param("appCode")String appCode);

	@Select("select count(*) from SMS_TOKEN where appCode like  CONCAT('%','${appCode}','%' ) ")
	public int findTokenCountAppCode(@Param("appCode")String appCode);



	@Select("select `id`, `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator` "
			+ "from SMS_TOKEN where id = #{id}")
	@Results(value={
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "appCode", column = "appCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "token", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "appName", column = "app_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "operator", column = "operator", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "createTime", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.DATE),
			@Result(property = "modifiedTime", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.DATE)
	})
	public SmsToken findTokenById(@Param("id") Integer id);

	@Update("UPDATE `SMS_TOKEN` SET `appCode`=#{appCode}, `token`=#{token}, "
			+ "`app_name`=#{appName}, `gmt_modified`=now(), "
			+ "`operator`=#{operator} WHERE (`id`=#{id})")
	public void updateToken(@Param("appCode") String appCode, @Param("token") String token,@Param("appName") String appName,
			@Param("operator") String operator,@Param("id") Integer id);

	@Insert("INSERT INTO SMS_TOKEN ( `appCode`, `token`, `app_name`, `gmt_create`, `gmt_modified`, `operator`) "
			+ "VALUES (#{appCode}, #{token}, #{appName}, now(), now(), #{operator})")
	public void insertToken(@Param("appCode") String appCode, @Param("token") String token,@Param("appName") String appName,
			@Param("operator") String operator);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findTokenByCondition")
	public List<Map<String,String>> findTokenByCondition(@Param("appCode") String appCode, @Param("token") String token,
			@Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findTokenSizeByCondition")
	public int findTokenSizeByCondition(@Param("appCode") String appCode, @Param("token") String token);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` "
			+ " from `SMS_FAILURE` where `TIME`>=#{startTime} and `TIME`<=#{endTime} "
			+ " order by `TIME` desc limit #{start}, #{size}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findFailureSms(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@Select("select count(*) from `SMS_FAILURE` where `TIME`>=#{startTime} and `TIME`<=#{endTime}")
	public int findFailureSmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` " +
			"from `SMS_FAILURE` where id = #{id}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public SmsEntity findFailureById(@Param("id") Integer id);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findFailureSmsByCondition")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findFailureSmsByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findFailureSmsSizeByCondition")
	public int findFailureSmsSizeByCondition(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` "
			+ " from `SMS_FAILURE_HISTORY` where `TIME`>=#{startTime} and `TIME`<=#{endTime} "
			+ " order by `TIME` desc limit #{start}, #{size}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findFailureHistorySms(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@Select("select count(*) from `SMS_FAILURE_HISTORY` where `TIME`>=#{startTime} and `TIME`<=#{endTime}")
	public int findFailureHistorySmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` " +
			"from `SMS_FAILURE_HISTORY` where id = #{id}")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public SmsEntity findFailureHistoryById(@Param("id") Integer id);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findFailHistSmsByCond")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsEntity> findFailHistSmsByCond(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findFailHistSmsSizeByCond")
	public int findFailHistSmsSizeByCond(@Param("smsTo") String smsTo,
			@Param("content") String content, @Param("startTime") String startTime, @Param("endTime") String endTime);

	@Select("SELECT `id`,SMS_CHANNEL as `name`,SMS_STATE as `state`,SMS_TYPE as `type`, weight,currentWeight FROM SMS_CONFIG order by SMS_TYPE,weight;")
	public List<SmsChannelEntity> findAllChannel();

	@Insert("INSERT INTO SMS_CONFIG(SMS_TYPE,SMS_CHANNEL,SMS_STATE,weight,currentWeight,gmt_create,gmt_modified) "
			+ "VALUES (#{type},#{channel},#{state},#{weight},#{weight},now(),now())")
	public void createChannel(@Param("type")String type,@Param("channel")String channel,@Param("state")String state,@Param("weight")int weight);


	@Delete("DELETE FROM SMS_CONFIG WHERE id = #{id};")
	public void deleteChannelById(@Param("id") long id);

	@Update("UPDATE SMS_CONFIG SET SMS_CHANNEL = #{channel},SMS_STATE = #{state},SMS_TYPE=#{type},weight=#{weight},currentWeight=#{weight},gmt_modified=now() WHERE id = #{id};")
	public void updateChannelById(@Param("type")String type,@Param("channel")String channel,@Param("state")String state,@Param("weight") int weight,@Param("id") long id);

	/**
	 * 获得通知类主通道id
	 * @return
	 */
	@Select("select id from SMS_CONFIG where weight = "
			+ " (select min(weight) from SMS_CONFIG where SMS_STATE='TRUE' and SMS_TYPE='NOTICE' and weight = currentWeight ) "
			+ " and SMS_TYPE='NOTICE';")
	public long getNoticeMasterId();

	/**
	 * 更新一个通道时，要把所有通道的currentWeight还原为weight
	 */
	@Update("UPDATE SMS_CONFIG SET currentWeight = weight;")
	public void recoverAllChannelCurrentWeight();

	/**
	 * 查询通知类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param start
	 * @param size
	 * @return
	 */
	@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
			+ " from (select * from SMS_NOTICE_STATUS where gmt_create >=#{startTime} and gmt_create<=#{endTime} ) s1, SMS_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to "
			+ " order by s1.gmt_create desc limit #{start},#{size};")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsCallbackEntity> findCallbackNoticeSms(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@Select("select count(*) "
			+ " from (select * from SMS_NOTICE_STATUS where gmt_create >=#{startTime} and gmt_create<=#{endTime} ) s1, SMS_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
	public int findCallbackNoticeSmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findCallbackNoticeSmsByCond")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsCallbackEntity> findCallbackNoticeSmsByCond(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("phone") String phone, @Param("msgid") String msgid, @Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findCallbackNoticeSmsSizeByCond")
	public int findCallbackNoticeSmsSizeByCond(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("phone") String phone, @Param("msgid") String msgid);

	@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
			+ " from (select * from SMS_NOTICE_STATUS where id=#{id} ) s1, SMS_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public SmsCallbackEntity findNoticeCallbackViewById(@Param("id") String id);

	/**
	 * 查询营销类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param start
	 * @param size
	 * @return
	 */
	@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
			+ " from (select * from SMS_SALE_STATUS where gmt_create >=#{startTime} and gmt_create<=#{endTime} ) s1, SMS_SALE_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to "
			+ " order by s1.gmt_create desc limit #{start},#{size};")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsCallbackEntity> findCallbackSaleSms(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("start") int start, @Param("size") int size);

	@Select("select count(*) "
			+ " from (select * from SMS_SALE_STATUS where gmt_create >=#{startTime} and gmt_create<=#{endTime} ) s1, SMS_SALE_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
	public int findCallbackSaleSmsSize(@Param("startTime") String startTime, @Param("endTime") String endTime);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findCallbackSaleSmsByCond")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsCallbackEntity> findCallbackSaleSmsByCond(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("phone") String phone, @Param("msgid") String msgid, @Param("start") int start, @Param("size") int size);

	@SelectProvider(type = DynamicSqlHandle.class, method = "findCallbackSaleSmsSizeByCond")
	public int findCallbackSaleSmsSizeByCond(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("phone") String phone, @Param("msgid") String msgid);

	@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
			+ " from (select * from SMS_SALE_STATUS where id=#{id} ) s1, SMS_SALE_SUCCESS s2 "
			+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
	@Results(value = {
			@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
	public SmsCallbackEntity findSaleCallbackViewById(@Param("id") String id);

	/**
	 * echart图表,返回营销短信数据
	 * @param startTime
	 * @param endTime
	 * @param content
	 * @return
	 */
	@SelectProvider(type = DynamicSqlHandle.class, method = "getSaleCallbackChartData")
	@Results(value = {
		@Result(property = "name", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR)})
	public List<SmsSaleCallbackChartData> getSaleCallbackChartData(@Param("startTime") String startTime,
			@Param("endTime") String endTime,@Param("content") String content);

	/**
	 * 获得营销短信发送数量
	 * @param startTime
	 * @param endTime
	 * @param content
	 * @return
	 */
	@SelectProvider(type = DynamicSqlHandle.class, method = "getSaleCount")
	public String getSaleCount(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("content") String content);

	//通知短信计费统计
	//查询通知类成功条数
	@MapKey(value="date")
	@Select("select date(time) as time,count(*) as count from SMS_SUCCESS "
			+ " where TIME BETWEEN #{startTime} and '${endTime} 23:59:59' "
			+ " and CHANNEL_NO in (${channels}) GROUP BY date(TIME);")
	@Results(value = {
			@Result(property="date",column="time",javaType=String.class,jdbcType=JdbcType.TIMESTAMP),
			@Result(property="number",column="count",javaType=Long.class,jdbcType=JdbcType.VARCHAR)
	})
	public Map<String, SmsStatisticsChartData> getNoticeSuccessCount(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("channels") String channels);

	//通知营销计费统计
	// 查询营销类成功条数
	@MapKey(value = "date")
	@Select("select date(time) as time,count(*) as count from SMS_SALE_SUCCESS "
			+ " where TIME BETWEEN #{startTime} and '${endTime} 23:59:59' "
			+ " and CHANNEL_NO in (${channels}) GROUP BY date(TIME);")
	@Results(value = {
			@Result(property = "date", column = "time", javaType = String.class, jdbcType = JdbcType.TIMESTAMP),
			@Result(property = "number", column = "count", javaType = Long.class, jdbcType = JdbcType.VARCHAR) })
	public Map<String, SmsStatisticsChartData> getSaleSuccessCount(
			@Param("startTime") String startTime,
			@Param("endTime") String endTime, @Param("channels") String channels);

	/**
	 * 根据通道查询失败条数
	 * @param startTime
	 * @param endTime
	 * @param channels
	 * @return
	 */
	@MapKey(value="date")
	@Select("select date(time) as time,count(*) as count from SMS_FAILURE "
			+ " where TIME BETWEEN #{startTime} and '${endTime} 23:59:59' "
			+ " and CHANNEL_NO in (${channels}) GROUP BY date(TIME);")
	@Results(value = {
			@Result(property="date",column="time",javaType=String.class,jdbcType=JdbcType.TIMESTAMP),
			@Result(property="number",column="count",javaType=Long.class,jdbcType=JdbcType.VARCHAR)
	})
	public Map<String, SmsStatisticsChartData> getFailureCount(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("channels") String channels);

	/**
	 * 根据通道查询失败历史条数
	 * @param startTime
	 * @param endTime
	 * @param channels
	 * @return
	 */
	@MapKey(value="date")
	@Select("select date(time) as time,count(*) as count from SMS_FAILURE_HISTORY "
			+ " where TIME BETWEEN #{startTime} and '${endTime} 23:59:59' "
			+ " and CHANNEL_NO in (${channels}) GROUP BY date(TIME);")
	@Results(value = {
			@Result(property="date",column="time",javaType=String.class,jdbcType=JdbcType.TIMESTAMP),
			@Result(property="number",column="count",javaType=Long.class,jdbcType=JdbcType.VARCHAR)
	})
	public Map<String, SmsStatisticsChartData> getFailureHistoryCount(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("channels") String channels);

	/**
	 * 根据通道查询计费条数
	 * @param startTime
	 * @param endTime
	 * @param channels
	 * @return
	 */
	@MapKey(value="date")
	@Select("select sum(count) as count,begin_time as date from SMS_STATISTICS "
			+ " where begin_time BETWEEN #{startTime} and #{endTime} and ccode in (${channels}) "
			+ " GROUP BY begin_time;")
	@Results(value = {
			@Result(property="date",column="date",javaType=String.class,jdbcType=JdbcType.TIMESTAMP),
			@Result(property="number",column="count",javaType=Long.class,jdbcType=JdbcType.VARCHAR)
	})
	public Map<String, SmsStatisticsChartData> getStatistics(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("channels") String channels);

	//根据类型查询全部通道名称
	@Select("select SMS_CHANNEL from SMS_CONFIG where SMS_TYPE=#{type};")
	public List<String> getAllChannelNoByType(@Param("type") String type);

	/**
	 * @param id
	 * @return
	 */
	@Select(" SELECT t.id, t.SMS_CHANNEL as name,t.SMS_STATE as smsState,t.gmt_create,t.gmt_modified as gmtModified,"
			+ "t.CHANNEL_CODE as channelCode,t.CHANNEL_COST as channelCost,t.REMARK as remark,t.CHANNEL_CONTACT as channelContact"
			+ " from SMS_CONFIG t  where t.id=#{id}")
	@Results(value = {
			@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.BIGINT),
            @Result(property = "name", column = "SMS_CHANNEL", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsState", column = "SMS_STATE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelCode", column = "CHANNEL_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelCost", column = "CHANNEL_COST", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "channelContact", column = "CHANNEL_CONTACT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "gmtModified", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
            @Result(property = "gmtCreate", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
	public SmsChannelEntity findChannelByID(String id);


	@Update("UPDATE SMS_CONFIG SET sms_state = #{status} WHERE id = #{id};")
	public void updateSttus(@Param("id")long id, @Param("status")String status);



		@Select(" SELECT t.TYPE_CODE,t.TYPE_NAME,t.REMARK,t.OPERATOR,t.CREATE_TIME,t.UPDATE_TIME from SMS_TYPE t  ")
		@Results(value = {
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "TYPE_NAME", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "operator", column = "OPERATOR", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "updateTime", column = "UPDATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
	public List<SmsType> findliseType();

		@SelectProvider(type = DynamicSqlHandle.class, method = "pageListSmsType")
		@Results(value = {
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "TYPE_NAME", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "operator", column = "OPERATOR", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "rateRemark", column = "rateRemark", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "updateTime", column = "UPDATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
		public List<SmsType> pageListSmsType(@Param("startTime")String startTime, @Param("endTime")String endTime,
				@Param("start")int start, @Param("pageSize")int pageSize, @Param("typeCode")String typeCode);


		@SelectProvider(type = DynamicSqlHandle.class, method = "pageListSmsTypeCount")
		public int pageListSmsTypeCount(@Param("startTime")String startTime, @Param("endTime")String endTime,
				@Param("typeCode")String typeCode);



		@Select(" SELECT t.id, t.SMS_CHANNEL as name,t.SMS_STATE as smsState,t.weight,t.gmt_create,t.gmt_modified as gmtModified,"
				+ "t.CHANNEL_CODE as channelCode,t.CHANNEL_COST as channelCost,t.REMARK as remark,t.CHANNEL_CONTACT as channelContact"
				+ " from SMS_CONFIG t ")
		@Results(value = {
				@Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.BIGINT),
	            @Result(property = "name", column = "SMS_CHANNEL", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "state", column = "smsState", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelCode", column = "CHANNEL_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelCost", column = "CHANNEL_COST", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelContact", column = "CHANNEL_CONTACT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "gmtModified", column = "gmt_modified", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "gmtCreate", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
		public List<SmsChannelEntity> findsmsList();



		@Insert("INSERT INTO SMS_TYPE (TYPE_CODE, TYPE_NAME, REMARK, OPERATOR, CREATE_TIME) "
				+ " VALUES (#{typeCode}, #{typeName},#{remark}, #{operator}, NOW());")
		public void createSmsType(@Param("typeCode")String typeCode,
				@Param("typeName")String typeName, @Param("operator")String operator,
				@Param("remark") String remark);


		@Select(" SELECT t.TYPE_CODE,t.TYPE_NAME,t.REMARK,t.OPERATOR,t.CREATE_TIME,t.UPDATE_TIME from SMS_TYPE t"
				+ " where TYPE_CODE=#{typeCode}  ")
		@Results(value = {
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "TYPE_NAME", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "spareChannelCode", column = "spare_channel_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "operator", column = "OPERATOR", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "updateTime", column = "UPDATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
		public SmsType findSmsTypeByID(@Param("typeCode") String typeCode);



		@Update("UPDATE SMS_TYPE SET operator=#{operator},type_name=#{typeName},"
				+ "update_time= now(),remark=#{remark} WHERE TYPE_CODE = #{typeCode};")
		public void updateSmsType(@Param("typeName")String typeName,
				@Param("operator")String operator, @Param("remark")String remark,
				@Param("typeCode") String typeCode);



		@SelectProvider(type = DynamicSqlHandle.class, method = "checksms")
		@Results(value = {
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "TYPE_NAME", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "remark", column = "REMARK", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "operator", column = "OPERATOR", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "updateTime", column = "UPDATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
		public SmsType checksms(@Param("typeName")String typeName, @Param("channelCode")String channelCode,@Param("typeCode")String typeCode);

		@Insert("INSERT INTO SMS_CHANNEL_RATE (type_code,channel_code,rate,operator,create_time) "
				+ "values(#{typeCode},#{channelCode},#{rate},#{operator},now())")
		public void insertChannelRate(@Param("typeCode")String typeCode, @Param("channelCode")String channelCode,
				@Param("rate")int rate, @Param("operator")String operator);



		@Select(" SELECT t2.CHANNEL_CODE,t2.RATE from SMS_TYPE t1 join SMS_CHANNEL_RATE t2 on t1.TYPE_CODE=t2.TYPE_CODE "
				+"  where t1.TYPE_CODE=#{typeCode}  ")
		@Results(value = {
				@Result(property = "rate", column = "rate", javaType = Integer.class, jdbcType = JdbcType.BIGINT),
	            @Result(property = "channelCode", column = "CHANNEL_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR)}
				)
		public List<ChannelRate> findRate(String typeCode);


		@Delete("delete from SMS_CHANNEL_RATE where TYPE_CODE = #{typeCode}")
		public void deleteRate(String typeCode);


		@Select("SELECT `id`,SMS_CHANNEL as `name`,SMS_STATE as `state`,SMS_TYPE as `type`, "
				+ " CHANNEL_CODE as channelCode,CHANNEL_COST as channelCost,REMARK AS remark,CHANNEL_CONTACT as channelContact "
				+ " FROM SMS_CONFIG order by SMS_TYPE,weight;")
		public List<SmsChannelEntity> findAllChannelsnew();


		@Insert("INSERT INTO SMS_CONFIG(SMS_CHANNEL,SMS_STATE,gmt_create,gmt_modified,channel_code,channel_cost,remark,channel_contact) "
				+ "VALUES (#{channel},'TRUE',now(),now(),#{channelCode},#{channelCost},#{remark},#{ChannelContact})")
		public void createChannelnew(@Param("channelCode")String channelCode,@Param("channel")String channel,@Param("channelCost")String channelCost,
				  @Param("remark")String remark, @Param("ChannelContact")String ChannelContact);


		@Update("UPDATE SMS_CONFIG SET SMS_CHANNEL = #{channel},"
				+ " channel_contact=#{channelContact},channel_cost=#{channelCost},remark=#{remark}, gmt_modified=now() WHERE id = #{id};")
		public void updateChannelnew(@Param("channel")String name, @Param("id")long id,
				@Param("channelContact")String channelContact, @Param("channelCost")String channelCost, @Param("remark")String remark);




				@Select("<script>" + "SELECT a.id,a.phone,a.CHANNEL_NO,a.APPCODE,a.type_code,a.template_code,a.status_code,"
						+ "a.gmt_create,b.SMS_CHANNEL as channelName,"
						+ "c.TYPE_NAME as typeName from "
						+ " ("
						+" <if test='typeCode == &apos;NOTICE&apos;  '> "
						+ " (SELECT t2.id,t1.SMS_TO as phone,t1.CHANNEL_NO,t1.APPCODE,t1.type_code,t1.template_code,t2.status_code,t2.gmt_create"
						+ "  from  SMS_SUCCESS t1  JOIN   SMS_NOTICE_STATUS t2 on t1.MSGID=t2.msgid   and t1.sms_to = t2.phone	where 1=1 "
						+ " <if test='startTime != null and startTime !=&apos;&apos; '> and t2.gmt_create &gt;= #{startTime}</if> "
						+ " <if test='endTime != null and endTime !=&apos;&apos; '>  and t2.gmt_create &lt;= #{endTime}</if> "
						+ " <if test='phone != null and phone !=&apos;&apos; '>   and t1.SMS_TO= #{phone}</if> "
						+ " <if test='templateCode != null and templateCode !=&apos;&apos; '>   and t1.template_code= #{templateCode}</if> "
						+ " <if test='appCode != null and appCode !=&apos;&apos; '>   and t1.APPCODE= #{appCode}</if> "
						+ " <if test='channelNo != null and channelNo !=&apos;&apos; '>   and t1.CHANNEL_NO= #{channelNo}</if> "
						+ " <if test='status == &apos;SUCCESS&apos;  '>   and t2.status_code in('DELIVRD','0') </if> "
						+ " <if test='status != &apos;SUCCESS&apos; and status!=null and status!=&apos;&apos;  '>    and t2.status_code not in('DELIVRD','0') </if> "
						+ " order by t2.gmt_create desc   LIMIT #{index}, #{pageSize}) "
						+ "</if> "
//						+ " UNION all "
						+" <if test='typeCode == &apos;SALE&apos;  '> "
						+ " (SELECT t2.id,t1.SMS_TO as phone,t1.CHANNEL_NO,t1.APPCODE,t1.type_code,t1.template_code,t2.status_code,t2.gmt_create"
						+ " from  SMS_SALE_SUCCESS t1 join SMS_NOTICE_STATUS t2 on t1.MSGID=t2.msgid  and  t1.sms_to = t2.phone where 1=1 "
						+ " <if test='startTime != null and startTime !=&apos;&apos; '> and t2.gmt_create &gt;= #{startTime}</if> "
						+ " <if test='endTime != null and endTime !=&apos;&apos; '>  and t2.gmt_create &lt;= #{endTime}</if> "
						+ " <if test='phone != null and phone !=&apos;&apos; '>   and t1.SMS_TO= #{phone}</if> "
						+ " <if test='templateCode != null and templateCode !=&apos;&apos; '>   and t2.template_code= #{templateCode}</if> "
						+ " <if test='appCode != null and appCode !=&apos;&apos; '>   and t1.APPCODE= #{appCode}</if> "
						+ " <if test='channelNo != null and channelNo !=&apos;&apos; '>   and t1.CHANNEL_NO= #{channelNo}</if> "
						+ " <if test='status == &apos;SUCCESS&apos;  '>   and t2.status_code in('DELIVRD','0') </if> "
						+ " <if test='status != &apos;SUCCESS&apos;  and status!=null and status!=&apos;&apos; '>   and t2.status_code not in('DELIVRD','0') </if> "
						+ " order by t2.gmt_create desc   LIMIT #{index}, #{pageSize} )"
						+ "</if> "
						+ " )a"

						+ ",SMS_CONFIG b,SMS_TYPE c "
						+ " where a.CHANNEL_NO=b.CHANNEL_CODE and a.type_code = c.type_code "

						+ "</script>")
		@Results(value = {
				@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
	            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelName", column = "channelName", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "typeName", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "appcode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeCode", column = "TYPE_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "templateCode", column = "template_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)})
		public List<SmsCallbackEntity> smsListSearch(@Param("startTime") String startTime,
				@Param("endTime")String endTime, @Param("phone")String phone,
				@Param("templateCode")String templateCode, @Param("typeCode")String typeCode, @Param("appCode")String appCode,
				@Param("channelNo")String channelNo, @Param("status")String status, @Param("index")int index, @Param("pageSize")int pageSize);





				@Select("<script>" + "SELECT count(a.id) from "
						+ " ("
						+" <if test='typeCode == &apos;NOTICE&apos;  '> "
						+ "SELECT t2.id,t1.SMS_TO as phone,t1.CHANNEL_NO,t1.APPCODE,t1.type_code,t1.template_code,t2.status_code,t2.gmt_create"
						+ "  from  SMS_SUCCESS t1  JOIN   SMS_NOTICE_STATUS t2 on t1.MSGID=t2.msgid   and t1.sms_to = t2.phone	where 1=1 "
						+ " <if test='startTime != null and startTime !=&apos;&apos; '> and t2.gmt_create &gt;= #{startTime}</if> "
						+ " <if test='endTime != null and endTime !=&apos;&apos; '>  and t2.gmt_create &lt;= #{endTime}</if> "
						+ " <if test='phone != null and phone !=&apos;&apos; '>   and t1.SMS_TO= #{phone}</if> "
						+ " <if test='templateCode != null and templateCode !=&apos;&apos; '>   and t1.template_code= #{templateCode}</if> "
						+ " <if test='appCode != null and appCode !=&apos;&apos; '>   and t1.APPCODE= #{appCode}</if> "
						+ " <if test='channelNo != null and channelNo !=&apos;&apos; '>   and t1.CHANNEL_NO= #{channelNo}</if> "
						+ " <if test='status == &apos;SUCCESS&apos;  '>   and t2.status_code in('DELIVRD','0') </if> "
						+ " <if test='status != &apos;SUCCESS&apos;  and status!=null and status!=&apos;&apos; '>     and t2.status_code not in('DELIVRD','0') </if> "
						+ "</if> "
//						+ " UNION all "
						+" <if test='typeCode == &apos;SALE&apos;  '> "
						+ " SELECT t2.id,t1.SMS_TO,t1.CHANNEL_NO,t1.APPCODE,t1.type_code,t1.template_code,t2.status_code,t2.gmt_create"
						+ " from  SMS_SALE_SUCCESS t1 join SMS_NOTICE_STATUS t2 on t1.MSGID=t2.msgid  and  t1.sms_to = t2.phone where 1=1 "
						+ " <if test='startTime != null and startTime !=&apos;&apos; '> and t2.gmt_create &gt;= #{startTime}</if> "
						+ " <if test='endTime != null and endTime !=&apos;&apos; '>  and t2.gmt_create &lt;= #{endTime}</if> "
						+ " <if test='phone != null and phone !=&apos;&apos; '>   and t1.SMS_TO= #{phone}</if> "
						+ " <if test='templateCode != null and templateCode !=&apos;&apos; '>   and t1.template_code= #{templateCode}</if> "
						+ " <if test='appCode != null and appCode !=&apos;&apos; '>   and t1.APPCODE= #{appCode}</if> "
						+ " <if test='channelNo != null and channelNo !=&apos;&apos; '>   and t1.CHANNEL_NO= #{channelNo}</if> "
						+ " <if test='status == &apos;SUCCESS&apos;  '>   and t2.status_code in('DELIVRD','0') </if> "
						+ " <if test='status != &apos;SUCCESS&apos;  and status!=null and status!=&apos;&apos; '>    and t2.status_code not in('DELIVRD','0') </if> "
						+ "</if> "
						+ " )a "
						+ ",SMS_CONFIG b,SMS_TYPE c "
						+ " where a.CHANNEL_NO=b.CHANNEL_CODE and a.type_code = c.type_code "
						+ "</script>")
		public int smsListSearchCount(@Param("startTime") String startTime,
				@Param("endTime")String endTime, @Param("phone")String phone,
				@Param("templateCode")String templateCode, @Param("typeCode")String typeCode, @Param("appCode")String appCode,
				@Param("channelNo")String channelNo, @Param("status")String status);



		@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
				+ " from (select * from SMS_NOTICE_STATUS where id=#{id} ) s1, SMS_SUCCESS s2 "
				+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
		@Results(value = {
				@Result(property = "id", column = "ID", javaType = String.class, jdbcType = JdbcType.BIGINT),
	            @Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "statusCode", column = "STATUS_CODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "description", column = "DESCRIPTION", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "createTime", column = "GMT_CREATE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "receiveTime", column = "GMT_RECEIVE", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
	            @Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)})
		public SmsCallbackEntity findsmsQueryViewById(String id);


		@Select("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type "
				+ " from (select * from SMS_NOTICE_STATUS where id=#{id} ) s1, SMS_SALE_SUCCESS s2 "
				+ " where s1.msgid = s2.msgid and s1.phone = s2.sms_to;")
		public SmsCallbackEntity findsmsQuerySaleViewById(String id);
		
		
		
		
		@Select("<script>" + "select t1.id,t1.`channel_code`, t1.`type_code`, t1.`app_code`, t1.`time`, t1.`send_total`, t1.`send_success_total`, "
				+ "t1.`send_fail_total`, t1.`send_success_rate`, t1.`receive_success_total`, t1.`receive_fail_total`,"
				+ " t1.`receive_success_rate`, t1.`fee_total`,t2.type_name,"
				+ " `gmt_create`, `gmt_modified` from SMS_RATE_STATISTICS t1 "
				+ " left join SMS_TYPE t2 on t1.TYPE_CODE = t2.TYPE_CODE "
				+ "where 1=1 "
				+ "<if test='channelCodes != null and channelCodes !=&apos;&apos; '> and t1.channel_code in "
				+ "<foreach item='channelCode' collection='channelCodes' open='(' separator=',' close=')' > #{channelCode} "
				+ "</foreach></if>" 
				+ "<if test='typeCodes != null and typeCodes !=&apos;&apos; '> and t1.type_code in "
				+ "<foreach item='typeCode' collection='typeCodes' open='(' separator=',' close=')' > #{typeCode} "
				+ "</foreach></if>" 
				+ "<if test='appCode != null and appCode !=&apos;&apos; '> and t1.app_code=#{appCode} </if>" 
				+ "<if test='startTime != null and startTime !=&apos;&apos; '> and t1.time&gt;= #{startTime} and t1.time&lt;= #{endTime}</if>" 
				+ "order by `TIME` desc limit #{start}, #{size}</script>" )
		@Results(
				value = {
	            @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "channelCode", column = "channel_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "typeName", column = "type_name", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "appCode", column = "app_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
	            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
	            @Result(property = "sendTotal", column = "send_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "sendSuccessTotal", column = "send_success_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "sendFailTotal", column = "send_fail_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "sendSuccessRate", column = "send_success_rate", javaType = Double.class, jdbcType = JdbcType.DOUBLE),
	            @Result(property = "receiveSuccessTotal", column = "receive_success_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "receiveFailTotal", column = "receive_fail_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
	            @Result(property = "receiveSuccessRate", column = "receive_success_rate", javaType = Double.class, jdbcType = JdbcType.DOUBLE),
	            @Result(property = "feeTotal", column = "fee_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
				}
		)
		List<SmsRateStatics> pageListSmsRateStatics(@Param(value = "start") int start,@Param(value = "size") int size,
				@Param(value = "appCode") String appCode,@Param(value = "typeCodes") List<String> typeCodes,
				@Param(value = "channelCodes") List<String> channelCodes,@Param(value = "startTime") String startTime, @Param(value = "endTime")String endTime);

		

		@Select("<script>" + "select count(*) from SMS_RATE_STATISTICS "
				+ "where 1=1 "
				+ "<if test='channelCodes != null and channelCodes !=&apos;&apos; '> and channel_code in "
				+ "<foreach item='channelCode' collection='channelCodes' open='(' separator=',' close=')' > #{channelCode} "
				+ "</foreach></if>" 
				+ "<if test='typeCodes != null and typeCodes !=&apos;&apos; '> and type_code in "
				+ "<foreach item='typeCode' collection='typeCodes' open='(' separator=',' close=')' > #{typeCode} "
				+ "</foreach></if>" 
				+ "<if test='appCode != null and appCode !=&apos;&apos; '> and app_code=#{appCode} </if>" 
				+ "<if test='startTime != null and startTime !=&apos;&apos; '> and time&gt;= #{startTime} and time&lt;= #{endTime}</if>" 
				+ "</script>" )
		
		int countSmsRateStatics(@Param(value = "appCode") String appCode,@Param(value = "typeCodes") List<String> typeCodes,
				@Param(value = "channelCodes") List<String> channelCodes,@Param(value = "startTime") String startTime, @Param(value = "endTime")String endTime);

}
