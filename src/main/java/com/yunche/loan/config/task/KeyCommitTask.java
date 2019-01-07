package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ModelsPara;
import com.yunche.loan.domain.param.SeriesPara;
import com.yunche.loan.domain.vo.BaseBrandInitial;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.domain.vo.NeedSendMesOrders;
import com.yunche.loan.domain.vo.SDCOrders;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.CarBrandDOMapper;
import com.yunche.loan.mapper.CarDetailDOMapper;
import com.yunche.loan.mapper.CarModelDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class KeyCommitTask
{

    private static final Logger LOG = LoggerFactory.getLogger(KeyCommitTask.class);

    private static final String NEED_SEND_MESSAGE_ORDERS = "commit-key:cache:send-message";

    private static final String SHUTDOWN_QUERYCREDIT_ORDERS = "commit-key:cache:shutdown-querycredit-orders";

    private static final String SHUTDOWN_QUERYCREDIT_PARTNERS = "commit-key:cache:shutdown-querycredit-partners";

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Scheduled(cron = "0 0 1 * * ?")
    @DistributedLock(200)
    public void setNeedSendMessageOrder()
    {

        //取出15<（today-垫款日）<21&& 收钥匙状态=待收的订单
        List<NeedSendMesOrders> list = loanQueryDOMapper.selectHasRimitOrder();

       // BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(NEED_SEND_MESSAGE_ORDERS);

        if (!CollectionUtils.isEmpty(list))
        {
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(NEED_SEND_MESSAGE_ORDERS);
            boundValueOps.set(JSON.toJSONString(list));
        }

    }

    @Scheduled(cron = "0 0 0 * * ?")
    @DistributedLock(200)
    public void setShutDownQueryCreditOrder()
    {
        //距离垫款日21日，并且收钥匙状态=“待收” 的订单
        List<SDCOrders> sdOrders = loanQueryDOMapper.selectShutDownQueryCreditOrder();

        refreshPartnerAndOrders(sdOrders);

    }


    public List<Long> getShutdownQuerycreditPartners()
    {
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_PARTNERS);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result))
        {
            Type type =new TypeToken<List<Long>>(){}  .getType();
            return JSON.parseObject(result, type);
        }

        return null;
    }


    public void refreshShutdownQuerycredit(Long orderId)
    {
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_ORDERS);
        String result = boundValueOps.get();
        if (StringUtils.isNotBlank(result))
        {
            Type type = new TypeToken<List<SDCOrders>>() {}.getType();
            List<SDCOrders> sdOrders = JSON.parseObject(result, type);

            if (!CollectionUtils.isEmpty(sdOrders))
            {
                List<SDCOrders> collect = sdOrders
                        .stream()
                        .filter(e -> e.getOrderId() != orderId)
                        .collect(Collectors.toList());


                refreshPartnerAndOrders(collect);

        }

    }

    }

    public void refreshPartnerAndOrders(List<SDCOrders> collect)
    {

        if (!CollectionUtils.isEmpty(collect))
        {

            List<Long> partners =
                    collect
                            .stream()
                            .map( e -> e.getPartnerId())
                            .distinct()
                            .collect(Collectors.toList());
            //封锁该合伙人团队查征信功能
            if (!CollectionUtils.isEmpty(partners))
            {
                BoundValueOperations<String, String> boundValueOpsPartner = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_PARTNERS);
                boundValueOpsPartner.set(JSON.toJSONString(partners));
            }

            BoundValueOperations<String, String> boundValueOpsOrders = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_ORDERS);
            boundValueOpsOrders.set(JSON.toJSONString(collect));
        }
    }



    @Scheduled(cron = "0 0 9 * * ?")
    @DistributedLock(200)
    public void sendMessage()
    {
        BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(NEED_SEND_MESSAGE_ORDERS);
        String result = boundValueOps.get();

        if (StringUtils.isNotBlank(result))
        {
            Type type =new TypeToken<List<NeedSendMesOrders>>(){}  .getType();
            List<NeedSendMesOrders> list =  JSON.parseObject(result, type);
            if (!CollectionUtils.isEmpty(list))
            {
                //发送消息
            }

        }

    }

}
