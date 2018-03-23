package com.yunche.loan;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.ApprovalInfoUtil;
import com.yunche.loan.domain.vo.ApprovalInfoVO;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.*;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

/**
 * @author liuzhe
 * @date 2018/2/27
 */
public class ActivitiTest extends BaseTest {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private TaskService taskService;


    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private ApprovalInfoUtil approvalInfoUtil;

    @Test
    public void test23() {
        String orderId = "2018032315372694978";
        String taskDefinitionKey = "usertask_credit_apply";
        ApprovalInfoVO approvalInfoVO = approvalInfoUtil.getApprovalInfoVO(orderId, taskDefinitionKey);
        System.out.println(JSON.toJSONString(approvalInfoVO));
    }


    @Test
    public void test21() {  // 790001    2018032212063246319-790008   2018032212071035757-790015

        // 2018032213302143959-
        String processInstanceId = "790015";
        runtimeService.deleteProcessInstance(processInstanceId, "弃单");
        System.out.println("del success");
    }

    @Test
    public void test22() {  // 790001    2018032212063246319-790008   2018032212071035757-790015
        String taskId = "790012";
        taskService.deleteTask(taskId);
//        taskService.deleteTask(taskId, true);
//        taskService.deleteTask(taskId, "弃单");
        System.out.println("del task success");
    }


    /**
     * 部署流程定义 方式一：加载单个的流程定义文件 方式二：加载zip压缩文件
     */
    @Test
    public void test1() {
        DeploymentBuilder deploymentBuilder = processEngine
                .getRepositoryService().createDeployment();
        // 方式一：加载单个的流程定义文件
        // deploymentBuilder.addClasspathResource("qjlc.bpmn");
        // deploymentBuilder.addClasspathResource("qjlc.png");
        // deploymentBuilder.deploy();

        // 方式二：加载zip压缩文件
        ZipInputStream zipInputStream = new ZipInputStream(this.getClass()
                .getClassLoader().getResourceAsStream("process.zip"));
        deploymentBuilder.addZipInputStream(zipInputStream);
        deploymentBuilder.deploy();
    }

    /**
     * 查询部署信息
     */
    @Test
    public void test2() {
        // 部署查询对象，查询部署表
        DeploymentQuery query = processEngine.getRepositoryService()
                .createDeploymentQuery();
        List<Deployment> list = query.list();
        for (Deployment deployment : list) {
            System.out.println(deployment.getId() + " "
                    + deployment.getDeploymentTime());
        }
    }

    /**
     * 删除部署信息,对应操作的数据表act_re_deployment 、act_re_procdef、act_ge_bytearray
     */
    @Test
    public void test3() {

        List<Integer> deploymentIdList = Lists.newArrayList(10001, 12501, 17501, 20001, 22501, 25001, 2501, 5001, 7501);

        deploymentIdList.stream()
                .forEach(e -> {

                    // 部署id
                    String deploymentId = String.valueOf(e);
                    // 是否级联删除
                    boolean cascade = true;
                    processEngine.getRepositoryService().deleteDeployment(deploymentId, cascade);
                });

    }

    /**
     * 查询最新版本的流程定义数据
     */
    @Test
    public void test4() {
        // 流程定义查询对象，查询流程定义表act_re_procdef
        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery();
        // 最新版本过滤
        query.latestVersion();
        List<ProcessDefinition> list = query.list();
        for (ProcessDefinition processDefinition : list) {
            System.out.println(processDefinition.getId());
        }
    }

    /**
     * 查询一次部署对应的流程定义文件名称和输入流
     *
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        String deploymentId = "201";// 部署id
        List<String> names = processEngine.getRepositoryService()
                .getDeploymentResourceNames(deploymentId);
        for (String name : names) {
            System.out.println(name);
            InputStream in = processEngine.getRepositoryService()
                    .getResourceAsStream(deploymentId, name);
//            FileUtils.copyInputStreamToFile(in, new File("e:\\" + name));
            in.close();
        }
    }

    /**
     * 获得文件名称和输入流
     *
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        String processDefinitionId = "qjlc:2:104";// 流程定义id

        // 根据流程定义id查询流程定义对象
        ProcessDefinitionQuery query = processEngine.getRepositoryService()
                .createProcessDefinitionQuery();
        query.processDefinitionId(processDefinitionId);
        ProcessDefinition processDefinition = query.singleResult();
        // 根据流程定义对象获得png图片名称
        String pngName = processDefinition.getDiagramResourceName();

        // 直接获得png图片对应的输入流
        InputStream pngStream = processEngine.getRepositoryService()
                .getProcessDiagram(processDefinitionId);
//        FileUtils.copyInputStreamToFile(pngStream, new File("e:\\" + pngName));
        pngStream.close();
    }

    /**
     * 启动流程实例 方式一：根据流程定义的id启动流程实例 方式二：根据流程定义的key启动流程实例（建议）----可以自动选择最新版本的流程定义
     */
    @Test
    public void test7() {
        String processDefinitionId = "qjlc:2:104";// 流程定义id
        // 方式一：根据流程定义的id启动流程实例
        /*
         * ProcessInstance processInstance = processEngine.getRuntimeService()
		 * .startProcessInstanceById(processDefinitionId);
		 * System.out.println(processInstance.getId());
		 */

        // 方式二：根据流程定义key启动流程实例
        String processDefinitionKey = "qjlc";// 流程定义key
        ProcessInstance processInstance = processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey);
        System.out.println(processInstance.getId());
    }

    /**
     * 查询流程实例
     */
    @Test
    public void test8() {
        // 流程实例查询对象，操作流程实例表act_ru_execution
        ProcessInstanceQuery query = processEngine.getRuntimeService()
                .createProcessInstanceQuery();
        List<ProcessInstance> list = query.list();
        for (ProcessInstance processInstance : list) {
            System.out.println(processInstance.getId());
        }
    }

    /**
     * 删除流程实例
     */
    @Test
    public void test9() {
        String processInstanceId = "1001";// 流程实例id
        String deleteReason = "不请假了";// 删除原因
        processEngine.getRuntimeService().deleteProcessInstance(
                processInstanceId, deleteReason);
    }

    /**
     * 查询任务
     */
    @Test
    public void test10() {
        //任务查询对象，操作数据表：act_ru_task任务表
        TaskQuery query = processEngine.getTaskService().createTaskQuery();
        query.taskAssignee("王五");
        List<Task> list = query.list();
        for (Task task : list) {
            System.out.println(task.getId() + " " + task.getName());
        }
    }

    /**
     * 办理任务
     */
    @Test
    public void test11() {
        String taskId = "1302";// 任务id
        processEngine.getTaskService().complete(taskId);
    }
}
