package com.yunche.loan.domain.vo;

import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OrderByCustomerIdVO
{

            private String leaderName;//领导人姓名
            private String partnerId;//合伙人id

            private String overdueMoney;//逾期金额

            private String remitAmount;//打款金额

            private String customerName;//主贷人姓名
            private String cardId;//身份证号
            private String phone;//电话
            private String loanBank;//贷款银行
            private String salesman;//业务员
            private String salesTeam;//业务团队
            private String loanAmount;//贷款金额
            private String bankInstallmentCapital;//银行分期本金
            private String carModel;//车型
            private String carPrice;//车价
            private Long num;//业务编号
            private String address;//现住地址
            private String cardAddress;//身份证地址
            private String company;//单位名称
            private String companyAddress;//单位名称
            private String companyPhone;//单位电话
            private String alternatePhone;//备用电话

            private String businessArea;//业务区域
            private String businessSource;//业务来源
            private String financialProductName;//金融产品名称
            private String loanPeriods;//贷款期数
            private String contractRate;//签约利率
            private String payments;//首付额
            private String payProportion;//首付比例
            private String bankBoundage;//银行手续费
            private String reimbursementAmount;//还款总额
            private String firstPayment;//首月还款
            private String monthlyPayments;//每月还款
            private String driverLicense;//行驶证车主
            private String carPrpperty;//车辆属性
            private String carType;//车辆类型
            private String carColor;//颜色
            private String registrationWay;//上牌方式
            private String registration;//上牌地
            private String engineNo;//发动机号
            private String registrationCertificateNo;//登记证书号
            private String vin;//车架号
            private String carbrand;//品牌
            /*private String overdueNumber;//逾期次数
            private String overdueAmount;//逾期金额*/

            private List<PartnerCompensations> partnerCompensationsList =new ArrayList<>();
         /* List<LoanApplyCompensationDO> loanApplyCompensationDOS;*/

}
