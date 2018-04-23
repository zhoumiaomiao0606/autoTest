package com.yunche.loan.service;

import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.param.MaterialUpdateParam;
import com.yunche.loan.domain.vo.RecombinationVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface MaterialService {

    public RecombinationVO detail(Long orderId);

    public void update(MaterialUpdateParam param);


    /**
     * 资料审核文件下载
     * @param httpServletRequest
     * @param httpServletResponse
     * @param orderId
     */
    public String downloadFiles(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,Long orderId);

    /**
     *
     * @param request
     * @param response
     * @return
     */
    public ResultBean zipFilesDown(HttpServletRequest request, HttpServletResponse response, Long orderId);
}
