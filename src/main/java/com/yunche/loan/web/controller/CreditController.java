package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.dataObj.LoanBaseInfoDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.domain.viewObj.LoanBaseInfoVO;
import com.yunche.loan.service.CreditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 征信申请
 *
 * @author liuzhe
 * @date 2018/2/26
 */
@CrossOrigin
@RestController
@RequestMapping("/credit")
public class CreditController {

    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    private CreditService creditService;


    /**
     * 保存-贷款基本信息
     *
     * @param orderId        业务单ID
     * @param loanBaseInfoDO
     * @return
     */
    @PostMapping(value = "/loanBaseInfo/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createLoanBaseInfo(@RequestParam("orderId") String orderId,
                                               @RequestBody LoanBaseInfoDO loanBaseInfoDO) {
        return creditService.createLoanBaseInfo(orderId, loanBaseInfoDO);
    }

    /**
     * 编辑-贷款基本信息
     *
     * @param loanBaseInfoDO
     * @return
     */
    @PostMapping(value = "/loanBaseInfo/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateLoanBaseInfo(@RequestBody LoanBaseInfoDO loanBaseInfoDO) {
        return creditService.updateLoanBaseInfo(loanBaseInfoDO);
    }

    /**
     * 编辑-贷款基本信息
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/loanBaseInfo/getById")
    public ResultBean<LoanBaseInfoVO> getLoanBaseInfoById(@RequestParam Long id) {
        return creditService.getLoanBaseInfoById(id);
    }

    /**
     * 征信申请单 -分页查询
     * <p>
     * 未提交 ==> 暂未关联 process_inst_id
     *
     * @param query
     * @return
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<InstLoanOrderVO>> query(@RequestBody OrderListQuery query) {
        return creditService.query(query);
    }
}
