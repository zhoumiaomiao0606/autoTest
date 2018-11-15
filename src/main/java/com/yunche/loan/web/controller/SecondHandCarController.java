package com.yunche.loan.web.controller;

import com.aliyun.oss.internal.OSSUtils;
import com.aliyun.oss.model.OSSObject;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.param.*;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import java.io.*;

@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/secondHandCar","/api/v1/app/secondHandCar"})
public class SecondHandCarController
{
    @Autowired
    private SecondHandCarService secondHandCarService;

    //orc识别
    @PostMapping(value = "/drivinglicense", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean drivinglicense(@RequestBody DrivinglicensePara evaluationPara)
    {

        // 将图片转为base64
        /*BASE64Encoder encoder = new BASE64Encoder();
        DrivinglicensePara evaluationPara =new DrivinglicensePara();

        try {
            String faceStirng = encoder.encode(face.getBytes());
            evaluationPara.setFace(faceStirng);

            String backStirng = encoder.encode(back.getBytes());
            evaluationPara.setBack(backStirng);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String faceBase64 = getOsspictureBase64(evaluationPara.getFace());

        String backBase64 = getOsspictureBase64(evaluationPara.getBack());

        evaluationPara.setFace(faceBase64);
        evaluationPara.setBack(backBase64);

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
    @GetMapping(value = "/queryEvuluate")
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


    private String getOsspictureBase64(String url)
    {
        //根据路径从oss上获取文件输入流---进行base64编码
        InputStream inputStream =null;

        byte[] data = null;
        try {
            inputStream = OSSUnit.getOSS2InputStream(url);

            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();

            // 对字节数组Base64编码

            BASE64Encoder encoder = new BASE64Encoder();

            String encode = encoder.encode(data);// 返回Base64编码过的字节数组字符串

            System.out.println("转化后大小"+encode.length()+"====读取到的内容为"+encode);

            return encode;
        }catch (Exception e){
            throw new BizException("读取oss文件失败");
        }
    }

    //查询第一车网
    @PostMapping(value = "/firstCarSite", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean firstCarSite(@RequestBody @Validated FirstCarSiteParam param)
    {
        return secondHandCarService.firstCarSite(param);

    }

    //app端

    //查询估价
    @PostMapping(value = "/evaluateList", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean evaluateList(@RequestBody @Validated EvaluateListParam param)
    {
        return secondHandCarService.evaluateList(param);

    }

    //根据订单id查询绑定的估价信息
    @GetMapping(value = "/queryEvuluateByEvuluateId")
    public ResultBean queryEvuluateByEvuluateid(@RequestParam(value="evuluateId",required=true) Long evuluateId)
    {
        return secondHandCarService.queryEvuluateByEvuluateid(evuluateId);

    }


}
