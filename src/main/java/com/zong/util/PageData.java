package com.zong.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @desc 说明：参数封装Map，通过request对象将请求参数进行封装，作为页面参数传递和查询参数传递
 * @author zong
 * @date 2016年3月23日
 */
@SuppressWarnings("rawtypes")
public class PageData extends HashMap implements Map, Serializable {

	private static final long serialVersionUID = 1L;

	HttpServletRequest request;

	@SuppressWarnings("unchecked")
	public PageData(HttpServletRequest request) {
		this.request = request;
		Map properties = request.getParameterMap();
		Iterator entries = properties.entrySet().iterator();
		Map.Entry entry;
		String name = "";
		String value = "";
		while (entries.hasNext()) {
			entry = (Map.Entry) entries.next();
			name = (String) entry.getKey();
			Object valueObj = entry.getValue();
			if (null == valueObj) {
				value = "";
			} else if (valueObj instanceof String[]) {
				String[] values = (String[]) valueObj;
				for (int i = 0; i < values.length; i++) {
					value = values[i] + ",";
				}
				value = value.substring(0, value.length() - 1);
			} else {
				value = valueObj.toString();
			}
			put(name, value);
		}
	}

	public PageData() {
	}

	@SuppressWarnings("unchecked")
	public PageData(Object key, Object value) {
		super.put(key, value);
	}

	@Override
	public Object get(Object key) {
		Object obj = null;
		if (super.get(key) instanceof Object[]) {
			Object[] arr = (Object[]) super.get(key);
			obj = request == null ? arr : (request.getParameter((String) key) == null ? arr : arr[0]);
		} else {
			obj = super.get(key);
		}
		return obj;
	}

	public String getString(Object key) {
		return (String) get(key);
	}

	@SuppressWarnings("unchecked")
	public PageData put(Object key, Object value) {
		super.put(key, value);
		return this;
	}

}
