package com.yunche.loan;

import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.util.Jpush;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.JpushService;
import com.yunche.loan.service.TaskSchedulingService;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class JpushTest{

/*    public static void main(String[] args){
        Jpush.sendToRegistrationId("121c83f7602f3ac5d12","1","1");
    }*/

    @Resource
    private TaskSchedulingService taskSchedulingService;

    //@Test
    public void test(){

        TaskListQuery taskListQuery = new TaskListQuery();
        taskListQuery.setPageIndex(1);
        taskListQuery.setPageSize(10);
        taskListQuery.setCustomer("包功");
        taskListQuery.setTaskStatus(0);
        taskListQuery.setTaskDefinitionKey("usertask_credit_apply");
        taskSchedulingService.queryTaskList(taskListQuery);


    }
    @Test
    public void doDSF(){
        List<LoanCustomerDO> customers = new ArrayList<>();
        LoanCustomerDO l = new LoanCustomerDO();
        l.setBankCreditReject(new Byte("0"));
        l.setCustType(new Byte("1"));
        l.setGuaranteeType(null);
        customers.add(l);
        LoanCustomerDO l1 = new LoanCustomerDO();
        l1.setBankCreditReject(new Byte("0"));
        l1.setCustType(new Byte("3"));
        l1.setGuaranteeType(new Byte("2"));
        customers.add(l1);

        customers = customers.stream()
                .filter(Objects::nonNull)
                // 银行征信拒绝的客户（错误代码1XXX、2XXX、3XXX），打回以后，如果选择“内部担保”，可以不提交给银行，而是直接将结果设定为“征信拒贷”。
                .filter(e -> BaseConst.K_YORN_YES.equals(e.getBankCreditReject())
                        && "3".equals(e.getCustType())
                        && "1".equals(e.getGuaranteeType())
                )
                .collect(Collectors.toList());
        System.out.println(customers.size());
    }
}
