package com.yunche.loan.web.controller;


import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.DictMapCache;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.task.ThreadTask;
import com.yunche.loan.config.thread.ThreadPool;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/universal")
public class UniversalController {

    @Resource
    private LoanQueryService loanQueryService;

    @Resource
    private PartnerDOMapper partnerDOMapper;

    @Autowired
    private DictMapCache dictMapCache;

    @Autowired
    private MaterialService materialService;


    @GetMapping(value = "/customer")
    public ResultBean customerDetail(@RequestParam String customer_id) {

        return ResultBean.ofSuccess(loanQueryService.universalCustomerDetail(Long.valueOf(customer_id)));
    }

    @GetMapping(value = "/getPartnerLeaderId")
    public ResultBean getPartnerLeaderId(@RequestParam @Validated String partnerId) {
        PartnerDO partnerDO = partnerDOMapper.selectByPrimaryKey(Long.valueOf(partnerId), new Byte("0"));
        if (partnerDO == null) {
            throw new BizException("合伙人团队不存在");
        }
        if (partnerDO.getLeaderId() == null) {
            throw new BizException("请先设置合伙人leader");
        }
        return ResultBean.ofSuccess(partnerDO.getLeaderId());
    }

    @GetMapping("/ftp")
    public ResultBean ftp() {
        FtpUtil.icbcUpload("/tmp/9999.jpg");
        return ResultBean.ofSuccess("9999");
    }

    @GetMapping("/ftp2")
    public ResultBean ftp2() {
        FtpUtil.icbcUpload("/tmp/8888.jpg");
        return ResultBean.ofSuccess("8888");
    }

    @GetMapping("/dictmapcache")
    public ResultBean refreshDictMap() {
        dictMapCache.refreshAll();
        return ResultBean.ofSuccess(null, "刷新成功");
    }

    @GetMapping("/jjq")
    public ResultBean test1() {
        return materialService.downSupplementFiles2OSS(Long.valueOf("1809051406599576357"), true, Long.valueOf("193"));
    }


    // 文件下载
    @GetMapping("/downreport")
    public String downreport() throws UnsupportedEncodingException {

        try {
            List<Long> orderLists = Lists.newArrayList();
            orderLists.add(1806151541217761225l);
            orderLists.add(1806221600152528006l);
            orderLists.add(1807041505471350219l);
            orderLists.add(1807041514375640555l);
            final CountDownLatch latch = new CountDownLatch(orderLists.size());//使用java并发库concurrent
            for (int i = 0; i < orderLists.size(); i++) {
                System.out.println(i + "：" + System.currentTimeMillis());
                ThreadTask threadTask = new ThreadTask();
                threadTask.setOrderId(orderLists.get(i));
                Long orderId = orderLists.get(i);

                ThreadPool.executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread().getName() + ":" + orderId);
                        latch.countDown();
                    }
                });

                System.out.println(i + "：" + System.currentTimeMillis());
            }
            latch.await();
            //打包
            //
            Runtime.getRuntime().exec("");
            System.out.println("结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
