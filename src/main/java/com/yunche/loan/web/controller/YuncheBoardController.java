package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CashierAccountConfParam;
import com.yunche.loan.domain.param.QueryCashierAccountConfParam;
import com.yunche.loan.domain.param.YuncheBoardParam;
import com.yunche.loan.service.CashierAccountConfService;
import com.yunche.loan.service.YuncheBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/yuncheBoard")
public class YuncheBoardController
{

    @Autowired
    private YuncheBoardService yuncheBoardService;

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody YuncheBoardParam yuncheBoardParam)
    {
        return yuncheBoardService.create(yuncheBoardParam);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody  YuncheBoardParam yuncheBoardParam)
    {
        return yuncheBoardService.update(yuncheBoardParam);
    }

    @PostMapping(value = "/listAll", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean listAll(@RequestBody YuncheBoardParam yuncheBoardParam)
    {
        return yuncheBoardService.listAll(yuncheBoardParam);
    }

    /*@GetMapping(value = "/listAllEmployName")
    public ResultBean listAllEmployName()
    {

        return yuncheBoardService.listAllEmployName();
    }

    @GetMapping(value = "/listAllCAConfByEmployeeId")
    public ResultBean listAllCashierAccountConfByEmployeeId(@RequestParam("employeeId") Long employeeId)
    {
        return yuncheBoardService.listAllCashierAccountConfByEmployeeId(employeeId);
    }

    @GetMapping(value = "/listAllCreateUserName")
    public ResultBean listAllCreateUserName()
    {
        return yuncheBoardService.listAllCreateUserName();
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id)
    {
        return yuncheBoardService.delete(id);
    }*/
    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Integer id)
    {
        return yuncheBoardService.delete(id);
    }
}
