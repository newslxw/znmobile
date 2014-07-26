package com.wlnet.mobile.net;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.wlnet.mobile.pojo.Dev;
import com.wlnet.mobile.pojo.Rev;


public class Receive {
	private static final String CMD_LASTREV="lastrev";
	private static final String CMD_DEVLIST="devlist";
	//private static byte[] ipBs = new byte[] { (byte) 122,  10, 81, (byte)128 };
	private static String serverIp = "122.10.81.128";
	//private static String serverIp = "192.168.1.8";
	private static int serverPort = 2035;
	
	/**
	 * 返回最新设备列表
	 * @return
	 * @throws IOException 
	 */
	public static List<Dev> requestDevList() throws IOException {
		List<Dev> list = null;
		// uuid`name`type`ip`port[uuid`name`type`ip`port
		InetAddress ip = InetAddress.getByName(serverIp);
		DatagramSocket getSocket = new DatagramSocket();
		getSocket.setBroadcast(true);
		String msg = CMD_DEVLIST;
		byte[] buf = msg.getBytes();
		DatagramPacket getPacket = new DatagramPacket(buf, buf.length, ip, serverPort);
		getSocket.send(getPacket);
		buf = new byte[1024];
		getPacket = new DatagramPacket(buf, buf.length, ip, serverPort);
		getSocket.setSoTimeout(60000);
		getSocket.receive(getPacket);
		getSocket.close();
		if (getPacket.getLength() == 0)
			return list;
		list = new ArrayList<Dev>();
		String ret = new String(buf, 0, getPacket.getLength(),"UTF-8");
		String arr1[] = ret.split("\\[");
		String arr2[] = null;
		for (int i = 0; i < arr1.length; i++) {
			arr2 = arr1[i].split("`");
			Dev dev = new Dev();
			if (arr2.length < 3) {
				Log.e("Receive.requestDevList", "server back msg [" + arr1[i]
						+ "]not in format uuid`name`type`ip`port");
				continue;
			}
			dev.setDevUuid(arr2[0]);
			dev.setDevName(arr2[1]);
			dev.setDevType(arr2[2]);
			dev.setInetIp(arr2.length>3?arr2[3]:null);
			if (arr2.length>4&&arr2[4].length() > 1)
				dev.setInetPort(Integer.parseInt(arr2[4]));
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
		Rev newRev = new Rev();
		newRev.setDevUuid(rev.getDevUuid());
		newRev.setMeasureValue(rev.getMeasureValue());
		newRev.setRevTime(rev.getRevTime());
		//uuid`value`revtime`   value格式 温度,湿度,甲醇,氧气,空气
		InetAddress ip = InetAddress.getByName(serverIp);
		DatagramSocket getSocket = new DatagramSocket();
		getSocket.setBroadcast(true);
		String msg = CMD_LASTREV+"#"+rev.getDevUuid();
		byte[] buf = msg.getBytes();
		DatagramPacket getPacket = new DatagramPacket(buf, buf.length, ip,serverPort);
		getSocket.send(getPacket);
		buf = new byte[1024];
		getPacket = new DatagramPacket(buf, buf.length, ip,serverPort);
		getSocket.setSoTimeout(60000);
		getSocket.receive(getPacket);
		getSocket.close();
		if(getPacket.getLength() == 0) return rev;
		String ret = new String(buf, 0, getPacket.getLength(),"UTF-8");
		String arr[] = ret.split("`");
		if(arr.length!=3){
			Log.e("Receive.requestRev", "server back msg ["+ret+"]not in format uuid`value`revtime`");
			return rev;
		}
		newRev.setMeasureValue(arr[1]);
		newRev.setRevTime(arr[2]);
			// 关闭套接字
		return newRev;
	}
}

