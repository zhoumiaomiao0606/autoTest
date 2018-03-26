package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.CalcParamVO;
import com.yunche.loan.service.ComputeModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin
@RestController
@RequestMapping("/computemode")
public class ComputeModeController {
    private static final Logger logger = LoggerFactory.getLogger(ComputeModeController.class);
    @Autowired
    private ComputeModeService computeModeService;
    @RequestMapping(value = "/calc",method = RequestMethod.GET)
    public ResultBean<CalcParamVO> getFormulaResult(@RequestParam("id")int id, @RequestParam("loanAmt")BigDecimal loanAmt, @RequestParam("exeRate")BigDecimal exeRate, @RequestParam("bankBaseRate")BigDecimal bankBaseRate, @RequestParam("year")int year, @RequestParam("carPrice")BigDecimal carPrice){
        return  computeModeService.calc(id,loanAmt,exeRate,bankBaseRate,year,carPrice);
    }
}
