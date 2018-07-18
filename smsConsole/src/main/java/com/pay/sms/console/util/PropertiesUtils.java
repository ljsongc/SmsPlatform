package com.pay.sms.console.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * 配置帮助类
 * @author chenchen.qi
 *
 */
public class PropertiesUtils {
	
	private static Map<String,Object> map = new HashMap<String, Object>();
	private static PropertiesUtils instance = new PropertiesUtils();
	public static PropertiesUtils getInstance(){
		return instance;
	}
	
	public PropertiesUtils(){
		loadProperties();
	}
	
	private void loadProperties(){
		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream("system.properties"));
			Set<Object> keySet = properties.keySet();
			Iterator<Object> it = keySet.iterator();
			while (it.hasNext()) {
				String next = it.next().toString();
				map.put(next, properties.getProperty(next));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Object getValue(String key){
		return map.get(key);
	}
	
	public int getIntValue(String key){
		return Integer.parseInt(this.getStringValue(key));
	}
	
	public boolean getBooleanValue(String key){
		return Boolean.parseBoolean(this.getStringValue(key));
	}
	
	public String getStringValue(String key){
		return map.get(key).toString();
	}
}
