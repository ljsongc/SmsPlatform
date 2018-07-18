package com.pay.sms.console.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.pay.sms.console.util.PropertiesUtils;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * Redis管理器
 * @author chenchen.qi
 *
 */
public class RedisManager{

	private JedisSentinelPool pool;
	
	public RedisManager(){
		
		Set<String> sentinels = new HashSet<String>();
		PropertiesUtils pu = PropertiesUtils.getInstance();
		String hosts = pu.getStringValue("com.pay.sms.console.smsserver.redis.host");
        for(String host : hosts.split(",")){
        	sentinels.add(host);
        }
        
        int minIdle = pu.getIntValue("com.pay.sms.console.smsserver.redis.minIdle");
        int database = pu.getIntValue("com.pay.sms.console.smsserver.redis.database");
		int connectTimeout = pu.getIntValue("com.pay.sms.console.smsserver.redis.connectTimeout");
		int socketTimeout = pu.getIntValue("com.pay.sms.console.smsserver.redis.socketTimeout");
        String master = pu.getStringValue("com.pay.sms.console.smsserver.redis.master");
        String password = pu.getStringValue("com.pay.sms.console.smsserver.redis.password");
        
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(true);

        pool = new JedisSentinelPool(master, sentinels, config, connectTimeout, socketTimeout, password, database);
	}
	
	/**
	 * 删除redis中指定key的数据
	 * @param key
	 * @return
	 */
	public Map<String, String> del(String... key){
        Jedis jedis = null;
        Map<String, String> map = new HashMap<String, String>();//操作结果
        try {
            jedis = this.pool.getResource();
    		jedis.del(key);
            map.put("oper_flag", "success");
        } catch (Exception e) {
        	e.printStackTrace();
        	map.put("oper_flag", "fail");
        	map.put("oper_error", e.getMessage());
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
	} 
	
	/**
	 * 获取key指定value值
	 * @param key
	 * @return
	 */
	public String get(String key){
        Jedis jedis = null;
        try {
            jedis = this.pool.getResource();
            if(!jedis.exists(key)){
            	return null;
            }
            return jedis.get(key);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	} 
	
	/**
	 * 设置key指定value值
	 * @param key
	 * @param value
	 * @return
	 */
	public Map<String, String> set(String key, String value){
		Jedis jedis = null;
        Map<String, String> map = new HashMap<String, String>();//操作结果
        try {
            jedis = this.pool.getResource();
    		jedis.set(key, value);
            map.put("oper_flag", "success");
        } catch (Exception e) {
        	e.printStackTrace();
        	map.put("oper_flag", "fail");
        	map.put("oper_error", e.getMessage());
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
	} 
	
	/**
	 * 设置key的有效期
	 * @param key			key
	 * @param expireTime	单位为秒
	 * @return
	 */
	public Map<String, String> expire(String key, int expireTime){
        Jedis jedis = null;
        Map<String, String> map = new HashMap<String, String>();//操作结果
        try {
            jedis = this.pool.getResource();
        	Long result = jedis.expire(key, expireTime);
        	if(result == 1){
        		map.put("oper_flag", "success");
        	}else if(result == 0){
        		map.put("oper_flag", "fail");
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        	map.put("oper_flag", "fail");
        	map.put("oper_error", e.getMessage());
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
	}

	/**
	 * 模糊匹配key值
	 * @param key
	 * @return
	 */
	public Set<String> keys(String key) {
		Jedis jedis = null;
        try {
            jedis = this.pool.getResource();
            return jedis.keys(key);
        } catch (Exception e) {
        	e.printStackTrace();
        	return null;
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	} 
	
	/**
	 * 读取redis中指定key的值
	 * @param key	标识
	 */
	public Map<String, String> hgetAll(String key){
        Jedis jedis = null;
        Map<String, String> map = null;
        try {
            jedis = this.pool.getResource();
            map = jedis.hgetAll(key);
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
	} 
	
	/**
	 * 设置指定key的指定字段的值
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Map<String, String> hset(String key, String field, String value){
        Jedis jedis = null;
        Map<String, String> map = new HashMap<String, String>();//操作结果
        try {
            jedis = this.pool.getResource();
            jedis.hset(key, field, value);
            map.put("oper_flag", "success");
        } catch (Exception e) {
        	e.printStackTrace();
        	map.put("oper_flag", "fail");
        	map.put("oper_error", e.getMessage());
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return map;
	}
	
	/**
	 * 读取redis中指定key和field的value值
	 * @param key	标识
	 * @param field	字段名
	 */
	public String hget(String key, String field){
        Jedis jedis = null;
        String value = null;
        try {
            jedis = this.pool.getResource();
            value = jedis.hget(key, field);
        } catch (Exception e) {
        	e.printStackTrace();
        }finally{
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
	} 
}
