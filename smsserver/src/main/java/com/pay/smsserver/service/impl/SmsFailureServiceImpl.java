package com.pay.smsserver.service.impl;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.service.SmsFailureService;

/**
 * 	
 * @author zhengzheng.ma
 *
 */
public class SmsFailureServiceImpl implements SmsFailureService {
	
	private Logger logger  = LoggerFactory.getLogger(getClass());
	
	private DataSource dataSource;
	
	private ChannelMapper channelMapper;
	
	/**
	 * 查询可以失败重试的sms
	 */
	@Override
	public List<SmsEntity> getRetrySmsEntityList(int total, String[] retryCodes){
		StringBuffer param = new StringBuffer();
		List<SmsEntity> list = new ArrayList<SmsEntity>();
		SmsEntity entity = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		for(int i = 0; i < retryCodes.length; i++){
			param.append("MEMO like'%").append(retryCodes[i]).append("%'");
			if(i != retryCodes.length - 1){
				param.append(" or ");
			}
		}
		String sql = "select * from SMS_FAILURE where " + param.toString() + " order by TIME DESC"
				+ " limit 0," + total;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				entity = new SmsEntity();
				entity.setId(rs.getLong("ID"));
				entity.setTo(rs.getString("SMS_TO"));
				entity.setContent(rs.getString("CONTENT"));
				entity.setLevel(SmsSendLevel.valueOf(rs.getString("SMS_LEVEL")));
				entity.setTime(rs.getDate("TIME"));
				entity.setAppCode(rs.getString("APPCODE"));
				entity.setType(SmsSendType.valueOf(rs.getString("TYPE")));
				entity.setToken(rs.getString("TOKEN"));
				entity.setIp(rs.getString("IP"));
				entity.setChannelNo(rs.getString("CHANNEL_NO"));
				entity.setFailCount(rs.getInt("FAIL_COUNT"));
				entity.setMemo(rs.getString("MEMO"));
				entity.setTemplateCode(rs.getString("TEMPLATE_CODE"));
				entity.setTypeCode(rs.getString("TYPE_CODE"));
				entity.setUniqueKey(rs.getString("UNIQUE_KEY"));
				String string = rs.getString("GMT_CREATE");
				logger.info("sms consloe gmt_create={}", string);
				try {
					Date createTime = DateUtils.parseDate(string, new String[]{"yyyy-MM-dd HH:mm:ss.SSS"});
					entity.setCreateTime(createTime);
				} catch (ParseException e) {
					logger.error("sms consloe parse date error", e);
				}
				list.add(entity);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("smsFailureService getRetrySmsEntityList find error {}", e);
		}finally{
			try {
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(con != null){
					con.close();
					con = null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error("smsFailureService getRetrySmsEntityList close error {}close error {}", e1);
			}	
		}
		return null;
	}
	
	@Override
	public boolean addSmsFailCount(SmsEntity smsEntity){
		Connection con = null;
		PreparedStatement ps = null;
		String sql = "update SMS_FAILURE set FAIL_COUNT = ?,MEMO = ?,CHANNEL_NO = ? where ID = ?";
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, smsEntity.getFailCount());
			ps.setString(2, smsEntity.getMemo());
			ps.setString(3, smsEntity.getChannelNo());
			ps.setLong(4, smsEntity.getId());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("smsFailureService addSmsFailCount error {}", e);
		}finally{
			try {
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(con != null){
					con.close();
					con = null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error("smsFailureService getRetrySmsEntityList close error {}close error {}", e1);
			}	
		}
		return false;
	}
	
	@Override
	public boolean insert(SmsEntity smsEntity){
		String sql = "insert into SMS_FAILURE(SMS_TO,CONTENT,SMS_LEVEL,MEMO,TIME,APPCODE,CHANNEL_NO,TOKEN,IP,REDIS_KEY,TYPE) values(?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, smsEntity.getTo());
			ps.setString(2, smsEntity.getContent());
			ps.setString(3, smsEntity.getLevel().name());
			ps.setString(4, smsEntity.getMemo());
			ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			ps.setString(6, smsEntity.getAppCode());
			ps.setString(7, smsEntity.getChannelNo());
			ps.setString(8, smsEntity.getToken());
			ps.setString(9, smsEntity.getIp());
			ps.setString(10, "");
			ps.setString(11, smsEntity.getType().name());
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms failure insert error {}", e);
		}finally{
			try {
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(con != null){
					con.close();
					con = null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error("sms failure insert close error {}", e1);
			}	
			
		}
		return false;
	}
	
	public boolean delete(SmsEntity smsEntity) {
		String sql = "delete from SMS_FAILURE where ID = '" + smsEntity.getId() + "'";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.executeUpdate();	
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms failure delete  {} error {}", smsEntity.getId(), e);
		}finally{
			try {
				if(ps != null){
					ps.close();
					ps = null;
				}
				if(con != null){
					con.close();
					con = null;
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
				logger.error("sms failure delete close error {}", e1);
			}	
			
		}
		return false;
	}
	
	//@Override
//	public boolean delete(String redisKey) {
//		String sql = "delete from SMS_FAILURE where REDIS_KEY = '" + redisKey + "'";
//		Connection con = null;
//		PreparedStatement ps = null;
//		try {
//			con = dataSource.getConnection();
//			ps = con.prepareStatement(sql);
//			ps.executeUpdate();	
//			return true;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			logger.error("sms failure delete  {} error {}", redisKey, e);
//		}finally{
//			try {
//				if(ps != null){
//					ps.close();
//					ps = null;
//				}
//				if(con != null){
//					con.close();
//					con = null;
//				}
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//				logger.error("sms failure delete close error {}", e1);
//			}	
//			
//		}
//		return false;
//		
//	}
	@Override
	public void insertHistory(SmsEntity smsEntity) {
		channelMapper.insertFailHistoryRecord(smsEntity);
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setChannelMapper(ChannelMapper channelMapper) {
		this.channelMapper = channelMapper;
	}


}

