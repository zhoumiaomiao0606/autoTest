package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;
import com.yunche.loan.domain.vo.VideoFaceFlagVO;
import com.yunche.loan.domain.vo.VideoFaceLogVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
public interface VideoFaceService {

    ResultBean<Long> saveLog(VideoFaceLogDO videoFaceLogDO);

    ResultBean<Void> updateLog(VideoFaceLogDO videoFaceLogDO);

    ResultBean<List<VideoFaceLogVO>> listLog(VideoFaceQuery videoFaceQuery);

    ResultBean<VideoFaceLogVO> getById(Long id);

    ResultBean<String> exportLog(VideoFaceQuery videoFaceQuery);

    ResultBean<List<String>> listQuestion(Long bankId, Long orderId, String address);

    VideoFaceFlagVO isFlag(Long orderId);

}
