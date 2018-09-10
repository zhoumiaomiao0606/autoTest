package com.yunche.loan;

import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.service.ActivitiService;
import org.activiti.engine.RepositoryService;
import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, PageHelperAutoConfiguration.class})
@EnableTransactionManagement
@EnableFeignClients
@EnableScheduling
@EnableAsync
@MapperScan(basePackages = "com.yunche.loan.mapper")
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    @Primary
    public TaskExecutor primaryTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        return executor;
    }

    /**
     * 1.1）自动寻找Validator实现。
     * LocalValidatorFactoryBean自动检查在classpath中的Bean Validation的实现，将
     *
     * @see javax.validation.ValidatorFactory 作为其缺省备选，本例将自动找到Hibernate Validator。
     * 但是如果在classpath下面有超过一个实现（例如运行在完全的J2EE web应用服务器，如GlassFish或WebSphere），
     * 这时通过下面方式指定采用哪个，以避免不可测性。
     * validator.setProviderClass(HibernateValidator.class);
     * 但这样的缺点在于是complie的而不是runtime的。要runtime，可以采用
     * validator.setProviderClass(Class.forName("org.hibernate.validator.HibernateValidator"));
     * 但如果类写错了，无法在compile的时候查出
     * validator.setProviderClass(Class.forName("org.hibernate.validator.HibernateValidator"));
     * <p>
     * <p>
     * 1.2）为Validator进行消息本地化
     * 缺省的使用classpath路径下的ValidationMessages.properties,
     * ValidationMessages_[language].properties, ValidationMessages_[language]_[region].properties），
     * 但在Bean validation1.1开始，可以自行提供国际化方式。
     */
    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        return validator;
    }


    @Bean
    public CommandLineRunner init(RepositoryService repositoryService,
                                  ActivitiCache activitiCache,
                                  ActivitiService activitiService) {

        return new CommandLineRunner() {

            @Override
            @DistributedLock(300)
            @Transactional(rollbackFor = Exception.class)
            public void run(String... args) throws Exception {

                // 部署
                deploy("processes/loan_process.bpmn", "消费贷流程");

                deploy("processes/loan_process_instead_pay.bpmn", "代偿流程");

                deploy("processes/loan_process_collection.bpmn", "上门催收流程");

                deploy("processes/loan_process_legal.bpmn", "法务处理流程");

                deploy("processes/loan_process_bridge.bpmn", "过桥资金流程");
            }

            /**
             * 部署新流程
             *
             * @param processClassPathResource
             * @param processName
             */
            private void deploy(String processClassPathResource, String processName) {

                repositoryService.createDeployment()
                        .addClasspathResource(processClassPathResource)
                        .name(processName)
                        .deploy();

                // 流程替换
                activitiService.replaceActivitiVersion(processClassPathResource);

                // 刷新activiti缓存数据
                activitiCache.refresh();
            }
        };

    }
}

