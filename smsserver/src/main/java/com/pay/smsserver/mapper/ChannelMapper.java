package com.pay.smsserver.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.type.JdbcType;
import org.springframework.stereotype.Component;

import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsRateStatics;
import com.pay.smsserver.bean.SmsStatusEntity;

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
public interface ChannelMapper {

	/**
	 * 查询指定消息类型时间间隔内的各通道成功总数
	 * @param smsSendType 短信发送类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(单位为分钟)
	 * @return 各通道成功总数集合
	 */
	@Select("SELECT CHANNEL_NO AS channelName, COUNT(ID) AS count FROM SMS_SUCCESS WHERE "
			+ "TIME > date_add(now(), INTERVAL - #{minuteIntervalTime} MINUTE) and TYPE = #{smsSendType} GROUP BY CHANNEL_NO")
	List<Map<String, String>> countSuccessMessages(@Param(value = "smsSendType") String smsSendType, @Param(value = "minuteIntervalTime") int minuteIntervalTime);

	/**
	 * 查询指定消息类型时间间隔内的各通道失败总数
	 * @param smsSendType 短信发送类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(单位为分钟)
	 * @return 各通道成功总数集合
	 */
	@Select("SELECT CHANNEL_NO AS channelName, COUNT(ID) AS count FROM SMS_FAILURE WHERE "
			+ "TIME > date_add(now(), INTERVAL - #{minuteIntervalTime} MINUTE) and TYPE = #{smsSendType} GROUP BY CHANNEL_NO")
	List<Map<String, String>> countFailMessages(@Param(value = "smsSendType") String smsSendType, @Param(value = "minuteIntervalTime") int minuteIntervalTime);

	/**
	 * 查询指定消息类型时间间隔内的各通道失败总数
	 * @param smsSendType 短信发送类型，通知还是营销
	 * @param minuteIntervalTime 时间间隔(单位为分钟)
	 * @return 各通道成功总数集合
	 */
	@Select("SELECT CHANNEL_NO AS channelName, COUNT(ID) AS count FROM SMS_FAILURE_HISTORY WHERE "
			+ "TIME > date_add(now(), INTERVAL - #{minuteIntervalTime} MINUTE) and TYPE = #{smsSendType} GROUP BY CHANNEL_NO")
	List<Map<String, String>> countFailHistoryMessages(@Param(value = "smsSendType") String smsSendType, @Param(value = "minuteIntervalTime") int minuteIntervalTime);

	/**
	 * 根据短信类型和状态获取通道信息
	 * @param status 通道状态，启用还是非启用
	 * @param smsSendType 短信类型，通知还是营销
	 * @return 通道信息集合
	 */
	@Select("select SMS_CHANNEL,SMS_STATE,SMS_TYPE,weight,currentWeight from SMS_CONFIG where "
			+ "SMS_STATE=#{status} and SMS_TYPE=#{smsSendType} and weight = currentWeight")
	@Results(
			value = {
			@Result(property = "smsChannel", column = "SMS_CHANNEL", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsState", column = "SMS_STATE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsType", column = "SMS_TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "weight", column = "weight", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "currentWeight", column = "currentWeight", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsConfig> listMasterSmsConfigs(@Param(value = "status") String status, @Param(value = "smsSendType") String smsSendType);

	/**
	 * 更新通道为备用通道(weight=currentWeight为主通道)
	 * @param channelName 通道名称
	 * @param status 通道状态
	 */
	@Update("update SMS_CONFIG set currentWeight = weight + 1, gmt_modified = now() where "
			+ "SMS_CHANNEL = #{channelName} and SMS_STATE = #{status}")
	void updateChannelToSpare(@Param(value = "channelName") String channelName, @Param(value = "status") String status);

	/**
	 * 保存上行短信
	 * @param upLinkEntity 上行短信实体
	 */
	@Insert("insert into UP_LINK_SMS(PHONE,CONTENT,CREATE_TIME,RECEIVE_TIME,TYPE) values("
			+ "#{upLinkEntity.phone},#{upLinkEntity.content},#{upLinkEntity.createTime},#{upLinkEntity.receiveTime},#{upLinkEntity.type})")
	void insertUplinkMessage(@Param(value = "upLinkEntity") UpLinkEntity upLinkEntity);

	/**
	 * 查询上行短信
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param createTime 创建时间
	 * @return 上行短信实体集合
	 */
	@Select("select PHONE,CONTENT,CREATE_TIME,RECEIVE_TIME,TYPE from UP_LINK_SMS where "
			+ "PHONE = #{phone} and CONTENT = #{content} and CREATE_TIME >= #{createTime}")
	@Results(
			value = {
				@Result(property = "phone", column = "PHONE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "createTime", column = "CREATE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
				@Result(property = "receiveTime", column = "RECEIVE_TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
				@Result(property = "type", column = "TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR)
			}
		)
	List<UpLinkEntity> listUplinkMessages(@Param(value = "phone") String phone, @Param(value = "content") String content,
			@Param(value = "createTime") Date createTime);

	/**
	 * 保存短信状态
	 * @param smsStatusEntity
	 */
	@Insert("insert into SMS_NOTICE_STATUS(msgid,phone,status_code,description,channel_no,gmt_create,gmt_receive) values("
			+ "#{smsStatusEntity.msgid},#{smsStatusEntity.phone},#{smsStatusEntity.errmsg},#{smsStatusEntity.description}"
			+ ",#{smsStatusEntity.channelNo},#{smsStatusEntity.createDate},#{smsStatusEntity.receiveDate})")
	void insertMessageStatus(@Param(value = "smsStatusEntity") SmsStatusEntity smsStatusEntity);

	/**
	 * 查询指定消息状态
	 * @param phone 手机号码
	 * @param msgid 下行标识
	 * @param createTime 创建时间
	 * @return 消息状态集合
	 */
	@Select("select msgid,phone,status_code,description,channel_no,gmt_create,gmt_receive from SMS_NOTICE_STATUS where "
			+ "phone = #{phone} and msgId = #{msgId} and gmt_create >= #{createTime}")
	@Results(
			value = {
				@Result(property = "msgid", column = "msgid", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "phone", column = "phone", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "errmsg", column = "status_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "description", column = "description", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "channelNo", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
				@Result(property = "createDate", column = "gmt_create", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
				@Result(property = "receiveDate", column = "gmt_receive", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP)
			}
		)
	List<SmsStatusEntity> listMessageStatuses(@Param(value = "phone") String phone, @Param(value = "msgId") String msgId,
			@Param(value = "createTime") Date createTime);

	/**
	 * 根据应用标识获取上行扩展码
	 * @param appCode
	 * @return
	 */
	@Select("select EXPANDCODE from SMS_APPCODE_EXPANDCODE where APPCODE = #{appCode}")
	String getExpandCodeByAppCode(@Param(value = "appCode") String appCode);

	/**
	 * 根据上行扩展码获取应用标识
	 * @param appCode
	 * @return
	 */
	@Select("select APPCODE from SMS_APPCODE_EXPANDCODE where EXPANDCODE = #{expandCode}")
	String getAppCodeByExpandCode(@Param(value = "expandCode") String expandCode);

	/**
	 * 新增短信通道
	 * @param smsConfig 短信通道实体
	 */
	@Insert("insert into SMS_CONFIG(SMS_CHANNEL,SMS_STATE,SMS_TYPE,weight,currentWeight,gmt_create,gmt_modified) "
			+ "values(#{smsConfig.smsChannel},#{smsConfig.smsState},#{smsConfig.smsType},#{smsConfig.weight},#{smsConfig.currentWeight},now(),now())")
	void insertSmsConfig(@Param(value = "smsConfig") SmsConfig smsConfig);

	/**
	 * 根据短信类型和状态获取通道信息
	 * @param status 通道状态，启用还是非启用
	 * @param type 短信类型，通知还是营销
	 * @return 通道信息集合
	 */
	@Select("select SMS_CHANNEL,SMS_STATE,SMS_TYPE,weight,currentWeight from SMS_CONFIG where SMS_STATE=#{status} and SMS_TYPE=#{type} and appCode is null")
	@Results(
			value = {
			@Result(property = "smsChannel", column = "SMS_CHANNEL", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsState", column = "SMS_STATE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsType", column = "SMS_TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "weight", column = "weight", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "currentWeight", column = "currentWeight", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsConfig> listChannelsByStatusType(@Param(value = "status") String status, @Param(value = "type") String type);


	/**
	 * 根据短信类型,状态和appCode获取通道信息
	 * @param status 通道状态，启用还是非启用
	 * @param type 短信类型，通知还是营销
	 * @param appCode 应用标识
	 * @return 通道信息集合
	 */
	@Select("select SMS_CHANNEL,SMS_STATE,SMS_TYPE,weight,currentWeight from SMS_CONFIG where SMS_STATE=#{status} and SMS_TYPE=#{type} and appCode=#{appCode}")
	@Results(
			value = {
			@Result(property = "smsChannel", column = "SMS_CHANNEL", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsState", column = "SMS_STATE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsType", column = "SMS_TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "weight", column = "weight", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "currentWeight", column = "currentWeight", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsConfig> listChannelsByStatusTypeAppCode(@Param(value = "status") String status, @Param(value = "type") String type, @Param(value="appCode") String appCode);



	/**
	 * 清空通道配置表
	 */
	@Delete("delete from SMS_CONFIG")
	void deleteChannelConfig();

	/**
	 * 插入通知类成功记录表
	 * @param smsEntity 短信实体
	 */
	@Insert("insert into SMS_SUCCESS(SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MSGID,"
			+ "unique_key,gmt_create,gmt_modified,type_code,template_code) "
			+ "values(#{smsEntity.to},#{smsEntity.content},#{smsEntity.level},#{smsEntity.time},#{smsEntity.appCode},"
			+ "#{smsEntity.channelNo},#{smsEntity.token},#{smsEntity.ip},#{smsEntity.type},#{smsEntity.msgid},"
			+ "#{smsEntity.uniqueKey},#{smsEntity.createTime},#{smsEntity.modifiedTime}"
			+ ",#{smsEntity.typeCode},#{smsEntity.templateCode})")

	void insertNoticeSuccessRecord(@Param(value = "smsEntity") SmsEntity smsEntity);


	/**
	 * 查询指定时间段内容对应手机号、内容的短信记录
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 短信实体集合
	 */
	@Select("select SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MSGID from SMS_SUCCESS where "
			+ "SMS_TO = #{phone} and CONTENT = #{content} and TIME >= #{startTime} and TIME <= #{endTime}")
	@Results(
		value = {
			@Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
			@Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)
		}
	)
	List<SmsEntity> listNoticeSuccessRecords(@Param(value = "phone") String phone, @Param(value = "content") String content,
			@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

	/**
	 * 插入营销类成功记录表
	 * @param smsEntity
	 */
	@Insert("insert into SMS_SALE_SUCCESS(SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MSGID,"
			+ "unique_key,gmt_create,gmt_modified,type_code,template_code) "
			+ "values(#{smsEntity.to},#{smsEntity.content},#{smsEntity.level},#{smsEntity.time},#{smsEntity.appCode},"
			+ "#{smsEntity.channelNo},#{smsEntity.token},#{smsEntity.ip},#{smsEntity.type},#{smsEntity.msgid},"
			+ "#{smsEntity.uniqueKey},#{smsEntity.createTime},#{smsEntity.modifiedTime}"
			+ ",#{smsEntity.typeCode},#{smsEntity.templateCode})")
	void insertSaleSuccessRecord(@Param(value = "smsEntity") SmsEntity smsEntity);

	/**
	 * 查询指定时间段内容对应手机号、内容的短信记录
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 短信实体集合
	 */
	@Select("select SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MSGID from SMS_SALE_SUCCESS where "
			+ "SMS_TO = #{phone} and CONTENT = #{content} and TIME >= #{startTime} and TIME <= #{endTime}")
	@Results(
		value = {
			@Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
			@Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "msgid", column = "MSGID", javaType = String.class, jdbcType = JdbcType.VARCHAR)
		}
	)
	List<SmsEntity> listSaleSuccessRecords(@Param(value = "phone") String phone, @Param(value = "content") String content,
			@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

	/**
	 * 插入失败表
	 * @param smsEntity
	 */
	@Insert("insert into SMS_FAILURE(SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MEMO,"
			+ "FAIL_COUNT,unique_key,gmt_create,gmt_modified,type_code,template_code) "
			+ "values(#{smsEntity.to},#{smsEntity.content},#{smsEntity.level},#{smsEntity.time},#{smsEntity.appCode},"
			+ "#{smsEntity.channelNo},#{smsEntity.token},#{smsEntity.ip},#{smsEntity.type},#{smsEntity.memo},#{smsEntity.failCount},"
			+ "#{smsEntity.uniqueKey},#{smsEntity.createTime},#{smsEntity.modifiedTime}"
			+ ",#{smsEntity.typeCode},#{smsEntity.templateCode})")
	void insertFailRecord(@Param(value = "smsEntity") SmsEntity smsEntity);

	/**
	 * 查询指定时间段内容对应手机号、内容的短信记录
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 短信实体集合
	 */
	@Select("select ID,SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,FAIL_COUNT,MEMO,type_code,template_code from SMS_FAILURE where "
			+ "SMS_TO = #{phone} and CONTENT = #{content} and TIME >= #{startTime} and TIME <= #{endTime}")
	@Results(
		value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
			@Result(property = "failCount", column = "FAIL_COUNT", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
			@Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
			@Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "templateCode", column = "template_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)
		}
	)
	List<SmsEntity> listFailRecords(@Param(value = "phone") String phone, @Param(value = "content") String content,
			@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

	/**
	 * 删除失败表记录
	 * @param smsEntity
	 */
	@Delete("delete from SMS_FAILURE where ID = #{smsEntity.id}")
	void deleteFailRecord(@Param(value = "smsEntity") SmsEntity smsEntity);

	/**
	 * 更新失败表信息
	 * @param smsEntity
	 */
	@Update("update SMS_FAILURE set FAIL_COUNT = #{smsEntity.failCount},MEMO = #{smsEntity.memo},CHANNEL_NO = #{smsEntity.channelNo} where ID = #{smsEntity.id}")
	void updateFailInfos(@Param(value = "smsEntity") SmsEntity smsEntity);

	/**
	 * 插入失败历史表
	 * @param smsEntity
	 */
	@Insert("insert into SMS_FAILURE_HISTORY(SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MEMO,unique_key,gmt_create,gmt_modified,type_code,template_code) "
			+ "values(#{smsEntity.to},#{smsEntity.content},#{smsEntity.level},#{smsEntity.time},#{smsEntity.appCode},"
			+ "#{smsEntity.channelNo},#{smsEntity.token},#{smsEntity.ip},#{smsEntity.type},#{smsEntity.memo},"
			+ "#{smsEntity.uniqueKey},#{smsEntity.createTime},now(),#{smsEntity.typeCode},#{smsEntity.templateCode})")
	void insertFailHistoryRecord(@Param(value = "smsEntity") SmsEntity smsEntity);

	/**
	 * 查询指定时间段内容对应手机号、内容的短信记录
	 * @param phone 手机号码
	 * @param content 短信内容
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return 短信实体集合
	 */
	@Select("select ID,SMS_TO,CONTENT,SMS_LEVEL,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,TYPE,MEMO from SMS_FAILURE_HISTORY where "
			+ "SMS_TO = #{phone} and CONTENT = #{content} and TIME >= #{startTime} and TIME <= #{endTime}")
	@Results(
		value = {
			@Result(property = "id", column = "ID", javaType = Long.class, jdbcType = JdbcType.BIGINT),
			@Result(property = "to", column = "SMS_TO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "content", column = "CONTENT", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "level", column = "SMS_LEVEL", javaType = SmsSendLevel.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "time", column = "TIME", javaType = Date.class, jdbcType = JdbcType.TIMESTAMP),
			@Result(property = "appCode", column = "APPCODE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "memo", column = "MEMO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "channelNo", column = "CHANNEL_NO", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "token", column = "TOKEN", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "ip", column = "IP", javaType = String.class, jdbcType = JdbcType.VARCHAR),
			@Result(property = "type", column = "TYPE", javaType = SmsSendType.class, jdbcType = JdbcType.VARCHAR)
		}
	)
	List<SmsEntity> listFailHistoryRecords(@Param(value = "phone") String phone, @Param(value = "content") String content,
			@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

	/**
	 * 插入统计信息
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @param channelName 通道名称
	 * @param count 统计条数
	 */
	@Insert("insert into SMS_STATISTICS(begin_time,end_time,ccode,count) values(#{beginTime}, #{endTime}, #{channelName}, #{count})")
	void insertStatisticsCount(@Param(value = "beginTime") String beginTime, @Param(value = "endTime") String endTime,
			@Param(value = "channelName") String channelName, @Param(value = "count") long count);

	/**
	 * 查询短信统计条数
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @param channelName 通道名称
	 */
	@Select("select count from SMS_STATISTICS where begin_time = #{beginTime} and end_time = #{endTime} and ccode = #{channelName}")
	Long countNumberStatistics(@Param(value = "beginTime") String beginTime, @Param(value = "endTime") String endTime,
			@Param(value = "channelName") String channelName);

	/**
	 * 更新统计信息
	 * @param beginTime 开始时间
	 * @param endTime 结束时间
	 * @param channelName 通道名称
	 * @param count 统计条数
	 */
	@Update("update SMS_STATISTICS set count = #{count}, version = version + 1 where ccode = #{channelName} "
			+ "and begin_time = #{beginTime} and end_time = #{endTime}")
	void updateStatisticsCount(@Param(value = "beginTime") String beginTime, @Param(value = "endTime") String endTime,
			@Param(value = "channelName") String channelName, @Param(value = "count") long count);

	/**
	 * 保存appCode和extandCode关系
	 * @param appcode
	 * @param expandCode
	 */
	@Insert("insert into SMS_APPCODE_EXPANDCODE (APPCODE,EXPANDCODE,GMT_CREATE) values (#{appCode},#{expandCode},NOW())")
	void insertAppCodeExpandCode(@Param(value = "appCode") String appCode, @Param(value = "expandCode") String expandCode);






	@Select(" SELECT t1.TYPE_NAME as smsType,t2.RATE as weight,t3.CHANNEL_CODE as channelCode, "
			+ " t3.SMS_CHANNEL as smsChannel from SMS_TYPE t1 , SMS_CHANNEL_RATE t2 ,SMS_CONFIG t3 "
			+ " where  t1.TYPE_CODE=t2.TYPE_CODE and  t2.CHANNEL_CODE=t3.CHANNEL_CODE "
			+ " and t1.TYPE_CODE=#{typeCode}  order by t2.rate desc ")
	@Results(
			value = {
            @Result(property = "smsType", column = "SMS_TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "weight", column = "weight", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "channelCode", column = "channelCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsChannel", column = "smsChannel", javaType = String.class, jdbcType = JdbcType.VARCHAR)
			}
	)
	List<SmsConfig> filterChannelBySmsType(@Param(value = "typeCode") String typeCode);


	@Select(" SELECT t1.TYPE_NAME as smsType,t2.RATE as weight,t3.CHANNEL_CODE as channelCode, "
			+ " t3.SMS_CHANNEL as smsChannel from SMS_TYPE t1 , SMS_CHANNEL_RATE t2 ,SMS_CONFIG t3 "
			+ " where  t1.TYPE_CODE=t2.TYPE_CODE and  t2.CHANNEL_CODE=t3.CHANNEL_CODE "
			+ " and t1.TYPE_CODE=#{typeCode}  and  t3.SMS_STATE='TRUE'  order by t2.rate desc ")
	@Results(
			value = {
            @Result(property = "smsType", column = "SMS_TYPE", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "weight", column = "weight", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "channelCode", column = "channelCode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "smsChannel", column = "smsChannel", javaType = String.class, jdbcType = JdbcType.VARCHAR)
			}
	)
	List<SmsConfig> filterChannelBySmsTypeAndStatus(@Param(value = "typeCode") String typeCode);

	
	@Insert("INSERT INTO `SMS_RATE_STATISTICS` ( `channel_code`, `type_code`, `app_code`, `time`, `send_total`, `send_success_total`, "
			+ "`send_fail_total`, `send_success_rate`, `receive_success_total`, `receive_fail_total`, `receive_success_rate`, `fee_total`,"
			+ " `gmt_create`, `gmt_modified`) VALUES (#{smsRateStatics.channelCode}, #{smsRateStatics.typeCode}, #{smsRateStatics.appCode},"
			+ " #{smsRateStatics.time}, #{smsRateStatics.sendTotal}, #{smsRateStatics.sendSuccessTotal}, #{smsRateStatics.sendFailTotal},"
			+ "  #{smsRateStatics.sendSuccessRate},#{smsRateStatics.receiveSuccessTotal}, #{smsRateStatics.receiveFailTotal}, "
			+ " #{smsRateStatics.receiveSuccessRate}, #{smsRateStatics.feeTotal}, now(),now())")
	void insertRateStatics(@Param(value = "smsRateStatics") SmsRateStatics smsRateStatics);
	
	@Update("UPDATE SMS_RATE_STATISTICS SET `send_total`=#{smsRateStatics.sendTotal}, `send_success_total`=#{smsRateStatics.sendSuccessTotal},"
			+ " `send_fail_total`=#{smsRateStatics.sendFailTotal}, `send_success_rate`=#{smsRateStatics.sendSuccessRate},`receive_success_total`= #{smsRateStatics.receiveSuccessTotal},"
			+ " `receive_fail_total`=#{smsRateStatics.receiveFailTotal}, `receive_success_rate`= #{smsRateStatics.receiveSuccessRate}, `fee_total`=#{smsRateStatics.feeTotal}, "
			+ "`gmt_modified`=now() WHERE (`id`=#{smsRateStatics.id})")
	void updateRateStatics(@Param(value = "smsRateStatics") SmsRateStatics smsRateStatics);
	
	@Select("<script>" + "select id,`channel_code`, `type_code`, `app_code`, `time`, `send_total`, `send_success_total`, "
			+ "`send_fail_total`, `send_success_rate`, `receive_success_total`, `receive_fail_total`, `receive_success_rate`, `fee_total`,"
			+ " `gmt_create`, `gmt_modified` from SMS_RATE_STATISTICS "
			+ "where channel_code=#{channelCode} "
			+ "<if test='typeCode != null and typeCode !=&apos;&apos; '> and type_code=#{typeCode}</if>" 
			+ "<if test='typeCode == null or typeCode ==&apos;&apos; '> and type_code is null</if>" 
			+ "<if test='appCode != null and appCode !=&apos;&apos; '> and app_code=#{appCode}</if>" 
			+ "<if test='appCode == null or appCode ==&apos;&apos; '> and app_code is null</if>" 
			+ " and time=#{time}" + "</script>" )
	@Results(
			value = {
            @Result(property = "id", column = "id", javaType = Integer.class, jdbcType = JdbcType.INTEGER),
            @Result(property = "channelCode", column = "channel_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
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
	SmsRateStatics findSmsRateStaticsByCodeAndTime(@Param(value = "appCode") String appCode,@Param(value = "typeCode") String typeCode,
			@Param(value = "channelCode") String channelCode,@Param(value = "time") Date time);
	
	@Select("select DATE_FORMAT(gmt_create,'%Y-%m-%d') time,appcode,type_code,channel_no,count(*) success_count from SMS_SUCCESS "
			+ "where gmt_create >=#{startTime} and  gmt_create <#{endTime} group by DATE_FORMAT(gmt_create,'%Y-%m-%d'),appcode,type_code,channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "sendSuccessTotal", column = "success_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findNoticeSuccessTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	@Select("select DATE_FORMAT(gmt_create,'%Y-%m-%d') time,appcode,type_code,channel_no,count(*) success_count from SMS_SALE_SUCCESS "
			+ "where gmt_create >=#{startTime} and  gmt_create <#{endTime} group by DATE_FORMAT(gmt_create,'%Y-%m-%d'),appcode,type_code,channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "sendSuccessTotal", column = "success_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findSaleSuccessTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	
	@Select("select DATE_FORMAT(t.gmt_create,'%Y-%m-%d') time,t.appcode,t.type_code type_code,t.channel_no channel_no,count(*) fail_count  from "
			+ "(select gmt_create, appCode,type_code,channel_no,sms_to from SMS_FAILURE_HISTORY  "
			+ "where gmt_create >=#{startTime} and  gmt_create <#{endTime}"
			+ " union all select gmt_create, appCode,type_code,channel_no,sms_to from SMS_FAILURE " 
			+ "where gmt_create >=#{startTime} and  gmt_create <#{endTime}) t "
			+ "group by DATE_FORMAT(gmt_create,'%Y-%m-%d'),appcode,type_code,channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "sendFailTotal", column = "fail_count", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findFailTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	@Select("select DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d') time,t2.appcode,t2.type_code,t1.channel_no, count(*) receive_success_total"
			+ " from SMS_NOTICE_STATUS t1 join SMS_SUCCESS t2 "
			+ "on t1.msgid = t2.msgid "
			+ "where t1.gmt_receive >= #{startTime} and  t1.gmt_receive < #{endTime} "
			+ "and t1.status_code in ('DELIVRD','0') "
			+ "group by DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d'),t2.appcode,t2.type_code,t1.channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "receiveSuccessTotal", column = "receive_success_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findNotiveReceiveSuccessTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	
	@Select("select DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d') time,t2.appcode,t2.type_code,t1.channel_no, count(*) receive_success_total"
			+ " from SMS_NOTICE_STATUS t1 join SMS_SALE_SUCCESS t2 "
			+ "on t1.msgid = t2.msgid "
			+ "where t1.gmt_receive >=#{startTime} and  t1.gmt_receive <#{endTime} "
			+ "and t1.status_code in ('DELIVRD','0') "
			+ "group by DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d'),t2.appcode,t2.type_code,t1.channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "receiveSuccessTotal", column = "receive_success_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findSaleReceiveSuccessTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	

	@Select("select DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d') time,t2.appcode,t2.type_code,t1.channel_no, count(*) receive_fail_total"
			+ " from SMS_NOTICE_STATUS t1 join SMS_SUCCESS t2 "
			+ "on t1.msgid = t2.msgid " 
			+ "where t1.gmt_receive >=#{startTime} and  t1.gmt_receive <#{endTime} "
			+ "and t1.status_code not in ('DELIVRD','0')"
			+ "group by DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d'),t2.appcode,t2.type_code,t1.channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "receiveFailTotal", column = "receive_fail_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findNoticeReceiveFailTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	
	@Select("select DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d') time,t2.appcode,t2.type_code,t1.channel_no, count(*) receive_fail_total"
			+ " from SMS_NOTICE_STATUS t1 join SMS_SALE_SUCCESS t2 "
			+ "on t1.msgid = t2.msgid " 
			+ "where t1.gmt_receive >=#{startTime} and  t1.gmt_receive <#{endTime} "
			+ "and t1.status_code not in ('DELIVRD','0')"
			+ "group by DATE_FORMAT( t1.gmt_receive,'%Y-%m-%d'),t2.appcode,t2.type_code,t1.channel_no")
	@Results(
			value = {
            @Result(property = "channelCode", column = "channel_no", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "typeCode", column = "type_code", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "appCode", column = "appcode", javaType = String.class, jdbcType = JdbcType.VARCHAR),
            @Result(property = "time", column = "time", javaType = Date.class, jdbcType = JdbcType.DATE),
            @Result(property = "receiveFailTotal", column = "receive_fail_total", javaType = Integer.class, jdbcType = JdbcType.INTEGER)
			}
	)
	List<SmsRateStatics> findSaleReceiveFailTotal(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);
	

}