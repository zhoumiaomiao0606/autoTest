package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 通用接口
 *
 * @author liuzhe
 * @date 2018/7/10
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/common")
public class CommonController {

    @Autowired
    private CommonService commonService;


    /**
     * 数据字典
     *
     * @return
     */
    @GetMapping(value = "/dict")
    public ResultBean<DataDictionaryVO> dictionary() throws Exception {
        return commonService.dictionary();
    }
}
