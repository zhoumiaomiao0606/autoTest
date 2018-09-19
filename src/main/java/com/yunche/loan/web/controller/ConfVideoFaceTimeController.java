package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.service.ConfVideoFaceTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/conf/videoFaceTime")
public class ConfVideoFaceTimeController {


    @Autowired
    private ConfVideoFaceTimeService confVideoFaceTimeService;


    @PostMapping(value = "save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> save(@RequestBody List<ConfVideoFaceTimeDO> confVideoFaceTimeDOS) {
        confVideoFaceTimeService.save(confVideoFaceTimeDOS);
        return ResultBean.ofSuccess(null, "保存成功");
    }
}
