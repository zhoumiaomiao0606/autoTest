package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.util.BeanPlasticityUtills;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.CreateExpensesDetailParam;
import com.yunche.loan.domain.param.LegworkReimbursementParam;
import com.yunche.loan.domain.param.SubimitVisitDoorParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LegworkReimbursementService;
import org.activiti.engine.impl.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class LegworkReimbursementServiceImpl implements LegworkReimbursementService {

    @Resource
    private TaskSchedulingDOMapper taskSchedulingDOMapper;

    @Resource
    private LegworkReimbursementRelevanceVisitDOMapper legworkReimbursementRelevanceVisitDOMapper;

    @Resource
    private LegworkReimbursementDOMapper legworkReimbursementDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryServiceImpl loanQueryService;

    @Resource
    private VisitDoorDOMapper visitDoorDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LegworkReimbursementFileDOMapper legworkReimbursementFileDOMapper;

    @Override
    public PageInfo subimitVisitDoorList(SubimitVisitDoorParam param) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        param.setLoginUserId(loginUserId);
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List<SubimitVisitDoorVO> list = taskSchedulingDOMapper.subimitVisitDoorList(param);
        PageInfo<TaskListVO> pageInfo = new PageInfo(list);
        return pageInfo;
    }

    @Override
    public PageInfo list(LegworkReimbursementParam param) {
        Long loginUserId = SessionUtils.getLoginUser().getId();
        param.setLoginUserId(loginUserId);
        param.setMaxGroupLevel(taskSchedulingDOMapper.selectMaxGroupLevel(loginUserId));
        PageHelper.startPage(param.getPageIndex(), param.getPageSize(), true);
        List list = taskSchedulingDOMapper.legworkReimbursementList(param);
        PageInfo<TaskListVO> pageInfo = new PageInfo(list);
        return pageInfo;
    }


    @Override
    public Long createExpensesDetail(CreateExpensesDetailParam param) {
        if (CollectionUtils.isEmpty(param.getIds())) {
            throw new BizException("请选择");
        }

        if (param.getIds() == null) {
            throw new BizException("请选择");
        }

        if (param.getIds().size() == 0) {
            throw new BizException("请选择");
        }

        for (Long visitDoorId : param.getIds()) {
            if (legworkReimbursementRelevanceVisitDOMapper.checkHaving(visitDoorId)) {
                throw new BizException("[" + visitDoorId + "]" + "此任务以被选择为报销项目");
            }

        }
        EmployeeDO user = SessionUtils.getLoginUser();
        LegworkReimbursementDO legworkReimbursementDO = new LegworkReimbursementDO();
        legworkReimbursementDO.setApplyUserId(user.getId());
        legworkReimbursementDO.setApplyUserName(user.getName());
        legworkReimbursementDOMapper.insertSelective(legworkReimbursementDO);
        if (legworkReimbursementDO.getId() == null) {
            throw new BizException("无效的记录");
        }
        for (Long visitDoorId : param.getIds()) {
            LegworkReimbursementRelevanceVisitDO legworkReimbursementRelevanceVisitDO = new LegworkReimbursementRelevanceVisitDO();
            legworkReimbursementRelevanceVisitDO.setLegworkReimbursementId(legworkReimbursementDO.getId());
            legworkReimbursementRelevanceVisitDO.setVisitDoorId(visitDoorId);
            legworkReimbursementRelevanceVisitDOMapper.insertSelective(legworkReimbursementRelevanceVisitDO);

        }

        return legworkReimbursementDO.getId();
    }

    @Override
    public RecombinationVO expensesDetail(Long id) {

        List<RecombinationVO> result = Lists.newArrayList();

        List<LegworkReimbursementRelevanceVisitDO> legworkReimbursementRelevanceVisitDOS = legworkReimbursementRelevanceVisitDOMapper.selectByLegworkReimbursementId(id);
        if (CollectionUtil.isNotEmpty(legworkReimbursementRelevanceVisitDOS)) {
            if (legworkReimbursementRelevanceVisitDOS.size() > 0) {
                if (legworkReimbursementRelevanceVisitDOS.get(0) != null) {
                    for (LegworkReimbursementRelevanceVisitDO legworkReimbursementRelevanceVisitDO : legworkReimbursementRelevanceVisitDOS) {
                        VisitDoorDO visitDoorDO = visitDoorDOMapper.selectByPrimaryKey(legworkReimbursementRelevanceVisitDO.getVisitDoorId());
                        if (visitDoorDO != null) {
                            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(visitDoorDO.getOrderId());
                            if (loanOrderDO != null) {
                                List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(loanOrderDO.getId());
                                for (UniversalCustomerVO universalCustomerVO : customers) {
                                    List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                                    universalCustomerVO.setFiles(files);
                                }
                                RecombinationVO recombinationVO = new RecombinationVO();
                                recombinationVO.setCustomers(customers);
                                recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(loanOrderDO.getId()));
                                recombinationVO.setCar(loanQueryDOMapper.selectUniversalCarInfo(loanOrderDO.getId()));
                                recombinationVO.setVisitDoor(visitDoorDO);
                                result.add(recombinationVO);
                            }
                        }
                    }
                }
            }
        }

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(result);
        recombinationVO.setLegworkReimbursementFiles(loanQueryDOMapper.selectUniversalFileByLegworkReimbursementId(id));
        recombinationVO.setLegworkReimbursement(legworkReimbursementDOMapper.selectByPrimaryKey(id));
        return recombinationVO;
    }

    @Override
    public void expensesUpdate(LegworkReimbursementUpdateParam param) {
        if (param.getId() == null) {
            throw new BizException("id is null");
        }
        LegworkReimbursementDO legworkReimbursementDO = BeanPlasticityUtills.copy(LegworkReimbursementDO.class, param);
        legworkReimbursementDOMapper.updateByPrimaryKeySelective(legworkReimbursementDO);

        legworkReimbursementFileDOMapper.deleteByLegworkReimbursementId(param.getId());
        for (String url : param.getFiles()) {
            LegworkReimbursementFileDO legworkReimbursementFileDO = new LegworkReimbursementFileDO();
            legworkReimbursementFileDO.setLegworkReimbursementId(param.getId());
            legworkReimbursementFileDO.setUrls(url);
            legworkReimbursementFileDOMapper.insertSelective(legworkReimbursementFileDO);
        }
    }
}
