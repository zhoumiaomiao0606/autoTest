package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankRelaQuestionDO;
import com.yunche.loan.domain.entity.VideoFaceLogDO;
import com.yunche.loan.domain.query.VideoFaceQuery;
import com.yunche.loan.mapper.BankRelaQuestionDOMapper;
import com.yunche.loan.mapper.VideoFaceLogDOMapper;
import com.yunche.loan.service.VideoFaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/5/17
 */
@Service
public class VideoFaceServiceImpl implements VideoFaceService {

    @Autowired
    private VideoFaceLogDOMapper videoFaceLogDOMapper;

    @Autowired
    private BankRelaQuestionDOMapper bankRelaQuestionDOMapper;


    @Override
    @Transactional
    public ResultBean<Long> saveLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkNotNull(videoFaceLogDO.getOrderId(), "订单号不能为空");

        videoFaceLogDO.setGmtCreate(new Date());
        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.insertSelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "保存失败");

        return ResultBean.ofSuccess(videoFaceLogDO.getId(), "保存成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> updateLog(VideoFaceLogDO videoFaceLogDO) {
        Preconditions.checkArgument(null != videoFaceLogDO && null != videoFaceLogDO.getId(), "id不能为空");

        videoFaceLogDO.setGmtModify(new Date());
        int count = videoFaceLogDOMapper.updateByPrimaryKeySelective(videoFaceLogDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<List<VideoFaceLogDO>> listLog(VideoFaceQuery videoFaceQuery) {

        PageHelper.startPage(videoFaceQuery.getPageIndex(), videoFaceQuery.getPageSize(), true);

        List<VideoFaceLogDO> videoFaceLogDOList = videoFaceLogDOMapper.query(videoFaceQuery);

        // 取分页信息
        PageInfo<VideoFaceLogDO> pageInfo = new PageInfo<>(videoFaceLogDOList);

        return ResultBean.ofSuccess(videoFaceLogDOList, Math.toIntExact(pageInfo.getTotal()),
                pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean<List<BankRelaQuestionDO>> listQuestion(Long bankId) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");

        List<BankRelaQuestionDO> bankRelaQuestionDOList = bankRelaQuestionDOMapper.listByBankIdAndType(bankId, null);

        return ResultBean.ofSuccess(bankRelaQuestionDOList);
    }
}
