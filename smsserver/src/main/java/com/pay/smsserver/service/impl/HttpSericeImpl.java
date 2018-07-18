package com.pay.smsserver.service.impl;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.service.HttpService;

public class HttpSericeImpl implements HttpService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public String post(Map<String, String> param) {
		
		String resultString = null;  
		if(param == null){
			JSONObject responseJson = new JSONObject();
            responseJson.put("result", -1);
            responseJson.put("errmsg", "参数为空");
            resultString = responseJson.toString();
            return resultString;
		}
		
    	String url = param.get(SmsConstants.SEND_URL);
    	param.remove(SmsConstants.SEND_URL);
    	String proxyIp = param.get(SmsConstants.PROXY_IP);
    	param.remove(SmsConstants.PROXY_IP);
    	Integer proxyPort = param.get(SmsConstants.PROXY_PORT) != null ? Integer.parseInt(param.get(SmsConstants.PROXY_PORT)) : null;
    	param.remove(SmsConstants.PROXY_PORT);
    	String requestCharset = param.get(SmsConstants.REQUEST_CHARSET);
    	param.remove(SmsConstants.REQUEST_CHARSET);
    	String responseCharset =  param.get(SmsConstants.RESPONSE_CHARSET);
    	param.remove(SmsConstants.RESPONSE_CHARSET);
    	Integer connectTimeOut = param.get(SmsConstants.CONNECT_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.CONNECT_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.CONNECT_TIME_OUT);
    	Integer socketTimeOut = param.get(SmsConstants.SOCKET_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.SOCKET_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.SOCKET_TIME_OUT);
    	
    	CloseableHttpResponse response = null; 
    	CloseableHttpClient httpClient = null;
        try {  
        	httpClient = HttpClients.createDefault();  
            HttpPost httpPost = new HttpPost(url);  
            if (param != null) {  
                List<NameValuePair> paramList = new ArrayList<>();  
                for (String key : param.keySet()) {  
                    paramList.add(new BasicNameValuePair(key, param.get(key)));  
                }  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, requestCharset);  
                httpPost.setEntity(entity);  
            } 
            RequestConfig config = null;
            if(!StringUtils.isEmpty(proxyIp)  && proxyPort != null && proxyPort > 0){
            	HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
            	config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }else{
            	config = RequestConfig.custom().setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }
			httpPost.setConfig(config);
            response = httpClient.execute(httpPost);  
            if (response.getStatusLine().getStatusCode() == 200) {  
                resultString = EntityUtils.toString(response.getEntity(), responseCharset);  
            }else{
            	logger.warn("post url={} param={} response statusCode={}", url, param, response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {  
            logger.error("post url={} param={} error", url, param, e);
        } finally {  
            try {  
            	if(response != null){
            		response.close();  
            	}
            } catch (IOException e) {  
                logger.error("post url={} param={} response close error", url, param, e);
            } finally{
            	response = null;
            }
            try {
				if(httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				logger.error("post url={} param={} httpClient close error", url, param, e);
			} finally{
				httpClient = null;
			}
        }  
        return resultString;  
	}

	@Override
	public String postJSON(Map<String, String> param) {
		
		String resultString = null;  
		if(param == null){
			JSONObject responseJson = new JSONObject();
            responseJson.put("result", -1);
            responseJson.put("errmsg", "参数为空");
            resultString = responseJson.toString();
            return resultString;
		}
		
		String url = param.get(SmsConstants.SEND_URL);
		param.remove(SmsConstants.SEND_URL);
    	String proxyIp = param.get(SmsConstants.PROXY_IP);
    	param.remove(SmsConstants.PROXY_IP);
    	Integer proxyPort = param.get(SmsConstants.PROXY_PORT) != null ? Integer.parseInt(param.get(SmsConstants.PROXY_PORT)) : null;
    	param.remove(SmsConstants.PROXY_PORT);
    	String responseCharset =  param.get(SmsConstants.RESPONSE_CHARSET);
    	param.remove(SmsConstants.RESPONSE_CHARSET);
    	Integer connectTimeOut = param.get(SmsConstants.CONNECT_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.CONNECT_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.CONNECT_TIME_OUT);
    	Integer socketTimeOut = param.get(SmsConstants.SOCKET_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.SOCKET_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.SOCKET_TIME_OUT);
		String json = param.get(SmsConstants.JSON);
		param.remove(SmsConstants.JSON);
    	
		CloseableHttpResponse response = null;  
		CloseableHttpClient httpClient = null;
        try {  
        	httpClient = HttpClients.createDefault();  
            HttpPost httpPost = new HttpPost(url);  
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            RequestConfig config = null;
            if(!StringUtils.isEmpty(proxyIp)  && proxyPort != null && proxyPort > 0){
            	HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
            	config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }else{
            	config = RequestConfig.custom().setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }
			httpPost.setConfig(config);
            httpPost.setEntity(entity);  
            response = httpClient.execute(httpPost);  
            if (response.getStatusLine().getStatusCode() == 200) {  
            	resultString = EntityUtils.toString(response.getEntity(), responseCharset);  
            }else{
            	logger.warn("postJson url={} param={} response statusCode={}", url, json, response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {  
            logger.error("postJson url={} param={} error", url, json, e);
        } finally {  
        	try {  
            	if(response != null){
            		response.close();  
            	}
            } catch (IOException e) {  
                logger.error("postJson url={} param={} response close error", url, json, e);
            } finally{
            	response = null;
            } 
        	try {
				if(httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				logger.error("postJson url={} param={} httpClient close error", url, param, e);
			} finally{
				httpClient = null;
			}
        }  
        return resultString;   
	}

	@Override
	public String get(Map<String, String> param) {
		
		String resultString = null;  
		if(param == null){
			JSONObject responseJson = new JSONObject();
            responseJson.put("result", -1);
            responseJson.put("errmsg", "参数为空");
            resultString = responseJson.toString();
            return resultString;
		}
		
		String url = param.get(SmsConstants.SEND_URL);
		param.remove(SmsConstants.SEND_URL);
    	String proxyIp = param.get(SmsConstants.PROXY_IP);
    	param.remove(SmsConstants.PROXY_IP);
    	Integer proxyPort = param.get(SmsConstants.PROXY_PORT) != null ? Integer.parseInt(param.get(SmsConstants.PROXY_PORT)) : null;
    	param.remove(SmsConstants.PROXY_PORT);
    	String responseCharset =  param.get(SmsConstants.RESPONSE_CHARSET);
    	param.remove(SmsConstants.RESPONSE_CHARSET);
    	Integer connectTimeOut = param.get(SmsConstants.CONNECT_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.CONNECT_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.CONNECT_TIME_OUT);
    	Integer socketTimeOut = param.get(SmsConstants.SOCKET_TIME_OUT) != null ? Integer.parseInt(param.get(SmsConstants.SOCKET_TIME_OUT)) : 5000;
    	param.remove(SmsConstants.SOCKET_TIME_OUT);
    	
    	CloseableHttpResponse response = null;  
    	CloseableHttpClient httpClient = null;
        try {  
        	httpClient = HttpClients.createDefault();
            URIBuilder builder = new URIBuilder(url);  
            if (param != null) {  
                for (String key : param.keySet()) {  
                    builder.addParameter(key, param.get(key));  
                }  
            }  
            URI uri = builder.build();  
            HttpGet httpGet = new HttpGet(uri);
            RequestConfig config = null;
            if(!StringUtils.isEmpty(proxyIp)  && proxyPort != null && proxyPort > 0){
            	HttpHost proxy = new HttpHost(proxyIp, proxyPort, "http");
            	config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }else{
            	config = RequestConfig.custom().setConnectTimeout(connectTimeOut)
            			.setSocketTimeout(socketTimeOut).build();
            }
            httpGet.setConfig(config);  
            response = httpClient.execute(httpGet);  
            if (response.getStatusLine().getStatusCode() == 200) {  
                resultString = EntityUtils.toString(response.getEntity(), responseCharset);  
            }else{
            	logger.warn("get url={} param={} response statusCode={}", url, param, response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            logger.error("get url={} param={} error", url, param, e);
        } finally {  
        	try {  
            	if(response != null){
            		response.close();  
            	}
            } catch (IOException e) {  
                logger.error("get url={} param={} response close error", url, param, e);
            } finally{
            	response = null;
            }
        	try {
				if(httpClient != null){
					httpClient.close();
				}
			} catch (IOException e) {
				logger.error("get url={} param={} httpClient close error", url, param, e);
			} finally{
				httpClient = null;
			}
        }  
        return resultString;  
	}

}
