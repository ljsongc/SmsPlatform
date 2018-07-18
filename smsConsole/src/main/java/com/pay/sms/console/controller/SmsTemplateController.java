package com.pay.sms.console.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.sms.console.bean.SmsTemplate;
import com.pay.sms.console.bean.SmsToken;
import com.pay.sms.console.bean.SmsType;
import com.pay.sms.console.enums.Constants;
import com.pay.sms.console.redis.RedisManager;
import com.pay.sms.console.service.SmsConsoleService;
import com.pay.sms.console.web.util.Page;
import com.pay.sms.console.web.util.PageUtil;

@Controller
public class SmsTemplateController {
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private SmsConsoleService smsConsoleService;
	@Autowired
	private RedisManager redisManager;
	
	
	@RequestMapping("/sms/indexTemplate.action")
	public ModelAndView index(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/smsTemplate");
		//短信类型信息
		//短信应用信息
		try {
			Page<SmsTemplate> page = new Page<SmsTemplate>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);
			page.setTotalCount(0);
			List<SmsTemplate> list = new ArrayList<SmsTemplate>();
			page.setResult(list);
			List<SmsType> types = smsConsoleService.findSmsType();
			model.addObject("types", types);
			List<SmsToken> tokens = smsConsoleService.tokenFindAll();
			model.addObject("tokens", tokens);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			logger.error("query smsTenplate list error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}
	
	@RequestMapping("/sms/searchTemplate.action")
	public ModelAndView search(String templateCode, String typeCode, String title, String appCode,
			String startTime, String endTime,HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/smsTemplate");
		try {
			Page<SmsTemplate> page = new Page<SmsTemplate>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);
			if(StringUtils.isNotBlank(startTime)){
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}
			if(StringUtils.isNotBlank(startTime)){
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
			if(!StringUtils.isEmpty(typeCode)){
				pageDatas.put("typeCode", typeCode);
				datas.put("typeCode", typeCode);
				model.addObject("typeCode",typeCode);
			}
			if(!StringUtils.isEmpty(templateCode)){
				pageDatas.put("templateCode",templateCode);
				datas.put("templateCode", templateCode);
				model.addObject("templateCode", templateCode);
			}
			if(!StringUtils.isEmpty(title)){
				pageDatas.put("title",title);
				datas.put("title", title);
				model.addObject("title", title);
			}
			
			page.setDatas(pageDatas);
			
			JSONObject result = smsConsoleService.smsTemplatePageList((page.getPageNo()-1)*page.getPageSize(),  page.getPageSize(), templateCode, typeCode, title, appCode, startTime, endTime);
			if(result != null){
				String total = result.getString("recordsTotal");
				JSONArray jsonArray = result.getJSONArray("data");
				List<SmsTemplate> list = new ArrayList<SmsTemplate>();
				if(jsonArray != null){
					for (Object object : jsonArray) {
						JSONObject jo = (JSONObject)object;
						SmsTemplate smsTemplate = new SmsTemplate();
						smsTemplate.setAppCode(jo.getString("appCode"));
						smsTemplate.setTemplateCode(jo.getString("templateCode"));
						smsTemplate.setTypeCode(jo.getString("typeCode"));
						smsTemplate.setTitle(jo.getString("title"));
						smsTemplate.setContent(jo.getString("content"));
						smsTemplate.setOperator(jo.getString("operator"));
						String createTime = jo.getString("createTimeStr");
						smsTemplate.setCreateTimeStr(createTime);
						SmsType smsType = smsConsoleService.findSmsTypeByID(jo.getString("typeCode"));
						if(smsType != null){
							smsTemplate.setTypeName(smsType.getTypeName());
						}
						smsTemplate.setAppCode(jo.getString("appCode"));
						//应用名称   根据appcode 查询token表
						list.add(smsTemplate);
					}
				}
				
				if(StringUtils.isBlank(total)){
					page.setTotalCount(0);
				}else{
					page.setTotalCount(Integer.valueOf(total));
				}
				page.setResult(list);
				
				List<SmsType> types = smsConsoleService.findSmsType();
				model.addObject("types", types);
				List<SmsToken> tokens = smsConsoleService.tokenFindAll();
				model.addObject("tokens", tokens);
			}
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			logger.error("query smsTenplate list error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}
	
	@RequestMapping("/sms/toAddTemplate.action")
	public ModelAndView add(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/smsTemplateAdd");
		List<SmsType> types = smsConsoleService.findSmsType();
		model.addObject("types", types);
		List<SmsToken> tokens = smsConsoleService.tokenFindAll();
		model.addObject("tokens", tokens);
		return model;
	}
	
	@RequestMapping("/sms/addTemplate.action")
	@ResponseBody
	public String add(SmsTemplate smsTemplate,HttpServletRequest request){
		try {
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = null;
	        if(object != null ){
        		operator =  ((AssertionImpl)object).getPrincipal().getName();
        		smsTemplate.setOperator(operator);
	        }
	        logger.info("operator :{} add smsTemplate={}",operator,smsTemplate);
			String result = smsConsoleService.saveSmsTemplate(smsTemplate);
			return result;
		} catch (Exception e) {
			logger.error("insert SmsTemplate error",e);
			return "fail";
		}
	}
	
	@RequestMapping("/sms/toUpdateTemplate.action")
	public ModelAndView  toUpdate(String templateCode){
		ModelAndView model = new ModelAndView("sms/smsTemplateUpdate");
		SmsTemplate smsTemplate = smsConsoleService.findSmsTemplateByCode(templateCode);
		model.addObject("smsTemplate", smsTemplate);
		List<SmsType> types = smsConsoleService.findSmsType();
		model.addObject("types", types);
		List<SmsToken> tokens = smsConsoleService.tokenFindAll();
		model.addObject("tokens", tokens);
		return model;
	}
	
	@RequestMapping("/sms/updateTemplate.action")
	@ResponseBody
	public String update(SmsTemplate smsTemplate,HttpServletRequest request){
		try {
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = null;
	        if(object != null ){
	        		operator =  ((AssertionImpl)object).getPrincipal().getName();
	        		smsTemplate.setOperator(operator);
	        }
	        logger.info("operator :{} update smsTemplate={}",operator,smsTemplate);
			String result = smsConsoleService.updateSmsTemplate(smsTemplate);
			return result;
		} catch (Exception e) {
			logger.error("update token error",e);
			return "fail";
		}
		
	}
	
}
