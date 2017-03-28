package com.zong.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zong.base.BaseController;
import com.zong.util.BusinessException;
import com.zong.util.Page;
import com.zong.util.PageData;
import com.zong.web.service.CommonService;

/**
 * @desc restful方式实现任何表的增删改查，根据后缀json和xml响应不同类型数据，表主键=id【自增】
 * @author zong
 * @date 2017年3月21日
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(CommonController.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private CommonService commonService;

	/**
	 * 查询列表页面
	 * 
	 * @param table 表名
	 */
	@RequestMapping("/{table}/list")
	public String list(@PathVariable String table, Model model) {
		Page page = super.getPage();
		page.setTable(table);
		List<PageData> list = commonService.findPage(page);
		List<String> datas = new ArrayList<String>();
		for (PageData pageData : list) {
			try {
				datas.add(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageData));
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		model.addAttribute("datas", datas);
		model.addAttribute("table", table);
		return "/list";
	}

	/**
	 * 查询列表
	 * 
	 * @param table 表名
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public PageData datas(@PathVariable String table) {
		PageData pd = new PageData("errMsg", "success");
		Page page = super.getPage();
		page.setTable(table);
		page.getPd().remove("table");
		pd.put("data", commonService.findPage(page)).put("page", page);
		logger.info("查询 {} 列表数据", table);
		return pd;
	}

	/**
	 * 查询单条
	 * 
	 * @param table 表名
	 * @param id 主键
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.GET)
	public PageData data(@PathVariable String table, @PathVariable String id) {
		PageData pd = new PageData("errMsg", "success");
		pd.put("data", commonService.load(table, new PageData("id", id)));
		logger.info("查询 {} 单条数据 id={}", table, id);
		return pd;
	}

	/**
	 * 增加
	 * 
	 * @param table 表名
	 * @param data POST方式提交的json或xml
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}", method = RequestMethod.POST)
	public PageData add(@PathVariable String table, @RequestBody PageData data) {
		PageData pd = new PageData("errMsg", "success");
		try {
			commonService.add(table, data);
			logger.info("新增 {} 数据", table);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			pd.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pd.put("errMsg", "系统错误");
		}
		return pd;
	}

	/**
	 * 修改
	 * 
	 * @param table 表名
	 * @param id 主键
	 * @param data data POST方式提交的json或xml
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.PUT)
	public PageData edit(@PathVariable String table, @PathVariable String id, @RequestBody PageData data) {
		PageData pd = new PageData("errMsg", "success");
		try {
			commonService.edit(table, data, new PageData("id", id));
			logger.info("修改 {} 数据 id={}", table, id);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			pd.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pd.put("errMsg", "系统错误");
		}
		return pd;
	}

	/**
	 * 删除
	 * 
	 * @param table 表名
	 * @param id 主键
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}/{id}", method = RequestMethod.DELETE)
	public PageData delete(@PathVariable String table, @PathVariable String id) {
		PageData pd = new PageData("errMsg", "success");
		try {
			commonService.delete(table, new PageData("id", id));
			logger.info("删除 {} 数据 id=", table, id);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			pd.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			pd.put("errMsg", "系统错误");
		}
		return pd;
	}

}
