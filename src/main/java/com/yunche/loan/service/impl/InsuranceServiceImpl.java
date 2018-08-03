package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.InsuranceInfoDO;
import com.yunche.loan.domain.entity.InsuranceRelevanceDO;
import com.yunche.loan.domain.entity.InsuranceRiskDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.InsuranceRelevanceUpdateParam;
import com.yunche.loan.domain.param.InsuranceRisksParam;
import com.yunche.loan.domain.param.InsuranceUpdateParam;
import com.yunche.loan.domain.query.RiskQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InsuranceService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class InsuranceServiceImpl implements InsuranceService {

    private static Logger logger = Logger.getLogger(InsuranceServiceImpl.class);

    @Resource
    private LoanOrderDOMapper loanOrderDOMapper;

    @Resource
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Resource
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Resource
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private InsuranceRiskDOMapper insuranceRiskDOMapper;

    @Override
    public InsuranceDetailVO riskDetail(Long orderId, Byte insuranceYear) {

        InsuranceDetailVO insuranceDetailVO = new InsuranceDetailVO();
        List<InsuranceCustomerVO> insuranceCustomerVOList = loanQueryDOMapper.selectInsuranceCustomerByYear(orderId, insuranceYear);
        for (InsuranceCustomerVO obj : insuranceCustomerVOList) {
            if (obj != null) {
                if (obj.getInsurance_info_id() != null) {
                    List<InsuranceRelevanceVO> insurance_relevance_list = loanQueryDOMapper.selectInsuranceRelevance(Long.valueOf(obj.getInsurance_info_id()));
                    obj.setInsurance_relevance_list(insurance_relevance_list);
                }
            }
        }
        insuranceDetailVO.setInfo(insuranceCustomerVOList);
        //出险信息
        insuranceDetailVO.setRisks(insuranceRiskDOMapper.riskInfoByOrderId(orderId, insuranceYear));

        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        insuranceDetailVO.setCustomers(customers);
        List<UniversalCreditInfoVO> credits = loanQueryDOMapper.selectUniversalCreditInfo(orderId);
        for (UniversalCreditInfoVO universalCreditInfoVO : credits) {
            if (!StringUtils.isBlank(universalCreditInfoVO.getCustomer_id())) {
                universalCreditInfoVO.setRelevances(loanQueryDOMapper.selectUniversalRelevanceOrderIdByCustomerId(orderId, Long.valueOf(universalCreditInfoVO.getCustomer_id())));
            }
        }
        insuranceDetailVO.setCredits(credits);

        insuranceDetailVO.setNewInsurance(insuranceRiskDOMapper.newInsuranceByOrderId(orderId));

        return insuranceDetailVO;
    }

    @Override
    public List<InsuranceRelevanceDO> insuranceCompany(String insurance_number) {
        List<InsuranceRelevanceDO> list = insuranceRelevanceDOMapper.selectCompanyByNum(insurance_number);
        return list;
    }

    @Override
    public void riskInsert(InsuranceRisksParam param) {
        InsuranceRiskDO insuranceRiskDO = new InsuranceRiskDO();
        BeanUtils.copyProperties(param, insuranceRiskDO);
        insuranceRiskDOMapper.insertSelective(insuranceRiskDO);
    }

    @Override
    public void riskUpdate(InsuranceRisksParam param) {
        InsuranceRiskDO insuranceRiskDO = new InsuranceRiskDO();
        BeanUtils.copyProperties(param, insuranceRiskDO);
        insuranceRiskDOMapper.updateByPrimaryKeySelective(insuranceRiskDO);
    }

    @Override
    public void riskDetele(Long insuranceNumberId) {
        insuranceRiskDOMapper.deleteByPrimaryKey(insuranceNumberId);

    }

    @Override
    public ResultBean<List<RiskQueryVO>> riskList(RiskQuery query) {
        Date nowDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int count = insuranceRiskDOMapper.insuranceRiskCount(query);
        if (count > 0) {
            List<RiskQueryVO> list = insuranceRiskDOMapper.insuranceRiskList(query);
            try {
                for (RiskQueryVO riskQueryVO : list) {
                    String insuranceType = riskQueryVO.getInsuranceType();
                    String endDateTotal = riskQueryVO.getEndDateTotal();
                    if (endDateTotal.contains(",")) {
                        String[] types = insuranceType.split(",");
                        String[] dates = endDateTotal.split(",");
                        if ("1".equals(types[0])) {
                            Date dates0 = sdf.parse(dates[0]);
                            Date dates1 = sdf.parse(dates[1]);
                            int dateNum0 = (int) ((dates0.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            int dateNum1 = (int) ((dates1.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            riskQueryVO.setEndDate1(dates0);
                            riskQueryVO.setDateNum1(dateNum0 < 0 ? "已到期": dateNum0+"");
                            riskQueryVO.setEndDate(dates1);
                            riskQueryVO.setDateNum(dateNum1 < 0 ? "已到期": dateNum1+"");
                        } else {
                            Date dates0 = sdf.parse(dates[0]);
                            Date dates1 = sdf.parse(dates[1]);
                            int dateNum0 = (int) ((dates0.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            int dateNum1 = (int) ((dates1.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            riskQueryVO.setEndDate1(dates1);
                            riskQueryVO.setDateNum1(dateNum1 < 0 ? "已到期": dateNum1+"");
                            riskQueryVO.setEndDate(dates0);
                            riskQueryVO.setDateNum(dateNum0 < 0 ? "已到期": dateNum0+"");
                        }
                    } else {
                        if ("1".equals(insuranceType)) {
                            Date date = sdf.parse(endDateTotal);
                            int dateNum = (int) ((date.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            riskQueryVO.setEndDate1(date);
                            riskQueryVO.setDateNum1(dateNum < 0 ? "已到期": dateNum+"");
                        } else {
                            Date date = sdf.parse(endDateTotal);
                            int dateNum = (int) ((date.getTime() - nowDate.getTime()) / (1000 * 3600 * 24));
                            riskQueryVO.setEndDate(date);
                            riskQueryVO.setDateNum(dateNum < 0 ? "已到期": dateNum+"");
                        }
                    }
                }
            } catch (ParseException e) {
                logger.error("逻辑处理异常，请联系管理员", e);
                throw new BizException("逻辑处理异常，请联系管理员");
            }
            return ResultBean.ofSuccess(list, count, query.getPageIndex(), query.getPageSize());
        }
        return ResultBean.ofSuccess(Collections.EMPTY_LIST, count, query.getPageIndex(), query.getPageSize());
    }

    @Override
    public RecombinationVO detail(Long orderId) {
        List<InsuranceCustomerVO> insuranceCustomerVOList = loanQueryDOMapper.selectInsuranceCustomer(orderId);
        for (InsuranceCustomerVO obj : insuranceCustomerVOList) {
            if (obj != null) {
                if (obj.getInsurance_info_id() != null) {
                    List<InsuranceRelevanceVO> insurance_relevance_list = loanQueryDOMapper.selectInsuranceRelevance(Long.valueOf(obj.getInsurance_info_id()));
                    obj.setInsurance_relevance_list(insurance_relevance_list);
                }
            }
        }
        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        RecombinationVO<List<InsuranceCustomerVO>> recombinationVO = new RecombinationVO<List<InsuranceCustomerVO>>();
        List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);

        List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
        insuranceInfoDOS.stream().forEach(e -> {
            UniversalInsuranceVO universalInsuranceVO = new UniversalInsuranceVO();
            Byte year = e.getInsurance_year();
            List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(orderId);

            universalInsuranceVO.setInsuranceYear(year);
            universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
            insuranceDetail.add(universalInsuranceVO);
        });
        recombinationVO.setCar(universalCarInfoVO);//车辆信息
        recombinationVO.setInfo(insuranceCustomerVOList);
        recombinationVO.setInsuranceDetail(insuranceDetail);
        return recombinationVO;
    }

    @Override
    public RecombinationVO query(Long orderId) {
        InsuranceCustomerVO insuranceCustomerVO = loanQueryDOMapper.selectInsuranceCustomerNormalizeInsuranceYear(orderId);
        if (insuranceCustomerVO != null) {
            if (insuranceCustomerVO.getInsurance_info_id() != null) {
                List<InsuranceRelevanceVO> insurance_relevance_list = loanQueryDOMapper.selectInsuranceRelevance(Long.valueOf(insuranceCustomerVO.getInsurance_info_id()));
                insuranceCustomerVO.setInsurance_relevance_list(insurance_relevance_list);
            }
        }
        RecombinationVO<InsuranceCustomerVO> recombinationVO = new RecombinationVO<InsuranceCustomerVO>();
        recombinationVO.setInfo(insuranceCustomerVO);
        return recombinationVO;
    }

    @Override
    public void update(InsuranceUpdateParam param) {
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(param.getOrderId()));
        if (loanOrderDO == null) {
            throw new BizException("此业务单不存在");
        }
        //新保录入接口只能查1-续保后期在做
        List<InsuranceRelevanceUpdateParam> insuranceRelevanceList = param.getInsuranceRelevanceList();
        insuranceRelevanceList.stream().forEach(e -> {
            InsuranceInfoDO insuranceInfoDO = insuranceInfoDOMapper.selectByInsuranceYear(param.getOrderId(), e.getYear());
            if (insuranceInfoDO == null) {
                //新增所有关联数据
                insuranceInfoDO = new InsuranceInfoDO();
                insuranceInfoDO.setOrder_id(param.getOrderId());
                insuranceInfoDO.setIssue_bills_date(new Date());
                insuranceInfoDO.setInsurance_year(e.getYear());
                int i = insuranceInfoDOMapper.insertSelective(insuranceInfoDO);
                Preconditions.checkArgument(i > 0, "保险信息保存失败");
                //开始新增保险公司关联表
                //先删除保险公司关联数据在进行新增-保持保险公司的关联信息是最新的`
                insuranceRelevanceDOMapper.deleteByInsuranceInfoIdAndType(insuranceInfoDO.getId(), e.getInsuranceType());
                InsuranceRelevanceDO insuranceRelevanceDO = new InsuranceRelevanceDO();
                insuranceRelevanceDO.setInsurance_info_id(insuranceInfoDO.getId());
                parseDao(e,insuranceRelevanceDO);
                insuranceRelevanceDOMapper.insertSelective(insuranceRelevanceDO);
            } else {
                //代表存在
                //开始更新保险公司关联表
                //先删除保险公司关联数据在进行新增-保持保险公司的关联信息是最新的
                insuranceRelevanceDOMapper.deleteByInsuranceInfoIdAndType(insuranceInfoDO.getId(), e.getInsuranceType());
                InsuranceRelevanceDO insuranceRelevanceDO = new InsuranceRelevanceDO();
                insuranceRelevanceDO.setInsurance_info_id(insuranceInfoDO.getId());
                parseDao(e,insuranceRelevanceDO);
                insuranceRelevanceDOMapper.updateByPrimaryKeySelective(insuranceRelevanceDO);
            }
        });


    }

    /**
     *
     * @param insuranceRelevanceUpdateParam
     * @param insuranceRelevanceDO
     * @return
     */
    private InsuranceRelevanceDO parseDao(InsuranceRelevanceUpdateParam insuranceRelevanceUpdateParam,InsuranceRelevanceDO insuranceRelevanceDO){
        /**
         *    @NotBlank
        private Byte year;//年次
         @NotBlank
         private String insuranceCompanyName;// 保险公司名称
         @NotBlank
         private String insuranceNumber;//  保单号
         @NotBlank
         private BigDecimal insuranceAmount;// 保险金额
         @NotBlank
         private Date startDate;// 开始日期
         @NotBlank
         private Date endDate;// 结束日期
         @NotBlank
         private Byte insuranceType;// 险种 1商业险 2交强险 3车船税
         */

        insuranceRelevanceDO.setInsurance_company_name(insuranceRelevanceUpdateParam.getInsuranceCompanyName());
        insuranceRelevanceDO.setInsurance_number(insuranceRelevanceUpdateParam.getInsuranceNumber());
        insuranceRelevanceDO.setInsurance_amount(insuranceRelevanceUpdateParam.getInsuranceAmount());
        insuranceRelevanceDO.setStart_date(insuranceRelevanceUpdateParam.getStartDate());
        insuranceRelevanceDO.setEnd_date(insuranceRelevanceUpdateParam.getEndDate());
        insuranceRelevanceDO.setInsurance_type(insuranceRelevanceUpdateParam.getInsuranceType());


        return insuranceRelevanceDO;
    }

}
