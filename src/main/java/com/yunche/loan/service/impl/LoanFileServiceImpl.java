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
import static com.yunche.loan.config.constant.LoanFileConst.TYPE_NAME_MAP;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;

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

        Map<Byte, FileVO> typeFilesMap = Maps.newConcurrentMap();

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerId(customerId);
        if (!CollectionUtils.isEmpty(loanFileDOS)) {

            loanFileDOS.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        Byte type = e.getType();
                        if (!typeFilesMap.containsKey(type)) {

                            FileVO fileVO = new FileVO();
                            fileVO.setType(type);
                            fileVO.setName(TYPE_NAME_MAP.get(type));

                            List<String> urls = Lists.newArrayList();
                            List<String> existUrls = JSON.parseArray(e.getPath(), String.class);
                            if (!CollectionUtils.isEmpty(existUrls)) {
                                urls.addAll(existUrls);
                            }
                            fileVO.setUrls(urls);

                            typeFilesMap.put(type, fileVO);

                        } else {
                            FileVO fileVO = typeFilesMap.get(type);
                            List<String> urls = JSON.parseArray(e.getPath(), String.class);
                            fileVO.getUrls().addAll(urls);
                        }

                    });
        }

        List<FileVO> fileVOS = typeFilesMap.values()
                .parallelStream()
                .map(e -> {
                    return e;
                })
                .sorted(Comparator.comparing(FileVO::getType))
                .collect(Collectors.toList());

        return ResultBean.ofSuccess(fileVOS);
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


    /**
     * 文件KEY列表保存
     *
     * @param loanFileDOS
     * @return
     */
    @Override
    public ResultBean<Void> batchInsert(List<LoanFileDO> loanFileDOS) {
        Preconditions.checkArgument(!CollectionUtils.isEmpty(loanFileDOS), "文件列表不能为空");

        int count = loanFileDOMapper.batchInsert(loanFileDOS);
        Preconditions.checkArgument(count > 0, "批量插入失败");

        return ResultBean.ofSuccess(null, "批量插入成功");
    }

    @Override
    public ResultBean<Void> updateByCustomerIdAndUploadType(Long customerId, List<FileVO> files, Byte uploadType) {

        if (!CollectionUtils.isEmpty(files)) {

            files.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        // get
                        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, e.getType(), uploadType);
                        if (CollectionUtils.isEmpty(loanFileDOS)) {
                            // insert
                            LoanFileDO loanFileDO = new LoanFileDO();
                            loanFileDO.setCustomerId(customerId);
                            loanFileDO.setType(e.getType());
                            loanFileDO.setPath(JSON.toJSONString(e.getUrls()));
                            loanFileDO.setUploadType(uploadType);

                            ResultBean<Long> resultBean = create(loanFileDO);
                            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());

                        } else {
                            // update
                            LoanFileDO loanFileDO = loanFileDOS.get(0);
                            if (null != loanFileDO) {
                                loanFileDO.setPath(JSON.toJSONString(e.getUrls()));

                                ResultBean<Void> resultBean = update(loanFileDO);
                                Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
                            }
                        }
                    });
        }

        return ResultBean.ofSuccess(null);
    }

    @Override
    public ResultBean<Void> batchInsert(Long customerId, List<FileVO> files) {

        if (!CollectionUtils.isEmpty(files)) {

            List<LoanFileDO> loanFileDOS = files.parallelStream()
                    .filter(Objects::nonNull)
                    .map(e -> {
                        LoanFileDO loanFileDO = new LoanFileDO();
                        loanFileDO.setCustomerId(customerId);
                        loanFileDO.setType(e.getType());
                        loanFileDO.setPath(JSON.toJSONString(e.getUrls()));
                        loanFileDO.setUploadType(UPLOAD_TYPE_NORMAL);
                        loanFileDO.setStatus(VALID_STATUS);
                        loanFileDO.setGmtCreate(new Date());
                        loanFileDO.setGmtModify(new Date());
                        return loanFileDO;
                    })
                    .collect(Collectors.toList());

            ResultBean<Void> resultBean = batchInsert(loanFileDOS);
            Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
        }

        return ResultBean.ofSuccess(null);
    }
}
