package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.ConfVideoFaceConst;
import com.yunche.loan.domain.entity.ConfVideoFaceBankDO;
import com.yunche.loan.domain.entity.ConfVideoFaceBankPartnerDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.query.ConfVideoFaceBankPartnerQuery;
import com.yunche.loan.domain.query.PartnerQuery;
import com.yunche.loan.domain.vo.ConfVideoFaceBankPartnerVO;
import com.yunche.loan.mapper.ConfVideoFaceBankDOMapper;
import com.yunche.loan.mapper.ConfVideoFaceBankPartnerDOMapper;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.ConfVideoFaceService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author liuzhe
 * @date 2019/1/4
 */
@Service
public class ConfVideoFaceServiceImpl implements ConfVideoFaceService {

    @Autowired
    private ConfVideoFaceBankDOMapper confVideoFaceBankDOMapper;

    @Autowired
    private ConfVideoFaceBankPartnerDOMapper confVideoFaceBankPartnerDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;


    @Override
    @Transactional
    public void artificialUpdate(ConfVideoFaceBankDO confVideoFaceBankDO) {

        ConfVideoFaceBankDO exist = confVideoFaceBankDOMapper.selectByPrimaryKey(confVideoFaceBankDO.getBankId());

        if (null == exist) {

            int count = confVideoFaceBankDOMapper.insertSelective(confVideoFaceBankDO);
            Preconditions.checkArgument(count > 0, "保存失败");

        } else {

            int count = confVideoFaceBankDOMapper.updateByPrimaryKeySelective(confVideoFaceBankDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        }
    }

    @Override
    public ConfVideoFaceBankDO artificialDetail(Long bankId) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");

        ConfVideoFaceBankDO confVideoFaceBankDO = confVideoFaceBankDOMapper.selectByPrimaryKey(bankId);

        return confVideoFaceBankDO;
    }

    @Override
    @Transactional
    public void machineUpdate(Long bankId, Long partnerId, Byte status) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");
        Preconditions.checkNotNull(partnerId, "partnerId不能为空");
        Preconditions.checkNotNull(status, "status不能为空");

        ConfVideoFaceBankPartnerDO confVideoFaceBankPartnerDO = new ConfVideoFaceBankPartnerDO();
        confVideoFaceBankPartnerDO.setBankId(bankId);
        confVideoFaceBankPartnerDO.setPartnerId(partnerId);

        if (new Byte("0").equals(status)) {

            // 0-关闭  insert
            int count = confVideoFaceBankPartnerDOMapper.insertSelective(confVideoFaceBankPartnerDO);
            Preconditions.checkArgument(count > 0, "失败");

        } else if (new Byte("1").equals(status)) {

            // 1-开启  del
            confVideoFaceBankPartnerDOMapper.deleteByPrimaryKey(confVideoFaceBankPartnerDO);
        }
    }

    @Override
    public PageInfo<ConfVideoFaceBankPartnerVO> listMachine(ConfVideoFaceBankPartnerQuery query) {

        // 合伙人分页展示
        PageHelper.startPage(query.getPageIndex(), query.getPageSize(), true);

        PartnerQuery partnerQuery = new PartnerQuery();
        partnerQuery.setId(query.getPartnerId());
        partnerQuery.setName(query.getPartnerName());
        List<PartnerDO> partnerDOList = partnerDOMapper.query(partnerQuery);

        if (CollectionUtils.isEmpty(partnerDOList)) {
            return PageInfo.of(Collections.EMPTY_LIST);
        }

        PageInfo<PartnerDO> pageInfo = PageInfo.of(partnerDOList);


        // 状态
        List<Long> partnerIdList = partnerDOList.stream()
                .filter(Objects::nonNull)
                .map(PartnerDO::getId)
                .collect(Collectors.toList());

        query.setPartnerIdList(partnerIdList);
        List<ConfVideoFaceBankPartnerDO> confVideoFaceBankPartnerDOS = confVideoFaceBankPartnerDOMapper.query(query);


        if (!CollectionUtils.isEmpty(confVideoFaceBankPartnerDOS)) {

            // 被禁partnerIdList
            List<Long> close_partnerIdList = confVideoFaceBankPartnerDOS.stream()
                    .filter(Objects::nonNull)
                    .map(ConfVideoFaceBankPartnerDO::getPartnerId)
                    .collect(Collectors.toList());

            List<ConfVideoFaceBankPartnerVO> confVideoFaceBankPartnerVOList = partnerDOList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long partnerId = e.getId();

                        ConfVideoFaceBankPartnerVO confVideoFaceBankPartnerVO = new ConfVideoFaceBankPartnerVO();

                        confVideoFaceBankPartnerVO.setPartnerId(partnerId);
                        confVideoFaceBankPartnerVO.setPartnerName(e.getName());
                        confVideoFaceBankPartnerVO.setPartnerLeaderName(e.getLeaderName());

                        if (close_partnerIdList.contains(partnerId)) {
                            // CLOSE
                            confVideoFaceBankPartnerVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_CLOSE);
                        } else {
                            // OPEN
                            confVideoFaceBankPartnerVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_OPEN);
                        }

                        return confVideoFaceBankPartnerVO;
                    })
                    .collect(Collectors.toList());


            PageInfo<ConfVideoFaceBankPartnerVO> pageInfo_ = PageInfo.of(confVideoFaceBankPartnerVOList);
            BeanUtils.copyProperties(pageInfo, pageInfo_);

            pageInfo_.setList(confVideoFaceBankPartnerVOList);

            return pageInfo_;

        } else {

            // 全OPEN

            List<ConfVideoFaceBankPartnerVO> confVideoFaceBankPartnerVOList = partnerDOList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long partnerId = e.getId();

                        ConfVideoFaceBankPartnerVO confVideoFaceBankPartnerVO = new ConfVideoFaceBankPartnerVO();

                        confVideoFaceBankPartnerVO.setPartnerId(partnerId);
                        confVideoFaceBankPartnerVO.setPartnerName(e.getName());
                        confVideoFaceBankPartnerVO.setPartnerLeaderName(e.getLeaderName());
                        confVideoFaceBankPartnerVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_OPEN);

                        return confVideoFaceBankPartnerVO;
                    })
                    .collect(Collectors.toList());


            PageInfo<ConfVideoFaceBankPartnerVO> pageInfo_ = PageInfo.of(confVideoFaceBankPartnerVOList);
            BeanUtils.copyProperties(pageInfo, pageInfo_);

            pageInfo_.setList(confVideoFaceBankPartnerVOList);

            return pageInfo_;
        }
    }
}
