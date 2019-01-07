package com.yunche.loan.service;

import com.github.pagehelper.PageInfo;
import com.yunche.loan.domain.entity.ConfVideoFaceBankDO;
import com.yunche.loan.domain.query.ConfVideoFaceBankPartnerQuery;
import com.yunche.loan.domain.vo.MachineVideoFaceVO;
import com.yunche.loan.domain.vo.ConfVideoFaceVO;

/**
 * @author liuzhe
 * @date 2019/1/4
 */
public interface ConfVideoFaceService {

    void artificialUpdate(ConfVideoFaceBankDO confVideoFaceBankDO);

    ConfVideoFaceBankDO artificialDetail(Long bankId);

    void machineUpdate(Long bankId, Long partnerId, Byte status);

    PageInfo<MachineVideoFaceVO> listMachine(ConfVideoFaceBankPartnerQuery query);

    ConfVideoFaceVO detail(Long bankId, Long partnerId);
}
