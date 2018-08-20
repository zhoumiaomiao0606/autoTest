package com.yunche.loan.service;

import com.yunche.loan.domain.entity.LitigationStateDO;
import com.yunche.loan.domain.param.FeeRegisterParam;
import com.yunche.loan.domain.param.FileInfoParam;
import com.yunche.loan.domain.param.ForceParam;
import com.yunche.loan.domain.param.LitigationParam;
import com.yunche.loan.domain.vo.LawWorksVO;

public interface LawWorksService {
    public LawWorksVO detail(Long orderid,Long bankRepayImpRecordId);

    public void litigationInstall(LitigationParam param);

    public void forceInstall(ForceParam param);

    public void feeInstall(FeeRegisterParam param);

    public void fileInfoInstall(FileInfoParam param);

    void litigationRevoke(LitigationStateDO litigationStateDO);
}
