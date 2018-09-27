package com.yunche.loan.web.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.DictMapCache;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
import com.yunche.loan.domain.vo.CreditPicExportVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.LoanStatementDOMapper;
import com.yunche.loan.mapper.PartnerDOMapper;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;


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
    @PostMapping(value = "/downreport", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String downreport(@RequestBody LoanCreditExportQuery loanCreditExportQuery) throws UnsupportedEncodingException {

        try {
            //查询符合要求的数据
            List<CreditPicExportVO> creditPicExportVOS = loanStatementDOMapper.selectCreditPicExport(loanCreditExportQuery);
            long start = System.currentTimeMillis();
            String name = SessionUtils.getLoginUser().getName();
            String dir = name+ DateUtil.getTime();
            final String localPath ="/tmp/"+dir+"/";
            RuntimeUtils.exe("mkdir "+localPath);
            creditPicExportVOS.stream().filter(Objects::nonNull).forEach(e->{
                //查图片
                Set types = Sets.newHashSet();
                //1:合成身份证图片 , 2:合成图片
                if("1".equals(loanCreditExportQuery.getMergeFlag())){
                    types.add(new Byte("2"));
                    types.add(new Byte("3"));
                }else{
                    types.add(new Byte("2"));
                    types.add(new Byte("3"));
                    types.add(new Byte("4"));
                    types.add(new Byte("5"));
                }
                String fileName = e.getOrderId()+e.getCustomerName()+e.getIdCard()+ IDict.K_SUFFIX.K_SUFFIX_JPG;

                List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(e.getLoanCustomerId(), types);
                List<String> urls = Lists.newLinkedList();
                for (UniversalMaterialRecordVO V : list) {
                    urls.addAll(V.getUrls());
                }
                ImageUtil.mergetImage2PicByConvert(localPath,fileName,urls);
                System.out.println(Thread.currentThread()+"图片合成");
            });



//            final CountDownLatch latch = new CountDownLatch(creditPicExportVOS.size());//使用java并发库concurrent
//
//            Runtime.getRuntime().exec("mkdir "+localPath);
//
//            long start = System.currentTimeMillis();
//            System.out.println("开始时间"+start);
//            for (int i = 0; i < creditPicExportVOS.size(); i++) {
//
//                ThreadTask threadTask = new ThreadTask(loanQueryDOMapper,latch,creditPicExportVOS.get(i),loanCreditExportQuery.getMergeFlag(),localPath);
//                ThreadPool.executorService.execute(threadTask);
//
//            }
//            latch.await();
//            //打包
            long end = System.currentTimeMillis();
            System.out.println("结束时间："+end);
            System.out.println("总用时："+(end-start)/1000);

            Runtime.getRuntime().exec("tar -zcvf "+localPath+".tar.gz "+localPath);
            System.out.println("结束时间"+System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "压缩完成";

    }
}
