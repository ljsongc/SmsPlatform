package com.pay.sms.console.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.AssertionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.sms.console.bean.SmsCallbackEntity;
import com.pay.sms.console.bean.SmsChannelEntity;
import com.pay.sms.console.bean.SmsRateStatics;
import com.pay.sms.console.bean.SmsToken;
import com.pay.sms.console.bean.SmsType;
import com.pay.sms.console.enums.Constants;
import com.pay.sms.console.redis.RedisManager;
import com.pay.sms.console.service.SmsConsoleService;
import com.pay.sms.console.web.util.Page;
import com.pay.sms.console.web.util.PageUtil;
import com.pay.smsplatform.isms.util.DateUtil;

@Controller
public class SmsAppController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private SmsConsoleService smsConsoleService;
	@Autowired
	private RedisManager redisManager;


	@RequestMapping("/sms/indexToken.action")
	public ModelAndView index(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/token");
		try {
			Page<SmsToken> page = new Page<SmsToken>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);
			JSONObject json = smsConsoleService.tokenPageList((page.getPageNo()-1)*page.getPageSize(), page.getPageSize(), null, null, null);
			List<SmsToken> list = new ArrayList<SmsToken>();

			if(json != null){
				String total = json.getString("recordsFiltered");
				JSONArray jsonArray = json.getJSONArray("data");
				if(jsonArray != null){
					for (Object object : jsonArray) {
						JSONObject jo = (JSONObject)object;
						SmsToken token = new SmsToken();
						token.setAppCode(jo.getString("appName"));
						Long time = jo.getLong("createTime");
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String d = format.format(time);
						Date date = format.parse(d);
						token.setCreateTime(date);
						list.add(token);
					}
				}
				if(StringUtils.isBlank(total)){
					page.setTotalCount(0);
				}else{
					page.setTotalCount(Integer.valueOf(total));
				}
				page.setResult(list);
			}else{
				page.setTotalCount(0);
				page.setResult(list);
			}
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			logger.error("query token list error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

	@RequestMapping("/sms/searchToken.action")
	public ModelAndView search(String appCode, String startTime, String endTime,HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/token");
		try {
			Page<SmsToken> page = new Page<SmsToken>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);
			if (!(StringUtils.isEmpty(startTime) || "null".equals(startTime))) {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (!(StringUtils.isEmpty(endTime) || "null".equals(endTime))) {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> datas = new HashMap<String, String>();
			Map<String,String> pageDatas = page.getDatas();

			if(!StringUtils.isEmpty(appCode)){
				pageDatas.put("appCode", appCode);
				datas.put("appCode", appCode);
				model.addObject("appCode",appCode);
			}
			if(!StringUtils.isEmpty(startTime)){
				pageDatas.put("startTime",startTime);
				datas.put("startTime", startTime);
				model.addObject("startTime", startTime);
			}
			if(!StringUtils.isEmpty(endTime)){
				pageDatas.put("endTime",endTime);
				datas.put("endTime", endTime);
				model.addObject("endTime", endTime);
			}
			page.setDatas(pageDatas);
			JSONObject	json = smsConsoleService.tokenPageList((page.getPageNo()-1)*page.getPageSize(), page.getPageSize(), appCode, startTime, endTime);


			List<SmsToken> list = new ArrayList<SmsToken>();
			if(json != null){
				String total = json.getString("recordsFiltered");
				JSONArray jsonArray = json.getJSONArray("data");

				if(jsonArray != null){
					for (Object object : jsonArray) {
						JSONObject jo = (JSONObject)object;
						SmsToken token = new SmsToken();
						token.setAppCode(jo.getString("appName"));
						Long time = jo.getLong("createTime");
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						String d = format.format(time);
						Date date = format.parse(d);
						token.setCreateTime(date);
						list.add(token);
					}
				}
				if(StringUtils.isBlank(total)){
					page.setTotalCount(0);
				}else{
					page.setTotalCount(Integer.valueOf(total));
				}
				page.setResult(list);

			}else{
				page.setTotalCount(0);
				page.setResult(list);
			}
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			logger.error("query token list error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}


	@RequestMapping("listchannel.action")
	public ModelAndView findAllChannels(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/channellistnew");
		try{
			List<SmsChannelEntity> smsEntitys = smsConsoleService.findAllChannelsnew();
			Page<SmsChannelEntity> page = new Page<SmsChannelEntity>(smsEntitys.size());
			PageUtil.init(page, request);
			page.setTotalCount(smsEntitys.size());
			page.setResult(smsEntitys);
			model.addObject("page", page);
		}catch(Exception e){
			logger.error("get channel list is errpr:{}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}



	@RequestMapping("addchannel.action")
	public ModelAndView addchannel(){
		ModelAndView model = new ModelAndView("sms/channelAdd");
		return model;
	}

	@RequestMapping("createchannel.action")
	@ResponseBody
	public String createChannel(SmsChannelEntity smsChannelEntity,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("createchannel.action operator={}",operator);
			smsConsoleService.createChannelnew(smsChannelEntity);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("create channel: {}, is error: {}", smsChannelEntity, e);
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping("toUpdatechannel.action")
	@ResponseBody
	public ModelAndView toUpdatechannel(@RequestParam("id") String id){
    	ModelAndView model = null;
    	try {
			model = new ModelAndView("/sms/channelUpdate");
			SmsChannelEntity smsEntity = smsConsoleService.findChannelByID(id);
			model.addObject("smsEntity", smsEntity);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query notice callback view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
    }



	@RequestMapping("updatechannel.action")
	@ResponseBody
	public String updateChannel(SmsChannelEntity smsChannelEntity,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("updatechannel.action operator={}",operator);
			smsConsoleService.updateChannelnew(smsChannelEntity);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("update channel: {}, is error: {}", smsChannelEntity, e);
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping("setsatatus.action")
	@ResponseBody
	public String setsatatus(long id,String status,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("updatechannel.action operator={}",operator);
			if(Constants.SMS_STATE_TRUE.equals(status)){
				status = Constants.SMS_STATE_FALSE;
			}else{
				status = Constants.SMS_STATE_TRUE;
			}
			smsConsoleService.updateSttus(id,status);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("delete channel by id: {}, is error: {}", e);
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping("sms/tosmsList.action")
	public ModelAndView tosmsList(){
		ModelAndView model = new ModelAndView("sms/smsQueryList");
		//短信类型
		List<SmsType> types = smsConsoleService.findSmsType();
		model.addObject("types", types);
		//短信应用
		List<SmsToken> tokens = smsConsoleService.tokenFindAll();
		model.addObject("tokens", tokens);
		//短信通道
		List<SmsChannelEntity> channels = smsConsoleService.findsmsList();
		model.addObject("channels", channels);

		String startTime = DateUtil.formatDate(new Date());
		String endTime = DateUtil.formatDate(new Date());

		model.addObject("startTime", startTime);
		model.addObject("endTime", endTime);

		return model;
	}


	 @RequestMapping(value = "sms/smsListSearch.action", method = RequestMethod.GET)
	    public ModelAndView smsListSearch(@RequestParam(value="startTime", required=false) String startTime,
	    		@RequestParam(value="endTime", required=false) String endTime,
	    		@RequestParam(value="phone", required=false) String phone, @RequestParam(value="templateCode", required=false) String templateCode,
	    		@RequestParam(value="typeCode", required=false) String typeCode, @RequestParam(value="appCode", required=false) String appCode,
	    		@RequestParam(value="channelNo", required=false) String channelNo, @RequestParam(value="status", required=false) String status,
	    		HttpServletRequest request) {
	    	ModelAndView model = null;
	    	try {
	    		model = new ModelAndView("/sms/smsQueryList");

	    		//短信类型
	    		List<SmsType> types = smsConsoleService.findSmsType();
	    		model.addObject("types", types);
	    		//短信应用
	    		List<SmsToken> tokens = smsConsoleService.tokenFindAll();
	    		model.addObject("tokens", tokens);
	    		//短信通道
	    		List<SmsChannelEntity> channels = smsConsoleService.findsmsList();
	    		model.addObject("channels", channels);

				Page<SmsCallbackEntity> page = new Page<SmsCallbackEntity>(PageUtil.PAGE_SIZE);
				PageUtil.init(page, request);

				//处理搜索分页不丢失查询条件--保存到Page的data中
				Map<String,String> pageDatas = page.getDatas();

				if(!StringUtils.isEmpty(phone)){
					phone = URLDecoder.decode(phone, "UTF-8");
					pageDatas.put("phone", phone);
					model.addObject("phone", phone);
				}
				if(!StringUtils.isEmpty(typeCode)){
					typeCode = URLDecoder.decode(typeCode, "UTF-8");
					pageDatas.put("typeCode", typeCode);
					model.addObject("typeCode", typeCode);
				}


				if(!StringUtils.isEmpty(appCode)){
					appCode = URLDecoder.decode(appCode, "UTF-8");
					pageDatas.put("appCode", appCode);
					model.addObject("appCode", appCode);
				}
				if(!StringUtils.isEmpty(channelNo)){
					channelNo = URLDecoder.decode(channelNo, "UTF-8");
					pageDatas.put("channelNo", channelNo);
					model.addObject("channelNo", channelNo);
				}

				if(!StringUtils.isEmpty(status)){
					status = URLDecoder.decode(status, "UTF-8");
					pageDatas.put("status", status);
					model.addObject("status", status);
				}

				if(!StringUtils.isEmpty(templateCode)){
					templateCode = URLDecoder.decode(templateCode, "UTF-8");
					pageDatas.put("templateCode", templateCode);
					model.addObject("templateCode", templateCode);
				}


				//时间不可能为空
				//处理搜索分页部丢失查询条件
				if(StringUtils.isNotBlank(startTime)){
					startTime = URLDecoder.decode(startTime, "UTF-8");
				}
				if(StringUtils.isNotBlank(endTime)){
					endTime = URLDecoder.decode(endTime, "UTF-8");
				}

				pageDatas.put("startTime", startTime);
				pageDatas.put("endTime", endTime);
				//在搜索框显示时间
				model.addObject("startTime", startTime);
				model.addObject("endTime", endTime);
				page.setDatas(pageDatas);

				List<SmsCallbackEntity> smsEntitys = smsConsoleService.smsListSearch(startTime, endTime, phone,
						templateCode,typeCode,appCode,channelNo,status,
						(page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
				int count = smsConsoleService.smsListSearchCount(startTime, endTime, phone,
						templateCode,typeCode,appCode,channelNo,status);
				page.setTotalCount(count);
				page.setResult(smsEntitys);
				model.addObject("page", page);
				return model;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("smsListSearch sms callback by phone={}&msgid={} error {}", phone, templateCode, e);
				model = new ModelAndView("error");
				model.addObject("error", e.getMessage());
				return model;
			}
	    }


	 @RequestMapping(value="sms/smsQueryView.action")
	    public ModelAndView smsQueryView(@RequestParam("id") String id){
	    	ModelAndView model = null;
	    	try {
				model = new ModelAndView("/sms/smsQueryView");
				SmsCallbackEntity smsEntity = smsConsoleService.findsmsQueryViewById(id);
				if(smsEntity==null){
					smsEntity = smsConsoleService.findsmsQuerySaleViewById(id);
				}
				String content = smsEntity.getContent();
				if(!StringUtils.isEmpty(content)){
					//数据库中的换行，在前台用</br>替代
					content= content.replace("\r\n", "<br/>");
					content= content.replace("\n", "<br/>");
					smsEntity.setContent(content);
				}
				model.addObject("smsEntity", smsEntity);
				return model;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("query notice callback view by id {} error {}", id, e);
				model = new ModelAndView("error");
				model.addObject("error", e.getMessage());
				return model;
			}
	    }
	 
	 
	@RequestMapping("indexRateStatics.action")
	@ResponseBody
	public ModelAndView indexRateStatics(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/smsRateStatics");
		try{
			List<SmsToken> tokens = smsConsoleService.tokenFindAll();
			List<SmsChannelEntity> channels = smsConsoleService.findAllChannels();
			List<SmsType> types = smsConsoleService.findliseType();
			Page<SmsRateStatics> page = new Page<SmsRateStatics>();
			PageUtil.init(page, request);
			page.setTotalCount(0);
			page.setResult(null);
			model.addObject("page", page);
			model.addObject("tokens", tokens);
			model.addObject("channels", channels);
			model.addObject("types", types);
		}catch(Exception e){
			logger.error("get channel list is errpr:{}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}
	
	@RequestMapping("searchRateStatics.action")
	@ResponseBody
	public ModelAndView searchRateStatics(String appCode,String channelCodes, String typeCodes,
			String startTime, String endTime, HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/smsRateStatics");
		try{
			Page<SmsRateStatics> page = new Page<SmsRateStatics>();
			PageUtil.init(page, request);
			if (!(StringUtils.isEmpty(startTime) || "null".equals(startTime))) {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (!(StringUtils.isEmpty(endTime) || "null".equals(endTime))) {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> datas = new HashMap<String, String>();
			Map<String,String> pageDatas = page.getDatas();
			List<String> channelCode = null;
			List<String> typeCode = null;
			if(!StringUtils.isEmpty(appCode)){
				pageDatas.put("appCode", appCode);
				datas.put("appCode", appCode);
				model.addObject("appCode",appCode);
			}
			if(!StringUtils.isEmpty(startTime)){
				pageDatas.put("startTime",startTime);
				datas.put("startTime", startTime);
				model.addObject("startTime", startTime);
			}
			if(!StringUtils.isEmpty(endTime)){
				pageDatas.put("endTime",endTime);
				datas.put("endTime", endTime);
				model.addObject("endTime", endTime);
			}
			if(!StringUtils.isEmpty(channelCodes)){
				channelCode = Arrays.asList(channelCodes.split(","));
				pageDatas.put("channelCodes", channelCodes);
				datas.put("channelCodes", channelCodes);
				model.addObject("channelCodes",channelCode);
			
			}
			if(!StringUtils.isEmpty(typeCodes)){
				typeCode = Arrays.asList(typeCodes.split(","));
				pageDatas.put("typeCodes", typeCodes);
				datas.put("typeCodes", typeCodes);
				model.addObject("typeCodes",typeCode);
				
			}
			page.setDatas(pageDatas);
			
			List<SmsRateStatics> list = smsConsoleService.pageListSmsRateStatics((page.getPageNo()-1)*page.getPageSize(), page.getPageSize(), 
				appCode, typeCode, channelCode, startTime, endTime);
			int count = smsConsoleService.countSmsRateStatics(appCode, typeCode, channelCode, startTime, endTime);
			page.setTotalCount(count);
			page.setResult(list);
			model.addObject("page", page);
			
			
			List<SmsToken> tokens = smsConsoleService.tokenFindAll();
			List<SmsChannelEntity> channels = smsConsoleService.findAllChannels();
			List<SmsType> types = smsConsoleService.findliseType();
			
			
			model.addObject("tokens", tokens);
			model.addObject("channels", channels);
			model.addObject("types", types);
		}catch(Exception e){
			logger.error("get smsRateStatics list is errpr:{}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

}
