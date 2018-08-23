package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.AppBusinessInfoVO;
import com.yunche.loan.domain.vo.AppCustomerInfoVO;
import com.yunche.loan.domain.vo.AppInsuranceInfoVO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.AppLoanOrderService;
import org.apache.ibatis.annotations.Param;
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
@RequestMapping("/api/v1/app/loanorder")
public class AppLoanOrderController {

    @Autowired
    private AppLoanOrderService appLoanOrderService;


    /**
     * 征信申请单  -新建（保存主贷人客户时，新建业务单）
     *
     * @param appCustomerParam
     * @return
     */
    @Limiter("/api/v1/app/loanorder/creditapply/create")
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<AppCreditApplyVO> createCreditApplyOrder(@RequestBody AppCustomerParam appCustomerParam) {
        return appLoanOrderService.createCreditApplyOrder(appCustomerParam);
    }


    /**
     * 众安接口
     */
    @PostMapping(value = "/zhonganquery")
    public ResultBean zhongAnQuery(@RequestBody ZhongAnQueryParam zhongAnQueryParam) {
        appLoanOrderService.zhongAnQuery(zhongAnQueryParam);
        return ResultBean.ofSuccess(null, "查询成功");
    }

    /**
     * 获取团队及业务员
     */
    @GetMapping(value = "/zhonganname")
    public ResultBean zhonganName(@RequestParam("orderid") String orderid) {
        return ResultBean.ofSuccess(appLoanOrderService.zhonganName(Long.valueOf(orderid)));
    }

    /**
     * 众安接口详情
     */
    @GetMapping(value = "/zhongandetail")
    // public ResultBean zhongAnDetail(@RequestParam (value = "idcard",required = false) String idcard,@RequestParam (value = "customername",required = false) String customername){
    public ResultBean zhongAnDetail(@RequestParam(value = "orderid", required = false) String orderId) {
        return ResultBean.ofSuccess(appLoanOrderService.zhongAnDetail(Long.valueOf(orderId)));

    }
    /**
     * 众安异步
     */
    @GetMapping(value = "/zhonganinsert")
    public ResultBean zhonganInsert(){
        appLoanOrderService.zhonganInsert();
        return ResultBean.ofSuccess(null,"插入成功");
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
    @Limiter("/api/v1/app/loanorder/baseinfo/create")
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
    public ResultBean<Void> updateBaseInfo(@RequestBody AppLoanBaseInfoDetailParam param) {
        return appLoanOrderService.updateBaseInfo(param);
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
    @Limiter("/api/v1/app/loanorder/customer/addrela")
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
    @GetMapping(value = "/customer/delrela")
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

    /**
     * 保存贷款车辆信息 -新增
     *
     * @param appLoanCarInfoParam
     * @return
     */
    @Limiter("/api/v1/app/loanorder/carinfo/create")
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
    @Limiter("/api/v1/app/loanorder/financialplan/create")
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
    @Limiter("/api/v1/app/loanorder/homevisit/save")
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
    @PostMapping(value = "/infosupplement/upload", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> infoSupplementUpload(@RequestBody InfoSupplementParam param) {
        return appLoanOrderService.infoSupplementUpload(param);
    }

    /**
     * 资料增补详情页
     *
     * @param supplementOrderId
     * @return
     */
    @GetMapping(value = "/infosupplement/detail")
    public ResultBean<AppInfoSupplementVO> infoSupplementDetail(@RequestParam Long supplementOrderId) {
        return appLoanOrderService.infoSupplementDetail(supplementOrderId);
    }

    /**
     * APP端 -客户信息查询 -基本信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/customerInfo")
    public ResultBean<AppCustomerInfoVO> customerInfo(@RequestParam Long orderId) {
        return appLoanOrderService.customerInfo(orderId);
    }

    /**
     * APP端 -客户信息查询 -业务信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/businessInfo")
    public ResultBean<AppBusinessInfoVO> businessInfo(@RequestParam Long orderId) {
        return appLoanOrderService.businessInfo(orderId);
    }

    /**
     * APP端 -客户信息查询 -保险信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/insuranceInfo")
    public ResultBean<List<AppInsuranceInfoVO>> insuranceInfo(@RequestParam Long orderId) {
        return appLoanOrderService.insuranceInfo(orderId);
    }

    /**
     * 订单任务进度
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/process")
    public ResultBean<AppOrderProcessVO> orderProcess(@RequestParam Long orderId) {
        return appLoanOrderService.orderProcess(orderId);
    }
}
