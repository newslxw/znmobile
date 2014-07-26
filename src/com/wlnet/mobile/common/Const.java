package com.wlnet.mobile.common;

public class Const {
	public static class Sym{
		public final static String prop="`";   //相同对象中属性连接符号
		public final static String cmd="#";		//命令和内容的连接符号
		public final static String obj = "[";	//不同对象的连接符号
	}
	
	public static class Cmd{
		//手机发过来的命令
		public final static String devlist="devlist";   //查询设备列表
		public final static String lastrev="lastrev";	 //查询最后接收的数据
		public final static String ctrl = "ctrl";		//智能插座控制命令
		public final static String qStatus = "qstatus";		//智能插座查询命令
		public final static String czOpen = "AA";		//打开智能插座查询命令
		public final static String czStop = "55";		//关闭智能插座查询命令
		public final static String czQ = "02";		//智能插座查询命令
	}
	

}
