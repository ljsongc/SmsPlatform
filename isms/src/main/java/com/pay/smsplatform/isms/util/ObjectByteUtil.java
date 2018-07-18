package com.pay.smsplatform.isms.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 	对象与字节转换工具类
 * @author zhengzheng.ma
 *
 */
public class ObjectByteUtil {
	
	public static byte[] objectToByte(Object obj){
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 	//构造一个字节输出流
		ObjectOutputStream oos=null;
		try {
			oos = new ObjectOutputStream(baos);	//构造一个类输出流
			oos.writeObject(obj); 				//写入这个对象至输入流
			byte[] buf = baos.toByteArray(); 	//将流中的内容写到字节数组中
			oos.flush();
			return buf;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				oos.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object byteToObject(byte[] buf){
		 ByteArrayInputStream bais = new ByteArrayInputStream(buf);
         ObjectInputStream ois=null;
		try {
			ois = new ObjectInputStream(bais);
			Object obj=ois.readObject();
			return obj;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}finally{
			try {
				ois.close();
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
