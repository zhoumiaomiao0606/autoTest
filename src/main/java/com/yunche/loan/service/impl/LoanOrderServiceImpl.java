package com.yunche.loan.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.constant.ProcessActionEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.dao.mapper.CustBaseInfoDOMapper;
import com.yunche.loan.dao.mapper.CustRelaPersonInfoDOMapper;
import com.yunche.loan.dao.mapper.InstLoanOrderDOMapper;
import com.yunche.loan.dao.mapper.InstProcessNodeDOMapper;
import com.yunche.loan.domain.dataObj.CustBaseInfoDO;
import com.yunche.loan.domain.dataObj.CustRelaPersonInfoDO;
import com.yunche.loan.domain.dataObj.InstLoanOrderDO;
import com.yunche.loan.domain.dataObj.InstProcessNodeDO;
import com.yunche.loan.domain.queryObj.OrderListQuery;
import com.yunche.loan.domain.viewObj.CustBaseInfoVO;
import com.yunche.loan.domain.viewObj.InstLoanOrderVO;
import com.yunche.loan.domain.viewObj.InstProcessNodeVO;
import com.yunche.loan.service.LoanOrderService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by zhouguoliang on 2018/2/5.
 */
@Service
public class LoanOrderServiceImpl implements LoanOrderService {
    @Autowired
    private InstLoanOrderDOMapper instLoanOrderDOMapper;

    @Autowired
    private CustBaseInfoDOMapper custBaseInfoDOMapper;

    @Autowired
    private InstProcessNodeDOMapper instProcessNodeDOMapper;

    @Autowired
    private CustRelaPersonInfoDOMapper custRelaPersonInfoDOMapper;

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
        Set<Long> orderIfSets = Sets.newHashSet();
        for (InstLoanOrderDO instLoanOrderDO : instLoanOrderDOList) {
            if (orderIfSets.contains(instLoanOrderDO.getOrderId())) continue;
            orderIfSets.add(instLoanOrderDO.getOrderId());
            InstLoanOrderVO instLoanOrderVO = new InstLoanOrderVO();
            instLoanOrderVOList.add(instLoanOrderVO);
            BeanUtils.copyProperties(instLoanOrderDO, instLoanOrderVO);
            CustBaseInfoDO custBaseInfoDO = custBaseInfoDOMapper.selectByPrimaryKey(instLoanOrderVO.getCustId());
            CustBaseInfoVO custBaseInfoVO = new CustBaseInfoVO();
            BeanUtils.copyProperties(custBaseInfoDO, custBaseInfoVO);
            instLoanOrderVO.setCustBaseInfoVO(custBaseInfoVO);

            if (!StringUtils.isEmpty(orderListQuery.getDoneProcessTask())) {
                List<InstProcessNodeDO> instProcessNodeDOList = instProcessNodeDOMapper.selectByOrderIdAndNodeCode(instLoanOrderDO.getOrderId(), orderListQuery.getDoneProcessTask());
                if (CollectionUtils.isNotEmpty(instProcessNodeDOList)) {
                    InstProcessNodeDO instProcessNodeDO = instProcessNodeDOList.get(0);
                    if (ProcessActionEnum.PASS.name().equals(instProcessNodeDO.getStatus())) {
                        instLoanOrderVO.setAction(ProcessActionEnum.PASS.getDetail());
                    } else if (ProcessActionEnum.REJECT.name().equals(instProcessNodeDO.getStatus())) {
                        instLoanOrderVO.setAction(ProcessActionEnum.REJECT.getDetail());
                    } else if (ProcessActionEnum.CANCEL.name().equals(instProcessNodeDO.getStatus())) {
                        instLoanOrderVO.setAction(ProcessActionEnum.CANCEL.getDetail());
                    }
                }
            }
        }

        return ResultBean.ofSuccess(instLoanOrderVOList, "条件查询订单成功");
    }

    @Override
    public ResultBean<InstLoanOrderVO> detail(Long orderId) {
        // 订单基本信息
        InstLoanOrderDO instLoanOrderDO = instLoanOrderDOMapper.selectByPrimaryKey(orderId);
        InstLoanOrderVO instLoanOrderVO = new InstLoanOrderVO();
        BeanUtils.copyProperties(instLoanOrderDO, instLoanOrderVO);

        // 客户信息
        CustBaseInfoDO custBaseInfoDO = custBaseInfoDOMapper.selectByPrimaryKey(instLoanOrderVO.getCustId());
        CustBaseInfoVO custBaseInfoVO = new CustBaseInfoVO();
        BeanUtils.copyProperties(custBaseInfoDO, custBaseInfoVO);
        List<CustRelaPersonInfoDO> custRelaPersonInfoDOList = custRelaPersonInfoDOMapper.selectByRelaCustId(custBaseInfoDO.getCustId());
        if (CollectionUtils.isNotEmpty(custRelaPersonInfoDOList)) {
            for (CustRelaPersonInfoDO custRelaPersonInfoDO : custRelaPersonInfoDOList) {
                if ("共贷人".equals(custRelaPersonInfoDO.getRelaCustType())) {
                    custBaseInfoVO.setShareLoanPerson(custRelaPersonInfoDO);
                } else if ("担保人".equals(custRelaPersonInfoDO.getRelaCustType())) {
                    custBaseInfoVO.setGuarantPerson(custRelaPersonInfoDO);
                } else if ("反担保人".equals(custRelaPersonInfoDO.getRelaCustType())) {
                    custBaseInfoVO.setBackGuarantorPerson(custRelaPersonInfoDO);
                }
            }
        }
        instLoanOrderVO.setCustBaseInfoVO(custBaseInfoVO);

        // 流程操作记录
        List<InstProcessNodeDO> instProcessNodeDOList = instProcessNodeDOMapper.selectByOrderId(orderId);
        if (CollectionUtils.isNotEmpty(instProcessNodeDOList)) {
            List<InstProcessNodeVO> instProcessNodeVOList = Lists.newArrayList();
            for (InstProcessNodeDO instProcessNodeDO : instProcessNodeDOList) {
                InstProcessNodeVO instProcessNodeVO = new InstProcessNodeVO();
                BeanUtils.copyProperties(instProcessNodeDO, instProcessNodeVO);
                instProcessNodeVOList.add(instProcessNodeVO);
            }
            instLoanOrderVO.setProcessRecordList(instProcessNodeVOList);
        }

        return ResultBean.ofSuccess(instLoanOrderVO, "查询订单详情成功");
    }
}
