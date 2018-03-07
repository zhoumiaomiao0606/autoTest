package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 业务单
 * <p>
 * Created by zhouguoliang on 2018/2/8.
 */
@CrossOrigin
@RestController
@RequestMapping("/loanorder")
public class LoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(LoanOrderController.class);

    @Autowired
    private LoanOrderService loanOrderService;


    /**
     * 分页查询 各个流程环节的业务流程单列表
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<BaseInstProcessOrderVO>> query(@RequestBody LoanOrderQuery query) {
        return loanOrderService.query(query);
    }

    /**
     * 征信申请单详情  [OK]
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/creditapply/detail")
    public ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.creditApplyOrderDetail(orderId);
    }

    /**
     * 征信申请单 -新建  [OK]
     *
     * @param creditApplyOrderVO
     * @return
     */
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createCreditApplyOrder(@RequestBody CreditApplyOrderVO creditApplyOrderVO) {
        return loanOrderService.createCreditApplyOrder(creditApplyOrderVO);
    }

    /**
     * 征信申请单 -编辑
     *
     * @param creditApplyOrderVO
     * @return
     */
    @PostMapping(value = "/creditapply/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCreditApplyOrder(@RequestBody CreditApplyOrderVO creditApplyOrderVO) {
        return loanOrderService.updateCreditApplyOrder(creditApplyOrderVO);
    }

    /**
     * 征信录入单详情
     *
     * @param orderId 业务单号
     * @param type    征信类型： 1-银行;  2-社会;
     * @return
     */
    @GetMapping(value = "/creditrecord/detail")
    public ResultBean<CreditRecordVO> creditRecordDetail(@RequestParam("orderId") Long orderId,
                                                         @RequestParam("type") Byte type) {
        return loanOrderService.creditRecordDetail(orderId, type);
    }

    /**
     * 征信录入
     *
     * @param creditRecordParam
     * @return
     */
    @PostMapping(value = "/creditrecord/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> creditRecord(@RequestBody CreditRecordParam creditRecordParam) {
        return loanOrderService.creditRecord(creditRecordParam);
    }

    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/loanapply/customer/detail")
    public ResultBean<CustDetailVO> customerDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param custDetailVO
     * @return
     */
    @PostMapping(value = "/loanapply/customer/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody CustDetailVO custDetailVO) {
        return loanOrderService.updateCustomer(custDetailVO);
    }

    /**
     * 主贷人和共贷人切换
     *
     * @param orderId
     * @param principalLenderId
     * @param commonLenderId
     * @return
     */
    @GetMapping(value = "/loanapply/customer/faceoff")
    public ResultBean<Void> faceOff(@RequestParam("orderId") Long orderId,
                                    @RequestParam("principalLenderId") Long principalLenderId,
                                    @RequestParam("commonLenderId") Long commonLenderId) {
        return loanOrderService.faceOff(orderId, principalLenderId, commonLenderId);
    }

    /**
     * 根据订单号获取贷款车辆信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/loanapply/carinfo/detail")
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.loanCarInfoDetail(orderId);
    }

    /**
     * 保存贷款车辆信息 【新增/编辑】
     *
     * @param loanCarInfoParam
     * @return
     */
    @PostMapping(value = "/loanapply/carinfo/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanCarInfo(@RequestBody LoanCarInfoParam loanCarInfoParam) {
        return loanOrderService.createOrUpdateLoanCarInfo(loanCarInfoParam);
    }

    /**
     * 根据订单号获取贷款金融方案详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/loanapply/financialplan/detail")
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.loanFinancialPlanDetail(orderId);
    }

    /**
     * 金融方案计算
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/loanapply/financialplan/calc", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        return loanOrderService.calcLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 保存贷款金融方案 【新增/编辑】
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/loanapply/financialplan/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        return loanOrderService.createOrUpdateLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 根据订单号获取上门家访详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/homevisit/detail")
    public ResultBean<LoanHomeVisitVO> homeVisitDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.homeVisitDetail(orderId);
    }

    /**
     * 保存上门家访资料 【新增/编辑】
     *
     * @param loanHomeVisitParam
     * @return
     */
    @PostMapping(value = "/homevisit/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanHomeVisit(@RequestBody LoanHomeVisitParam loanHomeVisitParam) {
        return loanOrderService.createOrUpdateLoanHomeVisit(loanHomeVisitParam);
    }

    /**
     * 资料增补 -客户证件图片信息
     *
     * @param infoSupplementParam
     * @return
     */
    @PostMapping(value = "/infosupplement", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> infoSupplement(@RequestBody InfoSupplementParam infoSupplementParam) {
        return loanOrderService.infoSupplement(infoSupplementParam);
    }
}


