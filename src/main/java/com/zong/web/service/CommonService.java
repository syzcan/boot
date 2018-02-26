package com.zong.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zong.util.Page;
import com.zong.util.PageData;
import com.zong.util.Result;
import com.zong.web.dao.CommonMapper;
import com.zong.zdb.bean.ColumnField;
import com.zong.zdb.bean.Table;
import com.zong.zdb.service.JdbcCodeService;

/**
 * @desc 通用业务层
 * @author zong
 * @date 2017年3月24日
 */
@Service
public class CommonService {
	@Autowired
	private CommonMapper commonMapper;
	@Autowired
	private JdbcCodeService codeService;

	@Transactional(rollbackFor = Exception.class)
	public void add(String table, PageData pd) throws Exception {
		commonMapper.insert(table, pd);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(String table, PageData pd) throws Exception {
		commonMapper.delete(table, pd);
	}

	@Transactional(rollbackFor = Exception.class)
	public void edit(String table, PageData pd, PageData idPd) throws Exception {
		commonMapper.update(table, pd, idPd);
	}

	public Result load(String table, PageData pd) {
		List<Result> list = commonMapper.find(table, pd);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<Result> find(String table, PageData pd) {
		return commonMapper.find(table, pd);
	}

	public List<Result> findPage(Page page) {
		return commonMapper.findPage(page);
	}

	public void createTable(String tableName, PageData columns) {
		String sql = "create table " + tableName + "(";
		for (Object key : columns.keySet()) {
			sql += key + " " + columns.getString(key) + ",";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
		commonMapper.executeSql(sql);
	}

	public void alterTable(String tableName, PageData columns) {
		List<ColumnField> list = codeService.currentTableColumns(tableName);
		for (Object key : columns.keySet()) {
			boolean flag = true;
			for (ColumnField column : list) {
				if (column.getColumn().equals(key)) {
					flag = false;
				}
			}
			if (flag) {
				commonMapper.executeSql(
						"alter table " + tableName + " add " + key.toString() + " " + columns.getString(key));
			}
		}
	}

	public Table showTable(String tableName) throws Exception {
		return codeService.currentTable(tableName);
	}

	public List<Table> showTables() throws Exception {
		return codeService.currentTables();
	}

	public List<ColumnField> showTableColumns(String tableName) throws Exception {
		return codeService.currentTableColumns(tableName);
	}

}
