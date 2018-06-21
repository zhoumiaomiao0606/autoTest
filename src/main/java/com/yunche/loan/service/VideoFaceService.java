package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
public interface VideoFaceService {

    ResultBean<Long> saveLog(VideoFaceLogDO videoFaceLogDO);

    ResultBean<Void> updateLog(VideoFaceLogDO videoFaceLogDO);

    ResultBean<List<VideoFaceLogDO>> listLog(VideoFaceQuery videoFaceQuery);

    ResultBean<List<BankRelaQuestionDO>> listQuestion(Long bankId);
}
