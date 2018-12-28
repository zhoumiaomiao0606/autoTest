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
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanCustomerConst.CUST_TYPE_COMMON;
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
        Preconditions.checkNotNull(relaOrderCustomerParam, "principalCustId不能为空");
        Preconditions.checkNotNull(relaOrderCustomerParam.getPrincipalCustId(), "principalCustId不能为空");

        List<RelaOrderCustomerParam.Rela> relaList = relaOrderCustomerParam.getRelaList();
        if (!CollectionUtils.isEmpty(relaList)) {

            relaList.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        doRela(relaOrderCustomerParam.getPrincipalCustId(), e);
                    });
        }
    }

    /**
     * 执行关联
     *
     * @param principalCustId
     * @param rela
     */
    private void doRela(Long principalCustId, RelaOrderCustomerParam.Rela rela) {
        Preconditions.checkNotNull(rela.getRelaCustType(), "relaCustType不能为空");
        Preconditions.checkNotNull(rela.getRelaCustRelation(), "relaCustRelation不能为空");
        // 关联的类型为：担保人时，需要选择担保类型
        if (CUST_TYPE_GUARANTOR.equals(rela.getRelaCustType())) {
            Preconditions.checkNotNull(rela.getRelaGuaranteeType(), "请选择担保类型");
        }

        // 只能绑定一个共贷人
        if (CUST_TYPE_COMMON.equals(rela.getRelaCustType())) {
            List<LoanCustomerDO> loanCustomerDOS = loanCustomerDOMapper.listByPrincipalCustIdAndType(principalCustId, rela.getRelaCustType(),VALID_STATUS );
            Preconditions.checkArgument(CollectionUtils.isEmpty(loanCustomerDOS), "当前订单已绑定共贷人");
        }

        // rela  -->  迁移
        LoanCustomerDO relaLoanCustomerDO = new LoanCustomerDO();
        relaLoanCustomerDO.setId(rela.getRelaCustomerId());

        // 主贷人
        relaLoanCustomerDO.setPrincipalCustId(principalCustId);
        // 关联人类型
        relaLoanCustomerDO.setCustType(rela.getRelaCustType());
        // 与主贷人关系
        relaLoanCustomerDO.setCustRelation(rela.getRelaCustRelation());
        // 担保类型
        relaLoanCustomerDO.setGuaranteeType(rela.getRelaGuaranteeType());

        int count = loanCustomerDOMapper.updateByPrimaryKeySelective(relaLoanCustomerDO);
        Preconditions.checkArgument(count > 0, "关联失败");
    }
}
