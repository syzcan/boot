package com.zong.util;

public class ServiceException extends Exception {
	private static final long serialVersionUID = -449052734293247868L;

	public static final String MESSAGE = "业务逻辑异常";

	protected int code = Result.APPLICATION_ERROR_CODE;

	public ServiceException() {
		super(MESSAGE);
	}

	public ServiceException(String message) {
		super(message);
		this.code = Result.SERVICE_ERROR_CODE;
	}

	public ServiceException(int code, String message) {
		super(message);
		this.code = code;
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
		this.code = Result.SERVICE_ERROR_CODE;
	}

	public ServiceException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public ServiceException(Throwable cause) {
		super(cause);
		this.code = Result.SERVICE_ERROR_CODE;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
