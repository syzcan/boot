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
		}
		return result;
	}

	@ResponseBody
	@RequestMapping("/list")
	public PageData list() {
		PageData result = new PageData("errMsg", "success");
		try {
			PageData pd = super.getPageData();
			List<PageData> data = JsoupUtil.parseList(pd);
			saveData(pd, data);
			result.put("data", data);
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return result;
	}

	private void saveData(PageData pd, List<PageData> data) {
		String craw_store = pd.getString(JsoupUtil.CRAW_STORE_TABLE);
		if (craw_store != null && !"".equals(craw_store)) {
			PageData table = commonService.findTable(craw_store);
			PageData columns = new PageData("title", "varchar(255)").put("url", "varchar(255) NOT NULL UNIQUE KEY")
					.put("content", "longtext").put("create_time", "datetime");
			for (Object key : pd.keySet()) {
				if (!key.equals(JsoupUtil.CRAW_URL) && !key.equals(JsoupUtil.CRAW_STORE_TABLE)
						&& !key.equals(JsoupUtil.RULE_ITEM) && !key.equals(JsoupUtil.RULE_NEXT)) {
					columns.put(key, "text");
				}
			}
			if (table == null) {
				commonService.createTable(craw_store, columns);
			} else {
				commonService.alterTable(craw_store, columns);
			}
			for (PageData pageData : data) {
				try {
					commonService.add(craw_store, pageData.put("create_time", new Date()));
					logger.info("抓取插入 {} : {}", craw_store, pageData.get("url"));
				} catch (Exception e) {
					logger.warn(e.toString());
				}
			}
		}
	}
}
