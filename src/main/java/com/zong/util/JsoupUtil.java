package com.zong.util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @desc jsoup爬虫解析网页
 * @author zong
 * @date 2017年4月3日
 */
public class JsoupUtil {
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
	public static final String CRAW_RULE_TABLE = "craw_rule";
	public static final String CRAW_STORE_TABLE = "craw_store";
	public static final String CRAW_URL = "craw_url";
	public static final String RULE_ITEM = "rule_item";
	public static final String RULE_NEXT = "rule_next";
	public static final String RULE_EXT_NAME = "rule_ext_name";
	public static final String RULE_EXT_CSS = "rule_ext_css";
	public static final String RULE_EXT_TYPE = "rule_ext_type";
	public static final String RULE_EXT_ATTR = "rule_ext_attr";
	public static final String RULE_EXT_MODE = "rule_ext_mode";

	/**
	 * 解析列表页面
	 *
	 * @param request
	 */
	public static List<PageData> parseList(PageData request) throws Exception {
		String craw_url = request.getString(CRAW_URL);
		String rule_item = request.getString(RULE_ITEM);
		String rule_next = request.getString(RULE_NEXT);
		List<PageData> extFields = extFields(request);
		Document document = Jsoup.connect(craw_url).timeout(60 * 1000).userAgent(USER_AGENT).get();
		Elements elements = document.select(rule_item);
		List<PageData> list = new ArrayList<PageData>();
		for (Element element : elements) {
			PageData data = parseExt(craw_url, element, extFields);
			list.add(data);
		}
		String link = parseElementAttr(document.select("body").first(),
				new PageData(RULE_EXT_CSS, rule_next).put(RULE_EXT_ATTR, "href"), craw_url).toString();
		request.put("next_url", dealLink(craw_url, link));
		return list;
	}

	/**
	 * 解析详细
	 *
	 * @param request
	 */
	public static PageData parseDetail(PageData request) throws Exception {
		String craw_url = request.getString(CRAW_URL);
		Document document = Jsoup.connect(craw_url).timeout(60 * 1000).userAgent(USER_AGENT).get();
		PageData data = parseExt(craw_url, document.select("body").first(), extFields(request));
		return data;
	}

	/**
	 * 获取当前请求规则扩展字段
	 *
	 * @param request
	 */
	private static List<PageData> extFields(PageData request) {
		List<PageData> fields = new ArrayList<PageData>();
		for (Object key : request.keySet()) {
			if (key.equals(CRAW_URL) || key.equals(CRAW_STORE_TABLE) || key.equals(RULE_ITEM) || key.equals(RULE_NEXT)) {
				continue;
			}
			PageData ext = new PageData(RULE_EXT_NAME, key.toString());
			String[] vals = request.getString(key).split("\\|");
			ext.put(RULE_EXT_CSS, vals[0]);
			ext.put(RULE_EXT_TYPE, vals[1]);
			if (vals.length > 2) {
				ext.put(RULE_EXT_ATTR, vals[2]);
			}
			if (vals.length > 3) {
				ext.put(RULE_EXT_MODE, vals[3]);
			}
			fields.add(ext);
		}
		return fields;
	}

	/**
	 * 解析扩展字段
	 *
	 * @param craw_url
	 * @param element
	 * @param extFields
	 */
	private static PageData parseExt(String craw_url, Element element, List<PageData> extFields) {
		PageData data = new PageData();
		for (PageData ext : extFields) {
			if ("text".equals(ext.getString(RULE_EXT_TYPE))) {
				data.put(ext.getString(RULE_EXT_NAME), parseElementText(element, ext));
			} else if ("html".equals(ext.getString(RULE_EXT_TYPE))) {
				data.put(ext.getString(RULE_EXT_NAME), parseElementHtml(element, ext));
			} else if ("attr".equals(ext.getString(RULE_EXT_TYPE))) {
				data.put(ext.getString(RULE_EXT_NAME), parseElementAttr(element, ext, craw_url));
			}
		}
		return data;
	}

	/**
	 * src和href等链接判断是否加http和项目路径
	 *
	 * @param craw_url
	 * @param link
	 */
	private static String dealLink(String craw_url, String link) {
		String protocal = craw_url.split("://")[0];
		String domain = craw_url.split("://")[0] + "://" + craw_url.split("://")[1].split("/")[0];
		if (link.startsWith("//")) {
			link = protocal + ":" + link;
		} else if (!link.startsWith("http") && !"".equals(link)) {
			if (link.startsWith("/")) {
				link = domain + link;
			} else {
				link = domain + "/" + link;
			}
		}
		return link;
	}

	private static Object parseElement(Element element, String cssQuery) {
		if (cssQuery == null || "".equals(cssQuery)) {
			return null;
		}
		if (cssQuery.indexOf(":eq") > -1) {
			String css1 = cssQuery.substring(0, cssQuery.indexOf(":eq"));
			String css2 = cssQuery.substring(cssQuery.indexOf(":eq") + 6);
			String index = cssQuery.substring(cssQuery.indexOf(":eq") + 3, cssQuery.indexOf(":eq") + 6).replace("(", "")
					.replace(")", "");
			Element e = element.select(css1).get(Integer.parseInt(index));
			if (!css2.equals("")) {
				return e.select(css2);
			}
			return e;
		} else {
			Elements es = element.select(cssQuery);
			return es;
		}
	}

	public static Object parseElementText(Element element, PageData ext) {
		Object object = parseElement(element, ext.getString(RULE_EXT_CSS));
		if (object != null) {
			if (object instanceof Elements) {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					for (Element e : (Elements) object) {
						list.add(e.text());
					}
					return list;
				} else {
					if (!((Elements) object).isEmpty()) {
						return ((Elements) object).get(0).text();
					}
				}
			} else {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					list.add(((Element) object).text());
					return list;
				} else {
					return ((Element) object).text();
				}
			}
		}
		return "";
	}

	public static Object parseElementHtml(Element element, PageData ext) {
		Object object = parseElement(element, ext.getString(RULE_EXT_CSS));
		if (object != null) {
			if (object instanceof Elements) {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					for (Element e : (Elements) object) {
						list.add(e.html());
					}
					return list;
				} else {
					if (!((Elements) object).isEmpty()) {
						return ((Elements) object).get(0).html();
					}
				}
			} else {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					list.add(((Element) object).html());
					return list;
				} else {
					return ((Element) object).html();
				}
			}
		}
		return "";
	}

	public static Object parseElementAttr(Element element, PageData ext, String craw_url) {
		String attr = ext.getString(RULE_EXT_ATTR);
		Object object = parseElement(element, ext.getString(RULE_EXT_CSS));
		if (object != null) {
			if (object instanceof Elements) {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					for (Element e : (Elements) object) {
						list.add(dealAttr(e.attr(attr), ext, craw_url));
					}
					return list;
				} else {
					if (!((Elements) object).isEmpty()) {
						return dealAttr(((Elements) object).get(0).attr(attr), ext, craw_url);
					}
				}
			} else {
				if ("array".equals(ext.getString(RULE_EXT_MODE))) {
					List<String> list = new ArrayList<String>();
					list.add(dealAttr(((Element) object).attr(attr), ext, craw_url));
					return list;
				} else {
					return dealAttr(((Element) object).attr(attr), ext, craw_url);
				}
			}
		}
		return "";
	}

	private static String dealAttr(String val, PageData ext, String craw_url) {
		// src和href等链接判断是否加http和项目路径
		if ("src".equals(ext.getString(RULE_EXT_ATTR)) || "href".equals(ext.getString(RULE_EXT_ATTR))) {
			val = dealLink(craw_url, val);
		}
		return val;
	}

}