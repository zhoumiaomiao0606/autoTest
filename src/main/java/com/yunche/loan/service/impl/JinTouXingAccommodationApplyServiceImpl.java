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
import com.yunche.loan.domain.query.TaskListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.ThirdPartyFundBusinessDOMapper;
import com.yunche.loan.service.JinTouXingAccommodationApplyService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.TaskSchedulingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;



public class JinTouXingAccommodationApplyServiceImpl implements JinTouXingAccommodationApplyService {


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
                // TODO
                approvalParam.setTaskDefinitionKey("");
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
     * 导出
     * @return
     */
    @Override
    public ResultBean export(TaskListQuery taskListQuery) {

        ResultBean<List<TaskListVO>> taskList = taskSchedulingService.queryTaskList(taskListQuery);
        List listData = taskList.getData();
        //TODO
        ArrayList<String> header = Lists.newArrayList("序号","委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "代购垫资金额（借款金额）", "借款期限", "利率", "借据号", "最终放款银行"
        );


        String ossResultKey = POIUtil.createExcelFile("购车融资业务推送清单",listData,header,AccommodationApplyVO.class,ossConfig);




        return null;
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
}
