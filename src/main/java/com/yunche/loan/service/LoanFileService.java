package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.FileVO;

import java.util.List;


/**
 * @author liuzhe
 * @date 2018/3/6
 */
public interface LoanFileService {

    ResultBean<Void> create(Long customerId, List<FileVO> files);

    ResultBean<List<FileVO>> listByCustomerId(Long customerId);

    ResultBean<Void> update(Long customerId, List<FileVO> files);
}
