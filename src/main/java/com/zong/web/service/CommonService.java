package com.zong.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
}
