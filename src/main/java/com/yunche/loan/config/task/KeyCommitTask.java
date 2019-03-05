package com.yunche.loan.config.task;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.common.FinanceConfig;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.ModelsPara;
import com.yunche.loan.domain.param.RenewInsuranceParam;
import com.yunche.loan.domain.param.SeriesPara;
import com.yunche.loan.domain.vo.BaseBrandInitial;
import com.yunche.loan.domain.vo.CommonFinanceResult;
import com.yunche.loan.domain.vo.NeedSendMesOrders;
import com.yunche.loan.domain.vo.SDCOrders;
import com.yunche.loan.manager.finance.BusinessReviewManager;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.InsuranceUrgeService;
import com.yunche.loan.service.LoanCommitKeyService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
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

import static com.yunche.loan.config.constant.LoanProcessEnum.COMMIT_KEY;

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

    @Resource
    private InsuranceUrgeService insuranceUrgeService;


    @Autowired
    private PartnerWhiteListDOMapper partnerWhiteListDOMapper;

    @Autowired
    private LoanCommitKeyService loanCommitKeyService;


    //配置白名单后的合伙人，配置之后产生的待收钥匙流程自动提交--风险分担比不变
    @Scheduled(cron = "0 59 23 * * ?")
    @DistributedLock(200)
    public void keyWhiteListOrderAutoCommit()
    {

        //
        List<PartnerWhiteListDO> partnerWhiteListDOS = partnerWhiteListDOMapper.selectByOperationType(COMMIT_KEY.getCode());
        if (!CollectionUtils.isEmpty(partnerWhiteListDOS))
        {
            //找出合伙人下，待收钥匙单子，且生成待收钥匙代办时间>白名单修改时间
            partnerWhiteListDOS
                    .stream()
                    .forEach(
                            e ->
                            {
                                List<Long> longs = loanQueryDOMapper.selectNeedAutoKeyCommitOrderByPartnerId(e.getPartnerId());
                                if (!CollectionUtils.isEmpty(longs))
                                {
                                    longs.stream()
                                            .forEach(
                                                    f ->
                                                    {
                                                        //订单自动提交
                                                        try {
                                                            loanCommitKeyService.uncollected(f);
                                                        }catch (Exception ex)
                                                        {
                                                            LOG.info(f+"订单自动提交不交钥匙失败，请查看详情！");
                                                        }
                                                    }
                                            );
                                }

                            }
                    );


        }


    }



    @Scheduled(cron = "0 0 1 * * ?")
    @DistributedLock(200)
    public void setNeedSendMessageOrder()
    {

        LOG.info("开始取出23<（today-垫款日）<30 && 收钥匙状态=待收的订单");

        //取出15<（today-垫款日）<21&& 收钥匙状态=待收的订单
        List<NeedSendMesOrders> list = loanQueryDOMapper.selectHasRimitOrder();

       // BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(NEED_SEND_MESSAGE_ORDERS);

        if (!CollectionUtils.isEmpty(list))
        {
            BoundValueOperations<String, String> boundValueOps = stringRedisTemplate.boundValueOps(NEED_SEND_MESSAGE_ORDERS);
            boundValueOps.set(JSON.toJSONString(list));
        }

        LOG.info("结束取出23<（today-垫款日）<30 && 收钥匙状态=待收的订单");

    }

    @Scheduled(cron = "1 0 0 * * ?")
    @DistributedLock(200)
    public void setShutDownQueryCreditOrder()
    {

        LOG.info("开始取出距离垫款日30日，并且收钥匙状态=“待收” 的订单");
        //距离垫款日21日，并且收钥匙状态=“待收” 的订单
        List<SDCOrders> sdOrders = loanQueryDOMapper.selectShutDownQueryCreditOrder();

        refreshPartnerAndOrders(sdOrders);

        LOG.info("结束取出距离垫款日30日，并且收钥匙状态=“待收” 的订单");
    }

    @PostConstruct
    public void refreshShutdown()
    {
        setShutDownQueryCreditOrder();
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
        LOG.info("刷新结束取出距离垫款日30日，并且收钥匙状态=“待收” 的订单");
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
                        .filter(e -> !e.getOrderId().equals(orderId))
                        .collect(Collectors.toList());


                refreshPartnerAndOrders(collect);

        }

    }

    }

    public void refreshPartnerAndOrders(List<SDCOrders> collect)
    {
        LOG.info("刷新待收钥匙--合伙人和订单");

        List<Long> partners = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(collect))
        {

             partners =
                    collect
                            .stream()
                            .map( e -> e.getPartnerId())
                            .distinct()
                            .collect(Collectors.toList());

        }

        //封锁该合伙人团队查征信功能
        BoundValueOperations<String, String> boundValueOpsPartner = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_PARTNERS);
        boundValueOpsPartner.set(JSON.toJSONString(partners));

        BoundValueOperations<String, String> boundValueOpsOrders = stringRedisTemplate.boundValueOps(SHUTDOWN_QUERYCREDIT_ORDERS);
        //System.out.println("===="+JSON.toJSONString(collect));
        boundValueOpsOrders.set(JSON.toJSONString(collect));
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
                //取出不同的号码
                List<String> mobiles = Lists.newArrayList();
                list
                        .stream()
                        .forEach(e ->
                        {
                            if (!mobiles.contains(e.getLeaderMobile()))
                            {
                                mobiles.add(e.getLeaderMobile());
                            }
                        });

                //对每个号码进行发送提示
                LOG.info("开始发送短信！");
                //发送消息
                mobiles
                        .forEach(
                                e ->
                                {
                                    StringBuilder message = new StringBuilder();
                                    list.forEach(
                                            f ->
                                            {
                                                if (f.getLeaderMobile().equals(e))
                                                {
                                                    message.append(f.getCustomerName()).append("、");
                                                }

                                            }
                                    );

                                    message.deleteCharAt(message.length()-1);


                                    RenewInsuranceParam renewInsuranceParam = new RenewInsuranceParam();
                                    renewInsuranceParam.setTelphone(e);

                                    renewInsuranceParam.setMessage("您的客户:"+message+"尚未上缴车钥匙，逾期后将触发停止进件，请及时处理。【云车金融】");
                                    insuranceUrgeService.sendSms(renewInsuranceParam);

                                    LOG.info("发送短信！号码："+e+"====客户名："+message);
                                }
                        );

                LOG.info("结束发送短信！");

                /*LOG.info("开始发送短信！");
                //发送消息
                list
                        .stream()
                        .forEach(e ->
                        {
                            RenewInsuranceParam renewInsuranceParam = new RenewInsuranceParam();
                            renewInsuranceParam.setTelphone(e.getLeaderMobile());

                            renewInsuranceParam.setMessage("您的客户:"+e.getCustomerName()+"尚未上缴车钥匙，逾期后将触发停止进件，请及时处理。【云车金融】");
                            insuranceUrgeService.sendSms(renewInsuranceParam);

                            LOG.info("发送短信！号码："+e.getLeaderMobile()+"====客户名："+e.getCustomerName());

                        });

                LOG.info("结束发送短信！");*/
            }

        }

    }

}
