package com.yunche.loan.web.controller;


import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.anno.Limiter;
import com.yunche.loan.config.cache.DictMapCache;
import com.yunche.loan.config.cache.ParamCache;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.client.MultimediauploadClient;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.PartnerDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.MultimediaUploadParam;
import com.yunche.loan.domain.query.LoanCreditExportQuery;
import com.yunche.loan.domain.vo.CreditPicExportVO;
import com.yunche.loan.domain.vo.UniversalMaterialRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.LoanQueryService;
import com.yunche.loan.service.MaterialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.lang.Process;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/loanorder/universal")
public class UniversalController {

    private static final Logger LOG = LoggerFactory.getLogger(UniversalController.class);


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

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private ParamCache paramCache;


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

    @GetMapping("/oss")
    public ResultBean ossTest() {
        OSSClient ossClient = OSSUnit.getOSSClient();
        String url="img/2018/201804/20180424/Z7M7TCTF3Y.jpg";
        try{
            OSSObject object = OSSUnit.getObject(ossClient, url);
        }catch (Exception e){
            LOG.info(e.getMessage());
        }

        LOG.info("oss链接成功：" + System.currentTimeMillis());
        String diskName = ossConfig.getZipDiskName();
        String bucketName = ossConfig.getZipBucketName();
        File file = new File("/tmp/hhhhhhhhhhhhh.txt");
        OSSUnit.uploadObject2OSS(ossClient, file, bucketName, diskName + File.separator);

        LOG.info("文件上传成功：" + System.currentTimeMillis());
        return ResultBean.ofSuccess("9999");
    }

    @GetMapping("/ftp2")
    public ResultBean ftp2() {
        FtpUtil.icbcUpload("/tmp/8888.jpg");
        return ResultBean.ofSuccess("8888");
    }

    @GetMapping("/refresh")
    public ResultBean refreshDictMap() {
        dictMapCache.refreshAll();
        paramCache.refreshAll();
        return ResultBean.ofSuccess(null, "刷新成功");
    }

    @GetMapping("/jjq")
    public ResultBean test1() {
        return materialService.downSupplementFiles2OSS(Long.valueOf("1809051406599576357"), true, Long.valueOf("193"));
    }
    @PostMapping("/t_vouchet")
    public ResultBean voucher(@RequestBody  ApprovalParam param){
        EventBusCenter.eventBus.post(param);
        return ResultBean.ofSuccess("987989880980980909");
    }


    // 文件下载
    @Limiter(1)
    @PostMapping(value = "/downreport", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResultBean downreport(@RequestBody LoanCreditExportQuery loanCreditExportQuery) {

        OSSClient ossUnit=null;
        String resultName= null;
        String diskName =null;
        List<CreditPicExportVO> exportVOS =Lists.newLinkedList();
        try {

            ossUnit = OSSUnit.getOSSClient();
            //查询符合要求的数据
            List<CreditPicExportVO> creditPicExportVOS = loanStatementDOMapper.selectCreditPicExport(loanCreditExportQuery);


            if(CollectionUtils.isEmpty(creditPicExportVOS)){
                return ResultBean.ofError("筛选条件查询记录为空");
            }
            if(creditPicExportVOS.size()>50){
                exportVOS = creditPicExportVOS.subList(0, 50);
            }else{
                exportVOS = creditPicExportVOS;
            }


            long start = System.currentTimeMillis();
            String name = SessionUtils.getLoginUser().getName();
            diskName = name+ DateUtil.getTime();
            final String localPath ="/tmp/"+diskName;

            resultName = diskName+".tar.gz";

            RuntimeUtils.exe("mkdir "+localPath);
            LOG.info("图片合成 开始时间："+start);
            exportVOS.stream().filter(Objects::nonNull).forEach(e->{
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
                try{
                    ImageUtil.mergetImage2PicByConvert(localPath+ File.separator,fileName,urls);
                    LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(e.getLoanCustomerId(), VALID_STATUS);
                    if(loanCustomerDO!=null){
                        loanCustomerDO.setCreditExpFlag(IDict.K_CREDIT_PIC_EXP.K_SUFFIX_JPG_YES);
                        loanCustomerDOMapper.updateByPrimaryKeySelective(loanCustomerDO);
                    }
                }catch (Exception ex){
                    LOG.info(e.getCustomerName()+"：图片合成失败["+fileName+"]");
                }
            });

            Process exec = Runtime.getRuntime().exec("tar -cPf " + "/tmp/" + resultName + " " + localPath);
            exec.waitFor();
            if(exec.exitValue()!=0){
                throw new BizException("压缩文件出错啦");
            }

            File file = new File("/tmp/" + resultName);



            OSSUnit.uploadObject2OSS(ossUnit, file, ossConfig.getBucketName(), ossConfig.getDownLoadDiskName()+File.separator);
            long end = System.currentTimeMillis();
            LOG.info("图片合成 结束时间："+end);
            LOG.info("总用时："+(end-start)/1000);

            LOG.info("打包结束啦啦啦啦啦啦啦");

        } catch (Exception e) {
           throw new BizException(e.getMessage());
        }

        return ResultBean.ofSuccess(ossConfig.getDownLoadDiskName()+File.separator+resultName);

    }

    @Autowired
    private BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    private MultimediauploadClient multimediauploadClient;
    @GetMapping("/videopush")
    public ResultBean videoPush(){
        List<String> videoPush = bankInterfaceSerialDOMapper.videoPush();
        for(String orderId:videoPush){
            MultimediaUploadParam param = new MultimediaUploadParam();
            param.setOrderId(orderId);
            LOG.info(param.getOrderId()+":视频推送开始");
            multimediauploadClient.multimediaUpload(param);
            try {
                Thread.sleep(1000*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ResultBean.ofSuccess("推送完成");
    }
}
