package com.zong.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zong.base.BaseController;
import com.zong.util.Page;
import com.zong.util.PageData;
import com.zong.web.dao.CommonMapper;

/**
 * @desc 
 * @author zong
 * @date 2017年3月21日
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(CommonController.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private CommonMapper commonMapper;

	@RequestMapping("/list")
	public String list(String table, Model model) {
		Page page = super.getPage();
		page.setTable(table);
		List<PageData> list = commonMapper.findPage(page);
		List<String> datas = new ArrayList<String>();
		for (PageData pageData : list) {
			try {
				datas.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageData));
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		model.addAttribute("datas", datas);
		return "/list";
	}

	@ResponseBody
	@RequestMapping("/datas")
	public PageData list(String table) {
		PageData pd = new PageData("errMsg", "success");
		Page page = super.getPage();
		page.setTable(table);
		pd.put("data", commonMapper.findPage(page)).put("page", page);
		logger.info("查询{}数据info", table);
		return pd;
	}
}
