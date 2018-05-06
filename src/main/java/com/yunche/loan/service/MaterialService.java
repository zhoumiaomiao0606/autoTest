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
     * @param orderId
     */
    public ResultBean downloadFilesToOSS(Long orderId);

    /**
     * 浏览器直接下载
     * @param request
     * @param response
     * @param orderId
     * @param taskDefinitionKey
     * @return
     */
    public String zipFilesDown(HttpServletRequest request, HttpServletResponse response,
                               Long orderId,String taskDefinitionKey,Long  customerId);

    /**
     * 中转tomcat下载
     * @param orderId
     * @param taskDefinitionKey
     * @param customerId
     * @return
     */
    public ResultBean down2tomcat( Long orderId,String taskDefinitionKey,Long customerId);
}
