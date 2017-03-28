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
     * 插入一条数据
     * insert into table(id,name,age) values(1,'zong',30)
     * @param table 表名
     * @param pd 字段键值封装{"id":1,"name":"zong","age":30}
     */
    void insert(@Param("table") String table, @Param("pd") PageData pd);
    
    /**
     * 删除数据
     * delete from user where id=1
     * @param table 表名
     * @param pd 查询参数{"id":1}
     */
    void delete(@Param("table") String table, @Param("pd") PageData pd);
 
    /**
     * 更新数据
     * update user set name='zonge',age=35 where id=1
     * @param table 表名
     * @param pd 字段键值封装{"name":"zonge","age":35}
     * @param idPd 主键键值封装{"id":1}
     */
    void update(@Param("table") String table, @Param("pd") PageData pd, @Param("idPd") PageData idPd);
 
 
    /**
     * 条件查询全部
     *
     * @param table 表名
     * @param pd
     * @return
     */
    List<PageData> find(@Param("table") String table, @Param("pd") PageData pd);
 
    /**
     * 条件分页查询全部
     * select * from user where age=30 and name like '%z%' order by age desc
     * @param page {"table":"user",pd:{"age":30,"like":{"name":"z"},"desc":"age"}...}
     * @return
     */
    List<PageData> findPage(Page page);
     
    int count(@Param("table") String table);
}
