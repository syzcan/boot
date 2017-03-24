package com.zong.web.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.zong.util.Page;
import com.zong.util.PageData;

/**
 * @desc 通用查询mapper
 * @author zong
 * @date 2017年3月22日
 */
public interface CommonMapper {
	/**
	 * 查询全部
	 * 
	 * @param table
	 * @return
	 */
	List<PageData> find(@Param("table") String table);

	/**
	 * 分页查询
	 * 
	 * @param page
	 * @return
	 */
	List<PageData> findPage(Page page);
}
