package com.pay.smsserver.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.smsserver.service.ChannelCheckStatusService;

/**
 * 检测通道发送状态实现类
 * @author haoran.liu
 *
 */
public class ChannelCheckStatusServiceImpl implements ChannelCheckStatusService{
	
	private Logger logger  = LoggerFactory.getLogger(getClass());
	
	private DataSource dataSource;
	
	@Override
	public Map<String, String> getSuccessCount(int intervalTime) {
		String sql = "SELECT CHANNEL_NO AS channelName, COUNT(ID) AS count FROM SMS_SUCCESS WHERE TIME > date_add(now(), INTERVAL - ? MINUTE) "
				+ " and TYPE='NOTICE' GROUP BY CHANNEL_NO";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, intervalTime);
			rs = ps.executeQuery();
			while(rs.next()){
				map.put(rs.getString("channelName"), rs.getString("count"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms check channel status, getSuccess count error {}", e);
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
				logger.error("sms check channel status, getSuccess count close error {}", e1);
			}	
		}
		return map;
	}

	@Override
	public Map<String, String> getFailureCount(int intervalTime) {
		String sql = "SELECT CHANNEL_NO AS channelName, COUNT(ID) AS count FROM SMS_FAILURE_HISTORY WHERE TIME > date_add(now(), INTERVAL - ? MINUTE) "
				+ " and TYPE='NOTICE' GROUP BY CHANNEL_NO";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, String> map = new HashMap<String, String>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setInt(1, intervalTime);
			rs = ps.executeQuery();
			while(rs.next()){
				map.put(rs.getString("channelName"), rs.getString("count"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms check channel status, getFailure count error {}", e);
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
				logger.error("sms check channel status, getFailure count close error {}", e1);
			}	
		}
		return map;
	}

	@Override
	public void updateChannelToMaster(String channelName, String status) {
		// TODO Auto-generated method stub
		String sql = "update SMS_CONFIG set currentWeight = weight , gmt_modified = now() where SMS_CHANNEL = ? and SMS_STATE= ?";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, channelName);
			ps.setString(2, status);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms update channel to master error {}", e);
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
				logger.error("sms update channel to master close error {}", e1);
			}	
		}
	}

	@Override
	public void updateChannelToSpare(String channelName, String status) {
		// TODO Auto-generated method stub
		String sql = "update SMS_CONFIG set currentWeight = weight + 1, gmt_modified = now() where SMS_CHANNEL = ? and SMS_STATE = ?";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setString(1, channelName);
			ps.setString(2, status);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms update channel to spare error {}", e);
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
				logger.error("sms update channel to spare close error {}", e1);
			}	
		}
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
