package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.ConfVideoFaceConst;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.query.ConfVideoFaceMachineQuery;
import com.yunche.loan.domain.query.PartnerQuery;
import com.yunche.loan.domain.vo.MachineVideoFaceVO;
import com.yunche.loan.domain.vo.ConfVideoFaceVO;
import com.yunche.loan.mapper.*;
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
    private ConfVideoFaceArtificialDOMapper confVideoFaceArtificialDOMapper;

    @Autowired
    private ConfVideoFaceMachineDOMapper confVideoFaceMachineDOMapper;

    @Autowired
    private PartnerDOMapper partnerDOMapper;


    @Override
    @Transactional
    public void artificialUpdate(ConfVideoFaceArtificialDO confVideoFaceArtificialDO) {

        ConfVideoFaceArtificialDO exist = confVideoFaceArtificialDOMapper.selectByPrimaryKey(confVideoFaceArtificialDO.getBankId());

        if (null == exist) {

            int count = confVideoFaceArtificialDOMapper.insertSelective(confVideoFaceArtificialDO);
            Preconditions.checkArgument(count > 0, "保存失败");

        } else {

            int count = confVideoFaceArtificialDOMapper.updateByPrimaryKeySelective(confVideoFaceArtificialDO);
            Preconditions.checkArgument(count > 0, "保存失败");
        }
    }

    @Override
    public ConfVideoFaceArtificialDO artificialDetail(Long bankId) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");

        ConfVideoFaceArtificialDO exist = confVideoFaceArtificialDOMapper.selectByPrimaryKey(bankId);

        if (null != exist) {
            return exist;
        }

        ConfVideoFaceArtificialDO confVideoFaceArtificialDO = new ConfVideoFaceArtificialDO();
        confVideoFaceArtificialDO.setBankId(bankId);
        confVideoFaceArtificialDO.setArtificialVideoFaceStatus(ConfVideoFaceConst.ARTIFICIAL_VIDEO_FACE_STATUS_CLOSE);
        confVideoFaceArtificialDO.setNeedLocation(ConfVideoFaceConst.ARTIFICIAL_VIDEO_FACE_NEED_LOCATION_TRUE);

        return confVideoFaceArtificialDO;
    }

    @Override
    @Transactional
    public void machineUpdate(Long bankId, Long partnerId, Byte status) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");
        Preconditions.checkNotNull(partnerId, "partnerId不能为空");
        Preconditions.checkNotNull(status, "status不能为空");

        ConfVideoFaceMachineDO confVideoFaceMachineDO = new ConfVideoFaceMachineDO();
        confVideoFaceMachineDO.setBankId(bankId);
        confVideoFaceMachineDO.setPartnerId(partnerId);

        if (new Byte("0").equals(status)) {

            // 0-关闭  insert
            int count = confVideoFaceMachineDOMapper.insertSelective(confVideoFaceMachineDO);
            Preconditions.checkArgument(count > 0, "失败");

        } else if (new Byte("1").equals(status)) {

            // 1-开启  del
            confVideoFaceMachineDOMapper.deleteByPrimaryKey(confVideoFaceMachineDO);
        }
    }

    @Override
    public PageInfo<MachineVideoFaceVO> listMachine(ConfVideoFaceMachineQuery query) {

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
        List<ConfVideoFaceMachineDO> confVideoFaceMachineDOS = confVideoFaceMachineDOMapper.query(query);


        if (!CollectionUtils.isEmpty(confVideoFaceMachineDOS)) {

            // 被禁partnerIdList
            List<Long> close_partnerIdList = confVideoFaceMachineDOS.stream()
                    .filter(Objects::nonNull)
                    .map(ConfVideoFaceMachineDO::getPartnerId)
                    .collect(Collectors.toList());

            List<MachineVideoFaceVO> machineVideoFaceVOList = partnerDOList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long partnerId = e.getId();

                        MachineVideoFaceVO machineVideoFaceVO = new MachineVideoFaceVO();

                        machineVideoFaceVO.setPartnerId(partnerId);
                        machineVideoFaceVO.setPartnerName(e.getName());
                        machineVideoFaceVO.setPartnerLeaderName(e.getLeaderName());

                        if (close_partnerIdList.contains(partnerId)) {
                            // CLOSE
                            machineVideoFaceVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_CLOSE);
                        } else {
                            // OPEN
                            machineVideoFaceVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_OPEN);
                        }

                        return machineVideoFaceVO;
                    })
                    .collect(Collectors.toList());


            PageInfo<MachineVideoFaceVO> pageInfo_ = PageInfo.of(machineVideoFaceVOList);
            BeanUtils.copyProperties(pageInfo, pageInfo_);

            pageInfo_.setList(machineVideoFaceVOList);

            return pageInfo_;

        } else {

            // 全OPEN

            List<MachineVideoFaceVO> machineVideoFaceVOList = partnerDOList.stream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        Long partnerId = e.getId();

                        MachineVideoFaceVO machineVideoFaceVO = new MachineVideoFaceVO();

                        machineVideoFaceVO.setPartnerId(partnerId);
                        machineVideoFaceVO.setPartnerName(e.getName());
                        machineVideoFaceVO.setPartnerLeaderName(e.getLeaderName());
                        machineVideoFaceVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_OPEN);

                        return machineVideoFaceVO;
                    })
                    .collect(Collectors.toList());


            PageInfo<MachineVideoFaceVO> pageInfo_ = PageInfo.of(machineVideoFaceVOList);
            BeanUtils.copyProperties(pageInfo, pageInfo_);

            pageInfo_.setList(machineVideoFaceVOList);

            return pageInfo_;
        }
    }

    @Override
    public ConfVideoFaceVO detail(Long bankId, Long partnerId) {
        Preconditions.checkNotNull(bankId, "bankId不能为空");
        Preconditions.checkNotNull(partnerId, "partnerId不能为空");

        ConfVideoFaceVO confVideoFaceVO = new ConfVideoFaceVO();

        // 人工
        ConfVideoFaceArtificialDO confVideoFaceArtificialDO = artificialDetail(bankId);

        confVideoFaceVO.setArtificialVideoFaceStatus(confVideoFaceArtificialDO.getArtificialVideoFaceStatus());
        confVideoFaceVO.setNeedLocation(confVideoFaceArtificialDO.getNeedLocation());


        // 机器
        ConfVideoFaceMachineDO key = new ConfVideoFaceMachineDO();
        key.setBankId(bankId);
        key.setPartnerId(partnerId);
        ConfVideoFaceMachineDO exist = confVideoFaceMachineDOMapper.selectByPrimaryKey(key);

        if (null != exist) {
            confVideoFaceVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_CLOSE);
        } else {
            confVideoFaceVO.setMachineVideoFaceStatus(ConfVideoFaceConst.MACHINE_VIDEO_FACE_STATUS_OPEN);
        }

        return confVideoFaceVO;
    }
}
