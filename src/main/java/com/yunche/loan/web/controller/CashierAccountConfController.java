package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CashierAccountConfParam;
import com.yunche.loan.domain.param.QueryCashierAccountConfParam;
import com.yunche.loan.service.CashierAccountConfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/cashierAccountConf")
public class CashierAccountConfController
{

    private CashierAccountConfService cashierAccountConfService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody CashierAccountConfParam cashierAccountConfParam)
    {
        return cashierAccountConfService.create(cashierAccountConfParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody  CashierAccountConfParam cashierAccountConfParam)
    {
        return cashierAccountConfService.update(cashierAccountConfParam);
    }

    @PostMapping(value = "/listAll", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean listAll(@RequestBody QueryCashierAccountConfParam queryCashierAccountConfParam)
    {
        return cashierAccountConfService.listAll(queryCashierAccountConfParam);
    }

    @GetMapping(value = "/listAllEmployName")
    public ResultBean listAllEmployName()
    {

        return cashierAccountConfService.listAllEmployName();
    }

    @GetMapping(value = "/listAllCAConfByEmployeeId")
    public ResultBean listAllCashierAccountConfByEmployeeId(@RequestParam("employeeId") Long employeeId)
    {
        return cashierAccountConfService.listAllCashierAccountConfByEmployeeId(employeeId);
    }

    @GetMapping(value = "/listAllCreateUserName")
    public ResultBean listAllCreateUserName()
    {
        return cashierAccountConfService.listAllCreateUserName();
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id)
    {
        return cashierAccountConfService.delete(id);
    }
}
