package com.yunche.loan.web.controller;

import com.yunche.loan.domain.query.PaddingCompanyQuery;
import com.yunche.loan.domain.vo.PaddingCompanyVO;
import com.yunche.loan.domain.entity.PaddingCompanyDO;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.PaddingCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/padding")
public class PaddingCompanyController {

    @Autowired
    private PaddingCompanyService paddingCompanyService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody PaddingCompanyDO paddingCompanyDO) {
        return paddingCompanyService.create(paddingCompanyDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody PaddingCompanyDO paddingCompanyDO) {
        return paddingCompanyService.update(paddingCompanyDO);
    }

    @GetMapping(value = "/delete")
    public ResultBean<Void> delete(@RequestParam("id") Long id) {
        return paddingCompanyService.delete(id);
    }

    @GetMapping("/getById")
    public ResultBean<PaddingCompanyVO> getById(@RequestParam("id") Long id) {
        return paddingCompanyService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<PaddingCompanyVO>> query(@RequestBody PaddingCompanyQuery query) {
        return paddingCompanyService.query(query);
    }
}
