package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.param.CarUpdateParam;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface MaterialService {

    RecombinationVO detail(Long orderId);

    void update(MaterialUpdateParam param);

    /**
     * 资料审核文件下载
     *
     * @param orderId
     * @param reGenerateZip 是否重新生成zip包
     */
    ResultBean<String> downloadFiles2OSS(Long orderId, Boolean reGenerateZip);

    /*
    *提车资料下载
     */
    ResultBean<String> downCarFiles2OSS(Long orderId, Boolean reGenerateZip);

    /**
     *资料增补文件下载
     */
    ResultBean<String> downSupplementFiles2OSS(Long orderId, Boolean reGenerateZip,Long infoSupplementId);


    /**
     * 浏览器直接下载
     *
     * @param request
     * @param response
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    String zipFilesDown(HttpServletRequest request, HttpServletResponse response,
                        Long orderId, String taskDefinitionKey, Long customerId);

    void carUpdate(CarUpdateParam param);


    ResultBean zipCheck(Long orderId);

    ResultBean zipSupCheck(Long orderId);

    List<LoanFileDO> selectAllSupFileByOrderId(Long orderId);
}
