package com.zong.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 分页组件
 * @author zong
 * @date 2016年3月23日
 */
public class Page {

	private int showCount = 10; // 每页显示记录数，默认10条
	private int totalPage; // 总页数
	private int totalResult; // 总记录数
	private int currentPage = 1; // 当前页，默认第一页
	private int currentResult; // 当前记录起始索引
	private List<Integer> nums;// 页码列表导航
	private int maxNums = 9;// 最大显示页码数默认5，用单数
	private boolean entityOrField; // true:需要分页的地方，传入的参数就是Page实体；false:需要分页的地方，传入的参数所代表的实体拥有Page属性
	private PageData pd = new PageData();// 封装页面查询参数
	private String table;

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Page() {
	}

	public Page(PageData pd) {
		this.pd = pd;
		try {
			// 页面传递的page参数通过pd封装到page
			currentPage = Integer.parseInt(pd.getString("currentPage"));
		} catch (Exception e) {
			currentPage = 1;
		}
		try {
			showCount = Integer.parseInt(pd.getString("showCount"));
		} catch (Exception e) {
			showCount = 10;
		}
	}

	public int getTotalPage() {
		if (totalResult % getShowCount() == 0)
			totalPage = totalResult / getShowCount();
		else
			totalPage = totalResult / getShowCount() + 1;
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(int totalResult) {
		this.totalResult = totalResult;
	}

	public int getCurrentPage() {
		if (currentPage <= 0)
			currentPage = 1;
		if (currentPage > getTotalPage())
			currentPage = getTotalPage();
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getShowCount() {
		return showCount;
	}

	public void setShowCount(int showCount) {

		this.showCount = showCount;
	}

	public int getCurrentResult() {
		currentResult = (getCurrentPage() - 1) * getShowCount();
		if (currentResult < 0)
			currentResult = 0;
		return currentResult;
	}

	public void setCurrentResult(int currentResult) {
		this.currentResult = currentResult;
	}

	public boolean isEntityOrField() {
		return entityOrField;
	}

	public void setEntityOrField(boolean entityOrField) {
		this.entityOrField = entityOrField;
	}

	public PageData getPd() {
		return pd;
	}

	public void setPd(PageData pd) {
		this.pd = pd;
	}

	public List<Integer> getNums() {
		List<Integer> ns = new ArrayList<Integer>();
		// 最多显示maxNums个页码
		if (getCurrentPage() <= (maxNums / 2 + 1)) {
			for (int i = 1; i <= maxNums; i++) {
				if (i <= getTotalPage()) {
					ns.add(i);
				}
			}
		}
		if (getCurrentPage() > (maxNums / 2 + 1)) {
			if (getTotalPage() - getCurrentPage() >= (maxNums / 2)) {
				for (int i = getCurrentPage() - (maxNums / 2); i <= getCurrentPage() + (maxNums / 2); i++) {
					if (i <= getTotalPage()) {
						ns.add(i);
					}
				}
			} else {
				for (int i = (getTotalPage() - maxNums + 1); i <= getTotalPage(); i++) {
					if (i >= 1) {
						ns.add(i);
					}
				}
			}
		}
		if (ns.size() == 0) {
			ns.add(1);
		}
		nums = ns;
		return nums;
	}

	public void setNums(List<Integer> nums) {
		this.nums = nums;
	}

}
