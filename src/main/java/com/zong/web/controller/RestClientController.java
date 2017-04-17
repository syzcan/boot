package com.zong.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zong.util.PageData;

/**
 * @desc rest接口测试客户端页面
 * @author zong
 * @date 2017年4月17日
 */
@Controller
@RequestMapping("/rest")
public class RestClientController {
	@RequestMapping
	public String rest() {
		return "/rest";
	}

	@RequestMapping(value = "/request", method = RequestMethod.POST)
	public PageData request(String url, String type) {
		PageData result = new PageData("errMsg", "success");

		return result;
	}
}
