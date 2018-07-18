package com.pay.smsserver.redis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.smsserver.util.PropertiesUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;

/**
 * Redis管理器
 * 
 * @author chenchen.qi
 * 
 */
public class RedisManager {

	private JedisSentinelPool pool;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public RedisManager() {

		Set<String> sentinels = new HashSet<String>();
		PropertiesUtils pu = PropertiesUtils.getInstance();
		String hosts = pu.getStringValue("com.pay.smsplatform.smsserver.redis.host");
		for (String host : hosts.split(",")) {
			sentinels.add(host);
		}

		int minIdle = pu.getIntValue("com.pay.smsplatform.smsserver.redis.minIdle");
		int database = pu.getIntValue("com.pay.smsplatform.smsserver.redis.database");
		int connectTimeout = pu.getIntValue("com.pay.smsplatform.smsserver.redis.connectTimeout");
		int socketTimeout = pu.getIntValue("com.pay.smsplatform.smsserver.redis.socketTimeout");
		String master = pu.getStringValue("com.pay.smsplatform.smsserver.redis.master");
		String password = pu.getStringValue("com.pay.smsplatform.smsserver.redis.password");

		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMinIdle(minIdle);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true); 

		pool = new JedisSentinelPool(master, sentinels, config, connectTimeout, socketTimeout, password, database);
	}
	
	/**
	 * 设置key的value值，如果不存在则设置value为指定值，返回1；存在则不设置value为指定值，返回0
	 * @param key
	 * @param value
	 * @return
	 */
	public Long setnx(String key, String value){
		Jedis jedis = null;
		Long size = null;
		try {
			jedis = this.pool.getResource();
			size = jedis.setnx(key, value);
		} catch (Exception e) {
			this.logger.error("redis setnx key " + key + " value " + value + " error {}", e);
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					this.logger.error("redis setnx key " + key + " value " + value + " jedis close error {}", e);
					e.printStackTrace();
				}
			}
		}
		return size;
	} 
	
	/**
	 * 删除redis中指定key的数据
	 * 
	 * @param key
	 * @return
	 */
	public Map<String, String> del(String... key) {
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();// 操作结果
		try {
			jedis = this.pool.getResource();
			jedis.del(key);
			map.put("oper_flag", "success");
		} catch (Exception e) {
			this.logger.error("redis delete key {} error {}", key, e);
			e.printStackTrace();
			map.put("oper_flag", "fail");
			map.put("oper_error", e.getMessage());
		} finally {
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
	 * 
	 * @param key
	 * @return
	 */
	public String get(String key) {
		Jedis jedis = null;
		try {
			jedis = this.pool.getResource();
			if(!jedis.exists(key)){
				return null;
			}
			return jedis.get(key);
		} catch (Exception e) {
			this.logger.error("redis get key {} by error {}", key, e);
			e.printStackTrace();
			return null;
		} finally {
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
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public Map<String, String> set(String key, String value) {
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();// 操作结果
		try {
			jedis = this.pool.getResource();
			jedis.set(key, value);
			map.put("oper_flag", "success");
		} catch (Exception e) {
			this.logger.error("redis set key {} value {} by error {}", key, value, e);
			e.printStackTrace();
			map.put("oper_flag", "fail");
			map.put("oper_error", e.getMessage());
		} finally {
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
	 * 
	 * @param key
	 *            key
	 * @param expireTime
	 *            单位为秒
	 * @return
	 */
	public Map<String, String> expire(String key, int expireTime) {
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();// 操作结果
		try {
			jedis = this.pool.getResource();
			Long result = jedis.expire(key, expireTime);
			if (result == 1) {
				map.put("oper_flag", "success");
			} else if (result == 0) {
				map.put("oper_flag", "fail");
			}
		} catch (Exception e) {
			this.logger.error("redis expire key {} expireTime {} error {}", key, expireTime, e);
			e.printStackTrace();
			map.put("oper_flag", "fail");
			map.put("oper_error", e.getMessage());
		} finally {
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
	 * 删除set中指定元素
	 * @param key
	 * @param value
	 * @return
	 */
	public Long srem(String key, String value) {
		Jedis jedis = null;
		Long number = -1l;
		try {
			jedis = this.pool.getResource();
			number = jedis.srem(key, value);
		} catch (Exception e) {
			this.logger.error("redis srem key {} value {} error {}", key, value, e);
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return number;
	}

	/**
	 * 为redis的set集合添加数据
	 * 
	 * @param key
	 *            set集合的key
	 * @param value
	 *            set集合的值
	 * @return 添加影响行数
	 */
	public Long sadd(String key, String value) {
		Jedis jedis = null;
		Long number = -1l;
		try {
			jedis = this.pool.getResource();
			number = jedis.sadd(key, value);
		} catch (Exception e) {
			this.logger.error("redis sadd key {} value {} error {}", key, value, e);
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return number;
	}

	/**
	 * 读取set集合所有元素
	 * 
	 * @param key
	 * @return
	 */
	public Set<String> smembers(String key) {
		Jedis jedis = null;
		Set<String> set = null;
		try {
			jedis = this.pool.getResource();
			set = jedis.smembers(key);
		} catch (Exception e) {
			this.logger.error("redis smembers key {} error {}", key, e);
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return set;
	}

	/**
	 * 读取redis中指定key的值
	 * 
	 * @param key
	 *            标识
	 */
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = null;
		Map<String, String> map = null;
		try {
			jedis = this.pool.getResource();
			map = jedis.hgetAll(key);
		} catch (Exception e) {
			this.logger.error("redis hgetAll key {} error {}", key, e);
			e.printStackTrace();
		} finally {
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
	 * 
	 * @param key
	 * @param field
	 * @param value
	 * @return
	 */
	public Map<String, String> hset(String key, String field, String value) {
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();// 操作结果
		try {
			jedis = this.pool.getResource();
			jedis.hset(key, field, value);
			map.put("oper_flag", "success");
		} catch (Exception e) {
			this.logger.error("redis hset key {} field {} value {} error {}", key, field, value, e);
			e.printStackTrace();
			map.put("oper_flag", "fail");
			map.put("oper_error", e.getMessage());
		} finally {
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
	 * 
	 * @param key
	 *            标识
	 * @param field
	 *            字段名
	 */
	public String hget(String key, String field) {
		Jedis jedis = null;
		String value = null;
		try {
			jedis = this.pool.getResource();
			value = jedis.hget(key, field);
		} catch (Exception e) {
			this.logger.error("redis hget key {} field {} error {}", key, field, e);
			e.printStackTrace();
		} finally {
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
	
	public Long incrBy(String key, long count) {
		Jedis jedis = null;
		Long result = null;
		try {
			jedis = this.pool.getResource();
			result = jedis.incrBy(key, count);
		} catch (Exception e) {
			this.logger.error("redis incr key {} error {}", key, e);
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				try {
					jedis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}
}
