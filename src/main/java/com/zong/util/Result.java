package com.zong.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @desc api返回数据封装
 * @author suyz
 * @date 2018年1月25日
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class Result extends HashMap implements Map, Serializable {
	public final static int SUCCESS_CODE = 1;// 成功
	public final static int ERROR_CODE = 0;// 失败
	// 通用错误以9开头
	public final static int APPLICATION_ERROR_CODE = 9001;// 应用级错误
	public final static int SERVICE_ERROR_CODE = 9002;// 业务逻辑验证错误
	public final static int UNKNOWN_ERROR_CODE = 9999;// 未知错误

	private static final String RET_CODE = "retCode";
	private static final String RET_MSG = "retMsg";

	public Result() {

	}

	public static Result success() {
		return new Result(SUCCESS_CODE);
	}

	public static Result error() {
		return new Result(ERROR_CODE);
	}

	public Result(int retCode) {
		put(RET_CODE, retCode);
	}

	public Result(int retCode, String retMsg) {
		put(RET_CODE, retCode);
		put(RET_MSG, retMsg);
	}

	public Result error(ServiceException e) {
		put(RET_CODE, e.getCode());
		put(RET_MSG, e.getMessage());
		return this;
	}

	public Result error(Exception e) {
		put(RET_CODE, ERROR_CODE);
		put(RET_MSG, "系统错误，请联系管理员");
		return this;
	}

	public Result error(String errorMsg) {
		put(RET_CODE, ERROR_CODE);
		put(RET_MSG, errorMsg);
		return this;
	}

	/**
	 * 返回当前对象，链式调用
	 */
	@SuppressWarnings("unchecked")
	public Result put(Object key, Object value) {
		super.put(key, value);
		return this;
	}
}
