package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.TelephoneVerifyVO;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.domain.query.LoanOrderQuery;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.service.LoanOrderService;
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

    @Autowired
    private LoanOrderService loanOrderService;


    /**
     * 分页查询 各个流程环节的业务流程单列表   -单节点列表查询
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<LoanOrderVO>> query(@RequestBody LoanOrderQuery query) {
        return loanOrderService.query(query);
    }

    /**
     * 多节点列表查询
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/multipartQuery", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<LoanOrderVO>> multipartQuery(@RequestBody LoanOrderQuery query) {
        return loanOrderService.multipartQuery(query);
    }

    /**
     * 征信申请单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/creditapply/detail")
    public ResultBean<CreditApplyOrderVO> creditApplyOrderDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.creditApplyOrderDetail(orderId);
    }

    /**
     * 征信申请单 -新建
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/creditapply/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<String> createCreditApplyOrder(@RequestBody CreditApplyOrderParam param) {
        return loanOrderService.createCreditApplyOrder(param);
    }

    /**
     * 征信申请单 -编辑
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/creditapply/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCreditApplyOrder(@RequestBody CreditApplyOrderParam param) {
        return loanOrderService.updateCreditApplyOrder(param);
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
    @PostMapping(value = "/creditrecord/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createCreditRecord(@RequestBody CreditRecordParam creditRecordParam) {
        return loanOrderService.createCreditRecord(creditRecordParam);
    }

    /**
     * 征信编辑
     *
     * @param creditRecordParam
     * @return
     */
    @PostMapping(value = "/creditrecord/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> updateCreditRecord(@RequestBody CreditRecordParam creditRecordParam) {
        return loanOrderService.updateCreditRecord(creditRecordParam);
    }

    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/customer/detail")
    public ResultBean<CustDetailVO> customerDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param allCustDetailParam
     * @return
     */
    @PostMapping(value = "/customer/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody AllCustDetailParam allCustDetailParam) {
        return loanOrderService.updateCustomer(allCustDetailParam);
    }

    /**
     * 增加关联人（共贷人/担保人/紧急联系人）
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/customer/addrela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> addRelaCustomer(@RequestBody CustomerParam param) {
        return loanOrderService.addRelaCustomer(param);
    }

    /**
     * 删除关联人（共贷人/担保人/紧急联系人）
     *
     * @param customerId
     * @return
     */
    @GetMapping(value = "/customer/delrela")
    public ResultBean<Long> delRelaCustomer(@RequestParam("customerId") Long customerId) {
        return loanOrderService.delRelaCustomer(customerId);
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
        return loanOrderService.faceOff(orderId, principalLenderId, commonLenderId);
    }

    /**
     * 保存贷款车辆信息 -新增
     *
     * @param loanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createLoanCarInfo(@RequestBody LoanCarInfoParam loanCarInfoParam) {
        return loanOrderService.createLoanCarInfo(loanCarInfoParam);
    }

    /**
     * 贷款车辆信息 -编辑
     *
     * @param loanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanCarInfo(@RequestBody LoanCarInfoParam loanCarInfoParam) {
        return loanOrderService.updateLoanCarInfo(loanCarInfoParam);
    }

    /**
     * 根据订单号获取贷款车辆信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/carinfo/detail")
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.loanCarInfoDetail(orderId);
    }

    /**
     * 根据订单号获取贷款金融方案详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/financialplan/detail")
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(@RequestParam("orderId") Long orderId) {
        return loanOrderService.loanFinancialPlanDetail(orderId);
    }

    /**
     * 金融方案计算
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/financialplan/calc", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        return loanOrderService.calcLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 保存贷款金融方案 【新增/编辑】
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/financialplan/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> saveLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
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
    public ResultBean<Long> saveLoanHomeVisit(@RequestBody LoanHomeVisitParam loanHomeVisitParam) {
        return loanOrderService.createOrUpdateLoanHomeVisit(loanHomeVisitParam);
    }

    /**
     * 资料增补 -客户证件图片信息
     *
     * @param infoSupplementParam
     * @return
     */
    @PostMapping(value = "/infosupplement/upload", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> infoSupplementUpload(@RequestBody InfoSupplementParam infoSupplementParam) {
        return loanOrderService.infoSupplementUpload(infoSupplementParam);
    }

    /**
     * 资料增补详情页
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/infosupplement/detail")
    public ResultBean<InfoSupplementVO> infoSupplementDetail(@RequestParam Long orderId) {
        return loanOrderService.infoSupplementDetail(orderId);
    }

    /**
     * 上门调查   -主贷客户信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/info/simple")
    public ResultBean<LoanSimpleInfoVO> simpleInfo(@RequestParam Long orderId) {
        return loanOrderService.simpleInfo(orderId);
    }

    /**
     * 上门调查   -贷款业务详细信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/customerinfo/simple")
    public ResultBean<List<LoanSimpleCustomerInfoVO>> simpleCustomerInfo(@RequestParam Long orderId) {
        return loanOrderService.simpleCustomerInfo(orderId);
    }

//    /**
//     * 电审页详情展示
//     *
//     * @param orderId
//     * @return
//     */
//    @GetMapping(value = "/telephoneverify/detail")
//    public ResultBean<TelephoneVerifyVO> telephoneVerifyDetail(@RequestParam Long orderId) {
//        return loanOrderService.telephoneVerifyDetail(orderId);
//    }
}


