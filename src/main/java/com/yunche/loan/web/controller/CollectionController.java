package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CollectionRecordUpdateParam;
import com.yunche.loan.domain.param.ManualDistributionBaseParam;
import com.yunche.loan.domain.param.ManualDistributionParam;
import com.yunche.loan.service.CollectionService;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/collection")
public class CollectionController {

    @Resource
    private CollectionService collectionService;

    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(collectionService.detail(Long.valueOf(order_id)));
    }

    @GetMapping(value = "/recordDetail")
    public ResultBean recordDetail(@RequestParam String collection_id) {
        return ResultBean.ofSuccess(collectionService.recordDetail(Long.valueOf(collection_id)));
    }

    @GetMapping(value = "/telephoneCollectionEmployee")
    public ResultBean recordDetail() {
        List list = collectionService.selectTelephoneCollectionEmployee();
        if(CollectionUtils.isEmpty(list)){
            list = new ArrayList();
        }
        return ResultBean.ofSuccess(list);
    }

    /**
     * 退款单更新
     */
    @PostMapping(value = "/recordUpdate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> update(@RequestBody @Validated CollectionRecordUpdateParam param) {
        collectionService.recordUpdate(param);
        return ResultBean.ofSuccess(null,"保存成功");
    }

    @PostMapping(value = "/manualDistribution", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> manualDistribution(@RequestBody @Validated ManualDistributionBaseParam param) {
        collectionService.manualDistribution(param.getManual_distribution_list());
        return ResultBean.ofSuccess(null,"保存成功");
    }


    @GetMapping(value = "/checkCollectionUserRole")
    public ResultBean checkCollectionUserRole() {
        return ResultBean.ofSuccess(collectionService.checkCollectionUserRole());
    }
}
