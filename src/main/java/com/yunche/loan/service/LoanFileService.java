package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.param.LoanFileParam;
import com.yunche.loan.domain.vo.FileVO;

import java.io.File;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanFileService {

    ResultBean<Long> create(LoanFileDO loanFileDO);

    ResultBean<Void> update(LoanFileDO loanFileDO);

    ResultBean<List<FileVO>> listByCustomerId(Long customerId);

    /**
     * @param customerId
     * @param uploadType
     * @return
     */
    ResultBean<List<FileVO>> listByCustomerIdAndUploadType(Long customerId, Byte uploadType);

    /**
     * 文件上传
     *
     * @param fileParams
     * @return
     */
//    ResultBean<Void> upload(List<LoanFileParam> fileParams);

    /**
     * 批量插入
     *
     * @param loanFileDOS
     * @return
     */
    ResultBean<Void> batchInsert(List<LoanFileDO> loanFileDOS);

    ResultBean<Void> updateByCustomerIdAndUploadType(Long customerId, List<FileVO> files, Byte uploadType);

    ResultBean<Void> batchInsert(Long customerId, List<FileVO> files);

    /**
     * 已经增补过的图片 ——> 正常上传
     *
     * @param customerId
     * @param type
     * @return
     */
    ResultBean<Void> moveOldSupplementToNormal(Long customerId, Byte type);

    /**
     * 保存新增补的文件
     *
     * @param customerId
     * @param type
     * @param urls
     * @return
     */
    ResultBean<Void> saveNewSupplementFiles(Long customerId, Byte type, List<String> urls);
}
