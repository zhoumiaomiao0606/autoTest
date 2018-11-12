package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanCustRoleChangeHisDO;
import com.yunche.loan.domain.entity.LoanCustRoleChangeHisDetailDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.yunche.loan.config.constant.LoanCustRoleChangeHisConst.TYPE_AFTER;
import static com.yunche.loan.config.constant.LoanCustRoleChangeHisConst.TYPE_BEFORE;

/**
 * @author liuzhe
 * @date 2018/11/12
 */
@Service
public class LoanCustRoleChangeServiceImpl implements LoanCustRoleChangeService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanCustomerService loanCustomerService;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Autowired
    private LoanCustRoleChangeHisDOMapper loanCustRoleChangeHisDOMapper;

    @Autowired
    private LoanCustRoleChangeHisDetailDOMapper loanCustRoleChangeHisDetailDOMapper;


    @Override
    public List<UniversalCustomerOrderVO> queryRoleChangeOrder(String name) {

        Long loginUserId = SessionUtils.getLoginUser().getId();
        Set<String> juniorIds = employeeService.getSelfAndCascadeChildIdList(loginUserId);
        Long maxGroupLevel = taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId);

        List<UniversalCustomerOrderVO> universalCustomerOrderVOS = loanQueryDOMapper.selectUniversalRoleChangeOrder(
                loginUserId,
                StringUtils.isBlank(name) ? null : name.trim(),
                maxGroupLevel == null ? 0 : maxGroupLevel,
                juniorIds
        );

        return universalCustomerOrderVOS;
    }

    @Override
    public RecombinationVO editDetail(Long orderId) {

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalBaseInfo(orderId));
        recombinationVO.setCustomers(customers);

        return recombinationVO;
    }

    @Override
    @Transactional
    public Void editSave(Long orderId, List<CustomerParam> customers) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkArgument(!CollectionUtils.isEmpty(customers), "客户不能为空");

        // 变更历史记录
        Long roleChangeHisId = insertRoleChangeHis(orderId);

        // 变更前历史详情记录
        insertRoleChangeHisDetail_before(roleChangeHisId, orderId);

        // 角色变更
        customers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 变更后历史详情记录
                    insertRoleChangeHisDetail_after(roleChangeHisId, e);

                    // update
                    updateCustomer(e);
                });

        return null;
    }


    @Override
    public ResultBean<List<TaskListVO>> queryHisList(TaskListQuery taskListQuery) {

        return taskSchedulingService.queryRoleChangeHisTaskList(taskListQuery);
    }

    @Override
    public LoanCustRoleChangeHisDetailVO hisDetail(Long roleChangeHisId) {

        List<LoanCustRoleChangeHisDetailDO> list = loanCustRoleChangeHisDetailDOMapper.listByRoleChangeHisId(roleChangeHisId);

        LoanCustRoleChangeHisDetailVO loanCustRoleChangeHisDetailVO = new LoanCustRoleChangeHisDetailVO();

        if (!CollectionUtils.isEmpty(list)) {

            List<CustomerVO> before = Lists.newArrayList();
            List<CustomerVO> after = Lists.newArrayList();


            list.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        // 客户更新类型：1-变更前；2-变更后；
                        Byte type = e.getType();

                        if (TYPE_BEFORE.equals(type)) {

                            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(e.getCustomerId(), null);

                            if (null != loanCustomerDO) {

                                CustomerVO customerVO = new CustomerVO();
                                // 客户详细信息
                                BeanUtils.copyProperties(loanCustomerDO, customerVO);
                                // 客户变化历史信息
                                BeanUtils.copyProperties(e, customerVO);

                                before.add(customerVO);
                            }

                        } else if (TYPE_AFTER.equals(type)) {

                            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(e.getCustomerId(), null);

                            if (null != loanCustomerDO) {

                                CustomerVO customerVO = new CustomerVO();
                                // 客户详细信息
                                BeanUtils.copyProperties(loanCustomerDO, customerVO);
                                // 客户变化历史信息
                                BeanUtils.copyProperties(e, customerVO);

                                after.add(customerVO);
                            }

                        }

                    });

            loanCustRoleChangeHisDetailVO.setBefore(before);
            loanCustRoleChangeHisDetailVO.setAfter(after);
        }

        return loanCustRoleChangeHisDetailVO;
    }

    /**
     * 编辑客户信息 -> 角色变更信息
     *
     * @param customerParam
     */
    private void updateCustomer(CustomerParam customerParam) {
        Preconditions.checkNotNull(customerParam.getId(), "客户ID不能为空");

        // update
        LoanCustomerDO loanCustomerDO = new LoanCustomerDO();
        BeanUtils.copyProperties(customerParam, loanCustomerDO);

        ResultBean<Void> resultBean = loanCustomerService.update(loanCustomerDO);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
    }

    /**
     * 变更历史记录
     *
     * @param orderId
     * @return
     */
    private Long insertRoleChangeHis(Long orderId) {

        LoanCustRoleChangeHisDO loanCustRoleChangeHisDO = new LoanCustRoleChangeHisDO();
        loanCustRoleChangeHisDO.setOrderId(orderId);
        loanCustRoleChangeHisDO.setGmtCreate(new Date());
        int count = loanCustRoleChangeHisDOMapper.insert(loanCustRoleChangeHisDO);
        Preconditions.checkArgument(count > 0, "角色变更历史记录失败");

        // 变更历史记录ID
        Long roleChangeHisId = loanCustRoleChangeHisDO.getId();

        return roleChangeHisId;
    }


    /**
     * 变更前历史详情记录
     *
     * @param roleChangeHisId
     * @param orderId
     */
    private void insertRoleChangeHisDetail_before(Long roleChangeHisId, Long orderId) {

        // 获取变更前customers
        List<LoanCustomerDO> beforeCustomers = loanCustomerDOMapper.selectCusByOrderId(orderId);
        Preconditions.checkArgument(!CollectionUtils.isEmpty(beforeCustomers), "当前订单客户信息不存在");

        beforeCustomers.stream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 变更前：客户信息历史记录
                    LoanCustRoleChangeHisDetailDO loanCustRoleChangeHisDetailDO = new LoanCustRoleChangeHisDetailDO();
                    loanCustRoleChangeHisDetailDO.setRoleChangeHisId(roleChangeHisId);
                    loanCustRoleChangeHisDetailDO.setType(TYPE_BEFORE);
                    // 变更信息
                    BeanUtils.copyProperties(e, loanCustRoleChangeHisDetailDO);

                    loanCustRoleChangeHisDetailDOMapper.insert(loanCustRoleChangeHisDetailDO);
                });
    }

    /**
     * 变更后历史详情记录
     *
     * @param roleChangeHisId
     * @param customerParam
     */
    private void insertRoleChangeHisDetail_after(Long roleChangeHisId, CustomerParam customerParam) {

        // 变更后：客户信息历史记录
        LoanCustRoleChangeHisDetailDO loanCustRoleChangeHisDetailDO = new LoanCustRoleChangeHisDetailDO();
        loanCustRoleChangeHisDetailDO.setRoleChangeHisId(roleChangeHisId);
        loanCustRoleChangeHisDetailDO.setType(TYPE_AFTER);
        // 变更信息
        BeanUtils.copyProperties(customerParam, loanCustRoleChangeHisDetailDO);

        loanCustRoleChangeHisDetailDOMapper.insert(loanCustRoleChangeHisDetailDO);
    }
}
