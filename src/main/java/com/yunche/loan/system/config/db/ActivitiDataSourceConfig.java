package com.yunche.loan.system.config.db;


import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = ActivitiDataSourceConfig.packageName,sqlSessionFactoryRef = ActivitiDataSourceConfig.sqlSessionFactory)
public class ActivitiDataSourceConfig {

    public static final String dbName = "activiti";

    public static final String dataSource = dbName+"DataSource";

    public static final String sqlSessionFactory = dbName+"SqlSessionFactory";

    public static final String transactionManager = dbName+"TransactionManager";

    public static final String packageName = "com.yunche.loan.dao."+dbName;

    public static final String prefix = "spring.datasource."+dbName;

    public static final String locationPattern = "classpath*:mapper/"+dbName+"/*.xml";

    @Bean(dataSource)
    @ConfigurationProperties(prefix)
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(sqlSessionFactory)
    public SqlSessionFactory dataSourceFactory() throws Exception {

        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();

        sessionFactoryBean.setDataSource(dataSource());

        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()

                .getResources(locationPattern));

        return sessionFactoryBean.getObject();

    }

    @Bean(transactionManager)
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
}
