package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.RelaOrderCustomerParam;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.LoanApplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.yunche.loan.config.constant.LoanCustomerConst.CUST_TYPE_GUARANTOR;

/**
 * @author liuzhe
 * @date 2018/9/18
 */
@Service
public class LoanApplyServiceImpl implements LoanApplyService {

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

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

    @Override
    public void relaOrderCustomer(RelaOrderCustomerParam relaOrderCustomerParam) {
        Preconditions.checkNotNull(relaOrderCustomerParam, "relaCustomerId不能为空");
        Preconditions.checkNotNull(relaOrderCustomerParam.getRelaCustomerId(), "relaCustomerId不能为空");
        Preconditions.checkNotNull(relaOrderCustomerParam.getRelaCustType(), "relaCustType不能为空");
        // 关联的类型为：担保人时，需要选择担保类型
        if (CUST_TYPE_GUARANTOR.equals(relaOrderCustomerParam.getRelaCustType())) {
            Preconditions.checkNotNull(relaOrderCustomerParam.getRelaGuaranteeType(), "请选择担保类型");
        }

        // rela  -->  迁移
        LoanCustomerDO relaLoanCustomerDO = new LoanCustomerDO();
        relaLoanCustomerDO.setId(relaOrderCustomerParam.getRelaCustomerId());

        // 主贷人
        relaLoanCustomerDO.setPrincipalCustId(relaOrderCustomerParam.getPrincipalCustId());
        // 关联人类型
        relaLoanCustomerDO.setCustType(relaOrderCustomerParam.getRelaCustType());
        // 与主贷人关系
        relaLoanCustomerDO.setCustRelation(relaOrderCustomerParam.getRelaCustRelation());
        // 担保类型
        relaLoanCustomerDO.setGuaranteeType(relaOrderCustomerParam.getRelaGuaranteeType());

        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(relaLoanCustomerDO);
        Preconditions.checkArgument(count > 0, "关联失败");


//        // copy
//        LoanCustomerDO relaLoanCustomerDO = new LoanCustomerDO();
//        BeanUtils.copyProperties(loanCustomerDO, relaLoanCustomerDO);
//
//        relaLoanCustomerDO.setId(null);
//
//        // 主贷人
//        relaLoanCustomerDO.setPrincipalCustId(relaOrderCustomerParam.getPrincipalCustId());
//        // 关联人类型
//        relaLoanCustomerDO.setCustType(relaOrderCustomerParam.getCustType());
//        // 与主贷人关系
//        relaLoanCustomerDO.setCustRelation(relaOrderCustomerParam.getCustRelation());
//        // 担保类型
//        relaLoanCustomerDO.setGuaranteeType(relaOrderCustomerParam.getGuaranteeType());
//
//        int count = loanCustomerDOMapper.insertSelective(loanCustomerDO);
//        Preconditions.checkArgument(count > 0, "关联成功");
    }
}
