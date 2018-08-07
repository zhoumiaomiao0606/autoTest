/**
 * @author: ZhongMingxiao
 * @create: 2018-08-07 08:46
 * @description: 录入提车资料
 **/
package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.ExtractionCarMaterialCardQuery;
import com.yunche.loan.domain.vo.ExtractionCarMaterialCardVO;
import com.yunche.loan.service.ExtractionCarMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/app/extractionCarMaterial")
public class AppExtractionCarMaterialController
{
    @Autowired
    private ExtractionCarMaterialService extractionCarMaterialService;
    @GetMapping(value = "/card")
    public ResultBean<List<ExtractionCarMaterialCardVO>> extractionCarMaterialCard(@RequestBody @Validated ExtractionCarMaterialCardQuery extractionCarMaterialQuery)
    {
        return extractionCarMaterialService.selectMaterialCard(extractionCarMaterialQuery);
    }

}
