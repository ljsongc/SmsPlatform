package com.pay.smsserver.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.service.SmsConfigService;

public class SmsConfigServiceImpl implements SmsConfigService {

	private Logger logger  = LoggerFactory.getLogger(getClass());
	
	private DataSource dataSource;
	
	@Override
	public List<SmsConfig> list(){
		String sql = "select * from SMS_CONFIG";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs=null;
		List<SmsConfig> list = new ArrayList<SmsConfig>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				SmsConfig sc = new SmsConfig();
				sc.setId(rs.getInt("ID"));
				sc.setSmsChannel(rs.getString("SMS_CHANNEL"));
				sc.setSmsState(rs.getString("SMS_STATE"));
				sc.setSmsType(rs.getString("SMS_TYPE"));
				sc.setWeight(rs.getInt("weight"));
				sc.setCurrentWeight(rs.getInt("currentWeight"));
				sc.setGmtCreate(rs.getTimestamp("gmt_create"));
				sc.setGmtModified(rs.getTimestamp("gmt_modified"));
				list.add(sc);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms config list error {}", e);
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
				logger.error("sms config list close error {}", e1);
			}	
		}
		return null;
	}
	
	@Override
	public List<SmsConfig> list(String state, String type){
		String sql = "select * from SMS_CONFIG where SMS_STATE=? and SMS_TYPE=?";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<SmsConfig> list = new ArrayList<SmsConfig>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setObject(1, state);
			ps.setObject(2, type);
			rs = ps.executeQuery();
			while(rs.next()){
				SmsConfig sc = new SmsConfig();
				sc.setId(rs.getInt("ID"));
				sc.setSmsChannel(rs.getString("SMS_CHANNEL"));
				sc.setSmsState(rs.getString("SMS_STATE"));
				sc.setSmsType(rs.getString("SMS_TYPE"));
				sc.setWeight(rs.getInt("weight"));
				sc.setCurrentWeight(rs.getInt("currentWeight"));
				sc.setGmtCreate(rs.getTimestamp("gmt_create"));
				sc.setGmtModified(rs.getTimestamp("gmt_modified"));
				list.add(sc);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms config list error {}", e);
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
				logger.error("sms config list close error {}", e1);
			}	
		}
		return null;
	}
	
	@Override
	public List<SmsConfig> getSpareList(String state, String type){
		String sql = "select * from SMS_CONFIG where SMS_STATE=? and SMS_TYPE=? and weight != currentWeight";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<SmsConfig> list = new ArrayList<SmsConfig>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setObject(1, state);
			ps.setObject(2, type);
			rs = ps.executeQuery();
			while(rs.next()){
				SmsConfig sc = new SmsConfig();
				sc.setId(rs.getInt("ID"));
				sc.setSmsChannel(rs.getString("SMS_CHANNEL"));
				sc.setSmsState(rs.getString("SMS_STATE"));
				sc.setSmsType(rs.getString("SMS_TYPE"));
				sc.setWeight(rs.getInt("weight"));
				sc.setCurrentWeight(rs.getInt("currentWeight"));
				sc.setGmtCreate(rs.getTimestamp("gmt_create"));
				sc.setGmtModified(rs.getTimestamp("gmt_modified"));
				list.add(sc);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms config list error {}", e);
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
				logger.error("sms config list close error {}", e1);
			}	
		}
		return null;
	}
	
	@Override
	public List<SmsConfig> getMasterList(String state, String type) {
		// TODO Auto-generated method stub
		String sql = "select * from SMS_CONFIG where SMS_STATE=? and SMS_TYPE=? and weight = currentWeight";
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<SmsConfig> list = new ArrayList<SmsConfig>();
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setObject(1, state);
			ps.setObject(2, type);
			rs = ps.executeQuery();
			while(rs.next()){
				SmsConfig sc = new SmsConfig();
				sc.setId(rs.getInt("ID"));
				sc.setSmsChannel(rs.getString("SMS_CHANNEL"));
				sc.setSmsState(rs.getString("SMS_STATE"));
				sc.setSmsType(rs.getString("SMS_TYPE"));
				sc.setWeight(rs.getInt("weight"));
				sc.setCurrentWeight(rs.getInt("currentWeight"));
				sc.setGmtCreate(rs.getTimestamp("gmt_create"));
				sc.setGmtModified(rs.getTimestamp("gmt_modified"));
				list.add(sc);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms config list error {}", e);
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
				logger.error("sms config list close error {}", e1);
			}	
		}
		return null;
	}
	
	@Override
	public void update(SmsConfig smsConfig){
		String sql="update SMS_CONFIG set SMS_STATE=?, gmt_modified = now() where id=?";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement(sql);
			ps.setObject(1, smsConfig.getSmsState());
			ps.setObject(2, smsConfig.getId());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("sms config update error {}", e);
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
				logger.error("sms config update close error {}", e1);
			}	
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
}
