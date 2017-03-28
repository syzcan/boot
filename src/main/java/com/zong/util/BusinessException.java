package com.zong.util;

/**
 * @desc 业务自定义异常
 * @author zong
 * @date 2017年3月10日
 */
public class BusinessException extends Exception {
	private static final long serialVersionUID = -2320685094638126902L;
	public String errCode;
	public String errMsg;

	public BusinessException() {

	}

	public BusinessException(String errCode, String errMsg) {
		super(errMsg);
		this.errCode = errCode;
		this.errMsg = errMsg;
	}

	public BusinessException(String errMsg) {
		super(errMsg);
		this.errMsg = errMsg;
	}

	public String getErrCode() {
		return this.errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return this.errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
