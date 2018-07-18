package com.pay.sms.console.sql;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SqlBuilder;

public class DynamicSqlHandle extends SqlBuilder{

	/**
	 * 通知类
	 * @param para
	 * @return
	 */
	public String findSuccessSmsByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` from `SMS_SUCCESS` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by `TIME` desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findSuccessSmsSizeByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from `SMS_SUCCESS` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		return sql.toString();
	}

	/**
	 * 营销类
	 * @param para
	 * @return
	 */
	public String findSaleSuccessSmsByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MSGID` from `SMS_SALE_SUCCESS` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by `TIME` desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findSaleSuccessSmsSizeByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from `SMS_SALE_SUCCESS` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		return sql.toString();
	}

	public String findTokenByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select `id`,`appCode`,`token` from `SMS_TOKEN` where 1=1");

		String appCode = null;
		String token = null;

		if(StringUtils.isNotBlank((String)para.get("appCode"))){
			appCode = String.valueOf(para.get("appCode"));
		}

		if(StringUtils.isNotBlank((String)para.get("token"))){
			token = String.valueOf(para.get("token"));
		}

		if(StringUtils.isNotBlank(appCode)){
			sql.append(" and `appCode` = '").append(appCode).append("' ");
		}

		if(StringUtils.isNotBlank(token)){
			sql.append(" and `token` = '").append(token).append("' ");
		}

		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" limit " + start + "," + size);

		return sql.toString();
	}


	public String findTokenSizeByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from `SMS_TOKEN` where 1=1");

		String appCode = null;
		String token = null;

		if(StringUtils.isNotBlank((String)para.get("appCode"))){
			appCode = String.valueOf(para.get("appCode"));
		}

		if(StringUtils.isNotBlank((String)para.get("token"))){
			token = String.valueOf(para.get("token"));
		}

		if(StringUtils.isNotBlank(appCode)){
			sql.append(" and `appCode` = '").append(appCode).append("' ");
		}

		if(StringUtils.isNotBlank(token)){
			sql.append(" and `token` = '").append(token).append("' ");
		}

		return sql.toString();
	}

	public String findFailureSmsByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` from `SMS_FAILURE` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by `TIME` desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findFailureSmsSizeByCondition(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from `SMS_FAILURE` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		return sql.toString();
	}


	public String findFailHistSmsByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select `ID`,`SMS_TO`,`CONTENT`,`SMS_LEVEL`,`TIME`,`APPCODE`,`CHANNEL_NO`,`TYPE`,`TOKEN`,`IP`,`MEMO` from `SMS_FAILURE_HISTORY` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by `TIME` desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findFailHistSmsSizeByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from `SMS_FAILURE_HISTORY` where 1=1");

		String smsTo = null;
		String content = null;
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));

		sql.append(" and `TIME`>='").append(startTime).append("' and `TIME`<='").append(endTime).append("' ");

		if(StringUtils.isNotBlank((String)para.get("smsTo"))){
			smsTo = String.valueOf(para.get("smsTo"));
		}

		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}

		if(StringUtils.isNotBlank(smsTo)){
			sql.append(" and `SMS_TO` = '").append(smsTo).append("' ");
		}

		if(StringUtils.isNotBlank(content)){
			sql.append(" and `CONTENT` like '%").append(content).append("%' ");
		}

		return sql.toString();
	}

	public String findCallbackNoticeSmsByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type  ")
			.append(" from (select * from SMS_NOTICE_STATUS where gmt_create >='").append(para.get("startTime"))
			.append("' and gmt_create<='").append(para.get("endTime"))
			.append("' ) s1, SMS_SUCCESS s2 ");
		String phone = null;
		String msgid = null;

		if(StringUtils.isNotBlank((String)para.get("phone"))){
			phone = String.valueOf(para.get("phone"));
		}

		if(StringUtils.isNotBlank((String)para.get("msgid"))){
			msgid = String.valueOf(para.get("msgid"));
		}

		//只要有查询
		if(StringUtils.isNotBlank(msgid) || StringUtils.isNotBlank(phone)){
			sql.append(" where ");
		}else{
			sql.append(" where s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		//仅查询msgid
		if(StringUtils.isNotBlank(msgid) && !StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid).append("' and s1.phone=s2.sms_to ");
		}

		//仅查询phone
		if(StringUtils.isNotBlank(phone) && !StringUtils.isNotBlank(msgid)){
			sql.append(" s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("' and s1.msgid=s2.msgid ");
		}

		//查询msgid和phone
		if(StringUtils.isNotBlank(msgid) && StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid)
				.append("' and s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("'")
				.append(" and s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}


		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by s1.gmt_create desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findCallbackNoticeSmsSizeByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) ")
			.append(" from (select * from SMS_NOTICE_STATUS where gmt_create >='").append(para.get("startTime"))
			.append("' and gmt_create<='").append(para.get("endTime"))
			.append("' ) s1, SMS_SUCCESS s2 ");
		String phone = null;
		String msgid = null;

		if(StringUtils.isNotBlank((String)para.get("phone"))){
			phone = String.valueOf(para.get("phone"));
		}

		if(StringUtils.isNotBlank((String)para.get("msgid"))){
			msgid = String.valueOf(para.get("msgid"));
		}

		//只要有查询
		if(StringUtils.isNotBlank(msgid) || StringUtils.isNotBlank(phone)){
			sql.append(" where ");
		}else{
			sql.append(" where s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		//仅查询msgid
		if(StringUtils.isNotBlank(msgid) && !StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid).append("' and s1.phone=s2.sms_to ");
		}

		//仅查询phone
		if(StringUtils.isNotBlank(phone) && !StringUtils.isNotBlank(msgid)){
			sql.append(" s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("' and s1.msgid=s2.msgid ");
		}

		//查询msgid和phone
		if(StringUtils.isNotBlank(msgid) && StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid)
				.append("' and s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("'")
				.append(" and s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		return sql.toString();
	}

	public String findCallbackSaleSmsByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select s1.id,s1.msgid,s1.phone,s1.status_code,s1.description,s1.channel_no,s1.gmt_create,s1.gmt_receive,s2.content,s2.type  ")
			.append(" from (select * from SMS_SALE_STATUS where gmt_create >='").append(para.get("startTime"))
			.append("' and gmt_create<='").append(para.get("endTime"))
			.append("' ) s1, SMS_SALE_SUCCESS s2 ");
		String phone = null;
		String msgid = null;

		if(StringUtils.isNotBlank((String)para.get("phone"))){
			phone = String.valueOf(para.get("phone"));
		}

		if(StringUtils.isNotBlank((String)para.get("msgid"))){
			msgid = String.valueOf(para.get("msgid"));
		}

		//只要有查询
		if(StringUtils.isNotBlank(msgid) || StringUtils.isNotBlank(phone)){
			sql.append(" where ");
		}else{
			sql.append(" where s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		//仅查询msgid
		if(StringUtils.isNotBlank(msgid) && !StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid).append("' and s1.phone=s2.sms_to ");
		}

		//仅查询phone
		if(StringUtils.isNotBlank(phone) && !StringUtils.isNotBlank(msgid)){
			sql.append(" s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("' and s1.msgid=s2.msgid ");
		}

		//查询msgid和phone
		if(StringUtils.isNotBlank(msgid) && StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid)
				.append("' and s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("'")
				.append(" and s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}


		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("size"));
		sql.append(" order by s1.gmt_create desc limit " + start + "," + size);

		return sql.toString();
	}

	public String findCallbackSaleSmsSizeByCond(Map<String, Object> para) {

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) ")
			.append(" from (select * from SMS_SALE_STATUS where gmt_create >='").append(para.get("startTime"))
			.append("' and gmt_create<='").append(para.get("endTime"))
			.append("' ) s1, SMS_SALE_SUCCESS s2 ");
		String phone = null;
		String msgid = null;

		if(StringUtils.isNotBlank((String)para.get("phone"))){
			phone = String.valueOf(para.get("phone"));
		}

		if(StringUtils.isNotBlank((String)para.get("msgid"))){
			msgid = String.valueOf(para.get("msgid"));
		}

		//只要有查询
		if(StringUtils.isNotBlank(msgid) || StringUtils.isNotBlank(phone)){
			sql.append(" where ");
		}else{
			sql.append(" where s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		//仅查询msgid
		if(StringUtils.isNotBlank(msgid) && !StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid).append("' and s1.phone=s2.sms_to ");
		}

		//仅查询phone
		if(StringUtils.isNotBlank(phone) && !StringUtils.isNotBlank(msgid)){
			sql.append(" s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("' and s1.msgid=s2.msgid ");
		}

		//查询msgid和phone
		if(StringUtils.isNotBlank(msgid) && StringUtils.isNotBlank(phone)){
			sql.append(" s1.msgid='").append(msgid).append("' and s2.msgid='").append(msgid)
				.append("' and s1.phone='").append(phone).append("' and s2.sms_to='").append(phone).append("'")
				.append(" and s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		}

		return sql.toString();
	}

	/**
	 * echart图表,返回营销短信数据
	 * @param para
	 * @return
	 */
	public String getSaleCallbackChartData(Map<String, Object> para){
		StringBuffer sql = new StringBuffer("select s1.status_code , count(*) as value ")
			.append(" from (select msgid, phone, status_code from SMS_SALE_STATUS where gmt_create >='").append(para.get("startTime"))
			.append("' and gmt_create<='").append(para.get("endTime"))
			.append("' ) s1, SMS_SALE_SUCCESS s2")
			.append(" where s1.msgid = s2.msgid and s1.phone = s2.sms_to ");
		String content = null;
		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}
		if(StringUtils.isNotBlank(content)){
			sql.append(" and s2.content like '%").append(content).append("%'");
		}
		sql.append(" group by s1.status_code");
		return sql.toString();
	}

	/**
	 * 获得营销短信发送数量
	 * @param para
	 * @return
	 */
	public String getSaleCount(Map<String, Object> para){
		StringBuffer sql = new StringBuffer("select count(*) ")
			.append(" from SMS_SALE_SUCCESS where time >='").append(para.get("startTime"))
			.append("' and time<='").append(para.get("endTime")).append("' ");
		String content = null;
		if(StringUtils.isNotBlank((String)para.get("content"))){
			content = String.valueOf(para.get("content"));
		}
		if(StringUtils.isNotBlank(content)){
			sql.append(" and content like '%").append(content).append("%'");
		}
		return sql.toString();
	}

	public String pageListSmsType(Map<String, Object> para) {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT t1.TYPE_NAME,t1.REMARK,t1.OPERATOR, t1.create_time,"
				+ " GROUP_CONCAT(concat(t3.SMS_CHANNEL,':',t2.RATE) )as rateRemark,t1.TYPE_CODE ");
		sql.append(" from SMS_TYPE t1 ,SMS_CHANNEL_RATE t2,SMS_CONFIG t3 ");
		sql.append(" where t1.TYPE_CODE=t2.TYPE_CODE and t2.CHANNEL_CODE = t3.CHANNEL_CODE ");
		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));
		String start = String.valueOf(para.get("start"));
		String size = String.valueOf(para.get("pageSize"));
		String typeCode = String.valueOf(para.get("typeCode"));


		if(StringUtils.isNotBlank(typeCode) && !"null".equals(typeCode)){
			sql.append(" and t1.type_code = '").append(typeCode).append("' ");
		}

		if(StringUtils.isNotBlank(startTime)&& !"null".equals(startTime)){
			sql.append(" and t1.create_time>= '").append(startTime).append("' ");
		}

		if(StringUtils.isNotBlank(endTime) && !"null".equals(endTime)){
			sql.append(" and t1.create_time<= '").append(endTime).append("' ");
		}
		sql.append(" GROUP BY t1.TYPE_CODE   ");
		sql.append(" order by t1.create_time desc limit " + start + "," + size);
		return sql.toString();

	}


	public String pageListSmsTypeCount(Map<String, Object> para) {
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(distinct t1.type_code) ");
		sql.append(" from SMS_TYPE t1 ,SMS_CHANNEL_RATE t2,SMS_CONFIG t3 ");
		sql.append(" where t1.TYPE_CODE=t2.TYPE_CODE and t2.CHANNEL_CODE = t3.CHANNEL_CODE ");

		String startTime = String.valueOf(para.get("startTime"));
		String endTime = String.valueOf(para.get("endTime"));
		String typeCode = String.valueOf(para.get("typeCode"));


		if(StringUtils.isNotBlank(typeCode) && !"null".equals(typeCode)){
			sql.append(" and t1.type_code = '").append(typeCode).append("' ");
		}

		if(StringUtils.isNotBlank(startTime) && !"null".equals(startTime) ){
			sql.append(" and t1.create_time>= '").append(startTime).append("' ");
		}

		if(StringUtils.isNotBlank(endTime) && !"null".equals(endTime)){
			sql.append(" and t1.create_time<= '").append(endTime).append("' ");
		}
		return sql.toString();
	}
	public String checksms(Map<String, Object> para) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.TYPE_CODE,t.TYPE_NAME,t.REMARK,t.OPERATOR,t.CREATE_TIME,"
				+ "t.UPDATE_TIME from SMS_TYPE t where 1=1 ");

		String typeCode = String.valueOf(para.get("typeCode"));
		String typeName = String.valueOf(para.get("typeName"));


		if(StringUtils.isNotBlank(typeCode)){
			sql.append(" and type_code != '").append(typeCode).append("' ");
		}

		if(StringUtils.isNotBlank(typeName)){
			sql.append(" and type_name= '").append(typeName).append("' ");
		}


		return sql.toString();


	}


}
