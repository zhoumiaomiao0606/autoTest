package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.service.ActivitiService;
import com.yunche.loan.service.LoanProcessOrderService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.yunche.loan.config.constant.ActivitiConst.LOAN_PROCESS_KEY;
import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.ORDER_STATUS_DOING;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
public class LoanProcessOrderServiceImpl implements LoanProcessOrderService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    private ActivitiService activitiService;


    @Override
    @Transactional
    public ResultBean<Long> create(LoanOrderDO loanOrderDO) {

        Long orderId = createOrderNum();
        loanOrderDO.setId(orderId);
        loanOrderDO.setGmtCreate(new Date());
        loanOrderDO.setGmtModify(new Date());

        int count = loanOrderDOMapper.insertSelective(loanOrderDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");

        return ResultBean.ofSuccess(orderId);
    }

    @Override
    @Transactional
    public ResultBean<Long> createLoanOrder(Long baseInfoId, Long customerId) {

        // 开启activiti流程   -消费贷流程
        ProcessInstance processInstance = activitiService.startProcessInstanceByKey(LOAN_PROCESS_KEY);

        // 插入
        LoanOrderDO loanOrderDO = new LoanOrderDO();
        loanOrderDO.setProcessInstId(processInstance.getProcessInstanceId());
        loanOrderDO.setLoanCustomerId(customerId);
        loanOrderDO.setLoanBaseInfoId(baseInfoId);

        ResultBean<Long> createResultBean = create(loanOrderDO);
        Preconditions.checkArgument(createResultBean.getSuccess(), createResultBean.getMsg());

        // 创建流程记录
        createLoanProcess(createResultBean.getData());

        return createResultBean;
    }

    @Override
    @Transactional
    public ResultBean<Void> update(LoanOrderDO loanOrderDO) {
        Preconditions.checkNotNull(loanOrderDO.getId(), "业务单ID不能为空");

        loanOrderDO.setGmtModify(new Date());
        int count = loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    /**
     * 生成业务单ID     -19位
     *
     * @return
     */
    private static Long createOrderNum() {
        // 日期格式   -12位
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        // 获取当前系统时间，也可使用当前时间戳
        String nowTime = df.format(new Date()).toString();

        // 返回固定长度的随机数    -7位
        String fixLenthString = getFixLenthString(7);

        // 订单号拼接
        String orderNum = nowTime + fixLenthString;

        return Long.valueOf(orderNum);
    }

    /**
     * 返回长度为【strLength】的随机数
     */
    private static String getFixLenthString(int strLength) {
        Random rm = new Random();
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(2, strLength + 2);
    }

    /**
     * 创建流程记录
     *
     * @param orderId
     */
    private void createLoanProcess(Long orderId) {
        LoanProcessDO loanProcessDO = new LoanProcessDO();
        loanProcessDO.setOrderId(orderId);
        loanProcessDO.setCreditApply(TASK_PROCESS_TODO);
        loanProcessDO.setOrderStatus(ORDER_STATUS_DOING);
        loanProcessDO.setGmtCreate(new Date());
        loanProcessDO.setGmtModify(new Date());
        int count = loanProcessDOMapper.insertSelective(loanProcessDO);
        Preconditions.checkArgument(count > 0, "创建流程记录失败");
    }
}
