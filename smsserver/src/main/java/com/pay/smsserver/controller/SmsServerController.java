package com.pay.smsserver.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.pay.dsp.common.biz.model.ReturnT;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.astrotrain.CamelAstrotrainProducer;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.channel.MessageChannelContext;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.service.HttpService;
import com.pay.smsserver.service.SmsConfigService;

@Controller
@RequestMapping(value="/sms")
public class SmsServerController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Value("#{configProperties['com.pay.smsserver.token.server.auth.url']}")
    private String authUrl;

	@Value("#{configProperties['com.pay.smsserver.app.name']}")
	private String dataSourceName;

	@Autowired
	private SmsConfigService smsConfigService;
	@Autowired
	private HttpService httpService;
	@Autowired
	private MessageChannelContext messageChannelContext;
	@Autowired
	private CamelAstrotrainProducer producer;
	@Autowired
	private ChannelService channelService;

	@Autowired
	private RedisManager redisManager;

	@RequestMapping(value="config/list", method={RequestMethod.GET})
	@ResponseBody
	public List<SmsConfig> configList(HttpServletRequest request,@RequestParam("token") String token){
		// 获取请求客户端的ip，即控制台端ip
		List<SmsConfig> list = null;
		String consoleIp = null;
		if (request.getHeader("x-forwarded-for") == null) {
			consoleIp = request.getRemoteAddr();
		} else {
			consoleIp = request.getHeader("x-forwarded-for");
		}

		logger.info("request smsConfig list by token=" + token + "&consoleIp=" + consoleIp);

		String responseStr = "no";

		try {
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("appName", dataSourceName);
			paraMap.put("consoleIp", consoleIp);
			paraMap.put("token", token);
			paraMap.put(SmsConstants.SEND_URL, authUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, "UTF-8");
			paraMap.put(SmsConstants.RESPONSE_CHARSET, "UTF-8");
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(10000));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(10000));
			responseStr = httpService.get(paraMap);
			JSONObject jsonObject = new JSONObject(responseStr);
			responseStr = jsonObject.getString("msg");
			if ("success".equals(responseStr)) {
				list = smsConfigService.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@RequestMapping(value="config/update", method={RequestMethod.GET})
	@ResponseBody
	public List<SmsConfig> configUpdate(HttpServletRequest request,@RequestParam("token") String token,@RequestParam("sales") String sales,@RequestParam("notices") String notices){
		// 获取请求客户端的ip，即控制台端ip
		List<SmsConfig> list = null;
		String consoleIp = null;
		if (request.getHeader("x-forwarded-for") == null) {
			consoleIp = request.getRemoteAddr();
		} else {
			consoleIp = request.getHeader("x-forwarded-for");
		}

		logger.info("request smsConfig update by token=" + token + "&consoleIp=" + consoleIp);

		String responseStr = "no";

		try {
			Map<String,String> paraMap = new HashMap<String,String>();
			paraMap.put("appName", dataSourceName);
			paraMap.put("consoleIp", consoleIp);
			paraMap.put("token", token);
			paraMap.put(SmsConstants.SEND_URL, authUrl);
			paraMap.put(SmsConstants.REQUEST_CHARSET, "UTF-8");
			paraMap.put(SmsConstants.RESPONSE_CHARSET, "UTF-8");
			paraMap.put(SmsConstants.CONNECT_TIME_OUT, String.valueOf(10000));
			paraMap.put(SmsConstants.SOCKET_TIME_OUT, String.valueOf(10000));
			responseStr = httpService.get(paraMap);
			JSONObject jsonObject = new JSONObject(responseStr);
			responseStr = jsonObject.getString("msg");
			if ("success".equals(responseStr)) {
				list = smsConfigService.list();
				String[] notice = notices.split("_");
				String[] sale = sales.split("_");
				for (SmsConfig smsConfig : list) {
					smsConfig.setSmsState("FALSE");
					if(smsConfig.getSmsType().equals("NOTICE")){
						for (String n : notice) {
							if(n.equals(smsConfig.getSmsChannel())){
								smsConfig.setSmsState("TRUE");
								break;
							}
						}
					}else if(smsConfig.getSmsType().equals("SALE")){
						for (String s : sale) {
							if(s.equals(smsConfig.getSmsChannel())){
								smsConfig.setSmsState("TRUE");
								break;
							}
						}
					}
					this.smsConfigService.update(smsConfig);
				}
				list = smsConfigService.list();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@RequestMapping(value="message/send", method={RequestMethod.GET})
	@ResponseBody
	public String sendMessage(String phone,String content,String channelName){
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setAppCode("zabbix");
		smsEntity.setChannelNo(channelName);
		smsEntity.setTo(phone);
		try {
			String encode = URLEncoder.encode(content,"ISO8859-1");
			encode = URLDecoder.decode(encode, "UTF-8");
			smsEntity.setContent(encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(channelName.contains("Sale")){
			smsEntity.setType(SmsSendType.SALE);
		}else{
			smsEntity.setType(SmsSendType.NOTICE);
		}
		smsEntity.setLevel(SmsSendLevel.NORMAL);
		logger.info("smsEntity={}",smsEntity);
		MessageChannel messageChannel = messageChannelContext.getChannel(channelName);
		List<MessageResponse> messageResponses = messageChannel.sendMessage(smsEntity);
		return messageResponses.toString();
	}

	@RequestMapping(value="send/mq", method={RequestMethod.GET})
	@ResponseBody
	public String sendMessage(String uniqueKey){
		 Date date = new Date();
		producer.sendStringMessage(uniqueKey, date);
		logger.info("producer send msg uniqueKey={},receiveTime={}",uniqueKey,date);
		return "success";
	}

	@RequestMapping(value="isOpen", method={RequestMethod.GET})
	@ResponseBody
	public String setisOpen(String isOpen){
		redisManager.set(SmsConfig.SMS_IS_OPEN_KEY, isOpen);
		logger.info("producer setisOpen={}",isOpen);
		return "success";
	}
	
	
	@RequestMapping(value="staticalSendRate", method={RequestMethod.GET})
	@ResponseBody
	public String StaticalSendRate(String day){
		try {
			logger.info("staticalSendRate begin, day={},",day);
			Date date = new Date();
			Calendar calendar = Calendar.getInstance();  
			calendar.setTime(date);  
			calendar.add(Calendar.DAY_OF_MONTH, -1); 
			date = calendar.getTime(); 
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String end = sdf.format(date);
			Date endTime = sdf.parse(end);
			
			Calendar calendar2 = Calendar.getInstance();  
			calendar2.setTime(date);  
			Integer integer = Integer.valueOf(day);
			calendar2.add(Calendar.DAY_OF_MONTH, -integer); 
			date = calendar2.getTime(); 
			String start = sdf.format(date);
			Date startTime = sdf.parse(start);
			logger.info("staticalSendRate begin, startTime={},endTime={}",startTime,endTime);
			channelService.StatisticalSuccessRate(startTime, endTime);
			logger.info("staticalSendRate end");
		} catch (ParseException e) {
			logger.error("staticalSendRate date parse eror",e);
		}
		return "success";
	}

}
