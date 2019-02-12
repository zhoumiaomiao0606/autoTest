package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CreditApplyOrderParam;
import com.yunche.loan.domain.param.CreditRecordParam;
import com.yunche.loan.domain.param.LoanCarInfoParam;
import com.yunche.loan.domain.param.LoanHomeVisitParam;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
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
@RequestMapping(value = "/api/v1/loanorder")
public class LoanOrderController {

    @Autowired
    private LoanOrderService loanOrderService;


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
    @Limiter
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
     * 征信录入单详情
     *
     * @param orderId 业务单号
     * @return
     */
    @GetMapping(value = "/creditrecord/newDetail")
    public ResultBean<RecombinationVO> newCreditRecordDetail(@RequestParam("orderId") Long orderId) {
        return ResultBean.ofSuccess(loanOrderService.newCreditRecordDetail(orderId));
    }

    /**
     * 征信录入
     *
     * @param creditRecordParam
     * @return
     */
    @Limiter
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
     * 银行征信图片导出
     *
     * @param loanCreditExportQuery
     * @return
     */
    @PostMapping(value = "/creditrecord/downreport", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean createCreditDownreport(@RequestBody LoanCreditExportQuery loanCreditExportQuery) {
        return loanOrderService.createCreditDownreport(loanCreditExportQuery);
    }

    /**
     * 银行征信图片压缩包检测
     *
     * @return
     */
    @GetMapping("/creditrecord/picCheck")
    public ResultBean picCheck() {
        return loanOrderService.picCheck();
    }

    /**
     * 保存贷款车辆信息 -新增
     *
     * @param loanCarInfoParam
     * @return
     */
    @Limiter
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
    @Limiter
    @PostMapping(value = "/homevisit/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> saveLoanHomeVisit(@RequestBody LoanHomeVisitParam loanHomeVisitParam) {
        return loanOrderService.createOrUpdateLoanHomeVisit(loanHomeVisitParam);
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
}


