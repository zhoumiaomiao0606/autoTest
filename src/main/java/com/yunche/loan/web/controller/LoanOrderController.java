package com.yunche.loan.web.controller;

import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.result.ResultBean;
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
@RequestMapping(value = "/api/v1/loanorder")
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
    @Limiter(route = "/api/v1/loanorder/creditapply/create")
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
    @Limiter(route = "/api/v1/loanorder/creditrecord/create")
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
     * 保存贷款车辆信息 -新增
     *
     * @param loanCarInfoParam
     * @return
     */
    @Limiter(route = "/api/v1/loanorder/carinfo/create")
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
    @Limiter(route = "/api/v1/loanorder/homevisit/save")
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
     * @param supplementOrderId
     * @return
     */
    @GetMapping(value = "/infosupplement/detail")
    public ResultBean<InfoSupplementVO> infoSupplementDetail(@RequestParam Long supplementOrderId) {
        return loanOrderService.infoSupplementDetail(supplementOrderId);
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


