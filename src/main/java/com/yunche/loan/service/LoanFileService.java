package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.vo.FileVO;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanFileService {

    ResultBean<Long> create(LoanFileDO loanFileDO);

    ResultBean<Void> update(LoanFileDO loanFileDO);

    /**
     * 文件打包
     *
     * @param customerId
     * @param fileTypes
     * @param retryNum   重试次数
     */
    void asyncPackZipFile(Long customerId, List<Byte> fileTypes, Integer retryNum);

    ResultBean<List<FileVO>> listByCustomerId(Long customerId, Byte fileUploadType);

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

    /**
     * 编辑 or 新增 文件信息
     *
     * @param customerId
     * @param files
     * @param uploadType
     * @return
     */
    ResultBean<Void> updateOrInsertByCustomerIdAndUploadType(Long customerId, List<FileVO> files, Byte uploadType);

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
