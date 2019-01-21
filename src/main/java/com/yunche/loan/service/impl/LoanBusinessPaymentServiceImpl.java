package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.LoanBusinessPaymentParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanBusinessPaymentService;
import com.yunche.loan.service.LoanFileService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.*;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_REFUND;


@Service
@Transactional
public class LoanBusinessPaymentServiceImpl implements LoanBusinessPaymentService {


    @Autowired
    LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;

    @Autowired
    RemitDetailsDOMapper remitDetailsDOMapper;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public ResultBean save(LoanBusinessPaymentParam loanBusinessPaymentParam) {

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(loanBusinessPaymentParam.getOrderId());

        // 关联ID
        Long remitDetailsId = loanOrderDO.getRemitDetailsId();

        if (remitDetailsId == null) {
            RemitDetailsDO remitDetailsDO = new RemitDetailsDO();
            remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getRemit_beneficiary_account());//收款账户
            remitDetailsDO.setBank_code(loanBusinessPaymentParam.getRemit_bank_code());//收款账户code
            remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getRemit_beneficiary_account_number());//收款账号
            remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getRemit_beneficiary_bank());//收款银行

            remitDetailsDO.setChild_bank(loanBusinessPaymentParam.getRemit_child_bank());//支行
            remitDetailsDO.setChild_bank_code(loanBusinessPaymentParam.getRemit_child_bank_code());//支行code

//            remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.get());//付款组织
            remitDetailsDO.setApplication_date(loanBusinessPaymentParam.getRemit_application_date());//申请日期
            remitDetailsDO.setRemark(loanBusinessPaymentParam.getRemark());
            remitDetailsDO.setCar_dealer_rebate(loanBusinessPaymentParam.getCar_dealer_rebate());
            remitDetailsDOMapper.insertSelective(remitDetailsDO);
            loanOrderDO.setRemitDetailsId(remitDetailsDO.getId());
            loanOrderDOMapper.updateByPrimaryKeySelective(loanOrderDO);

        } else {
            if (remitDetailsDOMapper.selectByPrimaryKey(remitDetailsId) == null) {
                //那order表中是脏数据
                //进行新增 但是id得用order_id表中存在的id
                RemitDetailsDO remitDetailsDO = new RemitDetailsDO();
                remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getRemit_beneficiary_account());//收款账户
                remitDetailsDO.setBank_code(loanBusinessPaymentParam.getRemit_bank_code());//收款账户code
                remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getRemit_beneficiary_account_number());//收款账号
                remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getRemit_beneficiary_bank());//收款银行

                remitDetailsDO.setChild_bank(loanBusinessPaymentParam.getRemit_child_bank());//支行
                remitDetailsDO.setChild_bank_code(loanBusinessPaymentParam.getRemit_child_bank_code());//支行code

//            remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.get());//付款组织
                remitDetailsDO.setApplication_date(loanBusinessPaymentParam.getRemit_application_date());//申请日期
                remitDetailsDO.setId(remitDetailsId);
                remitDetailsDO.setRemark(loanBusinessPaymentParam.getRemark());
                remitDetailsDO.setCar_dealer_rebate(loanBusinessPaymentParam.getCar_dealer_rebate());
                remitDetailsDOMapper.insertSelective(remitDetailsDO);
                //但是不用更新loanOrder 因为已经存在
            } else {
                //代表存在
                //进行更新
                RemitDetailsDO remitDetailsDO = new RemitDetailsDO();
                remitDetailsDO.setBeneficiary_account(loanBusinessPaymentParam.getRemit_beneficiary_account());//收款账户
                remitDetailsDO.setBank_code(loanBusinessPaymentParam.getRemit_bank_code());//收款账户code
                remitDetailsDO.setBeneficiary_account_number(loanBusinessPaymentParam.getRemit_beneficiary_account_number());//收款账号
                remitDetailsDO.setBeneficiary_bank(loanBusinessPaymentParam.getRemit_beneficiary_bank());//收款银行

                remitDetailsDO.setChild_bank(loanBusinessPaymentParam.getRemit_child_bank());//支行
                remitDetailsDO.setChild_bank_code(loanBusinessPaymentParam.getRemit_child_bank_code());//支行code
//            remitDetailsDO.setPayment_organization(loanBusinessPaymentParam.get());//付款组织
                remitDetailsDO.setApplication_date(loanBusinessPaymentParam.getRemit_application_date());//申请日期
                remitDetailsDO.setId(remitDetailsId);
                remitDetailsDO.setRemark(loanBusinessPaymentParam.getRemark());
                remitDetailsDO.setCar_dealer_rebate(loanBusinessPaymentParam.getCar_dealer_rebate());
                remitDetailsDOMapper.updateByPrimaryKeySelective(remitDetailsDO);
            }
        }

        // 文件保存
        ResultBean<Void> fileResultBean = loanFileService.updateOrInsertByCustomerIdAndUploadType(loanOrderDO.getLoanCustomerId(), loanBusinessPaymentParam.getFiles(), UPLOAD_TYPE_NORMAL);
        Preconditions.checkArgument(fileResultBean.getSuccess(), fileResultBean.getMsg());

//        LoanBusinessPaymentDO loanBusinessPaymentDO1 = loanBusinessPaymentDOMapper.selectByPrimaryKey(loanBusinessPaymentParam.getOrderId());
//        if(loanBusinessPaymentDO1==null){
//            loanBusinessPaymentDO.setGmtCreate(new Date());
//            int count = loanBusinessPaymentDOMapper.insertSelective(loanBusinessPaymentDO);
//            Preconditions.checkArgument(count>0,"业务申请单保存失败");
//        }else{
//            loanBusinessPaymentDO.setGmtModify(new Date());
//            int count = loanBusinessPaymentDOMapper.updateByPrimaryKeySelective(loanBusinessPaymentDO);
//            Preconditions.checkArgument(count>0,"业务申请单更新失败");
//        }
        return ResultBean.ofSuccess("创建成功");
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "订单不存在");
        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);

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

        universalInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);
        //客户基本信息
        recombinationVO.setInfo(universalInfoVO);

        UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectUniversalRemitDetails(orderId);
        if (universalRemitDetails == null) {
            universalRemitDetails = new UniversalRemitDetails();
        }
        if (TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview())) {
            universalRemitDetails.setRemit_is_sendback(String.valueOf(K_YORN_YES));
        } else {
            universalRemitDetails.setRemit_is_sendback(String.valueOf(K_YORN_NO));
        }
        recombinationVO.setRemit(universalRemitDetails);

        //共贷人信息
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        recombinationVO.setCustomers(customers);

        //贷款信息
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);
        recombinationVO.setFinancial(financialSchemeVO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public ResultBean<RecombinationVO> appDetail(Long orderId) {

        RecombinationVO recombinationVO = new RecombinationVO();
        LoanProcessDO loanProcessDO = loanProcessDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanProcessDO, "订单不存在");
        //客户基本信息
        recombinationVO.setInfo(loanQueryDOMapper.selectUniversalInfo(orderId));

        UniversalRemitDetails universalRemitDetails = loanQueryDOMapper.selectAppUniversalRemitDetails(orderId);
        if (universalRemitDetails == null) {
            universalRemitDetails = new UniversalRemitDetails();
        }
        if (TASK_PROCESS_REFUND.equals(loanProcessDO.getRemitReview())) {
            universalRemitDetails.setRemit_is_sendback(String.valueOf(K_YORN_YES));
        } else {
            universalRemitDetails.setRemit_is_sendback(String.valueOf(K_YORN_NO));
        }
        recombinationVO.setRemit(universalRemitDetails);

        //共贷人信息
        List<UniversalCustomerFileVO> totalFiles = new ArrayList<>();

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {

            if ("1".equals(universalCustomerVO.getCust_type())) {

                List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                for (UniversalCustomerFileVO file : files) {

                    String fileType = file.getType();
                    if ("57".equals(fileType) || "95".equals(fileType)) {
                        totalFiles.add(file);
                    }
                }
                universalCustomerVO.setFiles(totalFiles);
                break;
            }
        }
        recombinationVO.setCustomers(customers);

        return ResultBean.ofSuccess(recombinationVO);
    }
}
