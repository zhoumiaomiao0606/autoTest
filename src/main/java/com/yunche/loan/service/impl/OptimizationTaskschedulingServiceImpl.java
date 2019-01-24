package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.param.CreditApplyListQuery;
import com.yunche.loan.domain.param.QueryListParam;
import com.yunche.loan.domain.vo.ContractOverDueVO;
import com.yunche.loan.domain.vo.CreditApplyListVO;
import com.yunche.loan.domain.vo.QueryListVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.OptimizationTaskschedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class OptimizationTaskschedulingServiceImpl  implements OptimizationTaskschedulingService
{

    @Resource
    private EmployeeService employeeService;

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private UserGroupRelaBankDOMapper userGroupRelaBankDOMapper;

    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Autowired
    private OptTaskschedulingDOMapper optTaskschedulingDOMapper;

    @Override
    public ResultBean queryCreditApplyrList(CreditApplyListQuery param)
    {

        //权限控制
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //获取用户可见的区域
        param.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUserId));
        //获取用户可见的银行
        param.setBankList(getUserHaveBank(loginUserId));



        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<CreditApplyListVO> list = loanQueryDOMapper.queryCreditApplyrList(param);
        PageInfo<CreditApplyListVO> pageInfo = new PageInfo(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean queryList(QueryListParam param)
    {
        //权限控制
        Long loginUserId = SessionUtils.getLoginUser().getId();

        param.setJuniorIds(employeeService.getSelfAndCascadeChildIdList(loginUserId));
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));

        //获取用户可见的区域
        param.setBizAreaIdList(getUserHaveBizAreaPartnerId(loginUserId));
        //获取用户可见的银行
        param.setBankList(getUserHaveBank(loginUserId));



        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<QueryListVO> queryListVOList = optTaskschedulingDOMapper.queryList(param);
        PageInfo<CreditApplyListVO> pageInfo = new PageInfo(queryListVOList);


        return ResultBean.ofSuccess(queryListVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }


    /**
     * 获取 用户可见区域内的 所有合伙人ID列表
     *
     * @param userId
     */
    private List<Long> getUserHaveBizAreaPartnerId(Long userId) {
        List<Long> empBizAreaPartnerIds = loanQueryDOMapper.selectEmpBizAreaPartnerIds(userId);
        if (CollectionUtils.isEmpty(empBizAreaPartnerIds)) {
            return null;
        }
        if (empBizAreaPartnerIds.get(0) == null) {
            return null;
        }
        return empBizAreaPartnerIds;
    }

    /**
     * 获取用户可见的银行 名称
     *
     * @param userId
     */
    private List<String> getUserHaveBank(Long userId) {
        List<Long> groupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(userId);
        List<String> userBankIdList = Lists.newArrayList();
        groupIdList.parallelStream().filter(Objects::nonNull).forEach(groupId -> {
            List<String> tmpBankidList = userGroupRelaBankDOMapper.getBankNameListByUserGroupId(groupId);
            userBankIdList.addAll(tmpBankidList);
        });
        return userBankIdList.parallelStream().distinct().collect(Collectors.toList());

    }
}
