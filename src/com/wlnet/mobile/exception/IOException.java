package com.wlnet.mobile.exception;

/**
 * 数据库异常处理
 * @author xwlian
 *
 */
public class IOException extends BaseException {
	
	protected String errorCode =IO;
	protected String uiMsg ="文件读写异常";//显示在界面上的信息
	protected String logMsg ="文件读写异常" ;//日志中打印的信息
	
	public IOException() {
		super();
	}

	public IOException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public IOException(String detailMessage) {
		super(detailMessage);
	}

	public IOException(Throwable throwable) {
		super(throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param logMsg  日志消息
	 * @param uiMsg 显示在界面上的消息
	 * @param throwable
	 */
	public IOException(String logMsg,String uiMsg,Throwable throwable) {
		super(logMsg, throwable);
		this.logMsg = logMsg;
		this.uiMsg = uiMsg;
	}
	
	
}
