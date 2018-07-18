package com.pay.smsserver.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.UpLinkEntity;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsserver.astrotrain.CamelAstrotrainProducer;
import com.pay.smsserver.bean.MessageResponse;
import com.pay.smsserver.bean.SmsConfig;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.bean.SmsRateStatics;
import com.pay.smsserver.bean.SmsStatusEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.channel.MessageChannelContext;
import com.pay.smsserver.constants.SmsConstants;
import com.pay.smsserver.mapper.ChannelMapper;
import com.pay.smsserver.mapper.TokenMapper;
import com.pay.smsserver.redis.RedisManager;
import com.pay.smsserver.service.ChannelService;
import com.pay.smsserver.util.DateUtil;

public class ChannelServiceImpl implements ChannelService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private int expandCodeLength;//扩展码长度
	private int failCount;

	private MessageChannelContext messageChannelContext;
	private ChannelMapper channelMapper;
	private TokenMapper tokenMapper;
	private RedisManager redisManager;

	//redis中masterTotal的key,需和smsConsole项目一致
	private static final String MASTRER_TOTAL = "smsserver_master_total";
	//redis中spareTotal的key,需和smsConsole项目一致
	private static final String SPARE_TOTAL = "smsserver_spare_total";
	private int masterTotal;
	private int masterCount;
	private int spareTotal;
	private int spareCount;
	//备用通道list发送的下标
	private int spareIndex = -1;
	//营销通道list发送的下标
	private int saleIndex = -1;
	private boolean useMaster = true;//是否使用主通道
	private Object noticeLock = new Object();
	private Object saleLock = new Object();
	private Object telLock = new Object();
	private String masterChannelName = null;

	private CamelAstrotrainProducer producer;

	@Override
	public String getExpandCodeByAppCode(String appCode){
		String expandCode = redisManager.get(appCode);
		if(!StringUtils.isEmpty(expandCode)){
			logger.info("get expandCode={} in redis, by appCode={}", expandCode, appCode);
			return expandCode;
		}
		expandCode = channelMapper.getExpandCodeByAppCode(appCode);//redis中无appCode-expandCode,从db查询
		if(StringUtils.isEmpty(expandCode)){//db无appCode-expandCode，生成四位不重复的整数---采用SMS_TOKEN表主键，不足四位前面补充0，满足四位取最后四位
			Integer appCodeId = tokenMapper.getIdByAppCode(appCode);
			if(appCodeId == null){
				logger.info("find appCodeId={} by appCode={}, can not product expandCode", appCodeId, appCode);
				return null;
			}
			expandCode = getExpandCode(String.valueOf(appCodeId));
			channelMapper.insertAppCodeExpandCode(appCode, expandCode);
			logger.info("create expandCode={}, by appCode={}", expandCode, appCode);
		}
		redisManager.set(appCode, expandCode);
		redisManager.set(expandCode, appCode);
		logger.info("get expandCode={} in DB, by appCode={} save to redis", expandCode, appCode);
		return expandCode;
	}

	@Override
	public String getAppCodeByExpandCode(String portNumber) {
		if(StringUtils.isEmpty(portNumber)){
			return null;
		}
		String appCode = null;
		String expandCode = getExpandCode(portNumber);
		logger.info("portNumber={} change to expandCode={}", portNumber, expandCode);
		//数据合法性验证
		appCode = redisManager.get(expandCode);
		logger.info("get appCode={} from redis, by expandCode={}", appCode, expandCode);
		if(StringUtils.isEmpty(appCode)){
			appCode = channelMapper.getAppCodeByExpandCode(expandCode);
			logger.info("get appCode={} from db, by expandCode={}", appCode, expandCode);
			if(!StringUtils.isEmpty(appCode)){
				redisManager.set(expandCode, appCode);
			}
		}
		return appCode;
	}

	private String getExpandCode(String source){
		int length = source.length();
		String target = null;
		switch(length){
			case 0 : target = "0000"; break;
			case 1 : target = "000" + source; break;
			case 2 : target = "00" + source; break;
			case 3 : target = "0" + source; break;
			case 4 : target =  source; break;
			default : target = source.substring(source.length() - expandCodeLength, source.length()); break;
		}
		return target;
	}

	@Override
	public SmsEntity fillData(SmsBean smsBean){
		SmsEntity smsEntity = new SmsEntity();
		smsEntity.setTo(smsBean.getTo());
		String content = smsBean.getContent();
		content = content.replaceAll("\r\n", "\n");
		content = content.replaceAll("\\\\n", "\n");
		smsEntity.setContent(content);
		smsEntity.setIp(smsBean.getIp());
		smsEntity.setToken(smsBean.getToken());
		smsEntity.setUniqueKey(smsBean.getUniqueKey());
		if (smsBean.getTime() == null) {
			smsEntity.setPreTime(new Date());
		} else {
			smsEntity.setPreTime(smsBean.getTime());
		}
		if (smsBean.getLevel() == null) {
			smsEntity.setLevel(SmsSendLevel.NORMAL);
		} else {
			smsEntity.setLevel(smsBean.getLevel());
		}
		if (smsBean.getType() == null) {
			smsEntity.setType(SmsSendType.NOTICE);
		} else {
			smsEntity.setType(smsBean.getType());
		}
		smsEntity.setAppCode(smsBean.getAppCode());
		smsEntity.setTemplateCode(smsBean.getTemplateCode());
		smsEntity.setTypeCode(smsBean.getTypeCode());
		return smsEntity;
	}

	@Override
	public MessageChannel filterChannel(String status, String type, String failChannelName, String appCode) {
		MessageChannel channel = null;
		try {
			if(StringUtils.isEmpty(status)){
				status = SmsConfig.SMS_STATE_TRUE;
			}
			if(StringUtils.isEmpty(type)){
				type = SmsConfig.SMS_TYPE_NOTICE;
			}
			//根据appCode获取通道信息，无则获取全局通道信息
			List<SmsConfig> smsConfigs = channelMapper.listChannelsByStatusTypeAppCode(status, type, appCode);
			if(smsConfigs == null || smsConfigs.size() == 0){
				smsConfigs = channelMapper.listChannelsByStatusType(status, type);
			}
			if(smsConfigs == null || smsConfigs.size() == 0){
				logger.info("getChannel is null by status={} type={}", status, type);
				return null;
			}

			SmsConfig smsConfig = null;
			if(!StringUtils.isEmpty(failChannelName)){//失败重发则不再使用原通道
				if(smsConfigs.size() != 1){
					for(Iterator<SmsConfig> it = smsConfigs.iterator(); it.hasNext(); ){
						if(failChannelName.equals(it.next().getSmsChannel())){
							it.remove();
							break;
						}
					}
					Random random = new Random();
					int index = random.nextInt(smsConfigs.size());
					smsConfig = smsConfigs.get(index);
				}else{
					smsConfig = smsConfigs.get(0);
				}
			}else{
				if(SmsConfig.SMS_TYPE_NOTICE.equals(type)){
					smsConfig = getNoticeSmsConfig(smsConfigs);
				}else if(SmsConfig.SMS_TYPE_SALE.equals(type)){
					smsConfig = getSaleSmsConfig(smsConfigs);
				}
			}
			if(smsConfig != null){
				channel = messageChannelContext.getChannel(smsConfig.getSmsChannel());
			}
		} catch (Exception e) {
			logger.error("getChannel by status={} type={} error={}", status, type, e);
			e.printStackTrace();
		}
		return channel;
	}

	@Override
	public void handleMessageResponses(SmsEntity smsEntity, MessageChannel messageChannel, List<MessageResponse> messasgeResponses){
		if(messasgeResponses != null){
			for(MessageResponse messageResponse : messasgeResponses){
				if(messageResponse.getResult() == 0){
					smsEntity.setTo(messageResponse.getMobile());
					smsEntity.setMsgid(messageResponse.getMsgId());
					smsEntity.setTime(new Date());
					smsEntity.setCreateTime(new Date());
					smsEntity.setModifiedTime(new Date());
					if(smsEntity.getType() == SmsSendType.NOTICE){
						//新增 短信类型 短信模板字段
						channelMapper.insertNoticeSuccessRecord(smsEntity);
					}else{
						//新增短信类型 短信模板字段
						channelMapper.insertSaleSuccessRecord(smsEntity);
					}
					messageChannel.numberStatistics(smsEntity);
					if(!StringUtils.isEmpty(smsEntity.getUniqueKey())){
						producer.sendStringMessage(smsEntity.getUniqueKey(), smsEntity.getCreateTime());
						logger.info("producer send msg uniqueKey={},receiveTime={}",smsEntity.getUniqueKey(), smsEntity.getCreateTime());
					}

				}else{
					smsEntity.setTo(messageResponse.getMobile());
					smsEntity.setMemo(messageResponse.getMemo());
					smsEntity.setTime(new Date());
					smsEntity.setCreateTime(new Date());
					smsEntity.setModifiedTime(new Date());
					//新短信模板 短信类型字段
					channelMapper.insertFailRecord(smsEntity);
				}
			}
		}
	}

	@Override
	public void failHandleMessageResponses(SmsEntity smsEntity,
			MessageChannel messageChannel, List<MessageResponse> messasgeResponses){
		if(messasgeResponses != null){
			for(MessageResponse messageResponse : messasgeResponses){
				if(messageResponse.getResult() == 0){
					smsEntity.setTo(messageResponse.getMobile());
					smsEntity.setMsgid(messageResponse.getMsgId());
					smsEntity.setTime(new Date());
					smsEntity.setCreateTime(new Date());
					smsEntity.setModifiedTime(new Date());
					if(smsEntity.getType() == SmsSendType.NOTICE){
						channelMapper.insertNoticeSuccessRecord(smsEntity);
					}else{
						channelMapper.insertSaleSuccessRecord(smsEntity);
					}
					messageChannel.numberStatistics(smsEntity);
					channelMapper.deleteFailRecord(smsEntity);
					if(!StringUtils.isEmpty(smsEntity.getUniqueKey())){
						producer.sendStringMessage(smsEntity.getUniqueKey(), smsEntity.getCreateTime());
						logger.info("producer send msg uniqueKey={},receiveTime={}",smsEntity.getUniqueKey(), smsEntity.getCreateTime());
					}
				}else{
					smsEntity.setTo(messageResponse.getMobile());
					smsEntity.setMemo(messageResponse.getMemo());
					smsEntity.setFailCount(smsEntity.getFailCount() + 1);
					smsEntity.setCreateTime(new Date());
					smsEntity.setModifiedTime(new Date());
					if(smsEntity.getFailCount() >= failCount){
						channelMapper.insertFailHistoryRecord(smsEntity);
						channelMapper.deleteFailRecord(smsEntity);
					}else{
						channelMapper.updateFailInfos(smsEntity);
					}
				}
			}
		}
	}

	@Override
	public void numberStatistics(SmsEntity smsEntity, String signature, int singleMaxNum, int multiMaxNum) {

		int size = smsEntity.getTo().split(",").length;//手机号码的个数
		int length = smsEntity.getContent().length() + signature.length();
		int messageCount = 0;//短信条数
		if(length <= singleMaxNum){
			messageCount = size;
		}else{
			int count = length % multiMaxNum == 0 ? length / multiMaxNum : (length / multiMaxNum) + 1;
			messageCount = size * count;
		}

		Calendar cal = Calendar.getInstance();
		String startTime = DateUtil.dateToStr(cal.getTime(), "yyyyMMdd");
		cal.add(Calendar.DAY_OF_MONTH, 1);
		String endTime = DateUtil.dateToStr(cal.getTime(), "yyyyMMdd");

		String startKey = SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + startTime;
		Long currentCount = redisManager.incrBy(startKey, messageCount);

		if(currentCount == null){//redis出现故障，记录日志，以便后续补充
			logger.info("{} redis incrBy key={} messageCount={} error", smsEntity.getChannelNo(), startKey, messageCount);
		}

		if(currentCount == messageCount){//新一天的开始
			channelMapper.insertStatisticsCount(startTime, endTime, smsEntity.getChannelNo(), messageCount);
			logger.info("{} insert statistics count startTime={} endTime={} messageCount={}",
					smsEntity.getChannelNo(), startTime, endTime, messageCount);

			cal.add(Calendar.DAY_OF_MONTH, -2);
			String beforeTime = DateUtil.dateToStr(cal.getTime(), "yyyyMMdd");
			String beforeKey = SmsConstants.REDIS_SMS_COUNT_KEY + smsEntity.getChannelNo() + beforeTime;
			Long beforeCount = 0l;
			String beforeCountStr = redisManager.get(beforeKey);
			if(!StringUtils.isEmpty(beforeCountStr)){
				beforeCount = Long.parseLong(beforeCountStr);
			}
			if(beforeCount > 0l){
				channelMapper.updateStatisticsCount(beforeTime, startTime, smsEntity.getChannelNo(), beforeCount);
				logger.info("{} update statistics count startTime={} endTime={} messageCount={}",
						smsEntity.getChannelNo(), beforeTime, startTime, beforeCount);
			}else{
				logger.info("{} redis get key={} beforeCount={} error", smsEntity.getChannelNo(), beforeKey, beforeCount);
			}
//			redisManager.del(beforeKey);
		}
	}

	private SmsConfig getNoticeSmsConfig(List<SmsConfig> configs){
		synchronized (noticeLock) {
			//如果只有一个通道，则直接返回
			if(configs.size() == 1){
				logger.info("smsConfig size is 1, distribute get notice channel {}", configs.get(0).getSmsChannel());
				return configs.get(0);
			}
			//检测主通道是否变更
			checkMasterStatus(configs);
			//获取主备通道各自占比
			int masterTotalInRedis = this.getMasterTotalInRedis(masterTotal);
			int spareTotalInRedis = this.getSpareTotalInRedis(spareTotal);
			if(useMaster){//使用主通道
				masterCount++;
				if(masterCount >= masterTotalInRedis){
					masterCount = 0;
					useMaster = false;//开始使用备通道
				}
				SmsConfig masterSmsConfig = getMaster(configs);
				logger.info("distribute get master notice channel {}", masterSmsConfig.getSmsChannel());
				return masterSmsConfig;
			}else{//使用备通道
				spareCount++;
				if(spareCount >= spareTotalInRedis){
					spareCount = 0;
					useMaster = true;//开始使用主通道
				}
				List<SmsConfig> spareList = getSpareList(configs);
				//防止在获取备用通道时，备用通道变少的情况，如果不校验，数组下标可能越界
				if(spareIndex >= spareList.size() - 1){
					spareIndex = -1;
				}
				spareIndex++;
				SmsConfig spareSmsConfig = spareList.get(spareIndex);
				logger.info("distribute get spare notice channel {}", spareSmsConfig.getSmsChannel());
				return spareSmsConfig;
			}
		}
	}

	private SmsConfig getSaleSmsConfig(List<SmsConfig> configs){
		synchronized (saleLock) {
			//如果只有一个通道，则直接返回
			if(configs.size() == 1){
				logger.info("smsConfig size is 1, distribute get sale channel {}", configs.get(0).getSmsChannel());
				return configs.get(0);
			}
			if(saleIndex >= configs.size() - 1){
				saleIndex = -1;
			}
			saleIndex++;
			SmsConfig smsConfig = configs.get(saleIndex);
			logger.info("distribute get sale channel {}", smsConfig.getSmsChannel());
			return smsConfig;
		}
	}

	//检测主通道是否变更
	private void checkMasterStatus(List<SmsConfig> smsConfigs){
		//第一次查询通道状态
		if(masterChannelName == null){
			//获取主通道
			masterChannelName = getMaster(smsConfigs).getSmsChannel();
		}else{
			String newMasterChannelName = getMaster(smsConfigs).getSmsChannel();
			//主通道变更，清空计数
			if(!masterChannelName.equals(newMasterChannelName)){
				masterChannelName = newMasterChannelName;
				masterCount = 0;
				spareCount = 0;
				spareIndex = -1;
				useMaster = true;
			}
		}
	}

	private int getMasterTotalInRedis(int masterTotal){
		try{
			String value = redisManager.get(MASTRER_TOTAL);
			if(StringUtils.isEmpty(value)){
				redisManager.set(MASTRER_TOTAL, masterTotal+"");
				value = masterTotal+"";
			}
			//如果主通道分流值为0,不使用主通道
			if("0".equals(value)){
				useMaster = false;
			}
			return Integer.valueOf(value);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("get masterTotal in redis is error {}", e);
			return masterTotal;
		}
	}

	private int getSpareTotalInRedis(int spareTotal){
		try{
			String value = redisManager.get(SPARE_TOTAL);
			if(StringUtils.isEmpty(value)){
				redisManager.set(SPARE_TOTAL, spareTotal+"");
				value = spareTotal+"";
			}
			//如果备通道分流值为0,使用主通道
			if("0".equals(value)){
				useMaster = true;
			}
			return Integer.valueOf(value);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("get spareTotal in redis is error {}", e);
			return spareTotal;
		}
	}

	//获取主通道
	private SmsConfig getMaster(List<SmsConfig> configs){
		if(!configs.isEmpty()){
			int masterWeight = Integer.MAX_VALUE;
			SmsConfig master = configs.get(0);
			for(SmsConfig config: configs){
				int weight = config.getWeight();
				int currentWeight = config.getCurrentWeight();
				if((weight == currentWeight) && (weight < masterWeight)){//当权值和当前权值一致，并且权值最小的则为主通道
					masterWeight = weight;
					master = config;
				}
			}
			return master;
		}else{
			logger.info("SmsConfig list is EMPTY");
			return null;
		}
	}

	//获取备用通道
	private List<SmsConfig> getSpareList(List<SmsConfig> configs){
		SmsConfig master = this.getMaster(configs);
		configs.remove(master);
		return configs;
	}

	@Override
	public MessageChannel filterChannelBySmsType(String typeCode,String failChannelName,String uk) {
		MessageChannel channel = null;
		SmsConfig smsConfig = null;
		//查询类型中所有有用的通道
		List<SmsConfig> smsConfigs = channelMapper.filterChannelBySmsTypeAndStatus(typeCode);
		//无通道记录日志
		if(smsConfigs == null || smsConfigs.size() == 0){
			logger.info("getChannel is null by typeCode={},uk={}", typeCode,uk);
			return null;
		}

		//如果是失败重发
		if(!StringUtils.isEmpty(failChannelName)){//失败重发则不再使用原通道
			if(smsConfigs.size() != 1){
				for(Iterator<SmsConfig> it = smsConfigs.iterator(); it.hasNext(); ){
					if(failChannelName.equals(it.next().getSmsChannel())){
						it.remove();
						break;
					}
				}
				Random random = new Random();
				int index = random.nextInt(smsConfigs.size());
				smsConfig = smsConfigs.get(index);
			}else{
				smsConfig = smsConfigs.get(0);
			}
		}


		//一条通道直接返回
		if(smsConfigs.size()==1){
			 smsConfig = smsConfigs.get(0);
		}else{
			//多条通道根据不同的情况判断选择通道
			smsConfig = getSmsConfig(typeCode,smsConfigs,uk);
		}

		//筛选后获取通道接口
		if(smsConfig!=null){
			//根据编号获取对应名称可调用对应server
//			String channelName=smsConfig.getChannelCode();
			channel = messageChannelContext.getChannel(smsConfig.getSmsChannel());
		}else{
			logger.info("get new chooes have no config");
			return null;
		}

		logger.info("filterChannelBySmsType type={},channel",typeCode,channel);
		return channel;
	}

	private SmsConfig getSmsConfig(String typeCode,List<SmsConfig> smsConfigs, String uk) {
		SmsConfig config = null;
		//获取通道原有(按通道比从高到低排序)
		List<SmsConfig> smsConfigsCheck = channelMapper.filterChannelBySmsType(typeCode);

		//如果获取有效通道与原有通道数不一致，则重新设置分流比
		if(smsConfigsCheck.size()!=smsConfigs.size()){
			//重新设置分流比
			float count = getCount(smsConfigs);//当前分流比之和
			String length = String.valueOf(smsConfigs.get(0).getWeight());//辅助参数
			int d = (int) Math.pow( 10, length.length());
			 for (SmsConfig ratesms : smsConfigs) {
				 int weight =Math.round( ratesms.getWeight()*d/count);
				 ratesms.setWeight(weight);
			 }

			 //根据分流比选择通道
			 config = chooseChannel(smsConfigs,typeCode,uk);
		}else{
			//根据分流比选择通道
			config = chooseChannel(smsConfigs,typeCode,uk);
		}
		logger.info("new choose get config={},uk={}",config.toString(),uk);
		return config;
	}

	private SmsConfig chooseChannel(List<SmsConfig> smsConfigs,String typeCode, String uk) {
		synchronized (telLock) {
			SmsConfig config = null;
			for (SmsConfig ratesms : smsConfigs) {
				 int redisRate = 0;
				 String channelCode = ratesms.getChannelCode();
				 //获取redis 计数比例，与实际比例做比对
				 String redisStr = redisManager.get(SmsConfig.SMS_TYPE_RATE+channelCode);
				 logger.info("redis key={} value={},typeCode={},uk={}",SmsConfig.SMS_TYPE_RATE+channelCode,redisStr,typeCode,uk);
				 if(org.apache.commons.lang3.StringUtils.isNotBlank(redisStr)){
					 redisRate = Integer.valueOf(redisStr);
				 }else{
					 redisManager.set(SmsConfig.SMS_TYPE_RATE+channelCode, "0");
				 }
				 int rate = ratesms.getWeight();
				 if(redisRate>=rate){
					 continue;
				 }else{
					 redisRate++;
					 redisManager.set(SmsConfig.SMS_TYPE_RATE+channelCode, String.valueOf(redisRate));
					 config = ratesms;
					 break;
				 }

			}
			//如果smsConfigs大于0但是获取值为空说明redis 设置值都已经取满,将所有通道redis 设置为0 并取用第一条通道（占比最大）
			if(config==null && smsConfigs.size()>0){
				config = smsConfigs.get(0);
				for (SmsConfig ratesms : smsConfigs) {
					 redisManager.set(SmsConfig.SMS_TYPE_RATE+ratesms.getChannelCode(), "0");
				}
				redisManager.set(SmsConfig.SMS_TYPE_RATE+smsConfigs.get(0).getChannelCode(),"1");

			}
			return config;
		}

	}

	private float getCount(List<SmsConfig> smsConfigs) {
		float count =0;
		for (SmsConfig ratesms : smsConfigs) {
			float temp =+ ratesms.getWeight();
			count = count+temp;
		}
		return count;
	}

	@Override
	public List<Map<String, String>> countSuccessMessages(String smsSendType, int minuteIntervalTime) {
		return channelMapper.countSuccessMessages(smsSendType, minuteIntervalTime);
	}

	@Override
	public List<Map<String, String>> countFailMessages(String smsSendType, int minuteIntervalTime) {
		return channelMapper.countFailMessages(smsSendType, minuteIntervalTime);
	}

	@Override
	public List<Map<String, String>> countFailHistoryMessages(String smsSendType, int minuteIntervalTime) {
		return channelMapper.countFailHistoryMessages(smsSendType, minuteIntervalTime);
	}

	@Override
	public List<SmsConfig> listMasterSmsConfigs(String status, String smsSendType){
		return channelMapper.listMasterSmsConfigs(status, smsSendType);
	}

	@Override
	public void updateChannelToSpare(String channelName, String status){
		channelMapper.updateChannelToSpare(channelName, status);
	}

	@Override
	public void insertUplinkMessage(UpLinkEntity upLinkEntity) {
		channelMapper.insertUplinkMessage(upLinkEntity);
	}

	@Override
	public List<UpLinkEntity> listUplinkMessages(String phone, String content, Date createTime) {
		return channelMapper.listUplinkMessages(phone, content, createTime);
	}

	@Override
	public void insertMessageStatus(SmsStatusEntity smsStatusEntity) {
		channelMapper.insertMessageStatus(smsStatusEntity);
	}

	@Override
	public List<SmsStatusEntity> listMessageStatuses(String phone, String msgId, Date createTime) {
		return channelMapper.listMessageStatuses(phone, msgId, createTime);
	}

	public void setMasterTotal(int masterTotal) {
		this.masterTotal = masterTotal;
	}

	public void setSpareTotal(int spareTotal) {
		this.spareTotal = spareTotal;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	public void setChannelMapper(ChannelMapper channelMapper) {
		this.channelMapper = channelMapper;
	}

	public void setTokenMapper(TokenMapper tokenMapper) {
		this.tokenMapper = tokenMapper;
	}

	public void setMessageChannelContext(MessageChannelContext messageChannelContext) {
		this.messageChannelContext = messageChannelContext;
	}

	public void setExpandCodeLength(int expandCodeLength) {
		this.expandCodeLength = expandCodeLength;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public void setProducer(CamelAstrotrainProducer producer) {
		this.producer = producer;
	}

	@Override
	public void StatisticalSuccessRate(Date startTime, Date endTime) {
		//成功短信数
		List<SmsRateStatics> noticeSuccessTotalList = channelMapper.findNoticeSuccessTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : noticeSuccessTotalList) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setSendTotal(smsRateStatics.getSendSuccessTotal());
				smsRateStatics.setSendSuccessRate(100);
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setSendTotal(smsRateStatics.getSendSuccessTotal());
				smsRateStaticsDB.setSendSuccessTotal(smsRateStatics.getSendSuccessTotal());
				smsRateStaticsDB.setSendSuccessRate(100);
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}               
		}
		List<SmsRateStatics> saleSuccessTotalList = channelMapper.findSaleSuccessTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : saleSuccessTotalList) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setSendTotal(smsRateStatics.getSendSuccessTotal());
				smsRateStatics.setSendSuccessRate(100);
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setSendTotal(smsRateStatics.getSendSuccessTotal());
				smsRateStaticsDB.setSendSuccessRate(100);
				smsRateStaticsDB.setSendSuccessTotal(smsRateStatics.getSendSuccessTotal());
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
		//失败短信数
		List<SmsRateStatics> findFailTotal = channelMapper.findFailTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : findFailTotal) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setSendTotal(smsRateStatics.getSendFailTotal());
				smsRateStatics.setSendSuccessRate(0);
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setSendFailTotal(smsRateStatics.getSendFailTotal());
				//发送总数
				int total=smsRateStatics.getSendFailTotal()+smsRateStaticsDB.getSendSuccessTotal();
				double successRate = (double) smsRateStaticsDB.getSendSuccessTotal() *100/ total;
				//发送成功率
				smsRateStaticsDB.setSendTotal(total);
				smsRateStaticsDB.setSendSuccessRate(successRate);
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
		//送达成功
		List<SmsRateStatics> findNoticeReceiveSuccesssTotal = channelMapper.findNotiveReceiveSuccessTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : findNoticeReceiveSuccesssTotal) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setReceiveSuccessRate(100);
				smsRateStatics.setFeeTotal(smsRateStatics.getReceiveSuccessTotal());
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setReceiveSuccessRate(100);
				smsRateStaticsDB.setReceiveSuccessTotal(smsRateStatics.getReceiveSuccessTotal());
				smsRateStaticsDB.setFeeTotal(smsRateStatics.getReceiveSuccessTotal());
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
		List<SmsRateStatics> findSaleReceiveSuccessTotal = channelMapper.findSaleReceiveSuccessTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : findSaleReceiveSuccessTotal) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setReceiveSuccessRate(100);
				smsRateStatics.setFeeTotal(smsRateStatics.getReceiveSuccessTotal());
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setReceiveSuccessRate(100);
				smsRateStaticsDB.setReceiveSuccessTotal(smsRateStatics.getReceiveSuccessTotal());
				smsRateStaticsDB.setFeeTotal(smsRateStatics.getReceiveSuccessTotal());
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
		//送达失败
		List<SmsRateStatics> findNoticeReceiveFailTotal = channelMapper.findNoticeReceiveFailTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : findNoticeReceiveFailTotal) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setReceiveSuccessRate(0);
				smsRateStatics.setReceiveFailTotal(0);
				smsRateStatics.setFeeTotal(0);
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setReceiveFailTotal(smsRateStatics.getReceiveFailTotal());
				int total=smsRateStatics.getReceiveFailTotal()+smsRateStaticsDB.getReceiveSuccessTotal();
				double successRate = (double) smsRateStaticsDB.getReceiveSuccessTotal() *100/ total;
				smsRateStaticsDB.setReceiveSuccessRate(successRate);
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
		List<SmsRateStatics> findSaleReceiveFailTotal = channelMapper.findSaleReceiveFailTotal(startTime, endTime);
		for (SmsRateStatics smsRateStatics : findSaleReceiveFailTotal) {
			SmsRateStatics smsRateStaticsDB = channelMapper.findSmsRateStaticsByCodeAndTime(
					smsRateStatics.getAppCode(), smsRateStatics.getTypeCode(), smsRateStatics.getChannelCode(), smsRateStatics.getTime());
			if(smsRateStaticsDB == null){
				smsRateStatics.setReceiveSuccessRate(0);
				smsRateStatics.setReceiveFailTotal(0);
				smsRateStatics.setFeeTotal(0);
				channelMapper.insertRateStatics(smsRateStatics);
			}else{
				smsRateStaticsDB.setReceiveFailTotal(smsRateStatics.getReceiveFailTotal());
				int total=smsRateStatics.getReceiveFailTotal()+smsRateStaticsDB.getReceiveSuccessTotal();
				double successRate = (double) smsRateStaticsDB.getReceiveSuccessTotal() *100/ total;
				smsRateStaticsDB.setReceiveSuccessRate(successRate);
				channelMapper.updateRateStatics(smsRateStaticsDB);
			}
		}
	}
}
