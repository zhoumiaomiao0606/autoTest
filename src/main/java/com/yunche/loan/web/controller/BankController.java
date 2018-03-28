package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.yunche.loan.config.constant.BankConst.BANK_LIST;

/**
 * @author liuzhe
 * @date 2018/3/8
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    /**
     * 获取银行列表
     *
     * @return
     */
    @GetMapping(value = "/list")
    public ResultBean<List<String>> listAll() {
        return ResultBean.ofSuccess(BANK_LIST);
    }


//    @GetMapping(value = "/list")
//    public ResultBean<List<BaseVO>> listAll2() {
//        List<BaseVO> baseVOS = Lists.newArrayList();
//        BANK_MAP.forEach((k, v) -> {
//            BaseVO baseVO = new BaseVO();
//            baseVO.setId(k);
//            baseVO.setName(v);
//            baseVOS.add(baseVO);
//        });
//        return ResultBean.ofSuccess(baseVOS);
//    }
}
