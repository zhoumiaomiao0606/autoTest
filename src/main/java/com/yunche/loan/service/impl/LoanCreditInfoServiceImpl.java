package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.vo.CreditRecordVO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanCreditInfoDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.service.LoanCreditInfoService;
import com.yunche.loan.service.LoanFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.CustomerConst.*;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Service
@Transactional
public class LoanCreditInfoServiceImpl implements LoanCreditInfoService {


    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanFileService loanFileService;


    @Override
    public ResultBean<Long> create(LoanCreditInfoDO loanCreditInfoDO) {
        Preconditions.checkNotNull(loanCreditInfoDO.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(loanCreditInfoDO.getType(), "征信类型不能为空");
        Preconditions.checkNotNull(loanCreditInfoDO.getResult(), "征信结果不能为空");

        loanCreditInfoDO.setStatus(VALID_STATUS);
        loanCreditInfoDO.setGmtCreate(new Date());
        loanCreditInfoDO.setGmtModify(new Date());
        int count = loanCreditInfoDOMapper.insertSelective(loanCreditInfoDO);
        Preconditions.checkArgument(count > 0, "征信结果录入失败");

        return ResultBean.ofSuccess(loanCreditInfoDO.getId(), "征信结果录入成功");
    }

    @Override
    public ResultBean<Long> update(LoanCreditInfoDO loanCreditInfoDO) {
        Preconditions.checkNotNull(loanCreditInfoDO.getId(), "征信信息ID不能为空");

        loanCreditInfoDO.setGmtModify(new Date());
        int count = loanCreditInfoDOMapper.updateByPrimaryKeySelective(loanCreditInfoDO);
        Preconditions.checkArgument(count > 0, "征信结果修改失败");

        return ResultBean.ofSuccess(loanCreditInfoDO.getId(), "征信结果修改成功");
    }

    @Override
    public ResultBean<LoanCreditInfoVO> getByCustomerId(Long customerId, Byte type) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");
        Preconditions.checkNotNull(type, "征信类型不能为空");

        List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(customerId, type);
        LoanCreditInfoVO loanCreditInfoVO = new LoanCreditInfoVO();
        if (!CollectionUtils.isEmpty(loanCreditInfoDOS)) {
            LoanCreditInfoDO loanCreditInfoDO = loanCreditInfoDOS.get(0);
            if (null != loanCreditInfoDO) {
                BeanUtils.copyProperties(loanCreditInfoDO, loanCreditInfoVO);
            }
        }
        return ResultBean.ofSuccess(loanCreditInfoVO);
    }

    @Override
    public ResultBean<CreditRecordVO> detailAll(Long customerId, Byte creditType) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");
        Preconditions.checkNotNull(creditType, "征信类型不能为空");

        CreditRecordVO creditRecordVO = new CreditRecordVO();

        // 客户信息
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.listByPrincipalCustIdAndType(customerId, null, VALID_STATUS);
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {

            // 填充客户信息 和 征信结果
            fillCustInfoAndCreditRecord(creditRecordVO, loanCustomerDOList, creditType);
        }

        return ResultBean.ofSuccess(creditRecordVO);
    }

    /**
     * 填充客户信息 和 征信结果
     *
     * @param creditRecordVO
     * @param loanCustomerDOList
     * @param creditType
     */
    private void fillCustInfoAndCreditRecord(CreditRecordVO creditRecordVO, List<LoanCustomerDO> loanCustomerDOList, Byte creditType) {

        List<CreditRecordVO.CustomerCreditRecord> commonLenderList = Lists.newArrayList();
        List<CreditRecordVO.CustomerCreditRecord> guarantorList = Lists.newArrayList();
        List<CreditRecordVO.CustomerCreditRecord> emergencyContactList = Lists.newArrayList();

        loanCustomerDOList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {

                    // 主贷人
                    if (CUST_TYPE_PRINCIPAL.equals(e.getCustType())) {
                        CreditRecordVO.CustomerCreditRecord principalLender = new CreditRecordVO.CustomerCreditRecord();

                        // 客户信息
                        fillCustInfo(principalLender, e);
                        // 文件
                        fillFiles(principalLender, principalLender.getCustomerId());
                        // 征信结果
                        fillCreditMsg(principalLender, creditType);

                        creditRecordVO.setPrincipalLender(principalLender);
                    }

                    // 共贷人
                    else if (CUST_TYPE_COMMON.equals(e.getCustType())) {
                        CreditRecordVO.CustomerCreditRecord commonLender = new CreditRecordVO.CustomerCreditRecord();

                        // 客户信息
                        fillCustInfo(commonLender, e);
                        // 文件
                        fillFiles(commonLender, commonLender.getCustomerId());
                        // 征信结果
                        fillCreditMsg(commonLender, creditType);

                        commonLenderList.add(commonLender);
                    }
                    // 担保人
                    else if (CUST_TYPE_GUARANTOR.equals(e.getCustType())) {
                        CreditRecordVO.CustomerCreditRecord guarantor = new CreditRecordVO.CustomerCreditRecord();

                        // 客户信息
                        fillCustInfo(guarantor, e);
                        // 文件
                        fillFiles(guarantor, guarantor.getCustomerId());
                        // 征信结果
                        fillCreditMsg(guarantor, creditType);

                        guarantorList.add(guarantor);
                    }
                    // 紧急联系人
                    else if (CUST_TYPE_EMERGENCY_CONTACT.equals(e.getCustType())) {
                        CreditRecordVO.CustomerCreditRecord emergencyContact = new CreditRecordVO.CustomerCreditRecord();

                        // 客户信息
                        fillCustInfo(emergencyContact, e);
                        // 文件
                        fillFiles(emergencyContact, emergencyContact.getCustomerId());
                        // 征信结果
                        fillCreditMsg(emergencyContact, creditType);

                        emergencyContactList.add(emergencyContact);
                    }
                });

        List<CreditRecordVO.CustomerCreditRecord> sortedCommonLenderList = commonLenderList.parallelStream()
                .sorted(Comparator.comparing(CreditRecordVO.CustomerCreditRecord::getCustomerId)).collect(Collectors.toList());
        List<CreditRecordVO.CustomerCreditRecord> sortedGuarantorList = guarantorList.parallelStream()
                .sorted(Comparator.comparing(CreditRecordVO.CustomerCreditRecord::getCustomerId)).collect(Collectors.toList());
        List<CreditRecordVO.CustomerCreditRecord> sortedEmergencyContactList = emergencyContactList.parallelStream()
                .sorted(Comparator.comparing(CreditRecordVO.CustomerCreditRecord::getCustomerId)).collect(Collectors.toList());

        creditRecordVO.setCommonLenderList(sortedCommonLenderList);
        creditRecordVO.setGuarantorList(sortedGuarantorList);
        creditRecordVO.setEmergencyContactList(sortedEmergencyContactList);
    }

    /**
     * 文件信息
     *
     * @param customerCreditRecord
     * @param customerId
     */
    private void fillFiles(CreditRecordVO.CustomerCreditRecord customerCreditRecord, Long customerId) {
        ResultBean<List<FileVO>> filesResultBean = loanFileService.listByCustomerId(customerId);
        Preconditions.checkArgument(filesResultBean.getSuccess(), filesResultBean.getMsg());
        customerCreditRecord.setFiles(filesResultBean.getData());
    }

    /**
     * 客户信息
     *
     * @param customerCreditRecord
     * @param loanCustomerDO
     */
    private void fillCustInfo(CreditRecordVO.CustomerCreditRecord customerCreditRecord, LoanCustomerDO loanCustomerDO) {
        if (null != loanCustomerDO) {
            BeanUtils.copyProperties(loanCustomerDO, customerCreditRecord);
            customerCreditRecord.setCustomerId(loanCustomerDO.getId());
            customerCreditRecord.setCustomerName(loanCustomerDO.getName());
        }
    }

    /**
     * 填充征信信息
     *
     * @param customerCreditRecord
     * @param creditType
     */
    private void fillCreditMsg(CreditRecordVO.CustomerCreditRecord customerCreditRecord, Byte creditType) {
        ResultBean<LoanCreditInfoVO> resultBean = getByCustomerId(customerCreditRecord.getCustomerId(), creditType);
        Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        LoanCreditInfoVO loanCreditInfoVO = resultBean.getData();

        customerCreditRecord.setCreditId(loanCreditInfoVO.getId());
        customerCreditRecord.setCreditResult(loanCreditInfoVO.getResult());
        customerCreditRecord.setCreditInfo(loanCreditInfoVO.getInfo());
    }
}
