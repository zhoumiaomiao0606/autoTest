package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
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
        return ResultBean.ofSuccess(null,"保存成功");
    }


    /**
     * 资料下载（浏览器直接下载）
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId
     * @return
     */
    @GetMapping(value = "/download")
    public ResultBean downLoad(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,@RequestParam Long orderId){
        return materialService.zipFilesDown(httpServletRequest,httpServletResponse,orderId);
    }

    /**
     * 下载文件完成后上传至OSS
     * @param orderId
     * @return
     */
    @GetMapping(value = "/down2oss")
    public ResultBean down2OSS(@RequestParam Long orderId){
        return materialService.downloadFilesToOSS(orderId);
    }

}
