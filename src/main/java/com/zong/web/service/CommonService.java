package com.zong.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zong.util.BusinessException;
import com.zong.util.Page;
import com.zong.util.PageData;
import com.zong.web.dao.CommonMapper;

/**
 * @desc 通用业务层
 * @author zong
 * @date 2017年3月24日
 */
@Service
public class CommonService {
	@Autowired
	private CommonMapper commonMapper;
	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.url}")
	private String url;

	@Transactional(rollbackFor = Exception.class)
	public void add(String table, PageData pd) throws BusinessException {
		commonMapper.insert(table, pd);
	}

	@Transactional(rollbackFor = Exception.class)
	public void delete(String table, PageData pd) throws BusinessException {
		commonMapper.delete(table, pd);
	}

	@Transactional(rollbackFor = Exception.class)
	public void edit(String table, PageData pd, PageData idPd) throws BusinessException {
		commonMapper.update(table, pd, idPd);
	}

	public PageData load(String table, PageData pd) {
		List<PageData> list = commonMapper.find(table, pd);
		if (!list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	public List<PageData> findPage(Page page) {
		return commonMapper.findPage(page);
	}

	public List<PageData> findTables() {
		String database = url.split("\\?")[0].substring(url.lastIndexOf("/") + 1);
		String sql = "select table_name,table_comment,table_rows from information_schema.tables where table_schema='"
				+ database + "' and table_type='BASE TABLE'";
		List<PageData> datas = commonMapper.executeSql(sql);
		return datas;
	}

	public PageData findTable(String tableName) {
		String database = url.split("\\?")[0].substring(url.lastIndexOf("/") + 1);
		String sql = "select table_name,table_comment,table_rows from information_schema.tables where table_schema='"
				+ database + "' and table_type='BASE TABLE' and table_name='" + tableName + "'";
		List<PageData> datas = commonMapper.executeSql(sql);
		if (!datas.isEmpty()) {
			return datas.get(0);
		}
		return null;
	}

	public void createTable(String tableName, PageData columns) {
		String sql = "create table " + tableName + "(id int(11) NOT NULL auto_increment,";
		for (Object key : columns.keySet()) {
			sql += key + " " + columns.getString(key) + ",";
		}
		sql += "PRIMARY KEY  (id))";
		commonMapper.executeSql(sql);
	}

	public void alterTable(String tableName, PageData columns) {
		String database = url.split("\\?")[0].substring(url.lastIndexOf("/") + 1);
		String sql = "select * from information_schema.columns where table_schema=\'" + database
				+ "\' and table_name=\'" + tableName + "\'";
		List<PageData> datas = commonMapper.executeSql(sql);
		for (Object key : columns.keySet()) {
			boolean flag = true;
			for (PageData data : datas) {
				if (data.get("COLUMN_NAME").equals(key)) {
					flag = false;
				}
			}
			if (flag) {
				commonMapper.executeSql(
						"alter table " + tableName + " add " + key.toString() + " " + columns.getString(key));
			}
		}
	}

}
