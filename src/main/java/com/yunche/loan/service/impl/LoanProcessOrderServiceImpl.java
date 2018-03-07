package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.LoanProcessOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author liuzhe
 * @date 2018/3/6
 */
@Service
@Transactional
public class LoanProcessOrderServiceImpl implements LoanProcessOrderService {

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;


    @Override
    public ResultBean<Long> create(LoanOrderDO loanOrderDO) {
        Long orderId = createOrderNum();
        loanOrderDO.setId(orderId);
        loanOrderDO.setGmtCreate(new Date());
        loanOrderDO.setGmtModify(new Date());
        int count = loanOrderDOMapper.insertSelective(loanOrderDO);
        Preconditions.checkArgument(count > 0, "创建客户信息失败");
        return ResultBean.ofSuccess(orderId);
    }

    /**
     * 生成业务单ID
     *
     * @return
     */
    private Long createOrderNum() {
        // 设置日期格式
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        // new Date()为获取当前系统时间，也可使用当前时间戳
        String orderNum = "" + df.format(new Date());

        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, 6);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        fixLenthString = fixLenthString.substring(1, 6);
        orderNum = orderNum + fixLenthString;

        return Long.valueOf(orderNum);
    }
}
