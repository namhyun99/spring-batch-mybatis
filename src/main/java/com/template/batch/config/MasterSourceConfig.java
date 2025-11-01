package com.template.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(value = "com.template.batch.dao.master", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class MasterSourceConfig {

  @Primary
  @Bean(name = "masterDataSource")
  @ConfigurationProperties(prefix = "spring.datasource.master")
  public DataSource dataSource() {
    return DataSourceBuilder.create().type(HikariDataSource.class).build();
  }

  @Primary
  @Bean("masterSqlSessionFactory")
  public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
    org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
    configuration.setDefaultExecutorType(ExecutorType.BATCH);
    configuration.setMapUnderscoreToCamelCase(true);

    SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
    bean.setDataSource(dataSource);
    bean.setTypeAliasesPackage("com.template.batch.entity.master");
    bean.setConfiguration(configuration);

    Resource[] res = new PathMatchingResourcePatternResolver().getResources("classpath*:META-INF/sql/master/**/*-sql.xml");
    bean.setMapperLocations(res);

    /*
    Resource myBatisConfig = new PathMatchingResourcePatternResolver().getResource("classpath:META-INF/mybatis/mybatis-config.xml");
    bean.setConfigLocation(myBatisConfig);
    */
    return bean.getObject();
  }

  @Primary
  @Bean(name = "masterTransactionManager")
  public DataSourceTransactionManager masterTransactionManager(@Qualifier("masterDataSource") DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

}