package com.pay.smsserver.jobHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.dsp.common.biz.model.ReturnT;
import com.pay.dsp.core.handler.BaseJobHandler;
import com.pay.smsserver.service.ChannelService;

/***
 * 送达率统计定时
 * @author muya.cao
 *
 */
public class StaticalSendRateJobHandler extends BaseJobHandler{

	private final Logger log = LoggerFactory.getLogger(getClass());

	private ChannelService channelService;

	@Override
	public ReturnT<String> execute(String... params) throws Exception {
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
        calendar2.add(Calendar.DAY_OF_MONTH, -6); 
        date = calendar2.getTime(); 
        String start = sdf.format(date);
        Date startTime = sdf.parse(start);
        log.info("StaticalSendRateJobHandler begin, startTime={},endTime={}",startTime,endTime);
        channelService.StatisticalSuccessRate(startTime, endTime);
		log.info("StaticalSendRateJobHandler end");
		return ReturnT.SUCCESS;
	}

	public void setChannelService(ChannelService channelService) {
		this.channelService = channelService;
	}

	
	
}
