package com.zong;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSourceFactory;

/**
 * @desc springboot总配置类和启动入口类
 * @author zong
 * @date 2017年3月21日
 */
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan
@MapperScan(basePackages = "com.zong.**.dao")
public class Application extends SpringBootServletInitializer {
	private static Logger logger = LoggerFactory.getLogger(Application.class);

	@Autowired
	private Environment env;

	/**
	 * 创建数据源
	 */
	@Bean
	public DataSource getDataSource() throws Exception {
		Properties props = new Properties();
		props.put("driverClassName", env.getProperty("jdbc.driverClassName"));
		props.put("url", env.getProperty("jdbc.url"));
		props.put("username", env.getProperty("jdbc.username"));
		props.put("password", env.getProperty("jdbc.password"));
		return DruidDataSourceFactory.createDataSource(props);
	}

	/**
	 * 根据数据源创建SqlSessionFactory
	 */
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		// 配置mybatis插件
		sqlSessionFactoryBean.setConfigLocation(resolver.getResource("classpath:/mybatis-config.xml"));
		return sqlSessionFactoryBean.getObject();
	}

	/**
	 * 打包war发布到tomcat运行需要继承 SpringBootServletInitializer并重写 configure 方法
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(Application.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logger.info("zboot项目启动");
	}

}