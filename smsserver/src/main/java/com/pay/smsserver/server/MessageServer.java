package com.pay.smsserver.server;

import com.pay.iocommander.core.CommandCallback;
import com.pay.iocommander.core.server.CommandServer;
import com.pay.iocommander.core.server.platform.BuildinServer;

/**
 * 短信服务端
 * @author chenchen.qi
 *
 */
public class MessageServer {
	
	private CommandServer server = null; 	
	
	private int port = 0;					//监听端口
	
	private static int count = 1;
	private CommandCallback commandCallBack;	//监听短信回调
	
	public void init(){
		server = new BuildinServer();
		server.getListener().addListener(count, commandCallBack);
		server.start(port);
		count++;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	public void setCommandCallBack(CommandCallback commandCallBack) {
		this.commandCallBack = commandCallBack;
	}
}
