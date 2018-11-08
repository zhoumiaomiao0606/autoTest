package com.yunche.loan.web;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.EvaluationPara;
import com.yunche.loan.service.SecondHandCarService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/evaluation")
    public ResultBean evaluation(@RequestParam(value="face",required=true) MultipartFile face,@RequestParam(value="back",required=true) MultipartFile back)
    {

        // 将图片转为base64
        BASE64Encoder encoder = new BASE64Encoder();

        try {
            String imgData = encoder.encode(face.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        EvaluationPara evaluationPara =new EvaluationPara();
        return secondHandCarService.evaluation(evaluationPara);

    }
}
