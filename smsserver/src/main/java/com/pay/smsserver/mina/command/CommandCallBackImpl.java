package com.pay.smsserver.mina.command;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pay.iocommander.core.Command;
import com.pay.iocommander.core.CommandCallback;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.isms.enums.SmsSendFlag;
import com.pay.smsplatform.isms.util.ObjectByteUtil;
import com.pay.smsserver.handler.MessageHandler;
import com.pay.smsserver.service.ValidationService;

/**
 * 监听程序
 * @author chenchen.qi
 *
 */
public class CommandCallBackImpl implements CommandCallback{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private ValidationService validationService;
	private MessageHandler messageHandler;
	private ExecutorService fixedThreadPool;
	private int poolSize = Runtime.getRuntime().availableProcessors() + 1;//默认为CPU核数+1
	
	public void init(){
		fixedThreadPool = Executors.newFixedThreadPool(poolSize, new ThreadFactory() {
			private final String NAME_PREFFIX = "CommandCallBack-";
			private final AtomicInteger count = new AtomicInteger(0);
			
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName(NAME_PREFFIX + count.getAndIncrement());
				return t;
			}
		});
		this.logger.info("init command callback fixed pool size={}", poolSize);
	}
	
	@Override
	public void onCommand(Command cmd, IoSession session) {
		
		Object object = ObjectByteUtil.byteToObject(cmd.getContent());
		
		if(object instanceof SmsBean){
			
			final SmsBean smsBean = (SmsBean) object;							
			
			logger.info("receive message to={} content={} from appCode={} ip={}", 
					smsBean.getTo(), smsBean.getContent(), smsBean.getAppCode(), smsBean.getIp());
			
			SmsResponse smsResponse = new SmsResponse();
				fixedThreadPool.execute(new Runnable() {
					@Override
					public void run() {
						messageHandler.handle(smsBean);
					}
				});
				smsResponse.setResponseFlag(SmsSendFlag.SUCCESS);
			object = smsResponse;
		}
		
		/**返回响应**/
		if(session.isConnected()){
			Command rcmd = new Command();
			rcmd.setHead(cmd.getHead());
			rcmd.setIdentifier(cmd.getIdentifier());
			rcmd.setContent(ObjectByteUtil.objectToByte(object));
			rcmd.setLength(rcmd.getContent().length);
			session.write(rcmd);
		}
	}

	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

	public void setMessageHandler(MessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

}
