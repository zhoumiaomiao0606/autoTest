package com.yunche.loan.service;

import com.yunche.loan.domain.vo.ConfVideoFaceTimeVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
public interface ConfVideoFaceTimeService {

    void save(List<ConfVideoFaceTimeVO> confVideoFaceTimeDO);

    List<ConfVideoFaceTimeVO> listAll();
}
