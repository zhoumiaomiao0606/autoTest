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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 催保
 */
@Service
public class InsuranceUrgeServiceImpl implements InsuranceUrgeService{

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
    private SysConfig sysConfig;

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

            Preconditions.checkNotNull(loanOrderDO,"订单不存在");

            UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryDOMapper.selectUniversalCustomerDetail(orderId, loanOrderDO.getLoanCustomerId());

            FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

            UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);


            List<InsuranceInfoDO> insuranceInfoDOS = insuranceInfoDOMapper.listByOrderId(orderId);

            List<UniversalInsuranceVO> insuranceDetail = Lists.newArrayList();
            insuranceInfoDOS.stream().forEach(e->{
                UniversalInsuranceVO universalInsuranceVO = new UniversalInsuranceVO();
                Byte year = e.getInsurance_year();
                List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(e.getId());
                universalInsuranceVO.setInsuranceYear(year);
                universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
                insuranceDetail.add(universalInsuranceVO);
            });

            List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
            for (UniversalCustomerVO universalCustomerVO : customers) {
                List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                universalCustomerVO.setFiles(files);
            }
            List<RenewInsuranceDO> renewInsurList = renewInsuranceDOMapper.selectByOrderId(orderId);

            recombinationVO.setRenewInsurList(renewInsurList);
            recombinationVO.setInfo(universalCustomerDetailVO);
            recombinationVO.setFinancial(financialSchemeVO);
            recombinationVO.setCar(universalCarInfoVO);
            recombinationVO.setInsuranceDetail(insuranceDetail);
            recombinationVO.setCustomers(customers);
            return ResultBean.ofSuccess(recombinationVO);
        }
    }

    /**
     * 新催保记录
     * @return
     */
    @Override
    public Void renew(RenewInsuranceParam renewInsuranceParam) {
        Preconditions.checkNotNull(renewInsuranceParam,"新催保记录不能为空");

        RenewInsuranceDO renewInsuranceDO = new RenewInsuranceDO();
        BeanUtils.copyProperties(renewInsuranceParam,renewInsuranceDO);

        RenewInsuranceDO insuranceDO = renewInsuranceDOMapper.selectByPrimaryKey(renewInsuranceParam.getId());
        if(insuranceDO == null){
            int count = renewInsuranceDOMapper.insert(renewInsuranceDO);
            Preconditions.checkArgument(count>0,"新催保记录保存失败");
        }else{
            int count = renewInsuranceDOMapper.updateByPrimaryKeySelective(renewInsuranceDO);
            Preconditions.checkArgument(count>0,"更新新催保记录保存失败");
        }

        return null;
    }
    /**
     * 新催保记录详情页面
     * @param id
     * @return
     */
    @Override
    public ResultBean renewDetail(Long id, Long orderId) {

        RenewDetailVO renewDetailVO = new RenewDetailVO();
        if(id==null){
            Preconditions.checkNotNull(orderId,"业务单号不能为空");
            LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
            LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
            renewDetailVO.setCustomerName(loanCustomerDO.getName());
            renewDetailVO.setMobile(loanCustomerDO.getMobile());
        }else{
            RenewInsuranceDO renewInsuranceDO = renewInsuranceDOMapper.selectByPrimaryKey(id);
            Preconditions.checkNotNull(renewInsuranceDO,"新催保记录不存在");
            BeanUtils.copyProperties(renewInsuranceDO,renewDetailVO);
        }

        return ResultBean.ofSuccess(renewDetailVO);
    }

    /**
     * 生成短信内容
     * @param renewInsuranceParam
     * @return
     */
    @Override
    public String generateSms(RenewInsuranceParam renewInsuranceParam) {
        String tempSms="您好:{}(先生/女士)" +
                "这里是中顺汽车有限公司(你车子担保公司)的车险专员:{} 你的车子保险金额已经核算好." +
                "车损险:{} 保费:{}." +
                "三者{}万,保费:{}." +
                "(司机/乘客1万1座)保费:{}." +
                "盗抢险:{}.自燃险:{}." +
                "玻璃险:{}." +
                "不计免赔特约险:{}." +
                "交强险:{}.车船税:{}." +
                "共计:{}." +
                "公司法代账号：6222081202007385758" +
                ",包功.工商银行城站支行." +
                "如有问题可以直接联系我." +
                "电话:10000101" +
                "（*按揭期间要在我们担保公司投保," +
                "不然保证金会将受到影响,谢谢.）【云车金融】";
        ArrayList<Object> objects = Lists.newArrayList();

        objects.add(renewInsuranceParam.getCustomerName());
        objects.add(renewInsuranceParam.getEmployeeName());
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getDamageInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getDamageInsurFee(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsurFee(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getPersLiabilityInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getRobberyInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getAutoignitionInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getGlassInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getNotDeductInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getStrongInsur(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getVesselTax(),2));
        objects.add(BigDecimalUtil.format(renewInsuranceParam.getTotalPremium(),2));


        tempSms = parse("{", "}", tempSms, objects);


        return tempSms;
    }

    @Override
    public ResultBean  sendSms(RenewInsuranceParam param) {

        String url="https://sh2.ipyy.com/smsJson.aspx";
        HashMap<String, String> headers = Maps.newHashMap();
        HashMap<String, String> querys = Maps.newHashMap();
        querys.put("action","send");
        querys.put("userid", GeneratorIDUtil.execute());
        querys.put("account","hxwl0049");
        querys.put("password","780565");
        querys.put("mobile",param.getTelphone());
        querys.put("content",param.getSms());
        querys.put("sendTime","");
        querys.put("extno", "");
        try {
            HttpUtils.doGet("https://sh2.ipyy.com/","smsJson.aspx","GET",null,querys);
        } catch (Exception e) {
           throw new BizException(e.getMessage());
        }
        return ResultBean.ofSuccess(null,"发送成功");
    }

    /**
     *
     * @param openToken
     * @param closeToken
     * @param text
     * @param args
     * @return
     */
    public static String parse(String openToken, String closeToken, String text, ArrayList<Object> args ) {
        final StringBuilder builder = new StringBuilder();
        try{
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

        }catch (Exception e){
            e.printStackTrace();
        }
        return builder.toString();
    }
}
