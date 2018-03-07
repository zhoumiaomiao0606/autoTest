package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.AppLoanOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/5
 */
@CrossOrigin
@RestController
@RequestMapping("/app/loanorder")
public class AppLoanOrderController {

    private static final Logger logger = LoggerFactory.getLogger(AppLoanOrderController.class);

    @Autowired
    private AppLoanOrderService appLoanOrderService;


    /**
     * 分页查询 各个流程环节的业务流程单列表
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppLoanProcessOrderVO>> query(@RequestBody AppLoanOrderQuery query) {
        return appLoanOrderService.query(query);
    }

    /**
     * 征信申请单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/creditapply/detail")
    public ResultBean<AppCreditApplyOrderVO> creditApplyOrderDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.creditApplyOrderDetail(orderId);
    }

    /**
     * 征信申请单新建
     *
     * @param creditApplyOrderVO
     * @return
     */
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<String> createCreditApplyOrder(@RequestBody CreditApplyOrderVO creditApplyOrderVO) {
        return appLoanOrderService.createCreditApplyOrder(creditApplyOrderVO);
    }

    /**
     * 编辑征信申请单
     *
     * @param appCreditApplyOrderVO
     * @return
     */
    @PostMapping(value = "/creditapply/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCreditApplyOrder(AppCreditApplyOrderVO appCreditApplyOrderVO) {
        return appLoanOrderService.updateCreditApplyOrder(appCreditApplyOrderVO);
    }

    /**
     * 征信录入单详情
     *
     * @param orderId 业务单号
     * @param type    征信类型： 1-银行;  2-社会;
     * @return
     */
    @GetMapping(value = "/creditrecord/detail")
    public ResultBean<AppCreditRecordVO> creditRecordDetail(@RequestParam("orderId") Long orderId,
                                                            @RequestParam("type") Byte type) {
        return appLoanOrderService.creditRecordDetail(orderId, type);
    }

    /**
     * 征信录入
     *
     * @param creditRecordParam
     * @return
     */
    @PostMapping(value = "/creditrecord", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> creditRecord(@RequestBody CreditRecordParam creditRecordParam) {
        return appLoanOrderService.creditRecord(creditRecordParam);
    }

    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/customer/detail")
    public ResultBean<CustDetailVO> customerDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param custDetailVO
     * @return
     */
    @PostMapping(value = "/customer/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody CustDetailVO custDetailVO) {
        return appLoanOrderService.updateCustomer(custDetailVO);
    }

    /**
     * 主贷人和共贷人切换
     *
     * @param orderId
     * @param principalLenderId
     * @param commonLenderId
     * @return
     */
    @GetMapping(value = "/customer/faceoff")
    public ResultBean<Void> faceOff(@RequestParam("orderId") Long orderId,
                                    @RequestParam("principalLenderId") Long principalLenderId,
                                    @RequestParam("commonLenderId") Long commonLenderId) {
        return appLoanOrderService.faceOff(orderId, principalLenderId, commonLenderId);
    }

    /**
     * 根据订单号获取贷款车辆信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/carinfo/detail")
    public ResultBean<AppLoanCarInfoVO> loanCarInfoDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.loanCarInfoDetail(orderId);
    }

    /**
     * 保存贷款车辆信息 【新增/编辑】
     *
     * @param appLoanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanCarInfo(@RequestBody AppLoanCarInfoParam appLoanCarInfoParam) {
        return appLoanOrderService.createOrUpdateLoanCarInfo(appLoanCarInfoParam);
    }

    /**
     * 根据订单号获取贷款金融方案详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/financialplan/detail")
    public ResultBean<AppLoanFinancialPlanVO> loanFinancialPlanDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.loanFinancialPlanDetail(orderId);
    }

    /**
     * 金融方案计算
     *
     * @param appLoanFinancialPlanParam
     * @return
     */
    @PostMapping("/")
    public ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody AppLoanFinancialPlanParam appLoanFinancialPlanParam) {
        return appLoanOrderService.calcLoanFinancialPlan(appLoanFinancialPlanParam);
    }

    /**
     * 保存贷款金融方案 【新增/编辑】
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/financialplan/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanFinancialPlan(@RequestBody AppLoanFinancialPlanParam loanFinancialPlanParam) {
        return appLoanOrderService.createOrUpdateLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 根据订单号获取上门家访详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/homevisit/detail")
    public ResultBean<AppLoanHomeVisitVO> homeVisitDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.homeVisitDetail(orderId);
    }

    /**
     * 保存上门家访资料 【新增/编辑】
     *
     * @param loanHomeVisitParam
     * @return
     */
    @PostMapping(value = "/homevisit/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanHomeVisit(@RequestBody AppLoanHomeVisitParam loanHomeVisitParam) {
        return appLoanOrderService.createOrUpdateLoanHomeVisit(loanHomeVisitParam);
    }

    /**
     * 资料增补 -客户证件图片信息
     *
     * @param infoSupplementParam
     * @return
     */
    @PostMapping(value = "/infosupplement", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> infoSupplement(@RequestBody AppInfoSupplementParam infoSupplementParam) {
        return appLoanOrderService.infoSupplement(infoSupplementParam);
    }
}
