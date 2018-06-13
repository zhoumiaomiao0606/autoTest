package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.service.VideoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@CrossOrigin
@RestController
@RequestMapping("/api/v1/videoFace")
public class VideoFaceController {

    @Autowired
    private VideoFaceService videoFaceService;


    /**
     * 加入房间   排队
     *
     * @return
     */
    @GetMapping(value = "/addRoom")
    public ResultBean<Object> addRoom(@RequestParam Long customerId,
                                      @RequestParam Long orderId) {
        return videoFaceService.addRoom(customerId, orderId);
    }

    /**
     * 面签列表
     *
     * @return
     */
    @GetMapping(value = "/x")
    public ResultBean<List<Object>> x_() {
//        return videoFaceService.x();
        return ResultBean.ofSuccess(null);
    }

    /**
     * 面签排队
     *
     * @return
     */
    @GetMapping(value = "/xx")
    public ResultBean<List<Object>> xx() {
//        return videoFaceService.xx();
        return ResultBean.ofSuccess(null);
    }

    /**
     * 人工面签
     *
     * @return
     */
    @GetMapping(value = "/xxx")
    public ResultBean<List<Object>> xxx() {
//        return videoFaceService.xxx();
        return ResultBean.ofSuccess(null);
    }

    /**
     * 机器面签
     *
     * @return
     */
    @GetMapping(value = "/xxxx")
    public ResultBean<List<Object>> xxxx() {
//        return videoFaceService.xxxx();
        return ResultBean.ofSuccess(null);
    }
}
