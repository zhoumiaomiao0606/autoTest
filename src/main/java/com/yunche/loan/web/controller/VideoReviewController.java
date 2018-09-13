package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.service.VideoReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author liuzhe
 * @date 2018/9/12
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/videoReview", "/api/v1/app/videoReview"})
public class VideoReviewController {


    @Autowired
    private VideoReviewService videoReviewService;


    /**
     * 征信申请单详情
     *
     * @param orderId
     * @return
     */
    @GetMapping(value = "/detail")
    public ResultBean<RecombinationVO> detail(@RequestParam("orderId") Long orderId) {
        return videoReviewService.detail(orderId);
    }
}
