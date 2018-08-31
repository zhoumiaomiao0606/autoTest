package com.yunche.loan.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.vo.UniversalApprovalInfo;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.MsgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static com.yunche.loan.config.constant.LoanProcessEnum.*;

/**
 * @program: yunche-biz
 * @description: D
 * @author: Mr.WangGang
 * @create: 2018-08-30 11:52
 **/
@Service
@Transactional
public class MsgServiceImpl implements MsgService {

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Resource
    private LoanCreditInfoDOMapper loanCreditInfoDOMapper;

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private LoanTelephoneVerifyDOMapper loanTelephoneVerifyDOMapper;

    @Resource
    private MaterialAuditDOMapper materialAuditDOMapper;

    @Resource
    private LoanCarInfoDOMapper loanCarInfoDOMapper;

    @Resource
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Resource
    private FlowOperationMsgDOMapper flowOperationMsgDOMapper;

    @Override
    public Map creditDetail(Long orderId) {
        if(orderId == null){
            throw new BizException("orderId 为空");
        }
        //BANK_CREDIT_RECORD("usertask_bank_credit_record", "银行征信录入"),
        //SOCIAL_CREDIT_RECORD("usertask_social_credit_record", "社会征信录入"),
        List<Map> resultMap = Lists.newArrayList();
        List<LoanCustomerDO> customerDOS =  loanCustomerDOMapper.selectSelfAndRelevanceCustomersByCustTypes(orderId,null);
        for(LoanCustomerDO V : customerDOS){
            String bankReviewResult = "无";
            String societyReviewResult = "无";
            String customerIdCard = "无";
            String customerName = "无";
            String custType = "无";
            String bankRemark = "无";
            String societyRemark = "无";
            //征信结果: 0-不通过;1-通过;2-关注;
            List<LoanCreditInfoDO> bankLoanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(V.getId(),new Byte("1"));
            List<LoanCreditInfoDO> societyLoanCreditInfoDOS = loanCreditInfoDOMapper.getByCustomerIdAndType(V.getId(),new Byte("2"));
            if(!CollectionUtils.isEmpty(bankLoanCreditInfoDOS)){
                LoanCreditInfoDO bankUniversalApprovalInfo = bankLoanCreditInfoDOS.get(0);
                if(bankUniversalApprovalInfo!=null){
                    if("0".equals(bankUniversalApprovalInfo.getResult().toString())){
                        bankReviewResult = "不通过";
                    }
                    if("1".equals(bankUniversalApprovalInfo.getResult().toString())){
                        bankReviewResult = "通过";
                    }
                    if("2".equals(bankUniversalApprovalInfo.getResult().toString())){
                        bankReviewResult = "关注";
                    }
                    bankRemark = bankUniversalApprovalInfo.getInfo();
                }
            }

            if(!CollectionUtils.isEmpty(societyLoanCreditInfoDOS)){
                LoanCreditInfoDO societyUniversalApprovalInfo = societyLoanCreditInfoDOS.get(0);
                if(societyUniversalApprovalInfo!=null){
                    if("0".equals(societyUniversalApprovalInfo.getResult().toString())){
                        societyReviewResult = "不通过";
                    }
                    if("1".equals(societyUniversalApprovalInfo.getResult().toString())){
                        societyReviewResult = "通过";
                    }
                    if("2".equals(societyUniversalApprovalInfo.getResult().toString())){
                        societyReviewResult = "关注";
                    }
                    societyRemark = societyUniversalApprovalInfo.getInfo();
                }
            }
            if(V != null){
                customerIdCard = V.getIdCard();
                customerName = V.getName();
                //客户类型: 1-主贷人;2-共贷人;3-担保人;4-紧急联系人;
                if("1".equals(V.getCustType().toString())){
                    custType = "主贷人";
                }
                if("2".equals(V.getCustType().toString())){
                    custType = "共贷人";
                }
                if("3".equals(V.getCustType().toString())){
                    custType = "担保人";
                }
                if("4".equals(V.getCustType().toString())){
                    custType = "紧急联系人";
                }
            }

            Map map = Maps.newHashMap();
            map.put("bankReviewResult",bankReviewResult);
            map.put("societyReviewResult",societyReviewResult);
            map.put("bankRemark",bankRemark);
            map.put("societyRemark",societyRemark);
            map.put("customerName",customerName);
            map.put("customerIdCard",customerIdCard);
            map.put("customerType",customerIdCard);
            map.put("custType",custType);
            resultMap.add(map);
        }

        String bankTime = "无";
        String societyTime = "无";
        String customerName = "无";
        String bankTitle = "无";
        String societyTitle = "无";
        UniversalApprovalInfo bankUniversalApprovalInfo  = loanQueryDOMapper.selectUniversalApprovalInfo(BANK_CREDIT_RECORD.getCode(), orderId);
        UniversalApprovalInfo societyUniversalApprovalInfo  = loanQueryDOMapper.selectUniversalApprovalInfo(SOCIAL_CREDIT_RECORD.getCode(), orderId);
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        if(loanOrderDO!=null){
            Long customerId = loanOrderDO.getLoanCustomerId();
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(customerId,new Byte("0"));
            if(loanCustomerDO!=null){
                String bankResult = "无结果";
                String societyResult = "无结果";
                customerName = loanCustomerDO.getName();
                bankTitle ="<"+customerName+">"+"银行征信申请";
                societyTitle ="<"+customerName+">"+"社会征信申请";
                //征信结果: 0-不通过;1-通过;2-关注;
                List<LoanCreditInfoDO> bank = loanCreditInfoDOMapper.getByCustomerIdAndType(loanCustomerDO.getId(),new Byte("1"));
                List<LoanCreditInfoDO> society = loanCreditInfoDOMapper.getByCustomerIdAndType(loanCustomerDO.getId(),new Byte("2"));
                if(!CollectionUtils.isEmpty(bank)){
                    LoanCreditInfoDO b = bank.get(0);
                    if(b!=null){
                        if("0".equals(b.getResult().toString())){
                            bankResult = "不通过";
                        }
                        if("1".equals(b.getResult().toString())){
                            bankResult =  "通过";
                        }
                        if("2".equals(b.getResult().toString())){
                            bankResult =  "关注";
                        }
                    }
                }
                if(!CollectionUtils.isEmpty(society)){
                    LoanCreditInfoDO s = society.get(0);
                    if(s!=null){
                        if("0".equals(s.getResult().toString())){
                            societyResult =    "不通过";
                        }
                        if("1".equals(s.getResult().toString())){
                            societyResult =    "通过";
                        }
                        if("2".equals(s.getResult().toString())){
                            societyResult =    "关注";
                        }

                    }
                }
                bankTitle = bankTitle+bankResult;
                societyTitle = societyTitle + societyResult;
            }
        }
        if(bankUniversalApprovalInfo!=null){
            bankTime = bankUniversalApprovalInfo.getCreate_time();
        }
        if(societyUniversalApprovalInfo!=null){
            societyTime = societyUniversalApprovalInfo.getCreate_time();
        }
        Map result = Maps.newHashMap();
        result.put("bankTitle",bankTitle);
        result.put("societyTitle",societyTitle);
        result.put("bankTime",bankTime);
        result.put("societyTime",societyTime);
        result.put("customers",resultMap);
        return result;
    }

    @Override
    public Map msgDetail(Long msgId) {

        String tReviewResult = "无";
        String mReviewResult = "无";
        String tTime = "无";
        String mTime = "无";
        String tRemark = "无";
        String mRemark = "无";
        String customerName = "无";
        String customerIdCard = "无";
        String customerMobile = "无";
        String completeMaterialDate = "无";
        String gpsNum = "无";
        String isKey = "无";
        String deposit = "无";
        String extra = "无";
        String open = "无";
        String msgTime = "无";
        String msgInfo = "无";
        String sender = "无";
        String title = "无";
        String tAction = null;
        String mAction = null;
        FlowOperationMsgDO flowOperationMsgDO = flowOperationMsgDOMapper.selectByPrimaryKey(msgId);
        if(flowOperationMsgDO!=null){
            Long orderId = flowOperationMsgDO.getOrderId();
            UniversalApprovalInfo t  = loanQueryDOMapper.selectUniversalApprovalInfo(TELEPHONE_VERIFY.getCode(), orderId);
            UniversalApprovalInfo m  = loanQueryDOMapper.selectUniversalApprovalInfo(MATERIAL_REVIEW.getCode(), orderId);

            if(t!=null){
                if("1".equals(t.getCredit_result())){
                    tReviewResult = "通过";
                }
                if("2".equals(t.getCredit_result())){
                    tReviewResult = "通融通过";
                }
                if(t.getAction().equals("0")){
                    tReviewResult = "打回";
                }
                //if(t.getAction().equals("1")){
                //    tReviewResult = "通过";
                //}
                if(t.getAction().equals("2")){
                    tReviewResult = "弃单";
                }
                if(t.getAction().equals("3")){
                    tReviewResult = "资料增补";
                }
                if(t.getAction().equals("4")){
                    tReviewResult = "新增任务";
                }
                if(t.getAction().equals("5")){
                    tReviewResult = "反审";
                }
                tTime = t.getCreate_time();
                tAction = t.getAction();
            }
            if(m!=null){
                //* 审核结果：0-REJECT / 1-PASS / 2-CANCEL / 3-资料增补  / 4-新增任务  / 5-反审
                if(m.getAction().equals("0")){
                    mReviewResult = "打回";
                }
                if(m.getAction().equals("1")){
                    mReviewResult = "通过";
                }
                if(m.getAction().equals("2")){
                    mReviewResult = "弃单";
                }
                if(m.getAction().equals("3")){
                    mReviewResult = "资料增补";
                }
                if(m.getAction().equals("4")){
                    mReviewResult = "新增任务";
                }
                if(m.getAction().equals("5")){
                    mReviewResult = "反审";
                }
                mTime = m.getCreate_time();
                mAction = m.getAction();
            }

            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            if(loanOrderDO!=null){
                LoanCustomerDO loanCustomerDO =  loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
                if(loanCustomerDO!=null){
                    customerName = loanCustomerDO.getName();
                    customerIdCard = loanCustomerDO.getIdCard();
                    customerMobile = loanCustomerDO.getMobile();

                    if("0".equals(loanCustomerDO.getOpenCardOrder())){
                        open = "否";
                    }
                    if("1".equals(loanCustomerDO.getOpenCardOrder())){
                        open = "是";
                    }
                }

                LoanTelephoneVerifyDO loanTelephoneVerifyDO = loanTelephoneVerifyDOMapper.selectByPrimaryKey(orderId);
                if(loanTelephoneVerifyDO!=null){
                    tRemark = loanTelephoneVerifyDO.getInfo();
                }

                MaterialAuditDO materialAuditDO = materialAuditDOMapper.selectByPrimaryKey(loanOrderDO.getMaterialAuditId());
                if(materialAuditDO!=null){
                    mRemark = materialAuditDO.getRemark();
                    SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日");
                    if(materialAuditDO.getComplete_material_date()!=null){
                        completeMaterialDate = myFmt.format(materialAuditDO.getComplete_material_date());
                    }

                }
                LoanCarInfoDO loanCarInfoDO = loanCarInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCarInfoId());
                if(loanCarInfoDO!=null){
                    gpsNum = loanCarInfoDO.getGpsNum()==null?"无":loanCarInfoDO.getGpsNum().toString();
                    if(loanCarInfoDO.getCarKey()!=null){
                        if("0".equals(loanCarInfoDO.getCarKey().toString())){
                            isKey = "否";
                        }
                        if("1".equals(loanCarInfoDO.getCarKey().toString())){
                            isKey = "是";
                        }
                    }
                }

                LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                if(loanFinancialPlanDO!=null){
                    if(loanFinancialPlanDO.getCashDeposit()!=null){
                        deposit = loanFinancialPlanDO.getCashDeposit().toString();
                    }
                    if(loanFinancialPlanDO.getExtraFee()!=null){
                        extra =loanFinancialPlanDO.getExtraFee().toString();
                    }

                }

                if(flowOperationMsgDO.getSendDate()!=null){
                    SimpleDateFormat myFmt=new SimpleDateFormat("yyyy年MM月dd日");
                    msgTime =  myFmt.format(flowOperationMsgDO.getSendDate());
                }
                msgInfo = flowOperationMsgDO.getPrompt();
                sender = flowOperationMsgDO.getSender();
                title = flowOperationMsgDO.getTitle();
            }
        }

        Map result = Maps.newHashMap();
        result.put("tTitle","电审结果:"+"<"+tReviewResult+">");
        result.put("mTitle","纸审结果:"+"<"+mReviewResult+">");
        result.put("title",title);
        result.put("tTime",tTime);
        result.put("mTime",mTime);
        result.put("tRemark",tRemark);
        result.put("mRemark",mRemark);
        result.put("completeMaterialDate",completeMaterialDate);
        result.put("customerName",customerName);
        result.put("customerIdCard",customerIdCard);
        result.put("customerMobile",customerMobile);
        result.put("gpsNum",gpsNum);
        result.put("isKey",isKey);
        result.put("deposit",deposit);
        result.put("extra",extra);
        result.put("open",open);
        result.put("msgTime",msgTime);
        result.put("msgInfo",msgInfo);
        result.put("sender",sender);
        // 通用审核接口 action： 0-打回 / 1-提交 / 2-弃单 / 3-资料增补 / 4-新增任务 / 5-反审
        result.put("tAction",tAction);
        result.put("mAction",mAction);

        return result;
    }
}
