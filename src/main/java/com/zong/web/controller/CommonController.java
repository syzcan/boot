package com.zong.web.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.multipart.MultipartFile;

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
	 * 获取当前数据库的所有表，在config.json额外配置数据库信息
	 */
	@ResponseBody
	@RequestMapping(value = "/tables", method = RequestMethod.GET)
	public PageData tables() {
		PageData result = new PageData("errMsg", "success");
		try {
			result.put("data", commonService.showTables());
		} catch (Exception e) {
			result.put("errMsg", "数据库文件config.json配置错误");
		}
		return result;
	}

	/**
	 * 获取表的所有列详细信息
	 * 
	 * @param tableName
	 */
	@ResponseBody
	@RequestMapping(value = "/tables/{tableName}", method = RequestMethod.GET)
	public PageData table(@PathVariable String tableName) {
		PageData result = new PageData("errMsg", "success");
		try {
			result.put("data", commonService.showTable(tableName));
		} catch (Exception e) {
			result.put("errMsg", "系统错误");
		}
		return result;
	}

	@RequestMapping
	public String index(Model model) {
		try {
			model.addAttribute("tables", commonService.showTables());
		} catch (Exception e) {
			model.addAttribute("errMsg", "系统错误");
		}
		return "/index";
	}

	/**
	 * 查询列表页面
	 * 
	 * @param table 表名
	 */
	@RequestMapping("/{table}/list")
	public String list(@PathVariable String table, Page page, String type, String column, String keyword, Model model) {
		page.setTable(table);
		if (column != null && !column.equals("") && keyword != null && !keyword.equals("")) {
			PageData pd = new PageData();
			if ("1".equals(type)) {
				pd.put("like", new PageData(column, keyword));
			} else {
				pd.put(column, keyword);
			}
			page.setPd(pd);
		}
		List<PageData> list = commonService.findPage(page);
		List<PageData> datas = new ArrayList<PageData>();
		for (PageData pageData : list) {
			try {
				datas.add(new PageData("data", pageData).put("json",
						objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pageData)));
			} catch (Exception e) {
				logger.error(e.toString(), e);
			}
		}
		model.addAttribute("datas", datas);
		model.addAttribute("table", table);
		model.addAttribute("page", page);
		model.addAttribute("type", type);
		model.addAttribute("column", column);
		model.addAttribute("keyword", keyword);
		return "/list";
	}

	/**
	 * 渲染表单
	 * 
	 * @param table
	 */
	@RequestMapping("/{table}/form")
	public String form(@PathVariable String table, Model model) {
		try {
			model.addAttribute("table", table);
			model.addAttribute("columns", commonService.showTableColumns(table));
		} catch (Exception e) {
			model.addAttribute("errMsg", "系统错误");
		}
		return "/form";
	}

	/**
	 * 查询列表
	 * 
	 * @param table 表名
	 */
	@ResponseBody
	@RequestMapping(value = "/{table}", method = RequestMethod.GET)
	public PageData datas(@PathVariable String table, Page page, String column, String keyword) {
		PageData result = new PageData("errMsg", "success");
		page.setTable(table);
		if (column != null && !column.equals("") && keyword != null && !keyword.equals("")) {
			PageData pd = new PageData().put("like", new PageData(column, keyword));
			page.setPd(pd);
		}
		result.put("data", commonService.findPage(page)).put("page", page);
		logger.info("查询 {} 列表数据", table);
		return result;
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
		PageData result = new PageData("errMsg", "success");
		result.put("data", commonService.load(table, new PageData("id", id)));
		logger.info("查询 {} 单条数据 id={}", table, id);
		return result;
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
		PageData result = new PageData("errMsg", "success");
		try {
			commonService.add(table, data);
			logger.info("新增 {} 数据", table);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			result.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.put("errMsg", "系统错误");
		}
		return result;
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
		PageData result = new PageData("errMsg", "success");
		try {
			commonService.edit(table, data, new PageData("id", id));
			logger.info("修改 {} 数据 id={}", table, id);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			result.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.put("errMsg", "系统错误");
		}
		return result;
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
		PageData result = new PageData("errMsg", "success");
		try {
			commonService.delete(table, new PageData("id", id));
			logger.info("删除 {} 数据 id={}", table, id);
		} catch (BusinessException e) {
			logger.warn(e.getErrMsg());
			result.put("errMsg", e.getErrMsg());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.put("errMsg", "系统错误");
		}
		return result;
	}

	/**
	 * 上传附件
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public Map<String, String> uploadFile(MultipartFile file, HttpServletRequest request) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("fileName", file.getOriginalFilename());
		map.put("url", this.upload(file, request));
		return map;
	}

	/**
	 * 
	 * @param file
	 * @param uploadPath 文件保存位置
	 * @return 返回附件访问路径
	 */
	private String upload(MultipartFile file, HttpServletRequest request) {
		// 文件目录按时间归类文件夹
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateDir = dateFormat.format(new Date());
		String path = "upload/" + dateDir;
		Random random = new Random();
		String extName = "";
		if (file.getOriginalFilename().lastIndexOf(".") >= 0) {
			extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		}
		path += "/" + random.nextInt(10000) + System.currentTimeMillis() + extName;
		File f = new File(request.getSession().getServletContext().getRealPath("/" + path));
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			file.transferTo(f);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return path;
	}
}
