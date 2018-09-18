package com.yunche.loan.service.impl;

import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.LoanApplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author liuzhe
 * @date 2018/9/18
 */
@Service
public class LoanApplyServiceImpl implements LoanApplyService {


    @Autowired
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private EmployeeService employeeService;


    @Override
    public List<UniversalCustomerOrderVO> queryLoanApplyCustomerOrder(String name) {

        Long loginUserId = SessionUtils.getLoginUser().getId();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUserId);
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId);

        List<UniversalCustomerOrderVO> universalCustomerOrderVOS = loanQueryDOMapper.selectUniversalLoanApplyCustomerOrder(
                loginUserId,
                StringUtils.isBlank(name) ? null : name.trim(),
                maxGroupLevel == null ? 0 : maxGroupLevel,
                juniorIds
        );

        return universalCustomerOrderVOS;
    }
}
