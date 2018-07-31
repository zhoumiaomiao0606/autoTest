package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.common.SysConfig;
import com.yunche.loan.config.constant.BaseConst;
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
                List<InsuranceRelevanceDO> insuranceRelevanceDOS = insuranceRelevanceDOMapper.listByInsuranceInfoId(orderId);
                universalInsuranceVO.setInsuranceYear(year);
                universalInsuranceVO.setInsuranceRele(insuranceRelevanceDOS);
                insuranceDetail.add(universalInsuranceVO);
            });

            List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
            for (UniversalCustomerVO universalCustomerVO : customers) {
                List<UniversalCustomerFileVO> files = loanQueryDOMapper.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
                universalCustomerVO.setFiles(files);
            }
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
        int count = renewInsuranceDOMapper.insert(renewInsuranceDO);
        Preconditions.checkArgument(count>0,"新催保记录保存失败");
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
     * tempSms=您好:CUSTOMER(先生/女士) \
     这里是中顺汽车有限公司(你车子担保公司)的车险专员:EMPLOYEE 你的车子保险金额已经核算好.\
     车损险:DAMAGE_INSUR 保费:DAMAGE_INSUR_FEE\
     三者THIRD_DUTY_INSUR万,保费:THIRD_DUTY_INSUR_FEE\
     (司机/乘客1万1座)保费:PERS_LIABILITY_INSUR\
     盗抢险:ROBBERY_INSUR.自燃险:AUTOIGNITION_INSUR.\
     玻璃险:GLASS_INSUR.\
     不计免赔特约险:NOT_DEDUCT_INSUR.\
     交强险:STRONG_INSUR.车船税:VESSEL_TAX\
     共计:TOTAL_PREMIUM\
     公司法代账号：6222081202007385758\
     ,包功.工商银行城站支行.\
     如有问题可以直接联系我.\
     电话:10000101\
     （*按揭期间要在我们担保公司投保,\
     不然保证金会将受到影响,谢谢.）
     * @param renewInsuranceParam
     * @return
     */
    @Override
    public String generateSms(RenewInsuranceParam renewInsuranceParam) {
        String tempSms = sysConfig.getTempSms();
        tempSms.replaceAll("CUSTOMER",renewInsuranceParam.getCustomerName());
        tempSms.replaceAll("EMPLOYEE",renewInsuranceParam.getEmployeeName());
        tempSms.replaceAll("DAMAGE_INSUR", BigDecimalUtil.format(renewInsuranceParam.getDamageInsur(),2));
        tempSms.replaceAll("DAMAGE_INSUR_FEE",BigDecimalUtil.format(renewInsuranceParam.getDamageInsurFee(),2));
        tempSms.replaceAll("THIRD_DUTY_INSUR",BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsur(),2));
        tempSms.replaceAll("THIRD_DUTY_INSUR_FEE",BigDecimalUtil.format(renewInsuranceParam.getThirdDutyInsurFee(),2));
        tempSms.replaceAll("PERS_LIABILITY_INSUR",BigDecimalUtil.format(renewInsuranceParam.getPersLiabilityInsur(),2));
        tempSms.replaceAll("GLASS_INSUR",BigDecimalUtil.format(renewInsuranceParam.getGlassInsur(),2));
        tempSms.replaceAll("NOT_DEDUCT_INSUR",BigDecimalUtil.format(renewInsuranceParam.getNotDeductInsur(),2));
        tempSms.replaceAll("STRONG_INSUR",BigDecimalUtil.format(renewInsuranceParam.getStrongInsur(),2));
        tempSms.replaceAll("VESSEL_TAX",BigDecimalUtil.format(renewInsuranceParam.getVesselTax(),2));
        tempSms.replaceAll("TOTAL_PREMIUM",BigDecimalUtil.format(renewInsuranceParam.getTotalPremium(),2));
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
            e.printStackTrace();
        }

//        try {
//
//            if(querys !=null && !querys.isEmpty()){
//
//                List<NameValuePair> pairs = new ArrayList<NameValuePair>(querys.size());
//
//                for (String key :querys.keySet()){
//                    pairs.add(new BasicNameValuePair(key, querys.get(key).toString()));
//                }
//
//                url +="?"+ EntityUtils.toString(new StringEntity(pairs.toString()));
//            }
//
//            HttpGet httpGet = new HttpGet(url);
//            CloseableHttpClient httpclient = HttpClientBuilder.create().build();
//
//            CloseableHttpResponse response = httpclient.execute(httpGet);
//            int statusCode = response.getStatusLine().getStatusCode();
//            if(statusCode !=200){
//                httpGet.abort();
//                throw new RuntimeException("HttpClient,error status code :" + statusCode);
//            }
//        } catch (Exception e) {
//            throw  new BizException(e.getMessage());
//        }
        return ResultBean.ofSuccess(null,"发送成功");
    }


}
