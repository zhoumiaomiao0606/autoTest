package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.domain.entity.ConfVideoFaceTimeDO;
import com.yunche.loan.mapper.ConfVideoFaceTimeDOMapper;
import com.yunche.loan.service.ConfVideoFaceTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author liuzhe
 * @date 2018/9/19
 */
@Service
public class ConfVideoFaceTimeServiceImpl implements ConfVideoFaceTimeService {


    @Autowired
    private ConfVideoFaceTimeDOMapper confVideoFaceTimeDOMapper;


    @Override
    public void save(List<ConfVideoFaceTimeDO> confVideoFaceTimeDO) {

        if (CollectionUtils.isEmpty(confVideoFaceTimeDO)) {

            // del All
            confVideoFaceTimeDOMapper.deleteAll();

        } else {

            // del
            confVideoFaceTimeDOMapper.deleteAll();

            // insert
            confVideoFaceTimeDO.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Preconditions.checkNotNull(e.getBankId(), "bankId不能为空");
                        Preconditions.checkNotNull(e.getStartLoanAmount(), "startLoanAmount不能为空");
                        Preconditions.checkNotNull(e.getEndLoanAmount(), "endLoanAmount不能为空");
                        Preconditions.checkNotNull(e.getStartTime(), "startTime不能为空");
                        Preconditions.checkNotNull(e.getEndTime(), "endTime不能为空");
                        Preconditions.checkNotNull(e.getType(), "type不能为空");

                        int count = confVideoFaceTimeDOMapper.insertSelective(e);
                        Preconditions.checkArgument(count > 0, "插入失败");
                    });
        }
    }
}
