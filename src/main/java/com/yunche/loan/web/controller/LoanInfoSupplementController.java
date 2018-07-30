package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.InfoSupplementParam;
import com.yunche.loan.domain.vo.InfoSupplementVO;
import com.yunche.loan.domain.vo.InfoSupplementVO2;
import com.yunche.loan.service.LoanInfoSupplementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/25
 */
@CrossOrigin
@RestController
@RequestMapping(value = "/api/v1/loanorder/infosupplement")
public class LoanInfoSupplementController {


    @Autowired
    private LoanInfoSupplementService loanInfoSupplementService;


    /**
     * 资料增补 -客户证件图片信息
     *
     * @param infoSupplementParam
     * @return
     */
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> upload(@RequestBody InfoSupplementParam infoSupplementParam) {
        return loanInfoSupplementService.upload(infoSupplementParam);
    }

    /**
     * 资料增补详情页
     *
     * @param supplementOrderId
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<InfoSupplementVO> detail(@RequestParam Long supplementOrderId) {
        return loanInfoSupplementService.detail(supplementOrderId);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 资料增补 保存
     *
     * @param infoSupplementParam
     * @return
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> save(@RequestBody InfoSupplementParam infoSupplementParam) {
        return loanInfoSupplementService.save(infoSupplementParam);
    }

    /**
     * 资料增补 详情
     *
     * @param infoSupplementId
     * @return
     */
    @GetMapping(value = "/detail_")
    public ResultBean<InfoSupplementVO2> detail_(@RequestParam Long infoSupplementId) {
        return loanInfoSupplementService.detail_(infoSupplementId);
    }

    /**
     * 资料增补 历史列表
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/history")
    public ResultBean<List<InfoSupplementVO2>> history(@RequestParam Long orderId) {
        return loanInfoSupplementService.history(orderId);
    }

}
