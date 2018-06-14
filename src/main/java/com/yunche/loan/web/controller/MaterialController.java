package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.CarUpdateParam;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.service.MaterialService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/material")
public class MaterialController {

    @Resource
    private MaterialService materialService;

    /**
     * 录入资料
     */
    @GetMapping(value = "/detail")
    public ResultBean detail(@RequestParam String order_id) {
        return ResultBean.ofSuccess(materialService.detail(Long.valueOf(order_id)));
    }

    /**
     * 资料详情
     */
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean update(@RequestBody @Validated MaterialUpdateParam param) {
        materialService.update(param);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
     * 车辆信息编辑
     */
    @PostMapping(value = "/carUpdate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean carUpdate(@RequestBody @Validated CarUpdateParam param) {
        materialService.carUpdate(param);
        return ResultBean.ofSuccess(null, "保存成功");
    }

    /**
     * 资料下载（浏览器直接下载）
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId
     * @return
     */
    @GetMapping(value = "/download")
    public String downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                           @RequestParam Long orderId,
                           @RequestParam(value = "taskDefinitionKey", required = false) String taskDefinitionKey,
                           @RequestParam(value = "customerId", required = false) Long customerId) {

        return materialService.zipFilesDown(httpServletRequest, httpServletResponse, orderId, taskDefinitionKey, customerId);
    }

    /**
     * 下载文件完成后上传至OSS
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/down2oss")
    public ResultBean<String> down2OSS(@RequestParam Long orderId) {
        return materialService.downloadFiles2OSS(orderId, false);
    }

    /**
     * 下载文件完成后上传至tomcat
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/down2tomcat")
    public ResultBean down2tomcat(@RequestParam Long orderId,
                                  @RequestParam(value = "taskDefinitionKey") String taskDefinitionKey,
                                  @RequestParam(value = "customerId", required = false) Long customerId) {

        return materialService.down2tomcat(orderId, taskDefinitionKey, customerId);
    }
    @GetMapping(value = "/zipCheck")
    public ResultBean checkZipStatus(@RequestParam(value = "orderId") Long orderId){
       return materialService.zipCheck(orderId);
    }

}
