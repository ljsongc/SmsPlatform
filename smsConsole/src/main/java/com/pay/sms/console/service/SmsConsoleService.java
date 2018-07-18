package com.pay.sms.console.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.pay.sms.console.bean.ChannelRate;
import com.pay.sms.console.bean.SmsCallbackEntity;
import com.pay.sms.console.bean.SmsChannelEntity;
import com.pay.sms.console.bean.SmsEntity;
import com.pay.sms.console.bean.SmsRateStatics;
import com.pay.sms.console.bean.SmsSaleCallbackChartData;
import com.pay.sms.console.bean.SmsStatistics;
import com.pay.sms.console.bean.SmsTemplate;
import com.pay.sms.console.bean.SmsToken;
import com.pay.sms.console.bean.SmsType;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;

public interface SmsConsoleService {

	/**
	 * 获得营销短信发送数量
	 * @param startTime
	 * @param endTime
	 * @param content
	 * @return
	 */
	public String getSaleCount(String startTime, String endTime, String content);

	/**
	 * 获得通知类主通道id
	 * @return
	 */
	public long getNoticeMasterId();

	//通知类
	/**
	 * 分页查询短信信息
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findSuccessSms(String startTime, String endTime, int start, int size);

	/**
	 * 查询短信信息总条数
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return			短信总条数
	 */
	public int findSuccessSmsSize(String startTime, String endTime);

	/**
	 * 根据主键查询短信
	 * @param id		主键id
	 * @return			短信实体
	 */
	public SmsEntity findSmsById(int id);

	/**
	 * 根据条件查询
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findSuccessSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size);

	/**
	 * 根据条件查询条数
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return			短信条数
	 */
	public int findSuccessSmsSizeByCondition(String smsTo, String content, String startTime, String endTime);

	//营销类
	/**
	 * 分页查询短信信息
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findSaleSuccessSms(String startTime, String endTime, int start, int size);

	/**
	 * 查询短信信息总条数
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return			短信总条数
	 */
	public int findSaleSuccessSmsSize(String startTime, String endTime);

	/**
	 * 根据主键查询短信
	 * @param id		主键id
	 * @return			短信实体
	 */
	public SmsEntity findSaleSmsById(int id);

	/**
	 * 根据条件查询
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findSaleSuccessSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size);

	/**
	 * 根据条件查询条数
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return			短信条数
	 */
	public int findSaleSuccessSmsSizeByCondition(String smsTo, String content, String startTime, String endTime);

	/**
	 * 发送短信
	 * @param smsBean
	 * @return
	 */
	public SmsResponse sendMessage(SmsBean smsBean);

	/**
	 * 查找appCode和token对应关系
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Map<String, String>> findSmsToken(int start, int size);

	/**
	 * 查找appCode和token总数
	 * @return
	 */
	public int findSmsTokenSize();

	/**
	 * 根据主键删除token
	 * @param appCode
	 * @param id
	 */
	public void deleteToken(String appCode, Integer id);

	/**
	 * 根据appCode生成token并且保存
	 * @param appCode
	 */
	public void saveToken(String appCode);

	/**
	 * 查询appCode是否已经存在
	 * @param appCode
	 * @return
	 */
	public String validateToken(String appCode);

	/**
	 * 根据条件查询所有token信息
	 * @param appCode
	 * @param token
	 * @param start
	 * @param size
	 * @return
	 */
	public List<Map<String, String>> findTokenByCondition(String appCode, String token,
			int start, int size);

	/**
	 * 根据条件所有token总数
	 * @param appCode
	 * @param token
	 * @return
	 */
	public int findTokenSizeByCondition(String appCode, String token);

	/**
	 * 查询失败记录
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsEntity> findFailureSms(String startTime, String endTime, int start, int size);

	/**
	 * 查询失败记录总数
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public int findFailureSmsSize(String startTime, String endTime);

	/**
	 * 根据主键查询失败短信
	 * @param id
	 * @return
	 */
	public SmsEntity findFailureById(int id);

	/**
	 * 根据条件查询失败
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findFailureSmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size);

	/**
	 * 根据条件查询失败总数
	 * @param smsTo
	 * @param content
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public int findFailureSmsSizeByCondition(String smsTo, String content, String startTime, String endTime);

	/**
	 * 查询失败历史记录
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsEntity> findFailureHistorySms(String startTime, String endTime, int start, int size);

	/**
	 * 查询失败历史记录总数
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public int findFailureHistorySmsSize(String startTime, String endTime);

	public SmsEntity findFailureHistoryById(int id);

	/**
	 * 根据条件查询失败历史
	 * @param smsTo		短信号码
	 * @param content	短信内容
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @param start		分页开始索引
	 * @param size		分页大小
	 * @return			短信集合
	 */
	public List<SmsEntity> findFailureHistorySmsByCondition(String smsTo,
			String content, String startTime, String endTime, int start, int size);

	/**
	 * 根据条件查询失败历史总数
	 * @param smsTo
	 * @param content
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 * @return
	 */
	public int findFailureHistorySmsSizeByCondition(String smsTo, String content, String startTime, String endTime);

	/**
	 * 通道的增删改查
	 * @param smsChannelEntity
	 */
	public void createChannel(SmsChannelEntity smsChannelEntity);
	public void deleteChannelById(long id);
	public void updateChannel(SmsChannelEntity smsChannelEntity);
	public List<SmsChannelEntity> findAllChannels();
	public SmsChannelEntity findChannelByID(String id);


	/**
	 * 查询通知类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsCallbackEntity> findCallbackNoticeSms(String startTime, String endTime, int start, int size);

	/**
	 * 查询通知类短信总数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int findCallbackNoticeSmsSize(String startTime, String endTime);

	/**
	 * 根据条件,查询通知类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param phone
	 * @param msgid
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsCallbackEntity> findCallbackNoticeSmsByCond(String startTime, String endTime, String phone, String msgid, int start, int size);

	/**
	 * 根据条件,查询通知类短信送达情况总数
	 * @param startTime
	 * @param endTime
	 * @param phone
	 * @param msgid
	 * @return
	 */
	public int findCallbackNoticeSmsSizeByCond(String startTime, String endTime, String phone, String msgid);

	/**
	 * 查询通知类短信送达详情
	 * @param id
	 * @return
	 */
	public SmsCallbackEntity findNoticeCallbackViewById(String id);

	/**
	 * 查询营销类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsCallbackEntity> findCallbackSaleSms(String startTime, String endTime, int start, int size);

	/**
	 * 查询营销类短信总数
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int findCallbackSaleSmsSize(String startTime, String endTime);

	/**
	 * 根据条件,查询营销类短信的送达状态
	 * @param startTime
	 * @param endTime
	 * @param phone
	 * @param msgid
	 * @param start
	 * @param size
	 * @return
	 */
	public List<SmsCallbackEntity> findCallbackSaleSmsByCond(String startTime, String endTime, String phone, String msgid, int start, int size);

	/**
	 * 根据条件,查询营销类短信送达情况总数
	 * @param startTime
	 * @param endTime
	 * @param phone
	 * @param msgid
	 * @return
	 */
	public int findCallbackSaleSmsSizeByCond(String startTime, String endTime, String phone, String msgid);

	/**
	 * 查询营销类短信送达详情
	 * @param id
	 * @return
	 */
	public SmsCallbackEntity findSaleCallbackViewById(String id);

	/**
	 * echart图表,返回营销短信数据
	 * @param startTime
	 * @param endTime
	 * @param content
	 * @return
	 */
	public List<SmsSaleCallbackChartData> getSaleCallbackChartData(String startTime, String endTime, String content);

	//短信统计
	/**
	 * 统计短信
	 * @param startTime		开始时间
	 * @param endTime		结束时间
	 * @param channels		通道
	 * @param type			短信类型
	 * @return
	 */
	public List<SmsStatistics> getSmsStatisticsList(String startTime, String endTime, List<String> channels, String type);

	/**
	 * 根据类型查询全部通道的名称
	 * @param type
	 * @return
	 */
	public List<String> getAllChannelNoByType(String type);

	/**
	 * 查询计费条数，以echart规定的内容返回
	 * <br>格式[{date:"2017-09-15",number:10},{date:"2017-09-16",number:0}]
	 * @param startTime
	 * @param endTime
	 * @param channels
	 * @return
	 */
	public String getSmsStatisticsChartData(String startTime, String endTime, List<String> channels);

	public void updateSttus(long id, String status);

	/**
	 * 分页查询token
	 * @param page
	 * @param size
	 * @return
	 */
	public List<SmsToken> tokenPageList(int page, int size);

	/**
	 * 分页查询token
	 * @param page
	 * @param size
	 * @param startTime
	 * @param endTime
	 * @param appCode
	 * @return
	 */
	public List<SmsToken> tokenSearch(int page, int size, String startTime, String endTime, String appCode);

	public SmsToken findTokenById(Integer id);

	public void insertToken(SmsToken token);

	public void updateToken(SmsToken token);

	public int findTokenCountByAppCount(String startTime, String endTime, String appCode);

	public JSONObject tokenPageList(int start, int length, String appName, String startTime, String endTime);

	public List<SmsToken>  tokenFindAll();

	/**不分页查询短信类型
	 * @return
	 */
	public List<SmsType> findliseType();

	/**分页查询短信类型
	 * @param startTime
	 * @param endTime
	 * @param start
	 * @param pageSize
	 * @param typeCode
	 * @return
	 */
	public List<SmsType> pageListSmsType(String startTime, String endTime,
			int start, int pageSize, String typeCode);

	/**分页查询统计
	 * @param startTime
	 * @param endTime
	 * @param typeCode
	 * @return
	 */
	public int pageListSmsTypeCount(String startTime, String endTime,
			String typeCode);

	/**查询有效的短信通道
	 * @return
	 */
	public List<SmsChannelEntity> findsmsList();

	/**创建短信类型
	 * @param smsType
	 */
	public void createChannel(SmsType smsType);

	/**根据ID 查询短信类型
	 * @param id
	 * @return
	 */
	public SmsType findSmsTypeByID(String typeCode);

	/**修改短信类型
	 * @param smsType
	 */
	public void updateSmsType(SmsType smsType);

	/**查询相同通道的短信类型
	 * @param typeName
	 * @param channelCode
	 * @return
	 */
	public SmsType checksms(String typeName, String channelCode,String typeCode);

	/**
	 * 分页查询短信模板
	 * @param start
	 * @param end
	 * @param templateCode
	 * @param typeCode
	 * @param title
	 * @param appCode
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public JSONObject smsTemplatePageList(int start, int end, String templateCode, String typeCode, String title, String appCode,
			String startTime, String endTime);


	public SmsToken findTokenByAppCode(String appCode);

	public List<SmsType> findSmsType();

	public String saveSmsTemplate(SmsTemplate smsTemplate);

	public String updateSmsTemplate(SmsTemplate smsTemplate);

	public List<SmsToken> findAllToken();

	public SmsTemplate findSmsTemplateByCode(String templateCode);

	public List<ChannelRate> findRate(String typeCode);

	public List<SmsChannelEntity> findAllChannelsnew();

	public void createChannelnew(SmsChannelEntity smsChannelEntity);

	public void updateChannelnew(SmsChannelEntity smsChannelEntity);

	public List<SmsCallbackEntity> smsListSearch(String startTime,
			String endTime, String phone, String templateCode, String typeCode,
			String appCode, String channelNo, String status, int i, int pageSize);

	public int smsListSearchCount(String startTime, String endTime,
			String phone, String templateCode, String typeCode, String appCode,
			String channelNo, String status);

	public SmsCallbackEntity findsmsQueryViewById(String id);

	public SmsCallbackEntity findsmsQuerySaleViewById(String id);
	
	public List<SmsRateStatics> pageListSmsRateStatics (int start, int page, String appCode, List<String> typeCodes,
			List<String> channelCodes,String startTime, String endTime);
	
	public int countSmsRateStatics (String appCode, List<String> typeCodes,List<String> channelCodes,String startTime, String endTime);
}
