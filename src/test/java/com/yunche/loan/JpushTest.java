package com.yunche.loan;

import com.yunche.loan.config.util.Jpush;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.TaskSchedulingService;
import org.junit.Test;

import javax.annotation.Resource;

public class JpushTest extends BaseTest{

/*    public static void main(String[] args){
        Jpush.sendToRegistrationId("121c83f7602f3ac5d12","1","1");
    }*/

    @Resource
    private TaskSchedulingService taskSchedulingService;

    @Test
    public void test(){

        TaskListQuery taskListQuery = new TaskListQuery();
        taskListQuery.setPageIndex(1);
        taskListQuery.setPageSize(10);
        taskListQuery.setCustomer("包功");
        taskListQuery.setTaskStatus(0);
        taskListQuery.setTaskDefinitionKey("usertask_credit_apply");
        taskSchedulingService.queryTaskList(taskListQuery);


    }
}
