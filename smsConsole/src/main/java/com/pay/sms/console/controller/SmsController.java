package com.pay.sms.console.controller;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONObject;
import com.pay.sms.console.bean.ChannelRate;
import com.pay.sms.console.bean.SmsCallbackEntity;
import com.pay.sms.console.bean.SmsChannelEntity;
import com.pay.sms.console.bean.SmsEntity;
import com.pay.sms.console.bean.SmsSaleCallbackChartData;
import com.pay.sms.console.bean.SmsStatistics;
import com.pay.sms.console.bean.SmsType;
import com.pay.sms.console.enums.Constants;
import com.pay.sms.console.redis.RedisManager;
import com.pay.sms.console.service.SmsConsoleService;
import com.pay.sms.console.web.util.Page;
import com.pay.sms.console.web.util.PageUtil;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;

@Controller
@RequestMapping("/sms")
public class SmsController {

	private Logger logger = LoggerFactory.getLogger(getClass()) ;

	@Autowired
	private RedisManager redisManager;

	private static final String MASTRER_TOTAL = "smsserver_master_total";

	private static final String SPARE_TOTAL = "smsserver_spare_total";

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	@Autowired
	private SmsConsoleService smsConsoleService;

	@RequestMapping("channelTechnologyInfo.action")
	public ModelAndView channelTechnologyInfo(){
		ModelAndView model = new ModelAndView("sms/channelTechnologyInfo");
		return model;
	}

	@RequestMapping("listchannel.action")
	public ModelAndView findAllChannels(HttpServletRequest request){
		ModelAndView model = new ModelAndView("sms/channellist");
		try{
			List<SmsChannelEntity> smsEntitys = smsConsoleService.findAllChannels();
			long noticeMasterId = smsConsoleService.getNoticeMasterId();
			Page<SmsChannelEntity> page = new Page<SmsChannelEntity>(smsEntitys.size());
			PageUtil.init(page, request);
			page.setTotalCount(smsEntitys.size());
			page.setResult(smsEntitys);
			model.addObject("noticeMasterId", noticeMasterId);
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


	@RequestMapping("deletechannel.action")
	@ResponseBody
	public String deleteChannel(long id){
		try{
			smsConsoleService.deleteChannelById(id);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("delete channel by id: {}, is error: {}", e);
			return e.getMessage();
		}
		return "success";
	}

	@RequestMapping("createchannel.action")
	@ResponseBody
	public String createChannel(SmsChannelEntity smsChannelEntity,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("createchannel.action operator={}",operator);
			smsConsoleService.createChannel(smsChannelEntity);
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
			smsConsoleService.updateChannel(smsChannelEntity);
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



	/**
	 * 短信类型查询
	 * @param request
	 * @return
	 */
	@RequestMapping("listSmsType.action")
	public ModelAndView listSmsType(
			HttpServletRequest request,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime,
			@RequestParam(value = "typeCode", required = false) String typeCode) {
		ModelAndView model = null;
		try {
			List<SmsType> liseType= new ArrayList<SmsType>();
			liseType = smsConsoleService.findliseType();
			model = new ModelAndView("/sms/listSmsTypeList");
			if(StringUtils.isNotBlank(startTime)){
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}
			if(StringUtils.isNotBlank(startTime)){
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}
			Page<SmsType> page = new Page<SmsType>(PageUtil.PAGE_SIZE);
			Map<String, String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsType> smsEntitys = smsConsoleService.pageListSmsType(startTime, endTime,
					(page.getPageNo() - 1) * page.getPageSize(),
					page.getPageSize(),typeCode);
			int count = smsConsoleService.pageListSmsTypeCount(startTime, endTime,typeCode);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			pageDatas.put("typeCode", typeCode);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("typeCode", typeCode);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			model.addObject("liseType", liseType);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query notice sms list error", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}


	/**跳转至短信类型新增页面
	 * @return
	 */
	@RequestMapping("addSmsType.action")
	public ModelAndView addSmsType(){
		List<SmsChannelEntity> listChannel = smsConsoleService.findsmsList();
		ModelAndView model = new ModelAndView("sms/smsTypeAdd");
		model.addObject("smsEntity", listChannel);
		return model;
	}


	/**校验相同短信类型
	 * @param smsType
	 * @return
	 */
	@RequestMapping("checksms.action")
	@ResponseBody
	public String checksms(String typeName,String channelCode,String typeCode){
		try{
			SmsType smsType = smsConsoleService.checksms(typeName,channelCode,typeCode);
			if(smsType!=null){
				return "existence";
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("create checksms: {}, is error: {}", typeName, e);
			return e.getMessage();
		}
		return "success";
	}

	/**保存短信类型
	 * @param smsType
	 * @return
	 */
	@RequestMapping("createSmsType.action")
	@ResponseBody
	public String createSmsType(SmsType smsType,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("createSmsType.action operator={}",operator);
	        smsType.setOperator(operator);
			smsConsoleService.createChannel(smsType);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("create channel: {}, is error: {}", smsType, e);
			return e.getMessage();
		}
		return "success";
	}


	/**根据ID查询短信类型
	 * @param id
	 * @return
	 */
	@RequestMapping("toUpdateSmsType.action")
	@ResponseBody
	public ModelAndView toUpdateSmsType(@RequestParam("typeCode") String typeCode){
    	ModelAndView model = null;
    	try {
			model = new ModelAndView("/sms/smsTypeUpdate");
			//通道列表
			List<SmsChannelEntity> listChannel = smsConsoleService.findsmsList();
			List<ChannelRate> rateList = smsConsoleService.findRate(typeCode);

			SmsType smsType = smsConsoleService.findSmsTypeByID(typeCode);
			model.addObject("smsType", smsType);
			model.addObject("rateList", rateList);
			model.addObject("listChannel", listChannel);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query notice callback view by id {} error {}", typeCode, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
    }


	/**保存短信类型
	 * @param smsType
	 * @return
	 */
	@RequestMapping("updateSmsType.action")
	@ResponseBody
	public String updateSmsType(SmsType smsType,HttpServletRequest request){
		try{
			Object object = request.getSession().getAttribute(Constants.CAS_SESSION);
			String operator = ((AssertionImpl)object).getPrincipal().getName();
	        logger.info("updateSmsType.action operator={}",operator);
	        smsType.setOperator(operator);
			smsConsoleService.updateSmsType(smsType);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("create channel: {}, is error: {}", smsType, e);
			return e.getMessage();
		}
		return "success";
	}




	/**
	 * 通知类
	 * @param request
	 * @return
	 */
	@RequestMapping("list.action")
	public ModelAndView noticeList(
			HttpServletRequest request,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) {
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/list");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isEmpty(startTime) || "null".equals(startTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				startTime = sdf.format(calendar.getTime());
			} else {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (StringUtils.isEmpty(endTime) || "null".equals(endTime)) {
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			} else {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			Map<String, String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsEntity> smsEntitys = smsConsoleService.findSuccessSms(startTime, endTime,
					(page.getPageNo() - 1) * page.getPageSize(),
					page.getPageSize());
			int count = smsConsoleService.findSuccessSmsSize(startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query notice sms list error", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

	@RequestMapping("view.action")
	public ModelAndView view(@RequestParam int id){
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/view");
			SmsEntity smsEntity = smsConsoleService.findSmsById(id);
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
			logger.error("query sms view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getCause());
			return model;
		}
	}

	@RequestMapping(value = "search.action", method = RequestMethod.GET)
	public ModelAndView noticeSearch(
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "content", required = false) String content,
			HttpServletRequest request) {

		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/list");
			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			// 处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String, String> pageDatas = page.getDatas();

			if (!StringUtils.isEmpty(phone)) {
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if (!StringUtils.isEmpty(content)) {
				content = URLDecoder.decode(content, "UTF-8");
				pageDatas.put("content", content);
				model.addObject("content", content);
			}
			// 时间不可能为空
			// 处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			// 在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsEntity> smsEntitys = smsConsoleService.findSuccessSmsByCondition(phone, content, startTime,
				endTime, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findSuccessSmsSizeByCondition(phone, content, startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search notice sms list by phone={}&content={} error {}", phone, content, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    /**
     * 营销类
     * @param request
     * @return
     */
	@RequestMapping("saleList.action")
	public ModelAndView saleList(
			HttpServletRequest request,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) {
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/saleList");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isEmpty(startTime) || "null".equals(startTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				startTime = sdf.format(calendar.getTime());
			} else {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (StringUtils.isEmpty(endTime) || "null".equals(endTime)) {
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			} else {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			Map<String, String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsEntity> smsEntitys = smsConsoleService.findSaleSuccessSms(startTime, endTime,
					(page.getPageNo() - 1) * page.getPageSize(),
					page.getPageSize());
			int count = smsConsoleService.findSaleSuccessSmsSize(startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query sale sms list error", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

	@RequestMapping("saleView.action")
	public ModelAndView saleView(@RequestParam int id){
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/view");
			SmsEntity smsEntity = smsConsoleService.findSaleSmsById(id);
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
			logger.error("query sale sms view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

	@RequestMapping(value = "saleSearch.action", method = RequestMethod.GET)
	public ModelAndView saleSearch(
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "content", required = false) String content,
			HttpServletRequest request) {

		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/saleList");
			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			// 处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String, String> pageDatas = page.getDatas();

			if (!StringUtils.isEmpty(phone)) {
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if (!StringUtils.isEmpty(content)) {
				content = URLDecoder.decode(content, "UTF-8");
				pageDatas.put("content", content);
				model.addObject("content", content);
			}
			// 时间不可能为空
			// 处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			// 在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsEntity> smsEntitys = smsConsoleService.findSaleSuccessSmsByCondition(phone, content, startTime,
				endTime, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findSaleSuccessSmsSizeByCondition(phone, content, startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search sale sms list by phone={}&content={} error {}", phone, content, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    //no use
    @RequestMapping("toSend.action")
	public ModelAndView toSend(HttpServletRequest request){
    	ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/send");
			List<String> smsSendLevels = new ArrayList<String>();
			for(SmsSendLevel smsSendLevel : SmsSendLevel.values()){
				smsSendLevels.add(smsSendLevel.name());
			}
			model.addObject("smsSendLevels", smsSendLevels);
			List<String> smsSendTypes = new ArrayList<String>();
			for(SmsSendType smsSendType : SmsSendType.values()){
				smsSendTypes.add(smsSendType.name());
			}
			model.addObject("smsSendTypes", smsSendTypes);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("sms toSend error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    //no use
    @RequestMapping(value = "send.action", method = {RequestMethod.POST })
	public String send(SmsBean smsBean, @RequestParam(value="timeStr") String timeStr,
			RedirectAttributes redirectAttributes, HttpServletRequest request){

    	this.logger.info("smsConsole send message to={}&content={}", smsBean.getTo(), smsBean.getContent());

		try {
			if(!StringUtils.isEmpty(timeStr)){
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date time = dateFormat.parse(timeStr);
				smsBean.setTime(time);
			}else{
				smsBean.setTime(new Date());
			}

			SmsResponse response = this.smsConsoleService.sendMessage(smsBean);

			if(response == null){
				redirectAttributes.addFlashAttribute("error", "短信发送后台异常");
			}else{
				StringBuilder responseMessage = new StringBuilder();
				responseMessage.append("发送结果：" + response.getResponseFlag());
				if(!StringUtils.isEmpty(response.getResponseContent())){
					responseMessage.append("<br/>响应结果：" + response.getResponseContent());
				}
				redirectAttributes.addFlashAttribute("message", responseMessage.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "后台发生异常：" + e.getMessage());
			this.logger.error("sms console send message error ", e);
		}
		return "redirect:/sms/toSend.action";
    }

    @RequestMapping("tokenList.action")
	public ModelAndView tokenList(HttpServletRequest request){
    	ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/tokenList");
			Page<Map<String,String>> page = new Page<Map<String,String>>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);
			List<Map<String,String>> tokens = smsConsoleService.findSmsToken((page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findSmsTokenSize();
			page.setTotalCount(count);
			page.setResult(tokens);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query token list error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

    @RequestMapping(value = "deleteToken.action")
	public String deleteToken(@RequestParam(value="id") Integer id,@RequestParam(value="appCode") String appCode,
			RedirectAttributes redirectAttributes, HttpServletRequest request){

		try {
			this.smsConsoleService.deleteToken(appCode, id);
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "后台发生异常：" + e.getMessage());
			this.logger.error("sms console send message error ", e);
		}
		return "redirect:/sms/tokenList.action";
    }

    @RequestMapping("toSaveToken.action")
	public ModelAndView toSaveToken(HttpServletRequest request){
    	ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/tokenSave");
			return model;
		} catch (Exception e) {
			logger.error("token toSave error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

    @RequestMapping(value = "tokenSave.action", method = {RequestMethod.GET })
  	public String tokenSave(@RequestParam(value="appCode") String appCode,
  			RedirectAttributes redirectAttributes, HttpServletRequest request){
  		try {
  			if(!StringUtils.isEmpty(appCode)){
  				appCode = URLDecoder.decode(appCode, "UTF-8");
  			}
  			this.smsConsoleService.saveToken(appCode);
  		} catch (Exception e) {
  			this.logger.error("sms console save token by appCode {} error ", appCode, e);
  		}
  		return "redirect:/sms/tokenList.action";
    }

    @RequestMapping(value = "tokenValidate.action", method = {RequestMethod.GET })
    @ResponseBody
  	public String tokenValidate(@RequestParam(value="appCode") String appCode,
  			RedirectAttributes redirectAttributes, HttpServletRequest request){
  		try {
  			if(!StringUtils.isEmpty(appCode)){
  				appCode = URLDecoder.decode(appCode, "UTF-8");
  			}
  			return this.smsConsoleService.validateToken(appCode);
  		} catch (Exception e) {
  			e.printStackTrace();
  			this.logger.error("sms console save token by appCode {} error ", appCode, e);
  			return "error";
  		}
    }

    @RequestMapping(value = "tokenSearch.action", method = {RequestMethod.GET })
    public ModelAndView tokenSearch2(
    		@RequestParam(value="appCode",required=false) String appCode,
    		@RequestParam(value="token",required=false) String token,
    		HttpServletRequest request) {

    	ModelAndView model = null;
    	try {
    		model = new ModelAndView("/sms/tokenList");
			Page<Map<String, String>> page = new Page<Map<String, String>>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> datas = new HashMap<String, String>();
			Map<String,String> pageDatas = page.getDatas();

			if(!StringUtils.isEmpty(appCode)){
				appCode = URLDecoder.decode(appCode, "UTF-8");
				pageDatas.put("appCode", appCode);
				datas.put("appCode", appCode);
				model.addObject("appCode",appCode);
			}
			if(!StringUtils.isEmpty(token)){
				token = URLDecoder.decode(token, "UTF-8");
				pageDatas.put("token", token);
				datas.put("token", token);
				model.addObject("token", token);
			}
			page.setDatas(pageDatas);
			List<Map<String, String>> smsEntitys = smsConsoleService.findTokenByCondition(appCode, token, (page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findTokenSizeByCondition(appCode, token);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search sms token by appCode={}&token={} error {}", appCode, token, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
    }

    @RequestMapping(value = "tokenSearch.action", method = {RequestMethod.POST })
    public ModelAndView tokenSearch(
    		@RequestParam(value="appCode",required=false) String appCode,
    		@RequestParam(value="token",required=false) String token,
    		HttpServletRequest request) {

    	ModelAndView model = null;
    	try {
    		model = new ModelAndView("/sms/tokenList");
			Page<Map<String, String>> page = new Page<Map<String, String>>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> datas = new HashMap<String, String>();
			Map<String,String> pageDatas = page.getDatas();

			if(!StringUtils.isEmpty(appCode)){
				pageDatas.put("appCode", appCode);
				datas.put("appCode", appCode);
				model.addObject("appCode",appCode);
			}
			if(!StringUtils.isEmpty(token)){
				pageDatas.put("token", token);
				datas.put("token", token);
				model.addObject("token", token);
			}
			page.setDatas(pageDatas);
			List<Map<String, String>> smsEntitys = smsConsoleService.findTokenByCondition(appCode, token, (page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findTokenSizeByCondition(appCode, token);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search sms token by appCode={}&token={} error {}", appCode, token, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
    }

    /**
     * 失败短信查询
     * @param request
     * @return
     */
    @RequestMapping("failureList.action")
    public ModelAndView failureList(
			HttpServletRequest request,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) {
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/failureList");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isEmpty(startTime) || "null".equals(startTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				startTime = sdf.format(calendar.getTime());
			} else {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (StringUtils.isEmpty(endTime) || "null".equals(endTime)) {
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			} else {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			Map<String, String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsEntity> smsEntitys = smsConsoleService.findFailureSms(startTime, endTime,
					(page.getPageNo() - 1) * page.getPageSize(),
					page.getPageSize());
			int count = smsConsoleService.findFailureSmsSize(startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query failure sms list error", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

    @RequestMapping("failureView.action")
	public ModelAndView failureView(@RequestParam int id){
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/view");
			SmsEntity smsEntity = smsConsoleService.findFailureById(id);
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
			logger.error("query sms view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    @RequestMapping(value = "failureSearch.action", method=RequestMethod.GET)
    public ModelAndView failureSearch(
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "content", required = false) String content,
			HttpServletRequest request) {

		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/failureList");
			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			// 处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String, String> pageDatas = page.getDatas();

			if (!StringUtils.isEmpty(phone)) {
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if (!StringUtils.isEmpty(content)) {
				content = URLDecoder.decode(content, "UTF-8");
				pageDatas.put("content", content);
				model.addObject("content", content);
			}
			// 时间不可能为空
			// 处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			// 在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsEntity> smsEntitys = smsConsoleService.findFailureSmsByCondition(phone, content, startTime,
				endTime, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findFailureSmsSizeByCondition(phone, content, startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search failure sms list by phone={}&content={} error {}", phone, content, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    @RequestMapping("failureHistoryList.action")
    public ModelAndView failureHistoryList(
			HttpServletRequest request,
			@RequestParam(value = "startTime", required = false) String startTime,
			@RequestParam(value = "endTime", required = false) String endTime) {
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/failureHistoryList");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if (StringUtils.isEmpty(startTime) || "null".equals(startTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				startTime = sdf.format(calendar.getTime());
			} else {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (StringUtils.isEmpty(endTime) || "null".equals(endTime)) {
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			} else {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			Map<String, String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsEntity> smsEntitys = smsConsoleService.findFailureHistorySms(startTime, endTime,
					(page.getPageNo() - 1) * page.getPageSize(),
					page.getPageSize());
			int count = smsConsoleService.findFailureHistorySmsSize(startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query failureHistory sms list error", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
		return model;
	}

    @RequestMapping("failureHistoryView.action")
	public ModelAndView failureHistoryView(@RequestParam int id){
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/view");
			SmsEntity smsEntity = smsConsoleService.findFailureHistoryById(id);
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
			logger.error("query sms view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    @RequestMapping(value = "failureHistorySearch.action", method=RequestMethod.GET)
    public ModelAndView failureHistorySearch(
			@RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime,
			@RequestParam(value = "phone", required = false) String phone,
			@RequestParam(value = "content", required = false) String content,
			HttpServletRequest request) {

		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/failureHistoryList");
			Page<SmsEntity> page = new Page<SmsEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			// 处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String, String> pageDatas = page.getDatas();

			if (!StringUtils.isEmpty(phone)) {
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if (!StringUtils.isEmpty(content)) {
				content = URLDecoder.decode(content, "UTF-8");
				pageDatas.put("content", content);
				model.addObject("content", content);
			}
			// 时间不可能为空
			// 处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			// 在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsEntity> smsEntitys = smsConsoleService.findFailureHistorySmsByCondition(phone, content, startTime,
				endTime, (page.getPageNo() - 1) * page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findFailureHistorySmsSizeByCondition(phone, content, startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search failureHistory sms list by phone={}&content={} error {}", phone, content, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
	}

    //通知类短信送达查询
    @RequestMapping("noticeCallbackList.action")
	public ModelAndView noticeCallbackList(HttpServletRequest request, @RequestParam(value="startTime",required=false) String startTime,
			@RequestParam(value="endTime",required=false) String endTime){
		ModelAndView model = null;
    	try {
    		model = new ModelAndView("/sms/noticeCallbackList");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isEmpty(startTime) || "null".equals(startTime)){
				 Calendar calendar = Calendar.getInstance();
				 calendar.setTime(new Date());
	             calendar.set(Calendar.HOUR_OF_DAY, 0);
	             calendar.set(Calendar.MINUTE, 0);
	             calendar.set(Calendar.SECOND, 0);
	             startTime = sdf.format(calendar.getTime());
			}else{
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if(StringUtils.isEmpty(endTime) || "null".equals(endTime)){
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			}else{
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsCallbackEntity> page = new Page<SmsCallbackEntity>(PageUtil.PAGE_SIZE);
			Map<String,String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsCallbackEntity> smsEntitys = smsConsoleService.findCallbackNoticeSms(startTime,
					endTime, (page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findCallbackNoticeSmsSize(startTime, endTime);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query notice sms callback error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
    	return model;
	}

    @RequestMapping(value = "noticeCallbackSearch.action", method = RequestMethod.GET)
    public ModelAndView noticeCallbackSearch(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
    		@RequestParam(value="phone", required=false) String phone, @RequestParam(value="msgid", required=false) String msgid,
    		HttpServletRequest request) {

    	ModelAndView model = null;
    	try {
    		model = new ModelAndView("/sms/noticeCallbackList");
			Page<SmsCallbackEntity> page = new Page<SmsCallbackEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> pageDatas = page.getDatas();

			if(!StringUtils.isEmpty(phone)){
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if(!StringUtils.isEmpty(msgid)){
				msgid = URLDecoder.decode(msgid, "UTF-8");
				pageDatas.put("msgid", msgid);
				model.addObject("msgid", msgid);
			}
			//时间不可能为空
			//处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			//在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsCallbackEntity> smsEntitys = smsConsoleService.findCallbackNoticeSmsByCond(startTime, endTime, phone, msgid,
					(page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findCallbackNoticeSmsSizeByCond(startTime, endTime, phone, msgid);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search notice sms callback by phone={}&msgid={} error {}", phone, msgid, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
			return model;
		}
    }

    @RequestMapping(value="noticeCallbackView.action")
    public ModelAndView noticeCallbackView(@RequestParam("id") String id){
    	ModelAndView model = null;
    	try {
			model = new ModelAndView("/sms/callbackView");
			SmsCallbackEntity smsEntity = smsConsoleService.findNoticeCallbackViewById(id);
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

    @RequestMapping("saleCallbackList.action")
	public ModelAndView saleCallbackList(HttpServletRequest request, @RequestParam(value="startTime",required=false) String startTime,
			@RequestParam(value="endTime",required=false) String endTime){
    	ModelAndView model;
    	try {
    		model = new ModelAndView("/sms/saleCallbackList");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(StringUtils.isEmpty(startTime) || "null".equals(startTime)){
				 Calendar calendar = Calendar.getInstance();
				 calendar.setTime(new Date());
	             calendar.set(Calendar.HOUR_OF_DAY, 0);
	             calendar.set(Calendar.MINUTE, 0);
	             calendar.set(Calendar.SECOND, 0);
	             startTime = sdf.format(calendar.getTime());
			}else{
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if(StringUtils.isEmpty(endTime) || "null".equals(endTime)){
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			}else{
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			Page<SmsCallbackEntity> page = new Page<SmsCallbackEntity>(PageUtil.PAGE_SIZE);
			Map<String,String> pageDatas = page.getDatas();
			PageUtil.init(page, request);
			List<SmsCallbackEntity> smsEntitys = smsConsoleService.findCallbackSaleSms(startTime,
					endTime, (page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findCallbackSaleSmsSize(startTime, endTime);

			page.setTotalCount(count);
			page.setResult(smsEntitys);
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			page.setDatas(pageDatas);
			model.addObject("page", page);
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query sale sms callback error {}", e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
    	return model;
	}

    @RequestMapping(value = "saleCallbackSearch.action", method = RequestMethod.GET)
    public ModelAndView saleCallbackSearch(@RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
    		@RequestParam(value="phone", required=false) String phone, @RequestParam(value="msgid", required=false) String msgid,
    		HttpServletRequest request) {

    	ModelAndView model = null;
    	try {
    		model = new ModelAndView("/sms/saleCallbackList");
			Page<SmsCallbackEntity> page = new Page<SmsCallbackEntity>(PageUtil.PAGE_SIZE);
			PageUtil.init(page, request);

			//处理搜索分页不丢失查询条件--保存到Page的data中
			Map<String,String> pageDatas = page.getDatas();

			if(!StringUtils.isEmpty(phone)){
				phone = URLDecoder.decode(phone, "UTF-8");
				pageDatas.put("phone", phone);
				model.addObject("phone", phone);
			}
			if(!StringUtils.isEmpty(msgid)){
				msgid = URLDecoder.decode(msgid, "UTF-8");
				pageDatas.put("msgid", msgid);
				model.addObject("msgid", msgid);
			}
			//时间不可能为空
			//处理搜索分页部丢失查询条件
			startTime = URLDecoder.decode(startTime, "UTF-8");
			endTime = URLDecoder.decode(endTime, "UTF-8");
			pageDatas.put("startTime", startTime);
			pageDatas.put("endTime", endTime);
			//在搜索框显示时间
			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			page.setDatas(pageDatas);

			List<SmsCallbackEntity> smsEntitys = smsConsoleService.findCallbackSaleSmsByCond(startTime, endTime, phone, msgid,
					(page.getPageNo()-1)*page.getPageSize(), page.getPageSize());
			int count = smsConsoleService.findCallbackSaleSmsSizeByCond(startTime, endTime, phone, msgid);
			page.setTotalCount(count);
			page.setResult(smsEntitys);
			model.addObject("page", page);
			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("search sale sms callback by phone={}&msgid={} error {}", phone, msgid, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
    	return model;
    }

    @RequestMapping(value="saleCallbackView.action")
    public ModelAndView saleCallbackView(@RequestParam("id") String id){
    	ModelAndView model = null;
    	try {
			model = new ModelAndView("/sms/callbackView");
			SmsCallbackEntity smsEntity = smsConsoleService.findSaleCallbackViewById(id);
			String content = smsEntity.getContent();
			if(!StringUtils.isEmpty(content)){
				//数据库中的换行，在前台用</br>替代
				content= content.replace("\r\n", "<br/>");
				content= content.replace("\n", "<br/>");
				smsEntity.setContent(content);
			}
			model.addObject("smsEntity", smsEntity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query sale callback view by id {} error {}", id, e);
			model = new ModelAndView("error");
			model.addObject("error", e.getMessage());
		}
    	return model;
    }

    @RequestMapping(value="saleCallbackChart.action")
    public ModelAndView saleCallbackChart(){
    	ModelAndView model = new ModelAndView("/sms/saleCallbackChart");
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    	String startTime = sdf.format(calendar.getTime());
    	String endTime = sdf.format(new Date());
    	model.addObject("startTime", startTime);
    	model.addObject("endTime", endTime);
        return model;
    }

    @RequestMapping(value="getSaleCallbackChartData.action")
    @ResponseBody
    public String getSaleCallbackChartData(String startTime, String endTime, String content){
    	List<SmsSaleCallbackChartData> datas = null;
    	String count = null;
    	String json = null;
    	try{
    		startTime = URLDecoder.decode(startTime, "UTF-8");
    		endTime = URLDecoder.decode(endTime, "UTF-8");
    		if(!StringUtils.isEmpty(content)){
    			content = URLDecoder.decode(content, "UTF-8");
    			content = content.replace("\r\n", "\n");
    		}
    		//获得送达状态的数据
    		datas = smsConsoleService.getSaleCallbackChartData(startTime, endTime, content);
    		//获得营销短信发送成功条数
    		count = smsConsoleService.getSaleCount(startTime, endTime, content);
    		if(!"0".equals(count)){
    			SmsSaleCallbackChartData entity = new SmsSaleCallbackChartData();
        		entity.setName("count");
        		entity.setValue(count);
        		datas.add(entity);
    		}
    		json = JSONObject.toJSONString(datas);
    	}catch(Exception e){
    		e.printStackTrace();
    		logger.error("get sale callback chart by startTime: {}, endTime :{}, content:{}, error {}",
    				startTime, endTime, content, e);
    	}
    	return json;
    }

    @RequestMapping(value="getDistributeRate.action", method=RequestMethod.POST)
    @ResponseBody
    public String getDistributeRate(){
    	JSONObject json = new JSONObject();
    	try{
    		String masterTotal = redisManager.get(MASTRER_TOTAL);
    		String spareTotal = redisManager.get(SPARE_TOTAL);
    		if(StringUtils.isEmpty(masterTotal)){
    			masterTotal = "0";
    		}
    		if(StringUtils.isEmpty(spareTotal)){
    			spareTotal = "0";
    		}
        	json.put("masterTotal", masterTotal);
        	json.put("spareTotal", spareTotal);
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.error("get distributeRate is error {}", e);
    		json.put("error", e.getMessage());
    	}
    	return json.toJSONString();
    }

    @RequestMapping(value="updateDistributeRate.action", method=RequestMethod.GET)
    public String updateDistributeRate(String masterTotal, String spareTotal,
    		RedirectAttributes redirectAttributes){
    	try{
     		redisManager.set(MASTRER_TOTAL, masterTotal);
    		redisManager.set(SPARE_TOTAL, spareTotal);
    	}catch(Exception e){
    		logger.error("update distributeRate is error {}", e);
    		redirectAttributes.addFlashAttribute("error", e.getMessage());
    	}
    	return "redirect:/sms/listchannel.action";
    }

    //通知类计费条数
    @RequestMapping(value = "statistics.action", method = RequestMethod.GET)
    public ModelAndView statistics(@RequestParam(value="startTime",required=false) String startTime,
    		@RequestParam(value="endTime",required=false) String endTime,
    		@RequestParam(value="channels",required=false) String channels,
    		@RequestParam(value="type",required=false) String type) {
		ModelAndView model = null;
		try {
			model = new ModelAndView("/sms/statistics");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			if (StringUtils.isEmpty(startTime) || "null".equals(startTime)) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				startTime = sdf.format(calendar.getTime());
			} else {
				startTime = URLDecoder.decode(startTime, "UTF-8");
			}

			if (StringUtils.isEmpty(endTime) || "null".equals(endTime)) {
				endTime = sdf.format(new Date(System.currentTimeMillis()));
			} else {
				endTime = URLDecoder.decode(endTime, "UTF-8");
			}

			//默认查询通知类短信
			if (StringUtils.isEmpty(type) || "null".equals(type)) {
				type = "NOTICE";
			} else {
				type = URLDecoder.decode(type, "UTF-8");
			}
			//全部通道
			List<String> allChannels = smsConsoleService.getAllChannelNoByType(type);

			List<String> channelList = null;
			if (StringUtils.isEmpty(channels) || "null".equals(channels)) {
				//查询默认全部通道
				channelList = allChannels;
				if (!channelList.isEmpty()) {
					StringBuilder sbf = new StringBuilder();
					for (String c: allChannels) {
						sbf.append(c).append(",");
					}
					sbf.deleteCharAt(sbf.length() - 1);
					channels = sbf.toString();
				}
			} else {
				channels = URLDecoder.decode(channels, "UTF-8");
				channelList = Arrays.asList(channels.split(","));
			}
			List<SmsStatistics> statistics = null;

			statistics = smsConsoleService.getSmsStatisticsList(startTime, endTime, channelList, type);

			String smsStatisticsChartData = smsConsoleService.getSmsStatisticsChartData(startTime, endTime, channelList);

			model.addObject("startTime", startTime);
			model.addObject("endTime", endTime);
			model.addObject("channels", channels);
			model.addObject("result", statistics);
			model.addObject("allChannels", allChannels);
			model.addObject("type", type);
			model.addObject("smsStatisticsChartData", smsStatisticsChartData);

			return model;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("query sms statistics error {}", e);
			model = new ModelAndView("error");
		    model.addObject("error", e.getMessage());
		}
		return model;
    }

    @RequestMapping(value="getAllChannels.action", method=RequestMethod.GET)
    @ResponseBody
    public String getAllChannels(@RequestParam(value="type") String type){
    	try {
    		//全部通道
    		List<String> allChannels = smsConsoleService.getAllChannelNoByType(type);
    		return JSONObject.toJSONString(allChannels);
    	} catch (Exception e) {
    		e.printStackTrace();
    		logger.error("get all channels json is error {}", e);
    	}
    	return "";
    }
}
