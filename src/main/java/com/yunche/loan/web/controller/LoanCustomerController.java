package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.AllCustDetailParam;
import com.yunche.loan.domain.param.CustomerParam;
import com.yunche.loan.domain.vo.CustDetailVO;
import com.yunche.loan.domain.vo.LoanRepeatVO;
import com.yunche.loan.service.LoanCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/4/4
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/customer")
public class LoanCustomerController {

    @Autowired
    private LoanCustomerService loanCustomerService;


    /**
     * 获取贷款客户信息详情(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param orderId 业务单ID
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<CustDetailVO> customerDetail(@RequestParam("orderId") Long orderId) {
        return loanCustomerService.customerDetail(orderId);
    }

    /**
     * 贷款客户信息编辑
     *
     * @param allCustDetailParam
     * @return
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateCustomer(@RequestBody AllCustDetailParam allCustDetailParam) {
        return loanCustomerService.updateCustomer(allCustDetailParam);
    }

    /**
     * 增加关联人（共贷人/担保人/紧急联系人）
     *
     * @param param
     * @return
     */
    @PostMapping(value = "/addrela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> addRelaCustomer(@RequestBody CustomerParam param) {
        return loanCustomerService.addRelaCustomer(param);
    }

    /**
     * 删除关联人（共贷人/担保人/紧急联系人）
     *
     * @param customerId
     * @return
     */
    @GetMapping(value = "/customer/delrela")
    public ResultBean<Long> delRelaCustomer(@RequestParam("customerId") Long customerId) {
        return loanCustomerService.delRelaCustomer(customerId);
    }

    /**
     * 主贷人和共贷人切换
     *
     * @param orderId
     * @param principalLenderId
     * @param commonLenderId
     * @return
     */
    @GetMapping(value = "/faceoff")
    public ResultBean<Void> faceOff(@RequestParam("orderId") Long orderId,
                                    @RequestParam("principalLenderId") Long principalLenderId,
                                    @RequestParam("commonLenderId") Long commonLenderId) {
        return loanCustomerService.faceOff(orderId, principalLenderId, commonLenderId);
    }

    /**
     * 客户重复贷款校验
     *
     * @param idCard
     * @param orderId
     * @return
     */
    @GetMapping(value = "/checkRepeat")
    public ResultBean<LoanRepeatVO> checkRepeat(@RequestParam String idCard,
                                                @RequestParam(required = false) Long orderId) {
        return loanCustomerService.checkRepeat(idCard, orderId);
    }
}
