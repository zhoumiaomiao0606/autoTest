package com.yunche.loan.service.impl;

import com.yunche.loan.config.feign.client.TenantFeignClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.vo.DistributorVO;
import com.yunche.loan.service.DistributorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributorServiceImpl implements DistributorService {

    @Autowired
    private TenantFeignClient tenantFeignClient;
    @Override
    public ResultBean queryDistributor(String partnerId) {

        DistributorVO distributorVO = tenantFeignClient.queryDistributor(partnerId);
        List<DistributorVO.Distributor> datas = distributorVO.getDatas();
        return ResultBean.ofSuccess(datas);

    }
}
