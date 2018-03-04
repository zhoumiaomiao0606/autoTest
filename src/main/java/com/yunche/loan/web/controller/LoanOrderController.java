package com.yunche.loan.web.controller;

import com.alibaba.fastjson.JSON;
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
        logger.info("/loanorder/query", JSON.toJSONString(query));
        return loanOrderService.query(query);
    }

    /**
     * 征信申请单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/creditapply/detail")
    public ResultBean<InstProcessOrderVO> detail(@RequestParam("orderId") String orderId) {
        logger.info("/loanorder/creditapply/detail", JSON.toJSONString(orderId));
        return loanOrderService.creditApplyDetail(orderId);
    }


    /**
     * 征信录入单详情
     *
     * @param orderId 业务单号
     * @param type    征信类型： 1-银行;  2-社会;
     * @return
     */
    @GetMapping(value = "/creditrecord/detail")
    public ResultBean<CreditRecordVO> creditRecordDetail(@RequestParam("orderId") String orderId,
                                                         @RequestParam("type") Byte type) {
        logger.info("/loanorder/creditrecord/detail", JSON.toJSONString(orderId), JSON.toJSONString(type));
        return loanOrderService.creditRecordDetail(orderId, type);
    }

    /**
     * 征信录入
     *
     * @param creditRecordParam
     * @return
     */
    @PostMapping(value = "/creditrecord", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> creditRecord(@RequestBody CreditRecordParam creditRecordParam) {
        logger.info("/loanorder/creditrecord", JSON.toJSONString(creditRecordParam));
        return loanOrderService.creditRecord(creditRecordParam);
    }

    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/customer/detail")
    public ResultBean<CustDetailVO> customerDetail(@RequestParam("orderId") String orderId) {
        logger.info("/loanorder/customer/detail", JSON.toJSONString(orderId));
        return loanOrderService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param custDetailVO
     * @return
     */
    @PostMapping(value = "/customer/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody CustDetailVO custDetailVO) {
        logger.info("/loanorder/customer/update", JSON.toJSONString(custDetailVO));
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
    @GetMapping(value = "/customer/faceoff")
    public ResultBean<Void> faceOff(@RequestParam("orderId") String orderId,
                                    @RequestParam("principalLenderId") Long principalLenderId,
                                    @RequestParam("commonLenderId") Long commonLenderId) {
        logger.info("/loanorder/customer/faceoff", JSON.toJSONString(orderId), JSON.toJSONString(principalLenderId), JSON.toJSONString(commonLenderId));
        return loanOrderService.faceOff(orderId, principalLenderId, commonLenderId);
    }

    /**
     * 根据订单号获取贷款车辆信息
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/carinfo/detail")
    public ResultBean<LoanCarInfoVO> loanCarInfoDetail(@RequestParam("orderId") String orderId) {
        logger.info("/loanorder/carinfo/detail", JSON.toJSONString(orderId));
        return loanOrderService.loanCarInfoDetail(orderId);
    }

    /**
     * 保存贷款车辆信息 【新增/编辑】
     *
     * @param loanCarInfoParam
     * @return
     */
    @PostMapping(value = "/carinfo/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanCarInfo(@RequestBody LoanCarInfoParam loanCarInfoParam) {
        logger.info("/loanorder/carinfo/save", JSON.toJSONString(loanCarInfoParam));
        return loanOrderService.createOrUpdateLoanCarInfo(loanCarInfoParam);
    }

    /**
     * 根据订单号获取贷款金融方案详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/financialplan/detail")
    public ResultBean<LoanFinancialPlanVO> loanFinancialPlanDetail(@RequestParam("orderId") String orderId) {
        logger.info("/loanorder/financialplan/detail", JSON.toJSONString(orderId));
        return loanOrderService.loanFinancialPlanDetail(orderId);
    }

    /**
     * 金融方案计算
     *
     * @param loanFinancialPlanParam
     * @return
     */
    public ResultBean<LoanFinancialPlanVO> calcLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        logger.info("/loanorder/financialplan/calc", JSON.toJSONString(loanFinancialPlanParam));
        return loanOrderService.calcLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 保存贷款金融方案 【新增/编辑】
     *
     * @param loanFinancialPlanParam
     * @return
     */
    @PostMapping(value = "/financialplan/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> saveLoanFinancialPlan(@RequestBody LoanFinancialPlanParam loanFinancialPlanParam) {
        logger.info("/loanorder/financialplan/save", JSON.toJSONString(loanFinancialPlanParam));
        return loanOrderService.createOrUpdateLoanFinancialPlan(loanFinancialPlanParam);
    }

    /**
     * 根据订单号获取上门家访详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/homevisit/detail")
    public ResultBean<LoanHomeVisitVO> homeVisitDetail(@RequestParam("orderId") String orderId) {
        logger.info("/loanorder/homevisit/detail", JSON.toJSONString(orderId));
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
        logger.info("/loanorder/homevisit/save", JSON.toJSONString(loanHomeVisitParam));
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
        logger.info("/loanorder/infosupplement", JSON.toJSONString(infoSupplementParam));
        return loanOrderService.infoSupplement(infoSupplementParam);
    }
}


