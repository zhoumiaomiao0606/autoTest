package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.param.ContractOverDueParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.BaseAreaDOMapper;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.TaskSchedulingDOMapper;
import com.yunche.loan.service.ContractOverDueService;
import com.yunche.loan.service.EmployeeService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

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
    private BaseAreaDOMapper baseAreaDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;


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

    @Override
    public ContractOverDueDetailVO detail(Long orderId)
    {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        ContractOverDueDetailVO contractOverDueDetailVO = new ContractOverDueDetailVO();

        //客户主要信息
        ContractOverDueCustomerInfoVO contractOverDueCustomerInfoVO = loanQueryDOMapper.selectContractOverDueCustomerInfoInfo(orderId);

        //金融方案
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        //车辆信息
        VehicleInfoVO vehicleInfoVO = loanQueryDOMapper.selectVehicleInfo(orderId);
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }

        vehicleInfoVO.setApply_license_plate_area(tmpApplyLicensePlateArea);

        //客户详细信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files1 = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files1);
        }

        contractOverDueDetailVO.setContractOverDueCustomerInfoVO(contractOverDueCustomerInfoVO);
        contractOverDueDetailVO.setFinancialSchemeVO(financialSchemeVO);
        contractOverDueDetailVO.setVehicleInfoVO(vehicleInfoVO);
        contractOverDueDetailVO.setCustomers(customers);


        return contractOverDueDetailVO;
    }
}
