package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.AppLoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.AppLoanOrderService;
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
     * 征信申请单  -新建（保存主贷人客户时，新建业务单）
     *
     * @param appCustomerParam
     * @return
     */
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<AppCreditApplyVO> createCreditApplyOrder(@RequestBody AppCustomerParam appCustomerParam) {
        return appLoanOrderService.createCreditApplyOrder(appCustomerParam);
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
     * 创建贷款基本信息
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/baseinfo/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createBaseInfo(@RequestBody AppLoanBaseInfoParam param) {
        return appLoanOrderService.createBaseInfo(param);
    }

    /**
     * 编辑贷款基本信息
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/baseinfo/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateBaseInfo(@RequestBody AppLoanBaseInfoParam param) {
        return appLoanOrderService.updateBaseInfo(param);
    }

    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/customer/detail")
    public ResultBean<AppCustDetailVO> customerDetail(@RequestParam("orderId") Long orderId) {
        return appLoanOrderService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/customer/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody AppCustomerParam param) {
        return appLoanOrderService.updateCustomer(param);
    }

    /**
     * 增加关联人（共贷人/担保人/紧急联系人）
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/customer/addrela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> addRelaCustomer(@RequestBody AppCustomerParam param) {
        return appLoanOrderService.addRelaCustomer(param);
    }

    /**
     * 删除关联人（共贷人/担保人/紧急联系人）
     *
     * @param customerId
     * @return
     */
    @PostMapping(value = "/customer/delrela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> delRelaCustomer(@RequestParam("customerId") Long customerId) {
        return appLoanOrderService.delRelaCustomer(customerId);
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


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


//    /**
//     * 征信录入单详情
//     *
//     * @param orderId 业务单号
//     * @param type    征信类型： 1-银行;  2-社会;
//     * @return
//     */
//    @GetMapping(value = "/creditrecord/detail")
//    public ResultBean<AppCreditRecordVO> creditRecordDetail(@RequestParam("orderId") Long orderId,
//                                                            @RequestParam("type") Byte type) {
//        return appLoanOrderService.creditRecordDetail(orderId, type);
//    }
//
//    /**
//     * 征信录入
//     *
//     * @param creditRecordParam
//     * @return
//     */
//    @PostMapping(value = "/creditrecord", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResultBean<Void> creditRecord(@RequestBody CreditRecordParam creditRecordParam) {
//        return appLoanOrderService.creditRecord(creditRecordParam);
//    }

    /**
     * 保存贷款车辆信息 -新增
     *
     * @param appLoanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createLoanCarInfo(@RequestBody AppLoanCarInfoParam appLoanCarInfoParam) {
        return appLoanOrderService.createLoanCarInfo(appLoanCarInfoParam);
    }

    /**
     * 贷款车辆信息 -编辑
     *
     * @param appLoanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanCarInfo(@RequestBody AppLoanCarInfoParam appLoanCarInfoParam) {
        return appLoanOrderService.updateLoanCarInfo(appLoanCarInfoParam);
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
     * 金融方案计算
     *
     * @param param
     * @return
     */
    @PostMapping("/financialplan/calc")
    public ResultBean<AppLoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody AppLoanFinancialPlanParam param) {
        return appLoanOrderService.calcLoanFinancialPlan(param);
    }

    /**
     * 贷款金融方案  -新增
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/financialplan/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createLoanFinancialPlan(@RequestBody AppLoanFinancialPlanParam param) {
        return appLoanOrderService.createLoanFinancialPlan(param);
    }

    /**
     * 贷款金融方案  -编辑
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/financialplan/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateLoanFinancialPlan(@RequestBody AppLoanFinancialPlanParam param) {
        return appLoanOrderService.updateLoanFinancialPlan(param);
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
     * @param param
     * @return
     */
    @PostMapping(value = "/homevisit/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanHomeVisit(@RequestBody AppLoanHomeVisitParam param) {
        return appLoanOrderService.createOrUpdateLoanHomeVisit(param);
    }

    /**
     * 资料增补 -客户证件图片信息
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/infosupplement", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> infoSupplement(@RequestBody AppInfoSupplementParam param) {
        return appLoanOrderService.infoSupplement(param);
    }
}
