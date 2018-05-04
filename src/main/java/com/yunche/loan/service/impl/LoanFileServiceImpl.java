package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.vo.FileVO;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.service.LoanFileService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanFileConst.TYPE_NAME_MAP;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_NORMAL;
import static com.yunche.loan.config.constant.LoanFileConst.UPLOAD_TYPE_SUPPLEMENT;

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
    public ResultBean<List<FileVO>> listByCustomerId(Long customerId, Byte fileUploadType) {
        Preconditions.checkNotNull(customerId, "客户ID不能为空");

        Map<Byte, FileVO> typeFilesMap = Maps.newConcurrentMap();

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, null, fileUploadType);
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
                            if(!CollectionUtils.isEmpty(fileVO.getUrls())){
                                List<String> urls1 = fileVO.getUrls();
                                urls1.addAll(urls);
                                fileVO.setUrls(urls1);
                            }else{
                                fileVO.setUrls(urls);
                            }

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
                        fileVO.setName(TYPE_NAME_MAP.get(e.getType()));
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
    public ResultBean<Void> updateOrInsertByCustomerIdAndUploadType(Long customerId, List<FileVO> files, Byte uploadType) {

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

    /**
     * 已经增补过的图片 ——> 正常上传
     *
     * @param customerId
     * @param type
     * @return
     */
    @Override
    public ResultBean<Void> moveOldSupplementToNormal(Long customerId, Byte type) {

        List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, type, null);
        if (!CollectionUtils.isEmpty(loanFileDOS)) {

            final LoanFileDO[] supplementFile = {null};
            final LoanFileDO[] normalFile = {null};

            loanFileDOS.parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(f -> {

                        if (UPLOAD_TYPE_SUPPLEMENT.equals(f.getType())) {
                            supplementFile[0] = f;
                        } else if (UPLOAD_TYPE_NORMAL.equals(f.getType())) {
                            normalFile[0] = f;
                        }
                    });

            if (ArrayUtils.isNotEmpty(supplementFile)) {
                LoanFileDO supplementFileDO = supplementFile[0];
                if (null != supplementFileDO) {

                    String existSupplementPath = supplementFileDO.getPath();
                    if (StringUtils.isNotBlank(existSupplementPath)) {

                        // B - y
                        List<String> existSupplementPathList = JSON.parseArray(existSupplementPath, String.class);
                        if (!CollectionUtils.isEmpty(existSupplementPathList)) {

                            // A -y  编辑
                            if (ArrayUtils.isNotEmpty(normalFile)) {
                                LoanFileDO normalFileDO = normalFile[0];
                                if (null != normalFileDO) {
                                    String existNormalPath = normalFileDO.getPath();
                                    if (StringUtils.isNotBlank(existNormalPath)) {
                                        List<String> existNormalPathList = JSON.parseArray(existNormalPath, String.class);

                                        existNormalPathList.add(existSupplementPath);
                                        normalFileDO.setPath(JSON.toJSONString(existNormalPathList));
                                        ResultBean<Void> updateResult = update(normalFileDO);
                                        Preconditions.checkArgument(updateResult.getSuccess(), updateResult.getMsg());

                                        supplementFileDO.setPath(null);
                                        ResultBean<Void> updateSupplementFileResult = update(supplementFileDO);
                                        Preconditions.checkArgument(updateSupplementFileResult.getSuccess(), updateSupplementFileResult.getMsg());
                                    }
                                }
                            } else {

                                // A -n  新增
                                LoanFileDO normalFileDO = new LoanFileDO();
                                normalFileDO.setPath(JSON.toJSONString(existSupplementPathList));
                                normalFileDO.setType(UPLOAD_TYPE_NORMAL);
                                normalFileDO.setCustomerId(customerId);

                                ResultBean<Long> insertResult = create(normalFileDO);
                                Preconditions.checkArgument(insertResult.getSuccess(), insertResult.getMsg());
                            }
                        }
                    }
                }
            }
        }

        return ResultBean.ofSuccess(null);
    }

    /**
     * 保存新增补的文件
     *
     * @param customerId
     * @param type
     * @param urls
     * @return
     */
    @Override
    public ResultBean<Void> saveNewSupplementFiles(Long customerId, Byte type, List<String> urls) {

        if (!CollectionUtils.isEmpty(urls)) {
            List<LoanFileDO> loanFileDOS = loanFileDOMapper.listByCustomerIdAndType(customerId, type, UPLOAD_TYPE_SUPPLEMENT);
            if (!CollectionUtils.isEmpty(loanFileDOS)) {
                LoanFileDO loanFileDO = loanFileDOS.get(0);
                if (null != loanFileDO) {

                    loanFileDO.setPath(JSON.toJSONString(urls));
                    ResultBean<Void> resultBean = update(loanFileDO);
                    Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
                } else {

                    loanFileDO.setType(type);
                    loanFileDO.setUploadType(UPLOAD_TYPE_SUPPLEMENT);
                    loanFileDO.setCustomerId(customerId);
                    loanFileDO.setPath(JSON.toJSONString(urls));
                    ResultBean<Long> resultBean = create(loanFileDO);
                    Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
                }
            } else {
                LoanFileDO loanFileDO = new LoanFileDO();
                loanFileDO.setType(type);
                loanFileDO.setUploadType(UPLOAD_TYPE_SUPPLEMENT);
                loanFileDO.setCustomerId(customerId);
                loanFileDO.setPath(JSON.toJSONString(urls));
                ResultBean<Long> resultBean = create(loanFileDO);
                Preconditions.checkArgument(resultBean.getSuccess(), resultBean.getMsg());
            }
        }

        return ResultBean.ofSuccess(null);
    }
}
