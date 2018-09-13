package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.entity.ThirdPartyFundBusinessDO;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.LoanStatementDOMapper;
import com.yunche.loan.mapper.ThirdPartyFundBusinessDOMapper;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanProcessEnum.BRIDGE_HANDLE;


@Service
public class JinTouHangAccommodationApplyServiceImpl implements JinTouHangAccommodationApplyService {


    @Autowired
    private ThirdPartyFundBusinessDOMapper thirdPartyFundBusinessDOMapper;

    @Autowired
    private LoanProcessService loanProcessService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;
    
    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;


    /**
     * 批量贷款
     * @return
     */
    @Override
    public ResultBean applyLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        Preconditions.checkNotNull(param.getOrderId(),"业务单号不能为空");
        ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
        aDo.setOrderId(param.getOrderId());
        aDo.setLendDate(param.getLendDate());
        aDo.setGmtCreate(new Date());

        int count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
        Preconditions.checkArgument(count>0,"保存失败");
        return ResultBean.ofSuccess("借款申请成功");
    }
    /**
     * 批量贷款
     * @return
     */
    @Override
    public ResultBean batchLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        List<Long> orderIds = param.getOrderIds();

        List<ThirdPartyFundBusinessDO> collect = orderIds.stream().filter(Objects::nonNull).map(e -> {
            ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
            aDo.setOrderId(e);
            aDo.setLendDate(param.getLendDate());
            aDo.setGmtCreate(new Date());
            return aDo;
        }).collect(Collectors.toList());

        //批量导入、提交
        if(!CollectionUtils.isEmpty(collect)){
            int count = thirdPartyFundBusinessDOMapper.batchInsert(collect);
            Preconditions.checkArgument(count==collect.size(),"批量借款申请异常");
            //提交任务
            collect.stream().forEach(e->{
                ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setOrderId(e.getOrderId());
                approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
                approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                approvalParam.setNeedLog(true);
                approvalParam.setCheckPermission(false);
                ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(),approvalResultBean.getMsg());
            });
        }
        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 金投行过桥处理 -导出
     * @return
     */
    @Override
    public ResultBean export(ExportApplyLoanPushParam param) {


        List<ExportApplyLoanPushVO> voList = loanStatementDOMapper.exportApplyLoanPush(param);
        ArrayList<String> header = Lists.newArrayList("序号","委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "代购垫资金额（借款金额）", "借款期限", "利率", "借据号", "最终放款银行"
        );

        String ossResultKey = POIUtil.createExcelFile("购车融资业务推送清单",voList,header,ExportApplyLoanPushVO.class,ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行还款信息 -导出
     * @return
     */
    @Override
    public ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param) {


        List<JinTouHangRepayInfoVO> voList = loanStatementDOMapper.exportJinTouHangRepayInfo(param);
        ArrayList<String> header = Lists.newArrayList("借款时间","还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金", "还款类型", "备注"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行还款信息",voList,header,JinTouHangRepayInfoVO.class,ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行息费登记 -导出
     * @return
     */
    @Override
    public ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param) {


        List<JinTouHangInterestRegisterVO> voList = loanStatementDOMapper.exportJinTouHangInterestRegister(param);
        ArrayList<String> header = Lists.newArrayList("借款时间","还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行息费登记",voList,header,JinTouHangInterestRegisterVO.class,ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    @Override
    public ResultBean detail(Long orderId) {
        Preconditions.checkNotNull(orderId,"参数有误");

        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(orderDO,"订单信息不存在");
        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        UniversalCarInfoVO carInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);


        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setCar(carInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setCustomers(customers);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 异常还款
     * @param param
     * @return
     */
    @Override
    public ResultBean abnormalRepay(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        Preconditions.checkNotNull(param.getOrderId(),"业务单号不能为空");

        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
        thirdPartyFundBusinessDO.setOrderId(param.getOrderId());
        thirdPartyFundBusinessDO.setRepayType(param.getRepayType());
        thirdPartyFundBusinessDO.setRepayRemark(param.getRepayRemark());
        thirdPartyFundBusinessDO.setRepayDate(param.getRepayDate());
        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count>0,"异常还款跟新失败");
        return ResultBean.ofSuccess("保存成功");
    }

    /**
     * 金投行息费登记
     * @param param
     * @return
     */
    @Override
    public ResultBean repayInterestRegister(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        Preconditions.checkNotNull(param.getOrderId(),"业务单号不能为空");
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
        thirdPartyFundBusinessDO.setOrderId(param.getOrderId());
        thirdPartyFundBusinessDO.setInterest(param.getInterest());
        thirdPartyFundBusinessDO.setPoundage(param.getPoundage());
        thirdPartyFundBusinessDO.setRepayInterestDate(param.getRepayInterestDate());
        thirdPartyFundBusinessDO.setRepayRegisterRemark(param.getRepayRegisterRemark());

        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count>0,"金投行还款登记失败");

        return ResultBean.ofSuccess("保存成功");
    }


}
