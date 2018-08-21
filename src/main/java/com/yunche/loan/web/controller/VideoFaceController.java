package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;
import com.yunche.loan.domain.vo.VideoFaceLogVO;
import com.yunche.loan.service.VideoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
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
     * 保存面签记录
     *
     * @param videoFaceLogDO
     * @return
     */
    @PostMapping(value = "/log/save", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Long> saveLog(@RequestBody @NotNull VideoFaceLogDO videoFaceLogDO) {

        return videoFaceService.saveLog(videoFaceLogDO);
    }

    /**
     * 编辑面签记录
     *
     * @param videoFaceLogDO
     * @return
     */
    @PostMapping(value = "/log/update", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<Void> updateLog(@RequestBody @NotNull VideoFaceLogDO videoFaceLogDO) {

        return videoFaceService.updateLog(videoFaceLogDO);
    }

    /**
     * 面签记录查询
     *
     * @param videoFaceQuery
     * @return
     */
    @PostMapping(value = "/log/list", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<List<VideoFaceLogVO>> listLog(@RequestBody @NotNull VideoFaceQuery videoFaceQuery) {

        return videoFaceService.listLog(videoFaceQuery);
    }

    @GetMapping("/log/getById")
    public ResultBean<VideoFaceLogVO> getById(@RequestParam @NotNull Long id) {

        return videoFaceService.getById(id);
    }

    /**
     * 问题列表
     *
     * @param bankId
     * @param orderId
     * @param address
     * @return
     */
    @GetMapping(value = "/question/list")
    public ResultBean<List<String>> listQuestion(@RequestParam @NotNull(message = "bankId不能为空") Long bankId,
                                                 @RequestParam @NotNull(message = "orderId不能为空") Long orderId,
                                                 @RequestParam String address) {

        return videoFaceService.listQuestion(bankId, orderId, address);
    }

    /**
     * 面签记录 导出
     *
     * @param videoFaceQuery
     * @return
     */
    @PostMapping(value = "/log/export", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean<String> exportLog(@RequestBody VideoFaceQuery videoFaceQuery) {

        return videoFaceService.exportLog(videoFaceQuery);
    }

}
