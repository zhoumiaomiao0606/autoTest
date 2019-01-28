package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.EventBusCenter;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.query.FinanceErrQuery;
import com.yunche.loan.domain.vo.FinanceErrVO;
import com.yunche.loan.domain.vo.TaskListVO;
import com.yunche.loan.mapper.VoucherErrRecordDOMapper;
import com.yunche.loan.service.FinanceErrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_PASS;

@Service
public class FinanceErrServiceImpl implements FinanceErrService {

    @Autowired
    private VoucherErrRecordDOMapper voucherErrRecordDOMapper;
    @Override
    public ResultBean query(FinanceErrQuery financeErrQuery) {
        PageHelper.startPage(financeErrQuery.getPageIndex(), financeErrQuery.getPageSize(), true);
        List<FinanceErrVO> financeErrVOS = voucherErrRecordDOMapper.listErr(financeErrQuery);
        PageInfo<TaskListVO> pageInfo = new PageInfo(financeErrVOS);
        return ResultBean.ofSuccess(financeErrVOS, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean deal() {

        FinanceErrQuery financeErrQuery =new FinanceErrQuery();
        financeErrQuery.setStatus(new Byte("1"));

        List<FinanceErrVO> financeErrVOS = voucherErrRecordDOMapper.listErr(financeErrQuery);

        for (FinanceErrVO financeErrVO:financeErrVOS) {
            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setSerial_no(financeErrVO.getSerialNo());
            approvalParam.setTaskDefinitionKey(financeErrVO.getTaskDefinitionKey());
            approvalParam.setOrderId(Long.valueOf(financeErrVO.getOrderId()));
            approvalParam.setProcessId(financeErrVO.getProcessId()==null?0:Long.valueOf(financeErrVO.getProcessId()));
            approvalParam.setAction(ACTION_PASS);
            approvalParam.setSubmitTime(financeErrVO.getBusinessDate());
            EventBusCenter.eventBus.post(approvalParam);
        }
        return ResultBean.ofSuccess("处理完成，请舒心列表");
    }
}
