package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.DrivinglicensePara;
import com.yunche.loan.domain.param.QueryVINParam;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/secondHandCar")
public class SecondHandCarController
{
    @Autowired
    private SecondHandCarService secondHandCarService;

    @GetMapping("/drivinglicense")
    public ResultBean drivinglicense(@RequestParam(value="face",required=true) MultipartFile face,@RequestParam(value="back",required=true) MultipartFile back)
    {

        // 将图片转为base64
        BASE64Encoder encoder = new BASE64Encoder();

        try {
            String imgData = encoder.encode(face.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        DrivinglicensePara evaluationPara =new DrivinglicensePara();
        return secondHandCarService.drivinglicense(evaluationPara);

    }

    @GetMapping(value = "/queryCarTypeByVIN")
    public ResultBean queryCarTypeByVIN(@RequestParam(value="VIN",required=true) String VIN)
    {
        return secondHandCarService.queryCarTypeByVIN(VIN);

    }

    @PostMapping(value = "/queryVIN", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean queryVIN(@RequestBody @Validated QueryVINParam param)
    {
        return secondHandCarService.queryVIN(param);

    }
}
