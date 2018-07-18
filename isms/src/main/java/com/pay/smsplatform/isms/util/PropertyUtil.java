package com.pay.smsplatform.isms.util;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 	属性文件工具类
 * @author zhengzheng.ma
 *
 */
public class PropertyUtil {
	private static Object lock = new Object();	//同步锁
	private String sourceUrl;					//资源文件路径
	private ResourceBundle resourceBundle;		//资源绑定
	private static Map<String,PropertyUtil> propertyMap=new HashMap<String,PropertyUtil>();	//保存已经加载的资源
	   
	private PropertyUtil(String sourceUrl){
		this.sourceUrl = sourceUrl;
		load();
	}
	  
	/**
	 * 	工厂方法
	 * @param sourceUrl
	 * @return
	 */
	public static PropertyUtil getInstance(String sourceUrl){
		if(propertyMap.get(sourceUrl)==null){
			synchronized (lock) {
				if(propertyMap.get(sourceUrl) ==null){
					propertyMap.put(sourceUrl, new PropertyUtil(sourceUrl));
				}
			}
		}
		return propertyMap.get(sourceUrl);
	}
	  
	/**
	 * 	加载资源
	 */
	private void load(){
		try{
			resourceBundle = ResourceBundle.getBundle(sourceUrl);
		}catch(Exception e){
			throw new RuntimeException("sourceUrl = " + sourceUrl + " file load error!",e);
		}
	}
	   
	public String getProperty(String key) {
		return resourceBundle.getString(key);
	}
	
	public Set<String> getKeys(){
		return resourceBundle.keySet();
	}
	
}
