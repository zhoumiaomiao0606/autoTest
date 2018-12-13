package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.ContractOverDueParam;
import com.yunche.loan.domain.vo.ContractOverDueVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.ContractOverDueService;
import com.yunche.loan.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ContractOverDueServiceImpl implements ContractOverDueService
{
    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Override
    public ResultBean list(ContractOverDueParam param)
    {
        //权限控制
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));



        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<ContractOverDueVO> list = loanQueryDOMapper.contractOverDueList(param);
        PageInfo<ContractOverDueVO> pageInfo = new PageInfo(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public String exportContractOverDue(ContractOverDueParam param)
    {
        //权限控制
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        List<ContractOverDueVO> list = loanQueryDOMapper.contractOverDueList(param);

        ArrayList<String> header = Lists.newArrayList("业务编号", "客户姓名", "身份证号", "业务团队",
                "贷款银行", "贷款金额","垫款金额", "银行分期本金", "执行利率", "垫款时间", "超期天数"
        );


        String ossResultKey = POIUtil.createExcelFile("合同超期",list,header,ContractOverDueVO.class,ossConfig);
        return ossResultKey;

    }
}
