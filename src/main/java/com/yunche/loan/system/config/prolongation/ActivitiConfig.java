package com.yunche.loan.system.config.prolongation;

import com.yunche.loan.system.config.db.ActivitiDataSourceConfig;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;


@Configuration
public class ActivitiConfig extends AbstractProcessEngineAutoConfiguration {

    @Bean
    public SpringProcessEngineConfiguration springProcessEngineConfiguration(
            @Qualifier(ActivitiDataSourceConfig.transactionManager) PlatformTransactionManager transactionManager,
            @Qualifier(ActivitiDataSourceConfig.dataSource) DataSource dataSource,
            SpringAsyncExecutor springAsyncExecutor
    ) throws IOException {

        return baseSpringProcessEngineConfiguration(
                dataSource,
                transactionManager,
                springAsyncExecutor);
    }
}
