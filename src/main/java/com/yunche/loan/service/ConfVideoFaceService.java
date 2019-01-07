package com.yunche.loan.service;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.domain.entity.ConfVideoFaceArtificialDO;
import com.yunche.loan.domain.query.ConfVideoFaceMachineQuery;
import com.yunche.loan.domain.vo.MachineVideoFaceVO;
import com.yunche.loan.domain.vo.ConfVideoFaceVO;

/**
 * @author liuzhe
 * @date 2019/1/4
 */
public interface ConfVideoFaceService {

    void artificialUpdate(ConfVideoFaceArtificialDO confVideoFaceArtificialDO);

    ConfVideoFaceArtificialDO artificialDetail(Long bankId);

    void machineUpdate(Long bankId, Long partnerId, Byte status);

    PageInfo<MachineVideoFaceVO> listMachine(ConfVideoFaceMachineQuery query);

    ConfVideoFaceVO detail(Long bankId, Long partnerId);
}
