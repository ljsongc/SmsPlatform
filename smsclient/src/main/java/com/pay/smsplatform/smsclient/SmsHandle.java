package com.pay.smsplatform.smsclient;

import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pay.iocommander.core.Command;
import com.pay.iocommander.core.client.CommandClient;
import com.pay.iocommander.core.client.platform.BuildinClient;
import com.pay.smsplatform.isms.bean.SmsBean;
import com.pay.smsplatform.isms.bean.SmsResponse;
import com.pay.smsplatform.isms.enums.SmsSendFlag;
import com.pay.smsplatform.isms.enums.SmsSendLevel;
import com.pay.smsplatform.isms.enums.SmsSendType;
import com.pay.smsplatform.isms.util.ObjectByteUtil;

/**
 * 
 * @author zhengzheng.ma
 *
 */
public class SmsHandle{
	private static final Logger log = LoggerFactory.getLogger(SmsHandle.class);
	
	private CommandClient client=null;
	private static Object lock=new Object();
	private static Map<String,SmsHandle> map=new HashMap<String, SmsHandle>();
	private boolean closed=true;
	
	public static SmsHandle getInstance(String host,int port){
		log.info("host:"+host+",port"+port);
		if(map.get(host+":"+port)==null){
			synchronized (lock) {
				log.info("init SmsClient host:"+host+",port"+port);
				if(map.get(host+":"+port)==null){
					map.put(host+":"+port, new SmsHandle(host,port) );
				}
			}
		}
		return map.get(host+":"+port);
	}
	
	/**
	 * 
	 * @param host	服务器IP
	 * @param port	服务器端口号
	 */
	private SmsHandle(String host,int port){
		client=new BuildinClient(host, port);
	}
	
	/**
	 * 
	 * @param smsBean	发送内容
	 * @param synFlag	同步标志:true同步,false异步
	 * @return	是否发送成功
	 */
	public SmsResponse send(SmsBean smsBean, boolean synFlag){
		if(!closed){
			throw new RuntimeException("SmsClient is closed.");
		}
		if(StringUtils.isEmpty(smsBean.getTo())){
			throw new RuntimeException("smsBean to can not be null.");
		}
		if(StringUtils.isEmpty(smsBean.getContent())){
			throw new RuntimeException("smsBean content can not be null.");
		}
		if(StringUtils.isEmpty(smsBean.getToken())){
			throw new RuntimeException("smsBean token can not be null.");
		}
		if(StringUtils.isEmpty(smsBean.getAppCode())){
			throw new RuntimeException("smsBean appCode can not be null.");
		}
		
		IoSession session = null;
		
		try {
		
			SmsResponse sr = null;	//响应结果
			
			if(smsBean.getLevel() == null){	//默认为普通级别
				smsBean.setLevel(SmsSendLevel.NORMAL);
			}
			if(smsBean.getTime() == null){	//默认为当前发送时间
				smsBean.setTime(new Date());
			}
			if(smsBean.getType() == null){//默认为通知类短信
				smsBean.setType(SmsSendType.NOTICE);
			}else{
				smsBean.setType(smsBean.getType());
			}
			smsBean.setIp(InetAddress.getLocalHost().getHostAddress());
			
			byte[] buf = ObjectByteUtil.objectToByte(smsBean);	//将要发送的对象转换成字节数组
			session = client.getSession();
			session.getConfig().setUseReadOperation(true);
			Command cmd = new Command();
			cmd.setHead(1);
			cmd.setIdentifier(0);
			cmd.setLength(buf.length);
			cmd.setContent(buf);
			WriteFuture wf = session.write(cmd);
				if(synFlag==false){	//选择异步方式
					sr=new SmsResponse();
					sr.setResponseFlag(SmsSendFlag.SUCCESS);
					return sr;
				}		
				/*选择同步方式*/
				wf.await();
				if(wf.isWritten()){
					ReadFuture rf = session.read();
					rf.await();
					Object message = rf.getMessage();
					Command rt = (Command) message;
					sr=(SmsResponse)ObjectByteUtil.byteToObject(rt.getContent());
					return sr;
				}
		} catch (Exception e) {
			log.error("",e);
		}finally{
			if(session != null){
				session.close(true);
			}
		}
		return null;
	}
	
	/**
	 * 	关闭客户端
	 */
	public void close(){
		client.destroy();
		closed=false;
	}
}
