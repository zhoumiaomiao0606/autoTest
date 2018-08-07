package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankDO;
import com.yunche.loan.domain.param.BankParam;
import com.yunche.loan.domain.query.BankQuery;
import com.yunche.loan.domain.vo.BankVO;
import com.yunche.loan.domain.vo.CascadeAreaVO;
import com.yunche.loan.service.BankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    @Autowired
    private BankService bankService;


//    /**
//     * 获取银行列表
//     *
//     * @return
//     */
//    @GetMapping(value = "/list")
//    public ResultBean<List<String>> listAll() {
//        return ResultBean.ofSuccess(BANK_LIST);
//    }

    /**
     * 获取银行列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResultBean<List<String>> listAll() {
        return bankService.listAll();
    }


    /**
     * 获取银行列表
     *
     * @return
     */
    @GetMapping(value = "/lists")
    public ResultBean<List<BankDO>> lists() {
        return ResultBean.ofSuccess(bankService.lists());
    }

    @GetMapping(value = "/areaListByBankName")
    public ResultBean<List<Long>> areaListByBankName(@RequestParam String bankName) {
        return bankService.areaListByBankName(bankName);
    }

    /**
    * @Author: ZhongMingxiao
    * @Param:
    * @return:
    * @Date:
    * @Description:  根据bankName 查询银行关联省市名
    */
    @GetMapping(value = "/areaNameListByBankName")
    public ResultBean<List<CascadeAreaVO>> areaNameListByBankName(@RequestParam String bankName)
    {

        return bankService.areaNameListByBankName(bankName);
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody BankParam bankParam) {
        return bankService.create(bankParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody BankParam bankParam) {
        return bankService.update(bankParam);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return bankService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<BankVO> getById(@RequestParam("id") Long id) {
        return bankService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<BankDO>> query(@RequestBody BankQuery query) {
        return bankService.query(query);
    }
}
