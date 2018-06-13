package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.VideoFaceCallVO;
import com.yunche.loan.domain.vo.VideoFaceCustomerVO;
import com.yunche.loan.service.VideoFaceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/6/4
 */
@CrossOrigin
@RestController
@RequestMapping(value = {"/api/v1/videoFace/room", "/api/v1/app/videoFace/room"})
public class VideoFaceRoomController {

    @Autowired
    private VideoFaceRoomService videoFaceRoomService;


    /**
     * 进入队列
     *
     * @param type       1-PC; 2-APP;
     * @param bankId
     * @param customerId
     * @return
     */
    @GetMapping("/addQueue")
    public ResultBean<Void> addQueue(@RequestParam Integer type,
                                     @RequestParam Long bankId,
                                     @RequestParam Long customerId) {
        return videoFaceRoomService.addQueue(type, bankId, customerId);
    }

    /**
     * 退出队列
     *
     * @param type       1-PC; 2-APP;
     * @param bankId
     * @param customerId
     * @return
     */
    @GetMapping("/exitQueue")
    public ResultBean<Void> exitQueue(@RequestParam Integer type,
                                      @RequestParam Long bankId,
                                      @RequestParam Long customerId) {
        return videoFaceRoomService.exitQueue(type, bankId, customerId);
    }

    /**
     * 房间内 排队用户列表    -PC端显示
     *
     * @param bankId -即：queue_id
     * @return
     */
    @GetMapping("/listCustomer")
    public ResultBean<List<VideoFaceCustomerVO>> listCustomer(@RequestParam Long bankId) {
        return videoFaceRoomService.listCustomerInQueue(bankId);
    }

    /**
     * 房间内 排队用户列表    -APP端显示
     *
     * @param bankId -即：queue_id
     * @return
     */
    @GetMapping("/listQueue")
    public ResultBean<List<VideoFaceCustomerVO>> listQueue(@RequestParam Long bankId) {
        return videoFaceRoomService.listQueue(bankId);
    }

    /**
     * 发起通话
     *
     * @param sendUserId
     * @param receiveUserId
     * @return
     */
    @GetMapping("/call")
    public ResultBean<VideoFaceCallVO> call(@RequestParam Long sendUserId,
                                            @RequestParam Long receiveUserId) {
        return videoFaceRoomService.call(sendUserId, receiveUserId);
    }

}
