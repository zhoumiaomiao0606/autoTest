package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.query.VideoFaceExportQuery;
import com.yunche.loan.domain.vo.RecombinationVO;

/**
 * @author liuzhe
 * @date 2018/9/12
 */
public interface VideoReviewService {

    ResultBean<RecombinationVO> detail(Long orderId);

    String videoFaceExport(VideoFaceExportQuery query);
}
