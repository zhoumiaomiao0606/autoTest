package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.AppVersionDO;
import com.yunche.loan.domain.query.AppVersionQuery;
import com.yunche.loan.domain.vo.AppVersionVO;
import com.yunche.loan.service.AppVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/12
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/version")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;


    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> create(@RequestBody AppVersionDO appVersionDO) {
        return appVersionService.create(appVersionDO);
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody AppVersionDO appVersionDO) {
        return appVersionService.update(appVersionDO);
    }

    @GetMapping("/detail")
    public ResultBean<AppVersionVO> detail(@RequestParam("id") Long id) {
        return appVersionService.getById(id);
    }

    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<AppVersionVO>> query(@RequestBody AppVersionQuery query) {
        return appVersionService.query(query);
    }

    @PostMapping(value = "/check", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<AppVersionVO.Update> checkUpdate(@RequestBody AppVersionDO appVersionDO) {
        return appVersionService.checkUpdate(appVersionDO);
    }
}
