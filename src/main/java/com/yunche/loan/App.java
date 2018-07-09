package com.yunche.loan;

import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.service.ActivitiVersionService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableFeignClients
@EnableScheduling
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

    @Bean
    public CommandLineRunner init(final RepositoryService repositoryService,
//                                  final RuntimeService runtimeService,
//                                  final TaskService taskService,
                                  final ActivitiCache activitiCache,
                                  final ActivitiVersionService activitiVersionService) {

        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                // 部署
                repositoryService.createDeployment()
                        .name("消费贷流程-data_flow")
                        .addClasspathResource("processes/loan_process_df.bpmn")
                        .deploy();

                // 刷新activiti缓存数据
                activitiCache.refresh();

                // 流程替换
                activitiVersionService.replaceActivitiVersion();

//                System.out.println("Number of process definitions : "
//                        + repositoryService.createProcessDefinitionQuery().count());
//                System.out.println("Number of tasks : " + taskService.createTaskQuery().count());
//                System.out.println("Number of tasks after process start: " + taskService.createTaskQuery().count());
            }
        };

    }
}

