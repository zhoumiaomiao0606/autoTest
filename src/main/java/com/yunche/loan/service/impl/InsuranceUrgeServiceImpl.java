package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.BigDecimalUtil;
import com.yunche.loan.config.util.GeneratorIDUtil;
import com.yunche.loan.config.util.HttpUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.RenewInsuranceParam;
import com.yunche.loan.domain.query.InsuranceListQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InsuranceUrgeService;
import com.yunche.loan.service.LoanQueryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * 催保
 */
@Service
public class InsuranceUrgeServiceImpl implements InsuranceUrgeService {

    @Autowired
    private BankRecordQueryDOMapper bankRecordQueryDOMapper;

    @Autowired
    private InsuranceDistributeRecordDOMapper insuranceDistributeRecordDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private InsuranceInfoDOMapper insuranceInfoDOMapper;

    @Autowired
    private InsuranceRelevanceDOMapper insuranceRelevanceDOMapper;

    @Autowired
    private RenewInsuranceDOMapper renewInsuranceDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public List list(InsuranceListQuery insuranceListQuery) {

        List<InsuranceUrgeVO> urgeVOList = bankRecordQueryDOMapper.selectInsuranceUrgeTaskList(insuranceListQuery);

        return urgeVOList;
    }


    @Override
    public ResultBean detail(Long orderId) {
        {

            RecombinationVO recombinationVO = new RecombinationVO<>();
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);

            Preconditions.checkNotNull(loanOrderDO, "订单不存在");

            UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);

            FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

            UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);

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
            universalCarInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);


            List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);

            List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
            insuranceInfoDOS.stream().forEach(e -> {
                UniversalInsuranceVO universalInsuranceVO = new UniversalInsuranceVO();
                Byte year = e.getInsurance_year();
                List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(e.getId());
                universalInsuranceVO.setInsuranceYear(year);
                universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
                insuranceDetail.add(universalInsuranceVO);
            });

            List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
            for (UniversalCustomerVO universalCustomerVO : customers) {
                List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                universalCustomerVO.setFiles(files);
            }
            List<RenewInsuranceDO> renewInsurList = renewInsuranceDOMapper.selectByOrderId(orderId);

            recombinationVO.setRenewInsurList(renewInsurList);
            recombinationVO.setInfo(universalInfoVO);
            recombinationVO.setFinancial(financialSchemeVO);
            recombinationVO.setCar(universalCarInfoVO);
            recombinationVO.setInsuranceDetail(insuranceDetail);
            recombinationVO.setCustomers(customers);
            return ResultBean.ofSuccess(recombinationVO);
        }
    }

    /**
     * 新催保记录
     *
     * @return
     */
    @Override
    public Void renew(RenewInsuranceParam renewInsuranceParam) {
        Preconditions.checkNotNull(renewInsuranceParam, "新催保记录不能为空");
        RenewInsuranceDO renewInsuranceDO = new RenewInsuranceDO();
        BeanUtils.copyProperties(renewInsuranceParam, renewInsuranceDO);
        renewInsuranceDO.setOmbudsman(renewInsuranceParam.getEmployeeName());
        RenewInsuranceDO insuranceDO = renewInsuranceDOMapper.selectByPrimaryKey(renewInsuranceParam.getId());
        if (insuranceDO == null) {
            int count = renewInsuranceDOMapper.insert(renewInsuranceDO);
            Preconditions.checkArgument(count > 0, "新催保记录保存失败");
        } else {
            int count = renewInsuranceDOMapper.updateByPrimaryKeySelective(renewInsuranceDO);
            Preconditions.checkArgument(count > 0, "更新新催保记录保存失败");
        }

        return null;
    }

    /**
     * 新催保记录详情页面
     *
     * @param id
     * @return
     */
    @Override
    public ResultBean renewDetail(Long id, Long orderId) {

        RenewDetailVO renewDetailVO = new RenewDetailVO();
        if (id == null) {
            Preconditions.checkNotNull(orderId, "业务单号不能为空");
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
            renewDetailVO.setCustomerName(loanCustomerDO.getName());
            renewDetailVO.setMobile(loanCustomerDO.getMobile());
        } else {
            RenewInsuranceDO renewInsuranceDO = renewInsuranceDOMapper.selectByPrimaryKey(id);
            Preconditions.checkNotNull(renewInsuranceDO, "新催保记录不存在");
            BeanUtils.copyProperties(renewInsuranceDO, renewDetailVO);
        }

        return ResultBean.ofSuccess(renewDetailVO);
    }

    /**
     * 生成短信内容
     *
     * @param renewInsuranceParam
     * @return
     */
    @Override
    public String generateSms(RenewInsuranceParam renewInsuranceParam) {
        Preconditions.checkNotNull(renewInsuranceParam);
        String tempSms = "您好:{}(先生/女士)," +
                "这里是中顺汽车有限公司(你车子担保公司)的车险专员:{}。 你的车子保险金额已经核算好:" +
                "车损险:{}元、保费:{}元," +
                "三者{}万元,保费:{}元," +
                "(司机/乘客{}万1座)、保费:{}元，" +
                "盗抢险:{}元，自燃险:{}元，" +
                "玻璃险:{}元，" +
                "不计免赔特约险:{}元，" +
                "交强险:{}元，车船税:{}元，" +
                "共计:{}。" +
                "公司法代账号：6222081202007385758" +
                ",包功.工商银行城站支行." +
                "如有问题可以直接联系我." +
                "电话:10000101" +
                "（*按揭期间要在我们担保公司投保," +
                "不然保证金会将受到影响,谢谢.）【云车金融】";
        ArrayList<Object> objects = Lists.newArrayList();

        Preconditions.checkNotNull(renewInsuranceParam.getCustomerName(), "客户姓名不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getAutoInsuEmployee(), "车险专员不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getDamageInsur(), "车损险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getDamageInsurFee(), "车损险保费不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getThirdDutyInsur(), "三者费用不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getThirdDutyInsurFee(), "三者费用保费不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getPersLiabilityInsur(), "车上人员责任险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getPersLiabilityInsurFee(), "车上人员责任险保费不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getRobberyInsur(), "盗抢险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getAutoignitionInsur(), "自燃险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getGlassInsur(), "玻璃险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getNotDeductInsur(), "不计免赔特约险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getStrongInsur(), "交强险不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getVesselTax(), "车船税不能为空");
        Preconditions.checkNotNull(renewInsuranceParam.getTotalPremium(), "共计总额不能为空");


        objects.add(renewInsuranceParam.getCustomerName());
        objects.add(renewInsuranceParam.getAutoInsuEmployee());
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getDamageInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getDamageInsurFee(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsurFee(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getPersLiabilityInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getPersLiabilityInsurFee(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getRobberyInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getAutoignitionInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getGlassInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getNotDeductInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getStrongInsur(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getVesselTax(), 2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getTotalPremium(), 2));


        tempSms = parse("{", "}", tempSms, objects);


        return tempSms;
    }

    @Override
    public ResultBean sendSms(RenewInsuranceParam param) {

        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getMessage(), "短信内容为空");
        Preconditions.checkNotNull(param.getTelphone(), "电话号码必输*");

        String url = "https://sh2.ipyy.com/smsJson.aspx";
        HashMap<String, String> headers = Maps.newHashMap();
        HashMap<String, String> querys = Maps.newHashMap();
        querys.put("action", "send");
        querys.put("userid", GeneratorIDUtil.execute());
        querys.put("account", "hxwl0049");
        querys.put("password", "780565");
        querys.put("mobile", param.getTelphone());
        querys.put("content", param.getMessage());
        querys.put("sendTime", "");
        querys.put("extno", "");
        try {
            HttpUtils.doGet("https://sh2.ipyy.com/", "smsJson.aspx", "GET", null, querys);
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
        return ResultBean.ofSuccess(null, "发送成功");
    }

    /**
     * @param orderId
     * @return
     */
    @Override
    public ResultBean approval(Long orderId) {
        Preconditions.checkNotNull(orderId, "参数有误");
        InsuranceDistributeRecordDO recordDO = insuranceDistributeRecordDOMapper.selectRenewInsurLimit(orderId);
        int nextInsurYear = recordDO.getInsuranceYear() + 1;
        InsuranceInfoDO insuranceInfoDO = insuranceInfoDOMapper.selectByInsuranceYear(orderId, new Byte(String.valueOf(nextInsurYear)));
        if (insuranceInfoDO == null) {
            return ResultBean.ofError("续保信息未录入");
        } else {
            recordDO.setStatus(BaseConst.VALID_STATUS);
            int count = insuranceDistributeRecordDOMapper.updateByPrimaryKeySelective(recordDO);
            Preconditions.checkArgument(count > 0, "催保记录更新失败");
        }
        return ResultBean.ofSuccess("提交成功");
    }

    @Override
    public ResultBean insurQuery(Long orderId, Byte year) {
        InsuranceInfoDO insuranceInfoDO = insuranceInfoDOMapper.selectByInsuranceYear(orderId, year);
        List<InsuranceRelevanceDO> currInsurRele = Lists.newArrayList();
        if (insuranceInfoDO != null) {
            currInsurRele = insuranceRelevanceDOMapper.listByInsuranceInfoId(insuranceInfoDO.getId());
        }
        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setInfo(currInsurRele);
        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, ArrayList<Object> args) {
        final StringBuilder builder = new StringBuilder();
        try {
            if (args == null || args.size() <= 0) {
                return text;
            }
            int argsIndex = 0;

            if (text == null || text.isEmpty()) {
                return "";
            }
            char[] src = text.toCharArray();
            int offset = 0;
            // search open token
            int start = text.indexOf(openToken, offset);
            if (start == -1) {
                return text;
            }

            StringBuilder expression = null;
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    // this open token is escaped. remove the backslash and continue.
                    builder.append(src, offset, start - offset - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    // found open token. let's search close token.
                    if (expression == null) {
                        expression = new StringBuilder();
                    } else {
                        expression.setLength(0);
                    }
                    builder.append(src, offset, start - offset);
                    offset = start + openToken.length();
                    int end = text.indexOf(closeToken, offset);
                    while (end > -1) {
                        if (end > offset && src[end - 1] == '\\') {
                            // this close token is escaped. remove the backslash and continue.
                            expression.append(src, offset, end - offset - 1).append(closeToken);
                            offset = end + closeToken.length();
                            end = text.indexOf(closeToken, offset);
                        } else {
                            expression.append(src, offset, end - offset);
                            offset = end + closeToken.length();
                            break;
                        }
                    }
                    if (end == -1) {
                        // close token was not found.
                        builder.append(src, start, src.length - start);
                        offset = src.length;
                    } else {
                        String value = (argsIndex <= args.size() - 1) ?
                                (args.get(argsIndex) == null ? "" : args.get(argsIndex).toString()) : expression.toString();
                        builder.append(value);
                        offset = end + closeToken.length();
                        argsIndex++;
                    }
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
}
