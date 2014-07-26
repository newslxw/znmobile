package com.wlnet.mobile.net;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.wlnet.mobile.common.Const;
import com.wlnet.mobile.pojo.Dev;
import com.wlnet.mobile.pojo.Rev;
import com.wlnet.mobile.utils.StreamUtils;


public class TcpReceive {
	private static final String CMD_LASTREV="lastrev";
	private static final String CMD_DEVLIST="devlist";
	public static final String CMD_OPEN="AA";
	public static final String CMD_CLOSE="55";
	public static final String CMD_STATUS="03";
	//private static byte[] ipBs = new byte[] { (byte) 122,  10, 81, (byte)128 };
	private static String serverIp = "122.10.81.128";
	//private static String serverIp = "192.168.1.15";
	//private static String serverIp = "192.168.191.1";
	//private static String serverIp = "116.255.248.182";
	private static int serverPort = 2036;
	
	/**
	 * 返回最新设备列表
	 * @return
	 * @throws IOException 
	 */
	public static List<Dev> requestDevList() throws IOException {
		List<Dev> list = null;
		// uuid`name`type`ip`port[uuid`name`type`ip`port
		InetAddress ip = InetAddress.getByName(serverIp);
		String msg = CMD_DEVLIST;
		Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        int ncount =0; 
        byte[] reply = new byte[1024];
        try{
	        client = new Socket(ip,serverPort);
	        client.setSoTimeout(10000);
	        out = new DataOutputStream( (client.getOutputStream()));
	        byte[] request = msg.getBytes();
	        out.write(request);
	        out.flush();
	        //client.shutdownOutput();
	
	        in = new DataInputStream(client.getInputStream());
	        ncount = in.read(reply);
        }catch(SocketTimeoutException e ){
        	throw new IOException("设备没响应");
        }catch(IOException e){
        	Log.e("TcpReceive.requestDevList", "", e);
        	throw new IOException("网络不通或者设备没响应");
        }finally{
        	StreamUtils.closeStream("TcpReceive.requestDevList", in);
        	StreamUtils.closeStream("TcpReceive.requestDevList", out);
	        if(client!=null){client.close();client=null;}
        }
		if (ncount <= 0)
			return list;
		
		list = new ArrayList<Dev>();
		String ret = new String(reply, 0, ncount,"UTF-8");
		String arr1[] = ret.split("\\"+Const.Sym.obj);
		String arr2[] = null;
		for (int i = 0; i < arr1.length; i++) {
			arr2 = arr1[i].split("`");
			Dev dev = new Dev();
			if (arr2.length < 4) {
				Log.e("Receive.requestDevList", "server back msg [" + arr1[i]
						+ "]not in format uuid`name`type`apptype`ip`port");
				continue;
			}
			dev.setDevUuid(arr2[0]);
			dev.setDevName(arr2[1]);
			dev.setDevType(arr2[2]);
			dev.setAppType(arr2[3]);
			dev.setInetIp(arr2.length>4?arr2[4]:null);
			if (arr2.length>5&&arr2[5].length() > 1)
				dev.setInetPort(Integer.parseInt(arr2[5]));
			list.add(dev);
		}

		return list;
	}
	
	/**
	 * 向服务器查询设备的最新测量数据
	 * @param rev 必须有dev_uuid
	 * @return
	 * @throws IOException 
	 */
	public static Rev requestRev( Rev rev) throws IOException {

		String msg = rev.getDevUuid()+Const.Sym.cmd+CMD_LASTREV;
		Rev newRev = new Rev();
		newRev.setDevUuid(rev.getDevUuid());
		newRev.setMeasureValue(rev.getMeasureValue());
		newRev.setRevTime(rev.getRevTime());
		//uuid`value`revtime`   value格式 温度,湿度,甲醇,氧气,空气
		InetAddress ip = InetAddress.getByName(serverIp);
		
		Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        int ncount =0; 
        byte[] reply = new byte[1024];
        try{
	        client = new Socket(ip,serverPort);
	        client.setSoTimeout(20000);
	        out = new DataOutputStream( (client.getOutputStream()));
	        byte[] request = msg.getBytes();
	        out.write(request);
	        out.flush();
	       // client.shutdownOutput();
	
	        in = new DataInputStream(client.getInputStream());
	        ncount = in.read(reply);
        }catch(SocketTimeoutException e ){
        	throw new IOException("设备没响应");
        }catch(IOException e){
        	Log.e("TcpReceive.requestRev", "", e);
        	throw new IOException("网络不通或者设备没响应");
        }finally{
            if(in!=null){in.close();in=null;}
            if(out!=null){out.close();out=null;}
            if(client!=null){client.close();client=null;}
        }
		if (ncount <= 0)
			return rev;
		
		String ret = new String(reply, 0, ncount,"UTF-8");
		String arr[] = ret.split(Const.Sym.cmd);
		if(arr.length!=3){
			Log.e("Receive.requestRev", "server back msg ["+ret+"]not in format uuid#value#revtime`");
			return rev;
		}
		newRev.setMeasureValue(arr[1]);
		newRev.setRevTime(arr[2]);
			// 关闭套接字
		return newRev;
	}
	
	/**
	 * 向服务器查询智能插座
	 * @param rev 必须有dev_uuid
	 * @return
	 * @throws IOException 
	 */
	public static Rev requestCZ(Rev rev) throws IOException {
		String msg = rev.getDevUuid()+Const.Sym.cmd+CMD_STATUS;
		Rev newRev = new Rev();
		newRev.setDevUuid(rev.getDevUuid());
		newRev.setMeasureValue(rev.getMeasureValue());
		newRev.setRevTime(rev.getRevTime());
		InetAddress ip = InetAddress.getByName(serverIp);
		
		Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        int ncount =0; 
        byte[] reply = new byte[1024];
        try{
	        client = new Socket(ip,serverPort);
	        client.setSoTimeout(20000);
	        out = new DataOutputStream( (client.getOutputStream()));
	        byte[] request = msg.getBytes();
	        out.write(request);
	        out.flush();
	       // client.shutdownOutput();
	
	        in = new DataInputStream(client.getInputStream());
	        ncount = in.read(reply);
        }catch(SocketTimeoutException e ){
        	throw new IOException("设备没响应");
        }catch(IOException e){
        	e.printStackTrace();
        	Log.e("Receive.requestRevCZ","error", e);
        	throw new IOException("网络不通或者设备没响应");
        }finally{
            if(in!=null){in.close();in=null;}
            if(out!=null){out.close();out=null;}
            if(client!=null){client.close();client=null;}
        }
		if (ncount <= 0)
			return rev;
		
		String ret = new String(reply, 0, ncount,"UTF-8");
		String arr[] = ret.split(Const.Sym.cmd);
		if(arr.length!=2){
			Log.e("Receive.requestRevCZ", "server back msg ["+ret+"] not in format 2#AA");
			throw new IOException("设备没响应");
			//return rev;
		}
		newRev.setMeasureValue(ret);
		return newRev;
		
	}
	

	/**
	 * 向服务器查询智能插座
	 * @param rev 必须有dev_uuid
	 * @return
	 * @throws IOException 
	 */
	public static Rev switchCZ(Rev rev, String cmd) throws IOException {
		String msg = cmd;//+Const.Sym.cmd+rev.getDevUuid();
		Rev newRev = new Rev();
		newRev.setDevUuid(rev.getDevUuid());
		newRev.setMeasureValue(rev.getMeasureValue());
		InetAddress ip = InetAddress.getByName(serverIp);
		
		Socket client = null;
        DataOutputStream out = null;
        DataInputStream in = null;
        int ncount =0; 
        byte[] reply = new byte[1024];
        try{
	        client = new Socket(ip,serverPort);
	        client.setSoTimeout(20000);
	        out = new DataOutputStream( (client.getOutputStream()));
	        byte[] request = msg.getBytes();
	        out.write(request);
	        out.flush();
	     //   client.shutdownOutput();
	
	        in = new DataInputStream(client.getInputStream());
	        ncount = in.read(reply);
        }catch(SocketTimeoutException e ){
        	throw new IOException("设备没响应");
        }catch(IOException e){
        	Log.e("TcpReceive.requestRevCZ", "", e);
        	throw new IOException("网络不通或者设备没响应");
        }finally{
            if(in!=null){in.close();in=null;}
            if(out!=null){out.close();out=null;}
            if(client!=null){client.close();client=null;}
        }
		if (ncount <= 0)
			return rev;
		
		String ret = new String(reply, 0, ncount,"UTF-8");
		String arr[] = ret.split(Const.Sym.cmd);
		if(arr.length!=2){
			Log.e("Receive.requestRevCZ", "server back msg ["+ret+"] not in format 2#AA");
			throw new IOException("设备没响应");
		}
		newRev.setMeasureValue(ret);
		return newRev;
		
	}
}

