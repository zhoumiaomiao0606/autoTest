package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CustDetailParam;
import com.yunche.loan.domain.vo.CustBaseInfoVO;
import com.yunche.loan.domain.vo.CustRelaPersonInfoVO;
import com.yunche.loan.domain.vo.CustomerVO;
import com.yunche.loan.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 客户信息（主贷人/共贷人/担保人/紧急联系人）
 * Created by zhouguoliang on 2018/2/12.
 */
@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;


    /**
     * 创建主贷人信息
     *
     * @param custBaseInfoVO
     * @return
     */
    @PostMapping(value = "/createMain", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createMain(@RequestBody CustBaseInfoVO custBaseInfoVO) {
        return customerService.createMainCust(custBaseInfoVO);
    }

    /**
     * 更新主贷人信息
     *
     * @param custBaseInfoVO
     * @return
     */
    @PostMapping(value = "/updateMain", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> updateMain(@RequestBody CustBaseInfoVO custBaseInfoVO) {
        return customerService.updateMainCust(custBaseInfoVO);
    }

    /**
     * 创建关联人信息
     *
     * @param custRelaPersonInfoVO
     * @return
     */
    @PostMapping(value = "/createRela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> createMain(@RequestBody CustRelaPersonInfoVO custRelaPersonInfoVO) {
        return customerService.createRelaCust(custRelaPersonInfoVO);
    }

    /**
     * 更新关联人信息
     *
     * @param custRelaPersonInfoVO
     * @return
     */
    @PostMapping(value = "/updateRela", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> updateMain(@RequestBody CustRelaPersonInfoVO custRelaPersonInfoVO) {
        return customerService.updateRelaCust(custRelaPersonInfoVO);
    }

    /**
     * 删除关联人
     *
     * @param custId
     * @return
     */
    @GetMapping(value = "/deleteRelaCust")
    public ResultBean<Void> deleteRelaCust(@RequestParam("custId") Long custId) {
        return customerService.deleteRelaCust(custId);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 创建客户信息 -单个
     *
     * @param orderId
     * @param customerVO
     * @return
     */
    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestParam String orderId,
                                   @RequestBody CustomerVO customerVO) {
        return customerService.create(orderId, customerVO);
    }

    /**
     * 通过ID查询
     *
     * @param id
     * @return
     */
    @GetMapping(value = "/getById")
    public ResultBean<CustomerVO> getById(@RequestParam Long id) {
        return customerService.getById(id);
    }

    /**
     * 编辑客户信息 -单个
     *
     * @param customerVO
     * @return
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> update(@RequestBody CustomerVO customerVO) {
        return customerService.update(customerVO);
    }



    /**
     * 编辑客户信息(主贷人/共贷人/担保人/紧急联系人)
     *
     * @param custDetailParam
     * @return
     */
    @PostMapping(value = "/all/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> updateAll(@RequestBody CustDetailParam custDetailParam) {
        return customerService.updateAll(custDetailParam);
    }

    /**
     * 主贷人和共贷人切换
     *
     * @param orderId
     * @param principalLenderId
     * @param commonLenderId
     * @return
     */
    @GetMapping(value = "/faceOff")
    public ResultBean<Void> faceOff(@RequestParam("orderId") String orderId,
                                    @RequestParam("principalLenderId") Long principalLenderId,
                                    @RequestParam("commonLenderId") Long commonLenderId) {
        return customerService.faceOff(orderId, principalLenderId, commonLenderId);
    }
}


