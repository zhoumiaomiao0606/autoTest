package com.yunche.loan.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.entity.LoanCreditInfoDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.vo.BankInterfaceSerialReturnVO;
import com.yunche.loan.domain.vo.CreditRecordVO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.domain.vo.LoanCreditInfoVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanCreditInfoHisService;
import com.yunche.loan.service.LoanCreditInfoService;
import com.yunche.loan.service.LoanFileService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanCustomerConst.*;

/**
 * @author liuzhe
 * @date 2018/3/7
 */
@Service
public class LoanCreditInfoServiceImpl implements LoanCreditInfoService {


    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCreditInfoHisService loanCreditInfoHisService;

    @Autowired
    private LoanFileService loanFileService;

    @Autowired
    private LoanQueryService loanQueryService;


    @Override
    public void save(LoanCreditInfoDO loanCreditInfoDO) {

        // save
        create(loanCreditInfoDO);

        // 记录征信结果到 征信his表
        saveCreditInfoHis_CreditResult(loanCreditInfoDO);
    }

    @Override
    @Transactional
    public Long create(LoanCreditInfoDO loanCreditInfoDO) {
        Preconditions.checkNotNull(loanCreditInfoDO.getCustomerId(), "客户ID不能为空");
        Preconditions.checkNotNull(loanCreditInfoDO.getType(), "征信类型不能为空");
        Preconditions.checkNotNull(loanCreditInfoDO.getResult(), "征信结果不能为空");

        List<LoanCreditInfoDO> loanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(loanCreditInfoDO.getCustomerId(), loanCreditInfoDO.getType());

        // 前端存在重复create    变更为save
        if (!CollectionUtils.isEmpty(loanCreditInfoDOS)) {

            // update
            LoanCreditInfoDO existLoanCreditInfoDO = loanCreditInfoDOS.get(0);
            Long id = existLoanCreditInfoDO.getId();
            BeanUtils.copyProperties(loanCreditInfoDO, existLoanCreditInfoDO);
            existLoanCreditInfoDO.setId(id);
            update(existLoanCreditInfoDO);

        } else {

            loanCreditInfoDO.setStatus(VALID_STATUS);
            loanCreditInfoDO.setGmtCreate(new Date());
            loanCreditInfoDO.setGmtModify(new Date());
            int count = loanCreditInfoDOMapper.insertSelective(loanCreditInfoDO);
            Preconditions.checkArgument(count > 0, "征信结果录入失败");
        }

        // 记录征信结果到 征信his表
        saveCreditInfoHis_CreditResult(loanCreditInfoDO);

        return loanCreditInfoDO.getId();
    }

    @Override
    @Transactional
    public Long update(LoanCreditInfoDO loanCreditInfoDO) {
        Preconditions.checkNotNull(loanCreditInfoDO.getId(), "征信信息ID不能为空");

        loanCreditInfoDO.setGmtModify(new Date());
        int count = loanCreditInfoDOMapper.updateByPrimaryKeySelective(loanCreditInfoDO);
        Preconditions.checkArgument(count > 0, "征信结果修改失败");

        // 记录征信结果到 征信his表
        saveCreditInfoHis_CreditResult(loanCreditInfoDO);

        return loanCreditInfoDO.getId();
    }

    @Override
    public LoanCreditInfoVO getByCustomerId(Long customerId, Byte type) {
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
        return loanCreditInfoVO;
    }

    @Override
    public CreditRecordVO detailAll(Long customerId, Byte creditType) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");
        Preconditions.checkNotNull(creditType, "征信类型不能为空");

        CreditRecordVO creditRecordVO = new CreditRecordVO();

        // 客户信息
        List<LoanCustomerDO> loanCustomerDOList = loanCustomerDOMapper.listByPrincipalCustIdAndType(customerId, null, VALID_STATUS);
        if (!CollectionUtils.isEmpty(loanCustomerDOList)) {

            // 填充客户信息 和 征信结果 和 文件信息
            fillCustInfoAndCreditRecord(creditRecordVO, loanCustomerDOList, creditType);
        }

        return creditRecordVO;
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

        loanCustomerDOList.stream()
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
                        //线上征信查询结果
                        fillBankCreditMsg(principalLender);
                        List<Long> relevanceOrderlist = loanOrderDOMapper.selectRelevanceLoanOrderIdByCustomerId(principalLender.getCustomerId());
                        if (principalLender != null) {
                            principalLender.setRelevanceOrderlist(relevanceOrderlist);
                        }
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
                        //线上征信查询结果
                        fillBankCreditMsg(commonLender);
                        List<Long> relevanceOrderlist = loanOrderDOMapper.selectRelevanceLoanOrderIdByCustomerId(commonLender.getCustomerId());
                        if (commonLender != null) {
                            commonLender.setRelevanceOrderlist(relevanceOrderlist);
                        }
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
                        //线上征信查询结果
                        fillBankCreditMsg(guarantor);
                        List<Long> relevanceOrderlist = loanOrderDOMapper.selectRelevanceLoanOrderIdByCustomerId(guarantor.getCustomerId());
                        if (guarantor != null) {
                            guarantor.setRelevanceOrderlist(relevanceOrderlist);
                        }
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
     * @param creditRecord
     */
    private void fillBankCreditMsg(CreditRecordVO.CustomerCreditRecord creditRecord) {
        try {
            BankInterfaceSerialReturnVO returnVO = loanQueryService.selectLastBankInterfaceSerialByTransCode(creditRecord.getCustomerId(), IDict.K_TRANS_CODE.APPLYCREDIT);
            if (returnVO != null) {
                if (StringUtil.isEmpty(returnVO.getReject_reason())) {
                    String api_msg = returnVO.getApi_msg();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map map = objectMapper.readValue(api_msg, Map.class);
                    Map pub = (Map) map.get("pub");
                    String result = (String) pub.get("retmsg");
                    creditRecord.setBankCreditResponse(result);
                } else {
                    creditRecord.setBankCreditResponse(returnVO.getReject_reason());
                }
            }
        } catch (Exception e) {
            creditRecord.setBankCreditResponse("银行接口异常,请联系管理员");
        }


    }

    /**
     * 文件信息
     *
     * @param customerCreditRecord
     * @param customerId
     */
    private void fillFiles(CreditRecordVO.CustomerCreditRecord customerCreditRecord, Long customerId) {
        ResultBean<List<FileVO>> filesResultBean = loanFileService.listByCustomerId(customerId, null);
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
            customerCreditRecord.setGuaranteeType(loanCustomerDO.getGuaranteeType());
            customerCreditRecord.setBankCreditNote(loanQueryDOMapper.selectLastBankInterfaceSerialNoteByTransCode(loanCustomerDO.getId(), "applyCredit"));
            customerCreditRecord.setBankCreditStatus(loanQueryDOMapper.selectLastBankInterfaceSerialStatusByTransCode(loanCustomerDO.getId(), "applyCredit"));
        }
    }

    /**
     * 填充征信信息
     *
     * @param customerCreditRecord
     * @param creditType
     */
    private void fillCreditMsg(CreditRecordVO.CustomerCreditRecord customerCreditRecord, Byte creditType) {

        LoanCreditInfoVO loanCreditInfoVO = getByCustomerId(customerCreditRecord.getCustomerId(), creditType);

        customerCreditRecord.setCreditId(loanCreditInfoVO.getId());
        customerCreditRecord.setCreditResult(loanCreditInfoVO.getResult());
        customerCreditRecord.setCreditInfo(loanCreditInfoVO.getInfo());
        customerCreditRecord.setBankCreditStatus(loanQueryDOMapper.selectLastBankInterfaceSerialStatusByTransCode(customerCreditRecord.getCustomerId(), "applyCredit"));
    }

    /**
     * 记录征信结果到 征信his表
     *
     * @param loanCreditInfoDO
     */
    private void saveCreditInfoHis_CreditResult(LoanCreditInfoDO loanCreditInfoDO) {

        if (CREDIT_TYPE_BANK.equals(loanCreditInfoDO.getType())) {

            loanCreditInfoHisService.saveCreditInfoHis_BankCreditResult(loanCreditInfoDO.getCustomerId(), loanCreditInfoDO.getResult());

        } else if (CREDIT_TYPE_SOCIAL.equals(loanCreditInfoDO.getType())) {

            loanCreditInfoHisService.saveCreditInfoHis_SocialCreditResult(loanCreditInfoDO.getCustomerId(), loanCreditInfoDO.getResult());
        }
    }
}
