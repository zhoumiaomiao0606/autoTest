package com.yunche.loan.service.impl;

import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CustBaseInfoDOMapper;
import com.yunche.loan.dao.mapper.InstLoanOrderDOMapper;
import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.service.LoanOrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class LoanOrderServiceImpl implements LoanOrderService {
    @Autowired
    private InstLoanOrderDOMapper instLoanOrderDOMapper;

    @Autowired
    private CustBaseInfoDOMapper custBaseInfoDOMapper;

    @Override
    public ResultBean<InstLoanOrderDO> create(String processInstanceId) {
        InstLoanOrderDO instLoanOrderDO = new InstLoanOrderDO();
        instLoanOrderDO.setProcessInstId(processInstanceId);
        instLoanOrderDO.setStatus(0);

        // 生成订单编号
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        String orderNbr = "YC"+df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, 6);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        fixLenthString = fixLenthString.substring(1, 7);
        orderNbr = orderNbr + fixLenthString;
        instLoanOrderDO.setOrderNbr(orderNbr);

        instLoanOrderDOMapper.insert(instLoanOrderDO);

        return ResultBean.ofSuccess(instLoanOrderDO, "创建订单成功");
    }

    @Override
    public ResultBean<InstLoanOrderDO> update(InstLoanOrderVO instLoanOrderVO) {
        InstLoanOrderDO instLoanOrderDO = new InstLoanOrderDO();
        BeanUtils.copyProperties(instLoanOrderVO, instLoanOrderDO);
//        instLoanOrderDO.setStatus(0);
        instLoanOrderDOMapper.updateByPrimaryKeySelective(instLoanOrderDO);

        return ResultBean.ofSuccess(instLoanOrderDO, "更新订单成功");
    }

    @Override
    public ResultBean<List<InstLoanOrderVO>> queryOrderList(OrderListQuery orderListQuery) {
        List<InstLoanOrderDO> instLoanOrderDOList = instLoanOrderDOMapper.queryByCondition(orderListQuery);
        List<InstLoanOrderVO> instLoanOrderVOList = Lists.newArrayList();
        for (InstLoanOrderDO instLoanOrderDO : instLoanOrderDOList) {
            InstLoanOrderVO instLoanOrderVO = new InstLoanOrderVO();
            instLoanOrderVOList.add(instLoanOrderVO);
            BeanUtils.copyProperties(instLoanOrderDO, instLoanOrderVO);
            CustBaseInfoDO custBaseInfoDO = custBaseInfoDOMapper.selectByPrimaryKey(instLoanOrderVO.getCustId());
            CustBaseInfoVO custBaseInfoVO = new CustBaseInfoVO();
            BeanUtils.copyProperties(custBaseInfoDO, custBaseInfoVO);
            instLoanOrderVO.setCustBaseInfoVO(custBaseInfoVO);
        }

        return ResultBean.ofSuccess(instLoanOrderVOList, "条件查询订单成功");
    }
}
