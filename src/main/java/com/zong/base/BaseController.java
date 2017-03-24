package com.zong.base;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.zong.util.Page;
import com.zong.util.PageData;

/**
 * @desc Controller基类，包含表单参数封装等通用操作
 * @author zong
 * @date 2016年3月13日
 */
public class BaseController {

	protected Logger logger = Logger.getLogger(this.getClass());

	/**
	 * 封装分页page，同时转发到页面，返回page给service查询
	 */
	public Page getPage() {
		Page page = new Page(getPageData());// 分页组件封装查询参数
		this.getRequest().setAttribute("page", page);// 分页组件页面展示
		return page;
	}

	/**
	 * new PageData对象
	 * 
	 * @return PageData
	 */
	public PageData getPageData() {
		PageData pd = new PageData(this.getRequest());
		getRequest().setAttribute("pd", pd);
		return pd;
	}

	/**
	 * 得到request对象
	 * 
	 * @return HttpServletRequest
	 */
	public HttpServletRequest getRequest() {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		return request;
	}

	/**
	 * httpsession
	 * 
	 * @return HttpSession
	 */
	public HttpSession getSession() {
		return getRequest().getSession();
	}

	public ServletContext getApplication() {
		return getRequest().getSession().getServletContext();
	}

}
