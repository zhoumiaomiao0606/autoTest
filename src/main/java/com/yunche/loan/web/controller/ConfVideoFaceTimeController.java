package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.domain.vo.ConfVideoFaceTimeVO;
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
    public ResultBean<Void> save(@RequestBody List<ConfVideoFaceTimeVO> confVideoFaceTimeVOS) {
        confVideoFaceTimeService.save(confVideoFaceTimeVOS);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    @GetMapping("listAll")
    public ResultBean<List<ConfVideoFaceTimeVO>> listAll() {
        return ResultBean.ofSuccess(confVideoFaceTimeService.listAll(), "成功");
    }
}
