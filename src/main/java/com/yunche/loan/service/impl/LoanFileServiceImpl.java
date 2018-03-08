package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.service.LoanFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
@Transactional
public class LoanFileServiceImpl implements LoanFileService {


    @Autowired
    private LoanFileDOMapper loanFileDOMapper;


    @Override
    public ResultBean<Void> create(Long customerId, List<FileVO> files) {
        Preconditions.checkNotNull(customerId, "客户信息不能为空");

        if (!CollectionUtils.isEmpty(files)) {

            files.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        List<FileVO.FileDetail> fileDetails = e.getDetails();
                        if (!CollectionUtils.isEmpty(fileDetails)) {

                            fileDetails.parallelStream()
                                    .filter(Objects::nonNull)
                                    .forEach(f -> {
                                        LoanFileDO loanFileDO = new LoanFileDO();
                                        loanFileDO.setCustomerId(customerId);
                                        loanFileDO.setType(e.getType());
                                        BeanUtils.copyProperties(f, loanFileDO);
                                        loanFileDO.setPath(f.getUrl());
                                        loanFileDO.setStatus(VALID_STATUS);
                                        loanFileDO.setGmtCreate(new Date());
                                        loanFileDO.setGmtModify(new Date());
                                        int count = loanFileDOMapper.insertSelective(loanFileDO);
                                        Preconditions.checkArgument(count > 0, "文件信息保存失败");
                                    });
                        }

                    });
        }

        return ResultBean.ofSuccess(null, "文件信息保存成功");
    }

    @Override
    public ResultBean<List<FileVO>> listByCustomerId(Long customerId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerId(customerId);

        Map<Byte, FileVO> typeFilesMap = Maps.newConcurrentMap();

        if (!CollectionUtils.isEmpty(loanFileDOS)) {

            loanFileDOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Byte type = e.getType();
                        if (!typeFilesMap.containsKey(type)) {

                            FileVO fileVO = new FileVO();
                            fileVO.setType(type);

                            FileVO.FileDetail fileDetail = new FileVO.FileDetail();
                            BeanUtils.copyProperties(e, fileDetail);
                            fileDetail.setUrl(e.getPath());
                            List<FileVO.FileDetail> fileDetails = Lists.newArrayList(fileDetail);
                            fileVO.setDetails(fileDetails);

                            typeFilesMap.put(type, fileVO);

                        } else {
                            FileVO fileVO = typeFilesMap.get(type);

                            FileVO.FileDetail fileDetail = new FileVO.FileDetail();
                            BeanUtils.copyProperties(e, fileDetail);
                            fileDetail.setUrl(e.getPath());

                            fileVO.getDetails().add(fileDetail);
                        }

                    });
        }

        List<FileVO> fileVOS = typeFilesMap.values().stream()
                .map(e -> {
                    return e;
                })
                .sorted(Comparator.comparing(FileVO::getType))
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(fileVOS);
    }

    @Override
    public ResultBean<Void> update(Long customerId, List<FileVO> files) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        if (!CollectionUtils.isEmpty(files)) {

            files.parallelStream()
                    .forEach(e -> {

                        List<FileVO.FileDetail> details = e.getDetails();
                        if (!CollectionUtils.isEmpty(details)) {

                            details.parallelStream()
                                    .filter(Objects::nonNull)
                                    .forEach(f -> {

                                        if (null == f.getId()) {
                                            // insert
                                            LoanFileDO loanFileDO = new LoanFileDO();
                                            BeanUtils.copyProperties(f, loanFileDO);
                                            loanFileDO.setType(e.getType());
                                            loanFileDO.setCustomerId(customerId);
                                            loanFileDO.setPath(f.getUrl());
                                            loanFileDO.setStatus(VALID_STATUS);
                                            loanFileDO.setGmtCreate(new Date());
                                            loanFileDO.setGmtModify(new Date());
                                            int count = loanFileDOMapper.insertSelective(loanFileDO);
                                            Preconditions.checkArgument(count > 0, "图片信息保存失败");
                                        } else {
                                            // update (逻辑删除等)
                                            LoanFileDO loanFileDO = new LoanFileDO();
                                            BeanUtils.copyProperties(f, loanFileDO);
                                            loanFileDO.setType(e.getType());
                                            loanFileDO.setCustomerId(customerId);
                                            loanFileDO.setPath(f.getUrl());
                                            loanFileDO.setGmtModify(new Date());
                                            int count = loanFileDOMapper.updateByPrimaryKeySelective(loanFileDO);
                                            Preconditions.checkArgument(count > 0, "图片信息编辑失败,图片ID有误！");
                                        }

                                    });
                        }

                    });
        }

        return ResultBean.ofSuccess(null, "图片信息编辑成功");
    }
}
