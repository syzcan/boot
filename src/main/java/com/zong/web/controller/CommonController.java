package com.zong.web.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.zong.util.Page;
import com.zong.util.PageData;
import com.zong.util.Result;
import com.zong.web.service.CommonService;

/**
 * @desc restful方式实现任何表的增删改查，根据后缀json和xml响应不同类型数据，表主键=id【自增】
 * @author zong
 * @date 2017年3月21日
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {
	private Logger LOGGER = LoggerFactory.getLogger(CommonController.class);
	private static ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private CommonService commonService;

	@RequestMapping
	public String index(Model model) {
		try {
			model.addAttribute("tables", commonService.showTables());
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
		}
		return "/index";
	}

	/**
	 * 获取当前数据库的所有表，通过JdbcCodeService获取数据库信息
	 */
	@ResponseBody
	@RequestMapping(value = "/tables", method = RequestMethod.GET)
	public Result tables() {
		Result result = Result.success();
		try {
			result.put("rows", commonService.showTables());
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
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
	public Result table(@PathVariable String tableName) {
		Result result = Result.success();
		try {
			result.put("data", commonService.showTable(tableName));
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
		}
		return result;
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
		List<Result> list = commonService.findPage(page);
		List<PageData> datas = new ArrayList<PageData>();
		for (Result data : list) {
			try {
				datas.add(new PageData("data", data).put("json",
						objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(data)));
			} catch (Exception e) {
				LOGGER.error(e.toString(), e);
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
			LOGGER.error(e.toString(), e);
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
	public Result datas(@PathVariable String table, Page page, String column, String keyword) {
		Result result = Result.success();
		page.setTable(table);
		if (column != null && !column.equals("") && keyword != null && !keyword.equals("")) {
			PageData pd = new PageData().put("like", new PageData(column, keyword));
			page.setPd(pd);
		}
		result.put("rows", commonService.findPage(page)).put("page", page);
		LOGGER.info("查询 {} 列表数据", table);
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
	public Result data(@PathVariable String table, @PathVariable String id) {
		Result result = Result.success();
		result.put("data", commonService.load(table, new PageData("id", id)));
		LOGGER.info("查询 {} 单条数据 id={}", table, id);
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
	public Result add(@PathVariable String table, @RequestBody PageData data) {
		Result result = Result.success();
		try {
			commonService.add(table, data);
			LOGGER.info("新增 {} 数据", table);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
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
	public Result edit(@PathVariable String table, @PathVariable String id, @RequestBody PageData data) {
		Result result = Result.success();
		try {
			commonService.edit(table, data, new PageData("id", id));
			LOGGER.info("修改 {} 数据 id={}", table, id);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
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
	public Result delete(@PathVariable String table, @PathVariable String id) {
		Result result = Result.success();
		try {
			commonService.delete(table, new PageData("id", id));
			LOGGER.info("删除 {} 数据 id={}", table, id);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
		}
		return result;
	}

	/**
	 * 上传附件
	 * 
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public Result uploadFile(MultipartFile file, HttpServletRequest request) {
		Result result = this.upload(file, request);
		result.put("fileName", file.getOriginalFilename());
		return result;
	}

	/**
	 * 
	 * @param file
	 * @param uploadPath 文件保存位置
	 * @return 返回附件访问路径
	 */
	private Result upload(MultipartFile file, HttpServletRequest request) {
		Result result = Result.success();
		// 文件目录按时间归类文件夹
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String dateDir = dateFormat.format(new Date());
		String path = "upload/" + dateDir;
		int randomNum = new Random().nextInt(10000);
		String random = "";
		String extName = "";
		if (file.getOriginalFilename().lastIndexOf(".") >= 0) {
			extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		}
		if (randomNum < 10) {
			random = "000" + randomNum;
		} else if (randomNum < 100) {
			random = "00" + randomNum;
		} else if (randomNum < 1000) {
			random = "0" + randomNum;
		} else {
			random = "" + randomNum;
		}
		path += "/" + System.currentTimeMillis() + "_" + random + extName;
		File f = new File(request.getSession().getServletContext().getRealPath("/" + path));
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		try {
			file.transferTo(f);
			result.put("filePath", path);
		} catch (Exception e) {
			LOGGER.error(e.toString(), e);
			result.error(e);
		}
		return result;
	}
}
