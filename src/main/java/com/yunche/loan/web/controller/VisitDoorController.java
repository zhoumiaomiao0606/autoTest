package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LitigationStateDO;
import com.yunche.loan.domain.entity.VisitDoorDO;
import com.yunche.loan.domain.param.ForceParam;
import com.yunche.loan.service.VisitDoorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/visitdoor","/api/v1/app/visitdoor"})
public class VisitDoorController {
    @Autowired
    private VisitDoorService visitDoorService;
    /**
     * 上门详情
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam("order_id") String order_id,@RequestParam("id")String id) {
        return ResultBean.ofSuccess(visitDoorService.detail(Long.valueOf(order_id),Long.valueOf(id)));
    }
    @GetMapping(value = "/cusinfodetail")
    public ResultBean cusInfoDetatil(@RequestParam("order_id") String order_id,@RequestParam("id")String id) {
        return ResultBean.ofSuccess(visitDoorService.cusInfoDetatil(Long.valueOf(order_id),Long.valueOf(id)));
    }

    @GetMapping(value = "/visitdoordetail")
    public ResultBean visitDoorDetatil(@RequestParam("order_id") String order_id,@RequestParam("id")String id) {
        return ResultBean.ofSuccess(visitDoorService.visitDoorDetatil(Long.valueOf(order_id),Long.valueOf(id)));
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated VisitDoorDO visitDoorDO) {
        visitDoorService.update(visitDoorDO);
        return ResultBean.ofSuccess(null,"录入成功");
    }
    /*
     *撤销诉讼
     */
    @PostMapping(value = "/revoke")
    public ResultBean visitDoorRevoke(@RequestBody @Validated LitigationStateDO param) {
        visitDoorService.visitDoorRevoke(param);
        return ResultBean.ofSuccess(null,"撤诉成功");
    }

}
