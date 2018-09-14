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
import com.yunche.loan.service.LoanProcessBridgeService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.LoanProcessEnum.BRIDGE_HANDLE;

@Service
@Transactional
public class JinTouHangAccommodationApplyServiceImpl implements JinTouHangAccommodationApplyService {


    @Autowired
    private ThirdPartyFundBusinessDOMapper thirdPartyFundBusinessDOMapper;

    @Autowired
    private LoanProcessBridgeService loanProcessBridgeService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;


    /**
     * 借款
     *
     * @return
     */
    @Override
    public ResultBean applyLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");

        ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, aDo);
        aDo.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        aDo.setOrderId(param.getIdPair().getOrderId());

        ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        int count;
        if (fundBusinessDO != null) {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
        } else {
            count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
        }
        Preconditions.checkArgument(count > 0, "保存失败");

        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 批量贷款
     *
     * @return
     */
    @Override
    public ResultBean batchLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        List<AccommodationApplyParam.IDPair> idPairs = param.getIdPairs();
//        List<Long> orderIds = param.getOrderIds();

        List<ThirdPartyFundBusinessDO> collect = idPairs.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
                    aDo.setBridgeProcecssId(e.getBridgeProcessId());
                    aDo.setOrderId(e.getOrderId());
                    aDo.setLendDate(param.getLendDate());
                    aDo.setGmtCreate(new Date());

                    ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(e.getBridgeProcessId());
                    int count;
                    if (fundBusinessDO != null) {
                        count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
                    } else {
                        count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
                    }
                    Preconditions.checkArgument(count > 0, "插入失败");
                    return aDo;
                }).collect(Collectors.toList());

        //批量导入、提交
        if (!CollectionUtils.isEmpty(collect)) {

            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
            approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
            approvalParam.setNeedLog(true);
            approvalParam.setCheckPermission(false);


//            int count = thirdPartyFundBusinessDOMapper.batchInsert(collect);
//            Preconditions.checkArgument(count == collect.size(), "批量借款申请异常");

            //提交任务
            collect.stream().forEach(e -> {

                approvalParam.setOrderId(e.getOrderId());
                approvalParam.setProcessId(e.getBridgeProcecssId());


                ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
            });
        }
        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 金投行过桥处理 -导出
     *
     * @return
     */
    @Override
    public ResultBean export(ExportApplyLoanPushParam param) {

        List<ExportApplyLoanPushVO> voList = loanStatementDOMapper.exportApplyLoanPush(param);
        List<String> header = Lists.newArrayList("序号", "委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "代购垫资金额（借款金额）", "借款期限", "利率", "借据号", "最终放款银行"
        );

        String ossResultKey = POIUtil.createExcelFile("购车融资业务推送清单", voList, header, ExportApplyLoanPushVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行还款信息 -导出
     *
     * @return
     */
    @Override
    public ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param) {

        List<JinTouHangRepayInfoVO> voList = loanStatementDOMapper.exportJinTouHangRepayInfo(param);
        List<String> header = Lists.newArrayList("借款时间", "还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金", "还款类型", "备注"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行还款信息", voList, header, JinTouHangRepayInfoVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行息费登记 -导出
     *
     * @return
     */
    @Override
    public ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param) {

        List<JinTouHangInterestRegisterVO> voList = loanStatementDOMapper.exportJinTouHangInterestRegister(param);
        List<String> header = Lists.newArrayList("借款时间", "还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行息费登记", voList, header, JinTouHangInterestRegisterVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    @Override
    public ResultBean detail(Long bridgeProcessId, Long orderId) {
        Preconditions.checkNotNull(bridgeProcessId, "流程ID不能为空");
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);

        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(orderDO, "订单信息不存在");
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
        recombinationVO.setPartyFundBusinessVO(thirdPartyFundBusinessDO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 异常还款
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean abnormalRepay(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");

        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();

        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());

        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "异常还款跟新失败");

        return ResultBean.ofSuccess("保存成功");
    }

    /**
     * 金投行息费登记
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean repayInterestRegister(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");


        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "金投行还款登记失败");

        return ResultBean.ofSuccess("保存成功");
    }
}
