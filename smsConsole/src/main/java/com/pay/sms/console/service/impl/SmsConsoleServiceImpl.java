package com.pay.sms.console.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pay.commons.id.ObjectId;
import com.pay.sms.console.bean.ChannelRate;
import com.pay.sms.console.bean.SmsCallbackEntity;
import com.pay.sms.console.bean.SmsChannelEntity;
import com.pay.sms.console.bean.SmsEntity;
import com.pay.sms.console.bean.SmsRateStatics;
import com.pay.sms.console.bean.SmsSaleCallbackChartData;
import com.pay.sms.console.bean.SmsStatistics;
import com.pay.sms.console.bean.SmsStatisticsChartData;
import com.pay.sms.console.bean.SmsTemplate;
import com.pay.sms.console.bean.SmsToken;
import com.pay.sms.console.bean.SmsType;
import com.pay.sms.console.enums.Constants;
import com.pay.sms.console.mapper.SmsConsoleMapper;
import com.pay.sms.console.redis.RedisManager;
import com.pay.sms.console.service.SmsConsoleService;
import com.pay.sms.console.util.HttpUtil;
import com.pay.sms.console.util.KeyGenerateUtil;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.smsclient.SmsClientApiImpl;

public class SmsConsoleServiceImpl implements SmsConsoleService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private SmsClientApiImpl api;

	private String host;

	private int port;

	private String camelIp;

	private String camelConsole;

	private String pageListUrl;

	private String addSmsTemplateUrl;

	private String updateSmsTemplateUrl;

	private String findSmsTemplateByIdUrl;

	private String tokenPageListUrl;

	private String tokenFindAllUrl;

	@Autowired
	private RedisManager redisManager;

	@Autowired
	private SmsConsoleMapper smsConsoleMapper;

	private final String SUCCESS ="success";

	public void init(){
		this.api = new SmsClientApiImpl(host, port);
		this.logger.info("sms client api init host {} port {} finish", host, port);
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public List<SmsEntity> findSuccessSms(String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findSuccessSms(startTime, endTime, start, size);
	}

	@Override
	public int findSuccessSmsSize(String startTime, String endTime) {
		return this.smsConsoleMapper.findSuccessSmsSize(startTime, endTime);
	}

	@Override
	public SmsEntity findSmsById(int id) {
		return this.smsConsoleMapper.findSmsById(id);
	}

	@Override
	public List<SmsEntity> findSuccessSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findSuccessSmsByCondition(smsTo, content, startTime, endTime, start, size);
	}

	@Override
	public int findSuccessSmsSizeByCondition(String smsTo, String content, String startTime, String endTime) {
		return this.smsConsoleMapper.findSuccessSmsSizeByCondition(smsTo, content, startTime, endTime);
	}

	@Override
	public List<SmsEntity> findSaleSuccessSms(String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findSaleSuccessSms(startTime, endTime, start, size);
	}

	@Override
	public int findSaleSuccessSmsSize(String startTime, String endTime) {
		return this.smsConsoleMapper.findSaleSuccessSmsSize(startTime, endTime);
	}

	@Override
	public SmsEntity findSaleSmsById(int id) {
		return this.smsConsoleMapper.findSaleSmsById(id);
	}

	@Override
	public List<SmsEntity> findSaleSuccessSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findSaleSuccessSmsByCondition(smsTo, content, startTime, endTime, start, size);
	}

	@Override
	public int findSaleSuccessSmsSizeByCondition(String smsTo, String content, String startTime, String endTime ) {
		return this.smsConsoleMapper.findSaleSuccessSmsSizeByCondition(smsTo, content, startTime, endTime);
	}

	@Override
	public SmsResponse sendMessage(SmsBean smsBean) {
		try {
			return this.api.send(smsBean, false);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.info("sms console sendMesage to:{}&content=[{}] error {}", smsBean.getTo(), smsBean.getContent(), e);
			return null;
		}
	}

	@Override
	public List<Map<String, String>> findSmsToken(int start, int size) {
		return this.smsConsoleMapper.findSmsToken(start, size);
	}

	@Override
	public int findSmsTokenSize() {
		return this.smsConsoleMapper.findSmsTokenSize();
	}

	@Transactional
	@Override
	public void deleteToken(String appCode, Integer id) {
		String redisKey = Constants.REDIS_SMS_TOKEN + appCode;
		Map<String, String> result = this.redisManager.del(redisKey);
		if (!"success".equals(result.get("oper_flag"))) {
			throw new RuntimeException("delete redisKey " + redisKey + " error");
		}
		this.smsConsoleMapper.deleteToken(id);
	}

	@Override
	public void saveToken(String appCode) {
		String token = KeyGenerateUtil.generateKey(appCode);
		this.redisManager.set(Constants.REDIS_SMS_TOKEN + appCode, token);
		this.smsConsoleMapper.saveToken(appCode, token);
	}

	@Override
	public String validateToken(String appCode) {
		String token = this.redisManager.get(Constants.REDIS_SMS_TOKEN + appCode);
		if(StringUtils.isEmpty(token)){
			token = this.smsConsoleMapper.findSmsTokenByAppCode(appCode);
		}
		if(StringUtils.isEmpty(token)){
			return "notExisted";
		}else{
			return "isExisted";
		}
	}

	@Override
	public List<Map<String, String>> findTokenByCondition(String appCode, String token,
			int start, int size) {
		return this.smsConsoleMapper.findTokenByCondition(appCode, token, start, size);
	}

	@Override
	public int findTokenSizeByCondition(String appCode, String token) {
		return this.smsConsoleMapper.findTokenSizeByCondition(appCode, token);
	}

	@Override
	public List<SmsEntity> findFailureSms(String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findFailureSms(startTime, endTime, start, size);
	}

	@Override
	public int findFailureSmsSize(String startTime, String endTime) {
		return this.smsConsoleMapper.findFailureSmsSize(startTime, endTime);
	}

	@Override
	public SmsEntity findFailureById(int id) {
		return this.smsConsoleMapper.findFailureById(id);
	}

	@Override
	public List<SmsEntity> findFailureSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findFailureSmsByCondition(smsTo, content, startTime, endTime, start, size);
	}

	@Override
	public int findFailureSmsSizeByCondition(String smsTo, String content, String startTime, String endTime) {
		return this.smsConsoleMapper.findFailureSmsSizeByCondition(smsTo, content, startTime, endTime);
	}

	@Override
	public List<SmsEntity> findFailureHistorySms(String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findFailureHistorySms(startTime, endTime, start, size);
	}

	@Override
	public int findFailureHistorySmsSize(String startTime, String endTime) {
		return this.smsConsoleMapper.findFailureHistorySmsSize(startTime, endTime);
	}

	@Override
	public SmsEntity findFailureHistoryById(int id) {
		return this.smsConsoleMapper.findFailureHistoryById(id);
	}

	@Override
	public List<SmsEntity> findFailureHistorySmsByCondition(String smsTo, String content,
			String startTime, String endTime, int start, int size) {
		return this.smsConsoleMapper.findFailHistSmsByCond(smsTo, content, startTime, endTime, start, size);
	}

	@Override
	public int findFailureHistorySmsSizeByCondition(String smsTo, String content, String startTime, String endTime) {
		return this.smsConsoleMapper.findFailHistSmsSizeByCond(smsTo, content, startTime, endTime);
	}

	@Override
	public void createChannel(SmsChannelEntity smsChannelEntity) {
		smsConsoleMapper.recoverAllChannelCurrentWeight();
		smsConsoleMapper.createChannel(smsChannelEntity.getType(), smsChannelEntity.getName(), smsChannelEntity.getState(),
			smsChannelEntity.getWeight());
	}

	@Override
	public void deleteChannelById(long id) {
		smsConsoleMapper.recoverAllChannelCurrentWeight();
		smsConsoleMapper.deleteChannelById(id);
	}

	@Override
	public void updateChannel(SmsChannelEntity smsChannelEntity) {
		smsConsoleMapper.recoverAllChannelCurrentWeight();
		smsConsoleMapper.updateChannelById(smsChannelEntity.getType(), smsChannelEntity.getName(),
			smsChannelEntity.getState(), smsChannelEntity.getWeight(), smsChannelEntity.getId());

	}

	@Override
	public List<SmsChannelEntity> findAllChannels() {
		return 	smsConsoleMapper.findAllChannel();
	}

	@Override
	public List<SmsCallbackEntity> findCallbackNoticeSms(String startTime, String endTime, int start, int size) {
		return smsConsoleMapper.findCallbackNoticeSms(startTime, endTime, start, size);
	}

	@Override
	public int findCallbackNoticeSmsSize(String startTime, String endTime) {
		return smsConsoleMapper.findCallbackNoticeSmsSize(startTime, endTime);
	}

	@Override
	public List<SmsCallbackEntity> findCallbackNoticeSmsByCond(String startTime, String endTime, String phone, String msgid,
			int start, int size) {
		return this.smsConsoleMapper.findCallbackNoticeSmsByCond(startTime, endTime, phone, msgid, start, size);
	}

	@Override
	public int findCallbackNoticeSmsSizeByCond(String startTime, String endTime, String phone, String msgid){
		return this.smsConsoleMapper.findCallbackNoticeSmsSizeByCond(startTime, endTime, phone, msgid);
	}

	@Override
	public SmsCallbackEntity findNoticeCallbackViewById(String id){
		return this.smsConsoleMapper.findNoticeCallbackViewById(id);
	}

	@Override
	public List<SmsCallbackEntity> findCallbackSaleSms(String startTime, String endTime, int start, int size) {
		return smsConsoleMapper.findCallbackSaleSms(startTime, endTime, start, size);
	}

	@Override
	public int findCallbackSaleSmsSize(String startTime, String endTime) {
		return smsConsoleMapper.findCallbackSaleSmsSize(startTime, endTime);
	}

	@Override
	public List<SmsCallbackEntity> findCallbackSaleSmsByCond(String startTime, String endTime, String phone, String msgid,
			int start, int size) {
		return this.smsConsoleMapper.findCallbackSaleSmsByCond(startTime, endTime, phone, msgid, start, size);
	}

	@Override
	public int findCallbackSaleSmsSizeByCond(String startTime, String endTime, String phone, String msgid){
		return this.smsConsoleMapper.findCallbackSaleSmsSizeByCond(startTime, endTime, phone, msgid);
	}

	@Override
	public SmsCallbackEntity findSaleCallbackViewById(String id){
		return this.smsConsoleMapper.findSaleCallbackViewById(id);
	}

	@Override
	public List<SmsSaleCallbackChartData> getSaleCallbackChartData(String startTime, String endTime, String content){
		return this.smsConsoleMapper.getSaleCallbackChartData(startTime, endTime, content);
	}

	@Override
	public long getNoticeMasterId() {
		return this.smsConsoleMapper.getNoticeMasterId();
	}

	@Override
	public String getSaleCount(String startTime, String endTime, String content) {
		return this.smsConsoleMapper.getSaleCount(startTime, endTime, content);
	}

	@Override
	public List<SmsStatistics> getSmsStatisticsList(String startTime, String endTime, List<String> channels,
			String type) {
		List<SmsStatistics> result = new ArrayList<SmsStatistics>();
		StringBuilder channelParam = new StringBuilder();

		if (channels == null || channels.isEmpty()) {
			return result;
		} else {
			for (String channel: channels) {
				channelParam.append("'").append(channel).append("',");
			}
			channelParam.deleteCharAt(channelParam.length() - 1);
		}
		String channelParamStr = channelParam.toString();
		List<String> dates = getSomeDateStrDesc(startTime, endTime);
		//如果查询间隔错误(开始时间晚于结束时间)
		if(dates == null || dates.isEmpty()) {
			return result;
		}

		Map<String, SmsStatisticsChartData> success = null;
		if ("NOTICE".equals(type)) {
			success = smsConsoleMapper.getNoticeSuccessCount(startTime, endTime, channelParamStr);
		} else {
			success = smsConsoleMapper.getSaleSuccessCount(startTime, endTime, channelParamStr);
		}

		Map<String, SmsStatisticsChartData> fail = smsConsoleMapper.getFailureCount(startTime, endTime, channelParamStr);
		Map<String, SmsStatisticsChartData> failHistory = smsConsoleMapper.getFailureHistoryCount(startTime, endTime, channelParamStr);
		Map<String, SmsStatisticsChartData> statistics = smsConsoleMapper.getStatistics(startTime, endTime, channelParamStr);

		if(!success.isEmpty() || !fail.isEmpty() || !failHistory.isEmpty() || !statistics.isEmpty()) {
			for(String date: dates) {
				long successNumber = getNumber(date, success);
				long failNumber = getNumber(date, fail);
				long failHistoryNumber = getNumber(date, failHistory);
				long statisticsNumber = getNumber(date, statistics);
				//如果每日有发送，或有计费条数
				if ((successNumber + failNumber + failHistoryNumber != 0) || (statisticsNumber != 0)) {
					SmsStatistics s = new SmsStatistics();
					s.setDate(date);
					long sum = successNumber + failNumber + failHistoryNumber;
					s.setNumber(sum);
					s.setSuccessNumber(successNumber);
					float successRate = 0.0f;
					//保留2位小数
					if (sum != 0) {
						successRate = (float) Math.floor(successNumber * 10000 / (float) sum) / 100;
					}

					s.setSuccessRate(successRate);
					s.setCountNumber(statisticsNumber);
					result.add(s);
				}
			}
		}
		return result;
	}

	/**
	 * 获取一段时间间隔内的全部时间字符串[start,end]
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private List<String> getSomeDateStr(String startTime, String endTime) {
		List<String> dates = new ArrayList<String>();
		try {
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date endDate = sdf.parse(endTime);
			start.setTime(sdf.parse(startTime));
			end.setTime(endDate);
			//如果end大于start
			if (endDate.after(start.getTime())) {
				while (endDate.after(start.getTime())) {
					dates.add(sdf.format(start.getTime()));
					start.add(Calendar.DAY_OF_MONTH, 1);
				}
				dates.add(sdf.format(endDate));
			} else if (startTime.equals(endTime)) {//如果是同一天
				dates.add(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dates;
	}

	/**
	 * 获取一段时间间隔内的全部时间字符串[start,end](时间倒序)
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private List<String> getSomeDateStrDesc(String startTime, String endTime) {
		List<String> dates = new ArrayList<String>();
		try {
			Calendar start = Calendar.getInstance();
			Calendar end = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = sdf.parse(startTime);
			Date endDate = sdf.parse(endTime);
			start.setTime(startDate);
			end.setTime(endDate);
			//如果start小于end
			if (startDate.before(end.getTime())) {
				while (startDate.before(end.getTime())) {
					dates.add(sdf.format(end.getTime()));
					end.add(Calendar.DAY_OF_MONTH, -1);
				}
				dates.add(sdf.format(end.getTime()));
			} else if (startTime.equals(endTime)) {//如果是同一天
				dates.add(endTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dates;
	}

	/**
	 * 获取某日短信计数(对map的value==null的返回0)
	 * @param date
	 * @param map
	 * @return
	 */
	private long getNumber(String date, Map<String, SmsStatisticsChartData> map) {
		SmsStatisticsChartData data = map.get(date);
		if (data == null) {
			return 0;
		}
		return data.getNumber();
	}

	@Override
	public List<String> getAllChannelNoByType(String type) {
		return smsConsoleMapper.getAllChannelNoByType(type);
	}

	@Override
	public String getSmsStatisticsChartData(String startTime, String endTime,
			List<String> channels) {
		StringBuilder channelParam = new StringBuilder();
		//如果没有查询通道
		if (channels == null || channels.isEmpty()) {
			return "''";
		} else {
			for (String channel: channels) {
				channelParam.append("'").append(channel).append("',");
			}
			channelParam.deleteCharAt(channelParam.length() - 1);
		}
		String channelParamStr = channelParam.toString();

		List<String> dates = getSomeDateStr(startTime, endTime);
		//如果查询间隔错误(开始时间晚于结束时间)
		if (dates == null || dates.isEmpty()) {
			return "''";
		}

		Map<String, SmsStatisticsChartData> statistics = smsConsoleMapper.getStatistics(startTime, endTime, channelParamStr);
		//如果没有计费条数数据
		if (statistics.isEmpty()) {
			return "''";
		}
		List<SmsStatisticsChartData> list = new ArrayList<SmsStatisticsChartData>();
		for (String date: dates) {
			SmsStatisticsChartData data = statistics.get(date);
			if (data == null) {
				data = new SmsStatisticsChartData();
				data.setDate(date);
			}
			list.add(data);
		}
		return JSONObject.toJSONString(list);
	}

	@Override
	public SmsChannelEntity findChannelByID(String id) {
		return this.smsConsoleMapper.findChannelByID(id);

	}

	@Override
	public void updateSttus(long id, String status) {
		smsConsoleMapper.updateSttus(id,status);

	}

	@Override
	public List<SmsToken> tokenPageList(int page, int size) {
		return smsConsoleMapper.tokenPageList(page, size);
		//{"data":[{"id":19,"token":"fP0m3y3qpuTaiZoQXXmBbA==","appName":"dsp-monitor","lfsAuthority":false,"camelAuthority":true,"createTime":1526607370000,"updateTime":1526607370000},{"id":18,"token":"VcWPpUk5xK0XuiZ94RMgdg==","appName":"liuweyiceshi4","lfsAuthority":true,"camelAuthority":false,"createTime":1526607370000,"updateTime":1526608827000},{"id":17,"token":"IQzL9oA8vvYO10G7ux6Nnw==","appName":"liuweiyiceshi123","lfsAuthority":true,"camelAuthority":false,"createTime":1526607370000,"updateTime":1526607370000},{"id":16,"token":"D/+At3M0bL8YMetS4MRXnGq56doS7+gy","appName":"camelliuweiyiceshi","lfsAuthority":false,"camelAuthority":true,"createTime":1526466594000,"updateTime":1526466594000},{"id":15,"token":"RPpljiRWWaDx7iXQFyRe7g==","appName":"rgesfsefesfesf","lfsAuthority":false,"camelAuthority":true,"createTime":1526466346000,"updateTime":1526466346000},{"id":14,"token":"IQzL9oA8vvaIFpDdTI3V7g==","appName":"liuweiyiceshi11","lfsAuthority":true,"camelAuthority":true,"createTime":1526466033000,"updateTime":1526466033000},{"id":13,"token":"IQzL9oA8vvboqnKDM2737g==","appName":"liuweiyiceshi","lfsAuthority":false,"camelAuthority":true,"createTime":1526465793000,"updateTime":1526465793000},{"id":11,"token":"SMjy2zjnoatXiLVd3A6WGI436cqyH11o","appName":"smsserver","lfsAuthority":false,"camelAuthority":false,"createTime":1526020809000,"updateTime":1526020809000},{"id":8,"token":"SMjy2zjnoatXiLVd3A6WGI436cqyHT8o","appName":"hestia-core","lfsAuthority":false,"camelAuthority":false,"createTime":1526020751000,"updateTime":1526020751000},{"id":7,"token":"SMjy2zjnoatXiLVd3A6WGI436cqyHT7o","appName":"unified-product-core","lfsAuthority":false,"camelAuthority":false,"createTime":1526020727000,"updateTime":1526020727000}],"recordsTotal":16,"recordsFiltered":16}

	}


	public JSONObject tokenPageList(int start, int length, String appName, String startTime, String endTime) {

		String url = tokenPageListUrl;
		StringBuffer bf = new StringBuffer();
		JSONObject json = new JSONObject();
		try {
			bf.append("start=" + start + "&length=" + length);
			if(StringUtils.isNotBlank(startTime)){
				startTime = URLEncoder.encode(startTime, "UTF-8");
			}
			if(StringUtils.isNotBlank(startTime)){
				endTime =URLEncoder.encode(endTime, "UTF-8");
			}
			if(StringUtils.isNotBlank(appName)){
				bf.append("&appName=" + appName);
			}
			if(startTime != null){
				bf.append("&startTime=" + startTime);
			}
			if(endTime != null){
				bf.append("&endTime=" + endTime);
			}
			logger.info("url={}?{}",url,bf.toString());
			String trim = bf.toString().trim();
			String result = HttpUtil.sendGet(url, trim);
			logger.info("request token-server pageList result={}",result);
			json = (JSONObject)JSONObject.parse(result);
		} catch (UnsupportedEncodingException e) {
			logger.info("request token-server pageList error",e);
		}
		return json;
	}

	@Override
	public List<SmsToken> tokenSearch(int page, int size, String startTime,
			String endTime, String appCode) {
		List<SmsToken> tokens = new ArrayList<SmsToken>();
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)){
			tokens = smsConsoleMapper.tokenSearchByAppCode(page, size, appCode);
		}else{
			tokens = smsConsoleMapper.tokenSearch(page, size, startTime, endTime, appCode);
		}

		return tokens;
	}

	@Override
	public SmsToken findTokenById(Integer id) {
		return smsConsoleMapper.findTokenById(id);
	}

	@Override
	public void insertToken(SmsToken token) {
		smsConsoleMapper.insertToken(token.getAppCode(), token.getToken(), token.getAppName(), token.getOperator());
	}

	@Override
	public void updateToken(SmsToken token) {
		smsConsoleMapper.updateToken(token.getAppCode(), token.getToken(), token.getAppName(), token.getOperator(),token.getId());
	}

	@Override
	public int findTokenCountByAppCount(String startTime, String endTime,
			String appCode) {
		int count = 0;
		if(StringUtils.isBlank(startTime)||StringUtils.isBlank(endTime)){
			count = smsConsoleMapper.findTokenCountAppCode(appCode);
		}else{
			count = smsConsoleMapper.findTokenCount(startTime, endTime, appCode);

		}
		return count;
	}

	@Override
	public List<SmsType> findliseType() {
		return this.smsConsoleMapper.findliseType();
	}

	@Override
	public List<SmsType> pageListSmsType(String startTime, String endTime,
			int start, int pageSize, String typeCode) {
		return this.smsConsoleMapper.pageListSmsType(startTime,endTime,start,pageSize,typeCode);
	}

	@Override
	public int pageListSmsTypeCount(String startTime, String endTime,
			String typeCode) {
		return this.smsConsoleMapper.pageListSmsTypeCount(startTime,endTime,typeCode);
	}

	@Override
	public List<SmsChannelEntity> findsmsList() {
		return this.smsConsoleMapper.findsmsList();
	}

	@Override
	public void createChannel(SmsType smsType) {
		String typeCode =smsType.getTypeCode();
		smsType.setTypeCode(typeCode);
		insertChannelRate(smsType.getStrLis(),typeCode);
		smsConsoleMapper.createSmsType(smsType.getTypeCode(),smsType.getTypeName(),
				smsType.getOperator(),smsType.getRemark());
	}

	private void insertChannelRate(String strLis,String typeCode) {
		String[] channelRate = strLis.split("~~~~");
		for (int i = 0; i < channelRate.length; i++) {
			String zu = channelRate[i];
			if(StringUtils.isNotBlank(zu)){
				String zulist[] = zu.split("~~");
				String chanlel = zulist[0];//推送等级
				String rate = zulist[1];//消息类型
				ChannelRate channelRateentity = new ChannelRate();
				channelRateentity.setChannelCode(chanlel);
				channelRateentity.setTypeCode(typeCode);
				channelRateentity.setRate(Integer.valueOf(rate));
				smsConsoleMapper.insertChannelRate(channelRateentity.getTypeCode(),channelRateentity.getChannelCode(),channelRateentity.getRate(),
						channelRateentity.getOperator());
				}

		}

	}

	@Override
	public SmsType findSmsTypeByID(String typeCode) {
		return this.smsConsoleMapper.findSmsTypeByID(typeCode);
	}

	@Override
	public void updateSmsType(SmsType smsType) {

		if(StringUtils.isNotBlank(smsType.getStrLis())){
			List<ChannelRate> rateList = smsConsoleMapper.findRate(smsType.getTypeCode());
			logger.info("updateSmsType.action operator={},before list={},afert strlist={}",
					smsType.getOperator(),rateList.toString(),smsType.getStrLis());
			smsConsoleMapper.deleteRate(smsType.getTypeCode());
			insertChannelRate(smsType.getStrLis(),smsType.getTypeCode());
		}

		smsConsoleMapper.updateSmsType(smsType.getTypeName(),
				smsType.getOperator(),smsType.getRemark(), smsType.getTypeCode());

	}

	@Override
	public SmsType checksms(String typeName, String channelCode,String typeCode) {
		return this.smsConsoleMapper.checksms(typeName,channelCode,typeCode);
	}

	@Override
	public JSONObject smsTemplatePageList(int start, int end,
			String templateCode, String typeCode, String title, String appCode,
			String startTime, String endTime) {
		JSONObject json = new JSONObject();

		try {
			if(StringUtils.isNotBlank(startTime)){
				startTime = URLEncoder.encode(startTime, "UTF-8");
			}
			if(StringUtils.isNotBlank(startTime)){
				endTime =URLEncoder.encode(endTime, "UTF-8");
			}
			String url = camelIp + camelConsole + pageListUrl;
			String param="start=" + start + "&end=" + end + "&templateCode=" + templateCode + "&typeCode=" + typeCode
					+ "&title=" + title + "&appCode=" + appCode + "&startTime=" + startTime +"&endTime="+ endTime;
			logger.info("url={}?{}",url,param);
			String result = HttpUtil.sendGet(url, param);
			logger.info("request camel-console pageList result={}",result);
			if(StringUtils.isNotBlank(result)){
				json = (JSONObject)JSONObject.parse(result);
			}
		} catch (Exception e) {
			logger.error("request camel-console pageList error",e);
		}
		return json;
	}


	@Override
	public SmsToken findTokenByAppCode(String appCode) {
		return smsConsoleMapper.findTokenByAppCode(appCode);
	}

	@Override
	public List<SmsType> findSmsType() {
		List<SmsType> type = smsConsoleMapper.findliseType();
		return type;
	}


	public void setCamelIp(String camelIp) {
		this.camelIp = camelIp;
	}


	public void setCamelConsole(String camelConsole) {
		this.camelConsole = camelConsole;
	}

	public void setAddSmsTemplateUrl(String addSmsTemplateUrl) {
		this.addSmsTemplateUrl = addSmsTemplateUrl;
	}

	public void setUpdateSmsTemplateUrl(String updateSmsTemplateUrl) {
		this.updateSmsTemplateUrl = updateSmsTemplateUrl;
	}



	public void setPageListUrl(String pageListUrl) {
		this.pageListUrl = pageListUrl;
	}

	@Override
	public String saveSmsTemplate(SmsTemplate smsTemplate) {
		String result = null;
		try {
			String	content = URLEncoder.encode(smsTemplate.getContent(), "UTF-8");

			String url = camelIp + camelConsole + addSmsTemplateUrl;
			String param="templateCode=" + smsTemplate.getTemplateCode() + "&typeCode=" + smsTemplate.getTypeCode() + "&title=" + smsTemplate.getTitle()
					+ "&appCode=" + smsTemplate.getAppCode() + "&content=" + content + "&operator=" + smsTemplate.getOperator() ;


			result = HttpUtil.sendGet(url, param);
			JSONObject json = (JSONObject)JSONObject.parse(result);
			String resultString = json.getString("result");
			if("success".equals(resultString)){
				result = "success";
			}else {
				String msg = json.getString("msg");
				if(StringUtils.isBlank(msg)){
					result = "fail";
				}else{
					result = "repeat";
				}

			}
			logger.info("request camel-console addSmsTemplate result={}",result);

		} catch (Exception e) {
			logger.error("request camel-console addSmsTemplate error",e);
		}
		return result;
	}

	@Override
	public String updateSmsTemplate(SmsTemplate smsTemplate) {
		String result = null;
		try {
			String	content = URLEncoder.encode(smsTemplate.getContent(), "UTF-8");
			String url = camelIp + camelConsole + updateSmsTemplateUrl;
			String param="templateCode=" + smsTemplate.getTemplateCode() + "&typeCode=" + smsTemplate.getTypeCode() + "&title=" + smsTemplate.getTitle()
					+ "&appCode=" + smsTemplate.getAppCode() + "&content=" + content + "&operator=" + smsTemplate.getOperator();

			result = HttpUtil.sendGet(url, param);
			JSONObject json = (JSONObject)JSONObject.parse(result);
			String resultString = json.getString("result");
			if("success".equals(resultString)){
				result = "success";
			}else {
				String msg = json.getString("msg");
				if(StringUtils.isBlank(msg)){
					result = "fail";
				}else{
					result = "repeat";
				}

			}
			logger.info("request camel-console updateSmsTemplate result={}",result);

		} catch (Exception e) {
			logger.error("request camel-console updateSmsTemplate error",e);
		}
		return result;
	}

	@Override
	public List<SmsToken> findAllToken() {
		return  smsConsoleMapper.findAllToken();
	}

	@Override
	public List<ChannelRate> findRate(String typeCode) {
		return  smsConsoleMapper.findRate(typeCode);
	}

	public void setFindSmsTemplateByIdUrl(String findSmsTemplateByIdUrl) {
		this.findSmsTemplateByIdUrl = findSmsTemplateByIdUrl;
	}

	@Override
	public SmsTemplate findSmsTemplateByCode(String templateCode) {
		String result = null;
		SmsTemplate smsTemplate = new SmsTemplate();
		try {
			String url = camelIp + camelConsole + findSmsTemplateByIdUrl;
			String param="templateCode=" + templateCode;
			result = HttpUtil.sendGet(url, param);
			logger.info("request camel-console updateSmsTemplate result={}",result);
			if(StringUtils.isNotBlank(result)){
				JSONObject jo = (JSONObject)JSONObject.parse(result);
				smsTemplate.setAppCode(jo.getString("appCode"));
				smsTemplate.setTemplateCode(jo.getString("templateCode"));
				smsTemplate.setTypeCode(jo.getString("typeCode"));
				smsTemplate.setTitle(jo.getString("title"));
				smsTemplate.setContent(jo.getString("content"));
				smsTemplate.setOperator(jo.getString("operator"));
				smsTemplate.setTypeName(jo.getString("typeName"));
			}

		} catch (Exception e) {
			logger.error("request camel-console updateSmsTemplate error",e);
		}
		return smsTemplate;
	}

	public void setTokenPageListUrl(String tokenPageListUrl) {
		this.tokenPageListUrl = tokenPageListUrl;
	}

	public void setTokenFindAllUrl(String tokenFindAllUrl) {
		this.tokenFindAllUrl = tokenFindAllUrl;
	}

	@Override
	public List<SmsToken> tokenFindAll() {
		String url = tokenFindAllUrl;
		logger.info("url={}",url);
		String result = HttpUtil.sendGet(url,null);
		logger.info("request token-server findAll result={}",result);
		JSONObject json = (JSONObject)JSONObject.parse(result);
		List<SmsToken> list = new ArrayList<SmsToken>();
		if(json != null){
			JSONArray jsonArray = json.getJSONArray("data");
			if(jsonArray != null){
				for (Object object : jsonArray) {
					JSONObject jo = (JSONObject)object;
					SmsToken token = new SmsToken();
					token.setAppCode(jo.getString("appName"));
					list.add(token);
				}
			}
		}

		return list;

	}

	@Override
	public List<SmsChannelEntity> findAllChannelsnew() {
		return 	smsConsoleMapper.findAllChannelsnew();

	}

	@Override
	public void createChannelnew(SmsChannelEntity smsChannelEntity) {
		smsChannelEntity.setChannelCode(ObjectId.get().toString());
		smsConsoleMapper.createChannelnew(smsChannelEntity.getChannelCode(), smsChannelEntity.getName(), smsChannelEntity.getChannelCost(),
				smsChannelEntity.getRemark(),smsChannelEntity.getChannelContact());


	}

	@Override
	public void updateChannelnew(SmsChannelEntity smsChannelEntity) {
		smsConsoleMapper.updateChannelnew(smsChannelEntity.getName(),
				smsChannelEntity.getId(),smsChannelEntity.getChannelContact(),smsChannelEntity.getChannelCost(),smsChannelEntity.getRemark());


	}

	@Override
	public List<SmsCallbackEntity> smsListSearch(String startTime,
			String endTime, String phone, String templateCode, String typeCode,
			String appCode, String channelNo, String status, int index, int pageSize) {
		return this.smsConsoleMapper.smsListSearch(startTime, endTime, phone, templateCode, typeCode,
				appCode,channelNo,status,index,pageSize);
	}

	@Override
	public int smsListSearchCount(String startTime, String endTime,
			String phone, String templateCode, String typeCode, String appCode,
			String channelNo, String status) {
		return this.smsConsoleMapper.smsListSearchCount(startTime, endTime, phone, templateCode, typeCode,
				appCode,channelNo,status);
	}

	@Override
	public SmsCallbackEntity findsmsQueryViewById(String id) {
		return this.smsConsoleMapper.findsmsQueryViewById(id);

	}

	@Override
	public SmsCallbackEntity findsmsQuerySaleViewById(String id) {
		return this.smsConsoleMapper.findsmsQuerySaleViewById(id);
	}

	@Override
	public List<SmsRateStatics> pageListSmsRateStatics(int start, int page,
			String appCode, List<String> typeCodes, List<String> channelCodes,
			String startTime, String endTime) {
		List<SmsRateStatics> list = smsConsoleMapper.pageListSmsRateStatics(start, page, appCode, typeCodes, channelCodes, startTime, endTime);
		return list;
	}

	@Override
	public int countSmsRateStatics(String appCode, List<String> typeCodes,
			List<String> channelCodes, String startTime, String endTime) {

		return smsConsoleMapper.countSmsRateStatics(appCode, typeCodes, channelCodes, startTime, endTime);
	}



}
