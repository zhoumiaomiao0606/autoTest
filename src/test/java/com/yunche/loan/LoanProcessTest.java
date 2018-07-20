package com.yunche.loan;

import com.alibaba.fastjson.JSON;
import com.yunche.loan.config.cache.ActivitiCache;
import com.yunche.loan.config.cache.EmployeeCache;
import org.activiti.engine.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhouguoliang on 2018/1/18.
 * 工作流demo测试
 */
public class LoanProcessTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LoanProcessTest.class);

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ActivitiCache activitiCache;

    @Autowired
    private EmployeeCache employeeCache;

//    @Autowired
//    private ApplicantRepository applicantRepository;

//    private Wiser wiser;

    @Test
    public void testx() {
        long totalTime = 1000000L;
        logger.info("/car/import：导入车型库总耗时 : {}min{}s", (totalTime / 1000) / 60, (totalTime / 1000) % 60);
    }

    @Test
    public void test1() {
        Map<String, List<String>> map = activitiCache.getNodeRolesMap();
        System.out.println(map);
    }

    @Test
    public void test2() {
        Set<String> cascadeChildIdList = employeeCache.getCascadeChildIdList(238L);
        logger.info(JSON.toJSONString(cascadeChildIdList));
    }

    @Before
    public void setup() {
//        wiser = new Wiser();
//        wiser.setPort(1025);
//        wiser.start();
    }

    @After
    public void cleanup() {
//        wiser.stop();
    }

    @Test
    public void testHappyPath() {

        // Create test applicant
//        Applicant applicant = new Applicant("John Doe", "john@activiti.org", "12344");
//        applicantRepository.save(applicant);

        // Start process instance
        Map<String, Object> variables = new HashMap<String, Object>();
//        variables.put("applicant", applicant);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("loanDemo", variables);

        // First, the 'phone interview' should be active
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("partner_group")
                .singleResult();
        Assert.assertEquals("征信申请", task.getName());

        // Completing the phone interview with success should trigger two new tasks
        Map<String, Object> taskVariables = new HashMap<String, Object>();
        taskVariables.put("creditResult", true);
        taskService.complete(task.getId(), taskVariables);

        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .orderByTaskName().asc()
                .list();
        Assert.assertEquals(2, tasks.size());
        Assert.assertEquals("社会征信", tasks.get(0).getName());
        Assert.assertEquals("银行征信", tasks.get(1).getName());

        // Completing both should wrap up the subprocess, send out the 'welcome mail' and end the process instance
        taskVariables = new HashMap<String, Object>();
        taskVariables.put("techOk", true);
        taskService.complete(tasks.get(0).getId(), taskVariables);

        taskVariables = new HashMap<String, Object>();
        taskVariables.put("financialOk", true);
        taskService.complete(tasks.get(1).getId(), taskVariables);

        // Verify email
//        Assert.assertEquals(1, wiser.getMessages().size());

        Task orderTask = taskService.createTaskQuery()
                .processInstanceId(processInstance.getId())
                .taskCandidateGroup("loan_group")
                .singleResult();
        Assert.assertEquals("业务申请", orderTask.getName());

        taskVariables.put("orderResult", true);
        taskService.complete(orderTask.getId(), taskVariables);

        // Verify process completed
//        Assert.assertEquals(1, historyService.createHistoricProcessInstanceQuery().finished().count());
        long count = historyService.createHistoricProcessInstanceQuery().finished().count();
        System.out.println(count);
    }

}
