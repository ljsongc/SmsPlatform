package com.pay.smsserver.jobHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.pay.dsp.common.biz.model.ReturnT;
import com.pay.dsp.core.handler.BaseJobHandler;
import com.pay.dsp.core.handler.annotation.JobHander;
import com.pay.smsserver.channel.MessageChannel;

/**大汉营销短信回执状态拉取
 * @author mengjiao.liang
 *
 */
@JobHander(value="daHanSanTongSaleStatusJobHandler")
@Component
public class DaHanSanTongSaleStatusJobHandler extends BaseJobHandler{
	private final Logger log = LoggerFactory.getLogger(getClass());

	private MessageChannel messageChannel;

	@Override
	public ReturnT<String> execute(String... params) throws Exception {
		log.info("{} get message status job execute", messageChannel.getChannelName());
		messageChannel.pullCallbackStatuses();
		return ReturnT.SUCCESS;
	}

	public void setMessageChannel(MessageChannel messageChannel) {
		this.messageChannel = messageChannel;
	}

}
