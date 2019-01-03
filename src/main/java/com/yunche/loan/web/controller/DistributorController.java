package com.yunche.loan.web.controller;


import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.DistributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/distributor")
public class DistributorController {


    @Autowired
    private DistributorService distributorService;

    @GetMapping(value = "/query")
    public ResultBean queryDistributor(@RequestParam(value = "partnerId") String partnerId){

        return distributorService.queryDistributor(partnerId);
    }
}
