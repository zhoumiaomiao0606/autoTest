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
    public ResultBean detail(@RequestParam("order_id") String order_id,@RequestParam("id")String id,@RequestParam("bank_repay_imp_record_id") String bank_repay_imp_record_id) {
        return ResultBean.ofSuccess(visitDoorService.detail(Long.valueOf(order_id),Long.valueOf(id),Long.valueOf(bank_repay_imp_record_id)));
    }
    //app信息分开
    @GetMapping(value = "/cusinfodetail")
    public ResultBean cusInfoDetatil(@RequestParam("order_id") String order_id,@RequestParam("id")String id,@RequestParam("bank_repay_imp_record_id") String bank_repay_imp_record_id) {
        return ResultBean.ofSuccess(visitDoorService.cusInfoDetatil(Long.valueOf(order_id),Long.valueOf(id),Long.valueOf(bank_repay_imp_record_id)));
    }
    //app信息分开
    @GetMapping(value = "/visitdoordetail")
    public ResultBean visitDoorDetatil(@RequestParam("order_id") String order_id,@RequestParam("id")String id,@RequestParam("bank_repay_imp_record_id") String bank_repay_imp_record_id) {
        return ResultBean.ofSuccess(visitDoorService.visitDoorDetatil(Long.valueOf(order_id),Long.valueOf(id),Long.valueOf(bank_repay_imp_record_id)));
    }

    /**
     * 上门人员列表
     * @param
     * @return
     */
    @GetMapping(value = "/visitdooremployees")
    public ResultBean visitDoorEmployees() {
        return ResultBean.ofSuccess(visitDoorService.visitDoorEmployees());
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated VisitDoorDO visitDoorDO) {

        return ResultBean.ofSuccess(visitDoorService.update(visitDoorDO),"录入成功");
    }
    /*
     *撤销诉讼
     */
    @PostMapping(value = "/revoke")
    public ResultBean visitDoorRevoke(@RequestBody @Validated LitigationStateDO param) {
        visitDoorService.visitDoorRevoke(param);
        return ResultBean.ofSuccess(null,"撤诉成功");
    }
    /**
     * 新增一条代办任务
     */
    @PostMapping(value = "/insertnewinfo", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean insertNewInfo(@RequestBody @Validated VisitDoorDO visitDoorDO){
        visitDoorService.insertNewInfo(visitDoorDO);
        return ResultBean.ofSuccess("","录入成功");
    }

}
