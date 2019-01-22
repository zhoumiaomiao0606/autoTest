package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.cache.PartnerCache;
import com.yunche.loan.config.constant.EmployeeConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.EmployeeDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.query.OrderListQuery;
import com.yunche.loan.domain.vo.OrderListVO;
import com.yunche.loan.domain.vo.TaskDisVO;
import com.yunche.loan.mapper.EmployeeRelaUserGroupDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.OrderListQueryMapper;
import com.yunche.loan.mapper.UserGroupRelaBankDOMapper;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * @author liuzhe
 * @date 2019/1/9
 */
@Service
public class OrderListServiceImpl implements OrderListService {

    @Autowired
    private OrderListQueryMapper orderListQueryMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private EmployeeRelaUserGroupDOMapper employeeRelaUserGroupDOMapper;

    @Autowired
    private UserGroupRelaBankDOMapper userGroupRelaBankDOMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TaskDistributionService taskDistributionService;

    @Autowired
    private UserGroupService userGroupService;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private PartnerCache partnerCache;


    @Override
    public PageInfo<OrderListVO> query(OrderListQuery query) {
        Assert.isTrue(StringUtils.isNotBlank(query.getTaskDefinitionKey()), "taskDefinitionKey不能为空");
        Assert.notNull(query.getTaskStatus(), "taskStatus不能为空");

        // check
        permissionService.checkTaskPermission(query.getTaskDefinitionKey());

        EmployeeDO loginUser = SessionUtils.getLoginUser();
        if (EmployeeConst.TYPE_ZS.equals(loginUser.getType())) {
            // 内部员工
            query.setBankNameList(getUserHavBankNameList(loginUser.getId()));
            query.setPartnerIdList(getUserHaveBizAreaPartnerId(loginUser.getId()));
        } else if (EmployeeConst.TYPE_WB.equals(loginUser.getType())) {
            // 合伙人
            query.setSalesmanIdList(getUserHavSalesmanIdList(loginUser.getId()));
        } else {
            throw new BizException("用户类型非法：" + loginUser.getType());
        }

        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);
        List<OrderListVO> orderListVOList = orderListQueryMapper.query(query);

        // fillMsg
        fillMsg(orderListVOList, query.getTaskDefinitionKey());

        PageInfo<OrderListVO> pageInfo = PageInfo.of(orderListVOList);

        return pageInfo;
    }

    private void fillMsg(List<OrderListVO> orderListVOList, String taskKey) {

        if (CollectionUtils.isEmpty(orderListVOList)) {
            return;
        }

        List<Long> orderIdList = Lists.newCopyOnWriteArrayList();

        Collection<OrderListVO> synchronizedOrderListVOList = Collections.synchronizedCollection(orderListVOList);

        synchronizedOrderListVOList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    orderIdList.add(Long.valueOf(e.getOrderId()));

                    // partner
                    PartnerDO partnerDO = partnerCache.getById(Long.valueOf(e.getPartnerId()));
                    if (null != partnerDO) {
                        e.setPartnerCode(partnerDO.getPartnerCode());
                        e.setPartnerName(partnerDO.getName());
                    }

                    // bank
                    e.setBankId(bankCache.getIdByName(e.getBankName()));
                });


        // task
        Map<Long, TaskDisVO> taskMap = taskDistributionService.list(taskKey, orderIdList);
        if (!CollectionUtils.isEmpty(taskMap)) {
            synchronizedOrderListVOList.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        TaskDisVO taskDisVO = taskMap.get(Long.valueOf(e.getOrderId()));
                        if (null != taskDisVO) {
                            e.setTaskId(e.getOrderId());
                            e.setTaskReceiverId(taskDisVO.getSendee());
                            e.setTaskReceiverName(taskDisVO.getSendeeName());
                            e.setTaskDisStatus(taskDisVO.getStatus());
                        }
                    });
        }
    }

    private Set<String> getUserHavSalesmanIdList(Long userId) {
        Set<String> selfAndCascadeChildIdList = employeeService.getSelfAndCascadeChildIdList(userId);
        return selfAndCascadeChildIdList;
    }

    private List<Long> getUserHavBankIdList(Long userId) {
        Assert.notNull(userId, "userId不能为空");

        List<Long> userGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(userId);

        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return Collections.EMPTY_LIST;
        }

        List<Long> bankIdList = userGroupRelaBankDOMapper.listBankIdByUserGroupIdList(userGroupIdList);
        return bankIdList;
    }

    /**
     * 获取用户可见的银行 名称
     *
     * @param userId
     * @return
     */
    private List<String> getUserHavBankNameList(Long userId) {
        List<Long> userGroupIdList = employeeRelaUserGroupDOMapper.getUserGroupIdListByEmployeeId(userId);

        if (CollectionUtils.isEmpty(userGroupIdList)) {
            return Collections.EMPTY_LIST;
        }

        List<String> bankNameList = userGroupRelaBankDOMapper.listBankNameByUserGroupIdList(userGroupIdList);
        return bankNameList;
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
}
