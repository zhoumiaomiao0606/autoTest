package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.param.LoanFileParam;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.service.LoanFileService;
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
public class LoanFileServiceImpl implements LoanFileService {

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;


    @Override
    @Transactional
    public ResultBean<Long> create(LoanFileDO loanFileDO) {
        Preconditions.checkNotNull(loanFileDO, "客户信息不能为空");

        loanFileDO.setStatus(VALID_STATUS);
        loanFileDO.setGmtCreate(new Date());
        loanFileDO.setGmtModify(new Date());

        int count = loanFileDOMapper.insertSelective(loanFileDO);
        Preconditions.checkArgument(count > 0, "编辑图片信息失败");

        return ResultBean.ofSuccess(loanFileDO.getId(), "文件信息保存成功");
    }

    @Override
    @Transactional
    public ResultBean<Void> update(LoanFileDO loanFileDO) {
        Preconditions.checkArgument(null != loanFileDO && null != loanFileDO.getId(), "ID不能为空");

        loanFileDO.setGmtModify(new Date());
        int count = loanFileDOMapper.updateByPrimaryKeySelective(loanFileDO);
        Preconditions.checkArgument(count > 0, "编辑图片信息失败");

        return ResultBean.ofSuccess(null, "图片信息编辑成功");
    }

    @Override
    public ResultBean<List<FileVO>> listByCustomerId(Long customerId) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerId(customerId);

        Map<Byte, FileVO> typeFilesMap = Maps.newConcurrentMap();

        if (!CollectionUtils.isEmpty(loanFileDOS)) {


//            loanFileDOS.stream()
//                    .filter(Objects::nonNull)
//                    .forEach(e -> {
//
//                        Byte type = e.getType();
//                        if (!typeFilesMap.containsKey(type)) {
//
//                            FileVO fileVO = new FileVO();
//                            fileVO.setType(type);
//
//                            FileVO.FileDetail fileDetail = new FileVO.FileDetail();
//                            BeanUtils.copyProperties(e, fileDetail);
//                            fileDetail.setUrl(e.getPath());
//                            List<FileVO.FileDetail> fileDetails = Lists.newArrayList(fileDetail);
//                            fileVO.setDetails(fileDetails);
//
//                            typeFilesMap.put(type, fileVO);
//
//                        } else {
//                            FileVO fileVO = typeFilesMap.get(type);
//
//                            FileVO.FileDetail fileDetail = new FileVO.FileDetail();
//                            BeanUtils.copyProperties(e, fileDetail);
//                            fileDetail.setUrl(e.getPath());
//
//                            fileVO.getDetails().add(fileDetail);
//                        }
//
//                    });
        }
        return null;
    }

    @Override
    public ResultBean<List<FileVO>> listByCustomerIdAndUploadType(Long customerId, Byte uploadType) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        List<FileVO> fileVOS = Lists.newArrayList();

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, null, uploadType);
        if (!CollectionUtils.isEmpty(loanFileDOS)) {

            fileVOS = loanFileDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .map(e -> {

                        FileVO fileVO = new FileVO();
                        fileVO.setType(e.getType());
                        // TODO   Object Key  --> URL
                        List<String> urls = JSON.parseArray(e.getPath(), String.class);
                        fileVO.setUrls(urls);
                        return fileVO;
                    })
                    .collect(Collectors.toList());
        }

        return ResultBean.ofSuccess(fileVOS);
    }

    @Override
    public ResultBean<Void> upload(List<LoanFileParam> fileParams) {

        if (!CollectionUtils.isEmpty(fileParams)) {


        }


        return ResultBean.ofSuccess(null);
    }
}
