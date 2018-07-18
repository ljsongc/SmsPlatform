package com.pay.smsserver.jobHandler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.dsp.common.biz.model.ReturnT;
import com.pay.dsp.core.handler.BaseJobHandler;
import com.pay.smsserver.bean.SmsEntity;
import com.pay.smsserver.channel.MessageChannel;
import com.pay.smsserver.handler.MessageHandler;
import com.pay.smsserver.service.SmsFailureService;

/**失败重试
 * @author mengjiao.liang
 *
 */
public class SmsFailureJobHandler extends BaseJobHandler{

private final Logger logger = LoggerFactory.getLogger(getClass());

	private MessageHandler messageHandler;

	//可以重试的错误码，格式FAILURE[CODE]，以逗号分割，CODE具体参见各通道文档
	private String retryCodes;
	//每次获取失败记录的条数
	private int total;
	private SmsFailureService smsFailureService;
	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		logger.info("failure sms start to handle");
		try{
			String[] codes = retryCodes.split(",");
			List<SmsEntity> entitys = smsFailureService.getRetrySmsEntityList(total, codes);
			if(entitys.size() > 0){
				logger.info("failure sms set size is {}", entitys.size());
				for(SmsEntity smsEntity: entitys){
					try{
						this.messageHandler.failHandle(smsEntity);
					}catch(Exception e){
						e.printStackTrace();
						this.logger.error("failure sms{} handle error {}", smsEntity, e);
					}
				}
			}else{
				logger.info("failure sms set is null");
			}
			return ReturnT.SUCCESS;
		}catch(Exception e){
			e.printStackTrace();
			this.logger.error("failure handle error {}", e);
			throw new RuntimeException(e);
		}

	}

	public void setRetryCodes(String retryCodes) {
		this.retryCodes = retryCodes;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setSmsFailureService(SmsFailureService smsFailureService) {
		this.smsFailureService = smsFailureService;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}


}
