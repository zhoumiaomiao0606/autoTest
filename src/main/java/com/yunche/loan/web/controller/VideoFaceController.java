package com.yunche.loan.web.controller;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
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

    /**
     * 问题列表
     *
     * @param bankId
     * @return
     */
    @GetMapping("/question/list")
    public ResultBean<List<BankRelaQuestionDO>> listQuestion(@RequestParam @NotNull Long bankId) {

        return videoFaceService.listQuestion(bankId);
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
