package com.zong.web.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zong.base.BaseController;
import com.zong.util.JsoupUtil;
import com.zong.util.PageData;
import com.zong.web.service.CommonService;

@Controller
@RequestMapping("/craw")
public class CrawController extends BaseController {
	private Logger logger = LoggerFactory.getLogger(CommonController.class);
	@Autowired
	private CommonService commonService;

	/**
	 * 解析任何页面
	 * 
	 * @param craw_url 抓取地址【必须】
	 * @param exts 非craw_*关键参数的其他扩展规则
	 */
	@ResponseBody
	@RequestMapping("/data")
	public PageData data() {
		PageData result = new PageData("errMsg", "success");
		try {
			PageData pd = super.getPageData();
			PageData data = JsoupUtil.parseDetail(pd);
			result.put("data", data);
		} catch (Exception e) {
			logger.error(e.toString(), e);
			result.put("errMsg", "系统错误");
		}
		return result;
	}

	/**
	 * 解析列表页面
	 * 
	 * @param craw_url 抓取地址【必须】
	 * @param craw_item 条目规则【必须】
	 * @param craw_next 下一页规则
	 * @param craw_store 存储表名，只有指定才保存到数据库
	 * @param exts 非craw_*关键参数的其他扩展规则
	 */
	@ResponseBody
	@RequestMapping("/list")
	public PageData list() {
		PageData result;
		try {
			PageData pd = super.getPageData();
			result = JsoupUtil.parseList(pd);
			saveData(pd, (List<PageData>) result.get("data"));
			result.put("errMsg", "success");
		} catch (Exception e) {
			logger.error(e.toString(), e);
			result = new PageData("errMsg", "系统错误");
		}
		return result;
	}

	private void saveData(PageData pd, List<PageData> data) {
		String craw_store = pd.getString(JsoupUtil.CRAW_STORE_TABLE);
		if (craw_store != null && !"".equals(craw_store)) {
			craw_store = JsoupUtil.storeTable(craw_store);
			PageData table = commonService.findTable(craw_store);
			PageData columns = JsoupUtil.baseTableColumns(pd);
			if (table == null) {
				commonService.createTable(craw_store, columns);
			} else {
				commonService.alterTable(craw_store, columns);
			}
			for (PageData pageData : data) {
				try {
					commonService.add(craw_store, pageData.put(JsoupUtil.STORE_TABLE_COL_CREATE_TIME, new Date()));
					logger.info("抓取插入 {} : {}", craw_store, pageData.get("url"));
				} catch (Exception e) {
					logger.warn(e.toString());
				}
			}
		}
	}
}
