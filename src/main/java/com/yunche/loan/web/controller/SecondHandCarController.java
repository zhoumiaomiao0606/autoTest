package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.DrivinglicensePara;
import com.yunche.loan.domain.param.EvaluateParam;
import com.yunche.loan.domain.param.EvaluateWebParam;
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

    //orc识别
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

    //根据vin码查询车辆类型-返回list
    @GetMapping(value = "/queryCarTypeByVIN")
    public ResultBean queryCarTypeByVIN(@RequestParam(value="VIN",required=true) String VIN)
    {
        return secondHandCarService.queryCarTypeByVIN(VIN);

    }

    //模糊查询vin码
    @PostMapping(value = "/queryVIN", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean queryVIN(@RequestBody @Validated QueryVINParam param)
    {
        return secondHandCarService.queryVIN(param);

    }

    //根据订单id查询绑定的估价信息
    @PostMapping(value = "/queryEvuluate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean queryEvuluate(@RequestParam(value="orderId",required=true) Long orderId)
    {
        return secondHandCarService.queryEvuluate(orderId);

    }

    //查询估价
    @PostMapping(value = "/evaluate", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean evaluate(@RequestBody @Validated EvaluateWebParam param)
    {
        return secondHandCarService.evaluate(param);

    }
}
