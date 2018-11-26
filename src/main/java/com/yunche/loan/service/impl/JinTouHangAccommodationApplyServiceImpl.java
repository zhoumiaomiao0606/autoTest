package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunche.loan.config.cache.BankCache;
import com.yunche.loan.config.common.JtxConfig;
import com.yunche.loan.config.common.OSSConfig;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.constant.LoanFileEnum;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.config.util.Process;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.AccommodationApplyParam;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.param.ExportApplyLoanPushParam;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.JinTouHangAccommodationApplyService;
import com.yunche.loan.service.LoanProcessBridgeService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yunche.loan.config.constant.LoanProcessEnum.BRIDGE_HANDLE;
import static com.yunche.loan.config.constant.ProcessApprovalConst.ACTION_ROLL_BACK;

@Service
public class JinTouHangAccommodationApplyServiceImpl implements JinTouHangAccommodationApplyService {

    private Logger logger = LoggerFactory.getLogger(JinTouHangAccommodationApplyServiceImpl.class);
    @Autowired
    private ThirdPartyFundBusinessDOMapper thirdPartyFundBusinessDOMapper;

    @Autowired
    private LoanProcessBridgeService loanProcessBridgeService;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    private OSSConfig ossConfig;

    @Autowired
    private LoanStatementDOMapper loanStatementDOMapper;


    @Autowired
    private ConfThirdRealBridgeProcessDOMapper confThirdRealBridgeProcessDOMapper;

    @Autowired
    private ConfThirdPartyMoneyDOMapper confThirdPartyMoneyDOMapper;

    @Autowired
    private BankCache bankCache;

    @Autowired
    private BankLendRecordDOMapper bankLendRecordDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private LoanHomeVisitDOMapper loanHomeVisitDOMapper;

    @Resource
    private AsyncUpload asyncUpload;

    @Autowired
    private JtxCommunicationDOMapper jtxCommunicationDOMapper;

    @Autowired
    private LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    private LoanFileDOMapper loanFileDOMapper;

    @Autowired
    private JTXCommunicationUtil jtxCommunicationUtil;

    @Autowired
    private JtxConfig jtxConfig;

    @Autowired
    private LoanProcessBridgeDOMapper loanProcessBridgeDOMapper;

    @Autowired
    private JtxReturnFileDOMapper jtxReturnFileDOMapper;


//    @Override
//    public ResultBean revoke(AccommodationApplyParam param) {
//        return null;
//    }

    /**
     * 提交接口 业务后处理
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public void dealTask(ApprovalParam param) {

        //金投行过桥处理 -反审
        if (ACTION_ROLL_BACK.equals(param.getAction()) && BRIDGE_HANDLE.getCode().equals(param.getTaskDefinitionKey())) {
            ThirdPartyFundBusinessDO businessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getProcessId());
            businessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
            int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(businessDO);
            Preconditions.checkArgument(count > 0, "更新状态[未出借]失败");
        }

    }

    /**
     * 拒绝出借
     *
     * @param param
     * @return
     */
    @Override
    @Transactional
    public ResultBean reject(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");
        Preconditions.checkNotNull(param.getLendStatus(), "出借状态不能为空");
        int count;
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        ThirdPartyFundBusinessDO fundBusinessDO = new ThirdPartyFundBusinessDO();
        fundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        fundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        fundBusinessDO.setLendStatus(param.getLendStatus());
        if (thirdPartyFundBusinessDO == null) {
            fundBusinessDO.setGmtCreate(new Date());
            count = thirdPartyFundBusinessDOMapper.insertSelective(fundBusinessDO);
        } else {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(fundBusinessDO);
        }
        Preconditions.checkArgument(count > 0, "拒绝出借操作失败");
        return ResultBean.ofSuccess(null, "拒绝出借操作成功");
    }

    /**
     * 借款
     *
     * @return
     */
    @Override
    public ResultBean applyLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lenDate = sdf.format(param.getLendDate());

        ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, aDo);
        aDo.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        aDo.setOrderId(param.getIdPair().getOrderId());
        aDo.setLendStatus(IDict.K_CJZT.K_CJZT_INHAND);
        aDo.setLendAmount(param.getLendAmount());
        ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        int count;
        if (fundBusinessDO != null) {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
        } else {
            count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
        }
        Preconditions.checkArgument(count > 0, "保存失败");

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(param.getIdPair().getOrderId());
        LoanHomeVisitDO loanHomeVisitDO = new LoanHomeVisitDO();
        loanHomeVisitDO.setId(loanOrderDO.getLoanHomeVisitId());
        loanHomeVisitDO.setDebitCard(param.getBankCard());
        loanHomeVisitDOMapper.updateByPrimaryKeySelective(loanHomeVisitDO);
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),new Byte("0"));
        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.selectByPrimaryKey(loanOrderDO.getLoanBaseInfoId());
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
        Set types = Sets.newHashSet();
        types.add(LoanFileEnum.ID_CARD_FRONT.getType());
        types.add(LoanFileEnum.ID_CARD_BACK.getType());
        types.add(LoanFileEnum.VISIT_DOOR_CARD.getType());
        types.add(LoanFileEnum.LOAN_VOUCHER.getType());
        types.add(LoanFileEnum.DRIVER_LICENSE.getType());
        types.add(LoanFileEnum.DRIVING_LICENSE.getType());
        List<UniversalMaterialRecordVO> list = loanQueryDOMapper.selectUniversalCustomerFiles(loanOrderDO.getLoanCustomerId(), types);
        List<String> urls = Lists.newLinkedList();
        for (UniversalMaterialRecordVO V : list) {
            urls.addAll(V.getUrls());
        }
        asyncUpload.execute(new Process() {
            @Override
            public void process() {
                try{
                    String interest = param.getLendAmount().multiply(new BigDecimal("0.8").multiply(new BigDecimal("60").divide(new BigDecimal("365"),2,BigDecimal.ROUND_HALF_UP))).multiply(new BigDecimal("100")).setScale(0,BigDecimal.ROUND_HALF_UP)+"";
                    Map resultMap = jtxCommunicationUtil.borrowerInfoAuth(loanCustomerDO.getName(),loanCustomerDO.getIdCard(),param.getTel(),
                            loanBaseInfoDO.getBank(),param.getBankCard(),param.getIdPair());
                    if((Boolean) resultMap.get("FLAG")){
                        Map resultMap1 = jtxCommunicationUtil.assetRelease((String) resultMap.get("REF"),"云车-"+(String) resultMap.get("REF"),param.getLendAmount().multiply(new BigDecimal("100"))+"",
                                "800", lenDate,"60", interest,"BYMONTH",
                                "YC","车",loanFinancialPlanDO.getAppraisal().multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString(),"0",loanCustomerDO.getIdCard());
                        if((Boolean) resultMap1.get("FLAG")){
                            String path ="";
                            String jtxFtpPath = "/root/yunche/reqFile/";
                            path = zipFile(urls);
                            if(!"".equals(path)){
                                File file = new File(path);
                                InputStream is = null;
                                JTXFileUtil sftp = new JTXFileUtil("root", "jtx@1722", "183.136.187.207", 22);
                                is = new FileInputStream(file);
                                sftp.login();
                                sftp.upload(jtxFtpPath, file.getName(), is);
                                sftp.logout();
                                file.delete();
                                boolean flag = jtxCommunicationUtil.enclosureUpdate(file.getName(),jtxFtpPath+DateUtil.getDateTo8(new Date()),(String)resultMap1.get("AssetSn"));
                                if(flag){
                                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                                    thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
                                    thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_SUCCESS);
                                    thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                                }else{
                                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                                    thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
                                    thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_ASSETRELEASE_ERROR);
                                    thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                                }
                            }
                        }else{
                            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                            thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
                            thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_ASSETRELEASE_ERROR);
                            thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                        }
                    }else{
                        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
                        thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_AUTHINFIO_ERROR);
                        thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                    }

                }catch(Exception e){
                    logger.error("与金投行通讯异常",e);
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                    thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
                    thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_ASSETRELEASE_ERROR);
                    thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                }
            }
        });


        return ResultBean.ofSuccess("借款申请发起成功");
    }

    private String zipFile(List<String> urls){
        String resultZipPath="";
        List<File> fileList= Lists.newLinkedList();
        String localPath = jtxConfig.getJtxTempDirSend();
        fileList = urls.stream().map(pic -> {
            File file=null;
            try {
                file = new File(localPath +"JTX"+GeneratorIDUtil.execute() + IDict.K_SUFFIX.K_SUFFIX_JPG);
                InputStream oss2InputStream = OSSUnit.getOSS2InputStream(pic);
                FileUtils.copyInputStreamToFile(oss2InputStream, file);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return file;
        }).collect(Collectors.toList());
        File zipFile = new File(localPath + "/" + "JTX"+GeneratorIDUtil.execute() +".zip");
        FileOutputStream fos = null;
        ZipOutputStream zos = null;
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try{
            fos = new FileOutputStream(zipFile);
            zos = new ZipOutputStream(new BufferedOutputStream(fos));
            byte[] bufs = new byte[1024*10];
            for(int i=0;i<fileList.size();i++){

                ZipEntry zipEntry = new ZipEntry(fileList.get(i).getName());
                zos.putNextEntry(zipEntry);
                //读取待压缩的文件并写进压缩包里
                fis = new FileInputStream(fileList.get(i));
                bis = new BufferedInputStream(fis, 1024*10);
                int read = 0;
                while((read=bis.read(bufs, 0, 1024*10)) != -1){
                    zos.write(bufs,0,read);
                }
            }
            resultZipPath = zipFile.getPath();
        }catch(Exception e){
            throw new RuntimeException(e);
        }finally{
            //关闭流
            try {
                if(null != bis) bis.close();
                if(null != zos) zos.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            for(File file:fileList){
                file.delete();
            }
            return resultZipPath;
        }


    }


    @Override
    @Transactional
    public ResultBean applyOldLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair(), "参数有误");

        ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, aDo);
        aDo.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        aDo.setOrderId(param.getIdPair().getOrderId());
        aDo.setLendStatus(IDict.K_CJZT.K_CJZT_YES);
        aDo.setLendAmount(param.getLendAmount());
        ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(param.getIdPair().getBridgeProcessId());
        int count;
        if (fundBusinessDO != null) {
            count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
        } else {
            count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
        }
        Preconditions.checkArgument(count > 0, "保存失败");
        return ResultBean.ofSuccess("借款申请发起成功");
    }

    /**
     * 批量贷款
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBean batchLoan(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        List<AccommodationApplyParam.IDPair> idPairs = param.getIdPairs();
//        List<Long> orderIds = param.getOrderIds();

        List<ThirdPartyFundBusinessDO> collect = idPairs.stream()
                .filter(Objects::nonNull)
                .map(e -> {
                    ThirdPartyFundBusinessDO aDo = new ThirdPartyFundBusinessDO();
                    aDo.setBridgeProcecssId(e.getBridgeProcessId());
                    aDo.setOrderId(e.getOrderId());
                    aDo.setLendDate(param.getLendDate());
                    aDo.setGmtCreate(new Date());

                    ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(e.getBridgeProcessId());
                    int count;
                    if (fundBusinessDO != null) {
                        count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(aDo);
                    } else {
                        count = thirdPartyFundBusinessDOMapper.insertSelective(aDo);
                    }
                    Preconditions.checkArgument(count > 0, "插入失败");
                    return aDo;
                }).collect(Collectors.toList());

        //批量导入、提交
        if (!CollectionUtils.isEmpty(collect)) {

            ApprovalParam approvalParam = new ApprovalParam();
            approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
            approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
            approvalParam.setNeedLog(true);
            approvalParam.setCheckPermission(false);

            //提交任务
            collect.stream().forEach(e -> {

                approvalParam.setOrderId(e.getOrderId());
                approvalParam.setProcessId(e.getBridgeProcecssId());


                ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
            });
        }
        return ResultBean.ofSuccess("借款申请成功");
    }

    /**
     * 导入文件
     *
     * @param key
     * @return
     */
    @Override
    @Transactional
    public ResultBean batchImp(String key) {
        Preconditions.checkNotNull(key, "文件key不能为空");
        List<ThirdPartyFundBusinessDO> partyFundBusinessDOList = Lists.newArrayList();

        try {
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, key);
            if (!CollectionUtils.isEmpty(rowList)) {
                Preconditions.checkArgument(rowList.size() <= 2000, "最大支持导入2000条数据，当前条数：" + rowList.size());

                for (int i = 0; i < rowList.size(); i++) {
                    // 当前行数
                    int rowNum = i + 1;

                    String[] row = rowList.get(i);
                    // 空行跳过
                    if (ArrayUtils.isEmpty(row)) {
                        continue;
                    }
                    ThirdPartyFundBusinessDO partyFundBusinessDO = new ThirdPartyFundBusinessDO();
                    try {
                        partyFundBusinessDO.setBridgeProcecssId(Long.valueOf(row[0]));//流水号
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第1列格式有误：" + row[0]);
                    }


                    if (StringUtil.isEmpty(row[7].trim())) {
                        throw new BizException("第" + rowNum + "行，第8列格式有误：【借款金额不能为空】");
                    }
                    try {
                        partyFundBusinessDO.setLendAmount(new BigDecimal(row[7]));//乙方借款金额（导入）
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第8列格式有误：" + row[7]);
                    }
                    try {
                        partyFundBusinessDO.setOrderId(Long.valueOf(row[10]));//借据号
                    } catch (Exception e) {
                        throw new BizException("第" + rowNum + "行，第11列格式有误：" + row[10]);
                    }
                    partyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);

                    //添加数据
                    partyFundBusinessDOList.add(partyFundBusinessDO);

                }

                /*ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
                approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                approvalParam.setNeedLog(true);
                approvalParam.setCheckPermission(false);*/
                //插入数据库
                partyFundBusinessDOList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {

//                            //提交任务  与第三方通讯都走单笔不再做提交
//                            approvalParam.setOrderId(e.getOrderId());
//                            approvalParam.setProcessId(e.getBridgeProcecssId());
//
//                            ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
//                            Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());

                            //更新状态为已出借
                            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(e.getBridgeProcecssId());
                            int count;
                            if (thirdPartyFundBusinessDO == null) {
                                count = thirdPartyFundBusinessDOMapper.insertSelective(e);
                            } else {
                                count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(e);
                            }
                            Preconditions.checkArgument(count > 0, "更新状态[已出借]失败");
                        });
            }

        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }

        return ResultBean.ofSuccess(null,"导入成功!");
    }
    //组成批量认证文件
    public void buildVerifyFile(List<ThirdPartyFundBusinessDO> partyFundBusinessDOList ){
        Date date = new Date();
        SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMdd");
        String dateFile = sdf.format(date);
        String verifyFilePath = jtxConfig.getJtxTempDirSend()+dateFile+File.separator+"VERIFY";
        String assetFilePath = jtxConfig.getJtxTempDirSend()+dateFile+File.separator+"ASSET";
        File verifyFile = new File(verifyFilePath);
        if(!verifyFile.exists()){
            verifyFile.mkdirs();
        }
        File assetFile = new File(assetFilePath);
        if(!assetFile.exists()){
            assetFile.mkdirs();
        }
        for(ThirdPartyFundBusinessDO thirdPartyFundBusinessDO:partyFundBusinessDOList){

            //String lineInfo ="MGR" + System.nanoTime()+"|"+"VERIFY|"+;

        }
    }

    /**
     * 金投行过桥处理 -导出
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBean export(ExportApplyLoanPushParam param) {

        List<ExportApplyLoanPushVO> voList = loanStatementDOMapper.exportApplyLoanPush(param);
        List<String> header = Lists.newArrayList("流水号", "委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "甲方垫款金额（导出）", "乙方借款金额（导入）", "借款期限", "利率", "借据号", "最终放款银行","手机号码","借记卡号"
        );
        //生成Excel文件
        String ossResultKey = POIUtil.createExcelFile("购车融资业务推送清单", voList, header, ExportApplyLoanPushVO.class, ossConfig);

        // 更新记录为已导出
        voList.parallelStream()
                .filter(Objects::nonNull)
                .forEach(e -> {
                    ThirdPartyFundBusinessDO fundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(Long.valueOf(e.getBridgeProcessId()));
                    if (fundBusinessDO == null) {
                        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                        thirdPartyFundBusinessDO.setBridgeProcecssId(Long.valueOf(e.getBridgeProcessId()));
                        thirdPartyFundBusinessDO.setOrderId(Long.valueOf(e.getLoanForm()));
                        thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
                        thirdPartyFundBusinessDO.setGmtCreate(new Date());
                        thirdPartyFundBusinessDOMapper.insertSelective(thirdPartyFundBusinessDO);
                    } else {
                        fundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NO);
                        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(fundBusinessDO);
                        Preconditions.checkArgument(count > 0, "更新失败");
                    }
                });

        return ResultBean.ofSuccess(ossResultKey);
    }

    @Override
    public ResultBean errorExport(ExportApplyLoanPushParam param) {
        List<ExportErrorOrderVO> voList = loanStatementDOMapper.exportErrorOrder(param);
        List<String> header = Lists.newArrayList( "委托人（购车人、借款人）", "身份证号",
                "车辆品牌型号", "车价", "首付款", "甲方垫款金额", "借款期限", "最终放款银行"
        );
        //生成Excel文件
        String ossResultKey = POIUtil.createExcelFile("异常订单信息清单", voList, header, ExportErrorOrderVO.class, ossConfig);

        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行还款信息 -导出
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBean exportJinTouHangRepayInfo(ExportApplyLoanPushParam param) {

        List<JinTouHangRepayInfoVO> voList = loanStatementDOMapper.exportJinTouHangRepayInfo(param);
        List<String> header = Lists.newArrayList("主贷姓名", "身份证号", "借款时间",
                "银行放款时间", "还款时间", "借款天数", "借款金额", "放款本金", "放款-借款", "还款类型", "利息", "手续费", "备注"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行还款信息", voList, header, JinTouHangRepayInfoVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    /**
     * 金投行息费登记 -导出
     *
     * @return
     */
    @Override
    @Transactional
    public ResultBean exportJinTouHangInterestRegister(ExportApplyLoanPushParam param) {

        List<JinTouHangInterestRegisterVO> voList = loanStatementDOMapper.exportJinTouHangInterestRegister(param);
        List<String> header = Lists.newArrayList("借款时间", "还款时间", "借款金额",
                "主贷姓名", "身份证号", "分期本金"
        );

        String ossResultKey = POIUtil.createExcelFile("金投行息费登记", voList, header, JinTouHangInterestRegisterVO.class, ossConfig);
        return ResultBean.ofSuccess(ossResultKey);
    }

    @Override
    @Transactional
    public ResultBean calMoney(Long bridgeProcessId, Long orderId, String repayDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        CalMoneyVO calMoneyVO = new CalMoneyVO();
        Long conf_third_party_id;
        BigDecimal yearRate;
        BigDecimal singleRate;
        BigDecimal lend_amount;
        Date lendDate;
        Date repayDate1;
        int timeNum;
        try {
            ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
            if (confThirdRealBridgeProcessDO != null) {
                conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                yearRate = confThirdPartyMoneyDO.getYearRate();
                singleRate = confThirdPartyMoneyDO.getSingleRate();
                ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                lendDate = thirdPartyFundBusinessDO.getLendDate();
                repayDate1 = sdf.parse(repayDate);
                timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                lend_amount = thirdPartyFundBusinessDO.getLendAmount();
                if (lend_amount == null) {
                    lend_amount = new BigDecimal("0.00");
                }
                calMoneyVO.setInterest(String.valueOf(yearRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                calMoneyVO.setPoundage(String.valueOf(singleRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                calMoneyVO.setTimeNum(String.valueOf(timeNum));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess(calMoneyVO);
    }

    @Override
    @Transactional
    public ResultBean calMoneyDetail(Long bridgeProcessId, Long orderId, String repayDate, String flag) {
        CalMoneyVO calMoneyVO = new CalMoneyVO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        BigDecimal yearRate;
        BigDecimal singleRate;
        BigDecimal lend_amount;
        Date lendDate;
        Date repayDate1;
        int timeNum;

        Long conf_third_party_id;
        try {
            //0没有还款记录
            if ("0".equals(flag)) {
                ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
                if (confThirdRealBridgeProcessDO != null) {
                    conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                    ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                    yearRate = confThirdPartyMoneyDO.getYearRate();
                    singleRate = confThirdPartyMoneyDO.getSingleRate();
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                    lendDate = thirdPartyFundBusinessDO.getLendDate();
                    repayDate1 = sdf.parse(repayDate);
                    timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                    lend_amount = thirdPartyFundBusinessDO.getLendAmount();
                    if (lend_amount == null) {
                        lend_amount = new BigDecimal("0.00");
                    }
                    calMoneyVO.setInterest(String.valueOf(yearRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                    calMoneyVO.setPoundage(String.valueOf(singleRate.divide(BigDecimal.valueOf(100)).multiply(lend_amount).multiply(BigDecimal.valueOf(timeNum)).divide(BigDecimal.valueOf(365), 2, BigDecimal.ROUND_HALF_UP)));
                    calMoneyVO.setTimeNum(String.valueOf(timeNum));
                    calMoneyVO.setSingleRate(String.valueOf(singleRate));
                }
            } else {
                ConfThirdRealBridgeProcessDO confThirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
                if (confThirdRealBridgeProcessDO != null) {
                    conf_third_party_id = confThirdRealBridgeProcessDO.getConfThirdPartyId();
                    ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(conf_third_party_id);
                    singleRate = confThirdPartyMoneyDO.getSingleRate();
                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
                    lendDate = thirdPartyFundBusinessDO.getLendDate();
                    repayDate1 = thirdPartyFundBusinessDO.getRepayDate();
                    timeNum = (int) ((repayDate1.getTime() - lendDate.getTime()) / (1000 * 3600 * 24));
                    calMoneyVO.setInterest(String.valueOf(thirdPartyFundBusinessDO.getInterest()));
                    calMoneyVO.setPoundage(String.valueOf(thirdPartyFundBusinessDO.getPoundage()));
                    calMoneyVO.setTimeNum(String.valueOf(timeNum));
                    calMoneyVO.setSingleRate(String.valueOf(singleRate));
                    calMoneyVO.setReturnDate(sdf.format(repayDate1));
                    calMoneyVO.setReturnType(String.valueOf(thirdPartyFundBusinessDO.getRepayType()));
                }
            }
            BankLendRecordDO bankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
            if (bankLendRecordDO != null) {
                if (bankLendRecordDO.getLendDate() != null) {

                    Calendar c = Calendar.getInstance();
                    c.setTime(bankLendRecordDO.getLendDate());
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    calMoneyVO.setBankDate(sdf.format(c.getTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResultBean.ofSuccess(calMoneyVO);
    }

    @Override
    @Transactional
    public ResultBean isReturn(Long bridgeProcessId, Long orderId) {
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);
        if (thirdPartyFundBusinessDO != null) {
            if (thirdPartyFundBusinessDO.getRepayDate() == null) {
                return ResultBean.ofSuccess("0");
            } else {
                return ResultBean.ofSuccess(thirdPartyFundBusinessDO.getRepayDate());
            }
        } else {
            return ResultBean.ofSuccess("0");
        }
    }

    @Override
    public String jtxResult(String param) {
        JTXCommunicationUtil jtxCommunicationUtil = new JTXCommunicationUtil();
        String ref ="";
        String errorInfo="";
        Long orderId = System.currentTimeMillis();
        try {
            String xml = JTXByteUtil.decrypt(param,"netwxactive","GBK","des");
            logger.info("ASSET_03返回信息:"+xml);
            Map map = MapXmlUtil.Xml2Map(xml);
            Map bodyMap = (Map)map.get("MsgBody");
            Map headMap = (Map)map.get("MsgHdr");
            ref = (String)headMap.get("Ref");
            File file1 = new File(jtxConfig.getJtxTempDirRes());
            if(!file1.exists()){
                file1.mkdirs();
            }
            if(bodyMap !=null){
                String fileName = (String)bodyMap.get("FileName");
                String filePath = (String)bodyMap.get("FilePath");
                if(fileName!=null&&(!"".equals(fileName))&&filePath!=null&&(!"".equals(filePath))){
                    JtxReturnFileDO jtxReturnFileDO = new JtxReturnFileDO();
                    jtxReturnFileDO.setJtxid(ref);
                    jtxReturnFileDO.setFilePath(filePath);
                    jtxReturnFileDO.setFileName(fileName);
                    jtxReturnFileDO.setCreateDate(new Date());
                    jtxReturnFileDOMapper.insertSelective(jtxReturnFileDO);
                    asyncUpload.execute(new Process() {
                        @Override
                        public void process() {
                            String asyErrorInfo ="";
                            BufferedReader reader = null;
                            try{
                                JTXFileUtil sftp = new JTXFileUtil(jtxConfig.getJtxUserName(), jtxConfig.getJtxPassword(), jtxConfig.getJtxServierIP(), jtxConfig.getJtxPort());
                                sftp.login();
                                sftp.download(filePath,fileName,jtxConfig.getJtxTempDirRes()+fileName);
                                File file = new File(jtxConfig.getJtxTempDirRes()+fileName);
                                if(file.exists()){
                                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                                    String temp = null;
                                    while ((temp = reader.readLine()) != null){
                                        String[] result = temp.split("\\|");
                                        if(result[2]!=null&&result[3]!=null){
                                            String assetId = result[2];
                                            JtxCommunicationDO jtxCommunicationDO =jtxCommunicationDOMapper.selectByAssetId(assetId);
                                            if(jtxCommunicationDO !=null){
                                                if("0000".equals(result[3])){
                                                        //提交任务
                                                        ApprovalParam approvalParam = new ApprovalParam();
                                                        approvalParam.setTaskDefinitionKey(BRIDGE_HANDLE.getCode());
                                                        approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                                                        approvalParam.setNeedLog(true);
                                                        approvalParam.setCheckPermission(false);
                                                        approvalParam.setOrderId(jtxCommunicationDO.getOrderId());
                                                        approvalParam.setProcessId(jtxCommunicationDO.getBridgeProcecssId());
                                                        ResultBean<Void> approvalResultBean = loanProcessBridgeService.approval(approvalParam);
                                                        if(approvalResultBean.getSuccess()) {
                                                            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                                                            thirdPartyFundBusinessDO.setBridgeProcecssId(jtxCommunicationDO.getBridgeProcecssId());
                                                            thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_YES);
                                                            thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                                                        }else{
                                                            ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                                                            thirdPartyFundBusinessDO.setBridgeProcecssId(jtxCommunicationDO.getBridgeProcecssId());
                                                            thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_HANDLE_ERROR);
                                                            thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                                                            asyErrorInfo = "BridgeProcecssId:"+jtxCommunicationDO.getBridgeProcecssId()+"云车任务提交异常,"+approvalResultBean.getMsg();
                                                        }
                                                }else{
                                                    ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
                                                    thirdPartyFundBusinessDO.setBridgeProcecssId(jtxCommunicationDO.getBridgeProcecssId());
                                                    thirdPartyFundBusinessDO.setLendStatus(IDict.K_CJZT.K_CJZT_NOPASS);
                                                    thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
                                                }
                                            }else{
                                                asyErrorInfo = "assetId:"+assetId+"该任务不存在";
                                            }
                                        }else{
                                            asyErrorInfo ="金投行数据内容异常";
                                        }
                                    }
                                    file.delete();
                                }else{
                                    asyErrorInfo = "ftp文件下载不存在";
                                }
                            }catch(Exception e){
                                logger.error("异步处理数据异常",e);
                            }finally {
                                if(!"".equals(asyErrorInfo)){
                                    logger.error("03接口文件处理异常:"+asyErrorInfo);
                                }
                                if(reader!=null){
                                    try {
                                        reader.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    });

                }else{
                    errorInfo ="文件名或文件路径为空";
                }
            }else{
                errorInfo = "请求报文解析异常";
            }
        } catch (Exception e) {
            logger.error("解析金投行数据出错",e);
            errorInfo ="解析金投行数据出错";
        }finally {
            if("".equals(errorInfo)){
                return jtxCommunicationUtil.buildResultInfo("0000","交易成功",orderId+"",ref);
            }else{
                return jtxCommunicationUtil.buildResultInfo("1111",errorInfo,orderId+"",ref);
            }
        }

    }

    @Override
    public ResultBean getBankCard(Long orderId) {
        BankCardAndTelVO bankCardAndTelVO = new BankCardAndTelVO();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        LoanHomeVisitDO loanHomeVisitDO = loanHomeVisitDOMapper.selectByPrimaryKey(loanOrderDO.getLoanHomeVisitId());
        LoanCustomerDO loanCustomerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(),null);
        bankCardAndTelVO.setBankCard(loanHomeVisitDO.getDebitCard());
        bankCardAndTelVO.setTel(loanCustomerDO.getMobile());
        return ResultBean.ofSuccess(bankCardAndTelVO);
    }

    @Override
    public ResultBean batchEnd(AccommodationApplyParam accommodationApplyParam) {
        int i =loanProcessBridgeDOMapper.batchEndProcessBridge(accommodationApplyParam.getBridgeIdList());
        Preconditions.checkArgument(i > 0, "异常订单批量完结失败");
        return ResultBean.ofSuccess("异常订单批量完结成功");
    }

    @Override
    public ResultBean detail(Long bridgeProcessId, Long orderId) {
        Preconditions.checkNotNull(bridgeProcessId, "流程ID不能为空");
        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = thirdPartyFundBusinessDOMapper.selectByPrimaryKey(bridgeProcessId);

        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(orderDO, "订单信息不存在");
        RecombinationVO<Object> recombinationVO = new RecombinationVO<>();

//        ConfThirdRealBridgeProcessDO thirdRealBridgeProcessDO = confThirdRealBridgeProcessDOMapper.selectByPrimaryKey(bridgeProcessId);
//        if(thirdRealBridgeProcessDO!=null){
//            Long confThirdPartyId = thirdRealBridgeProcessDO.getConfThirdPartyId();
//            ConfThirdPartyMoneyDO confThirdPartyMoneyDO = confThirdPartyMoneyDOMapper.selectByPrimaryKey(confThirdPartyId);
//            Long bankId = confThirdPartyMoneyDO.getBankId();
//            String bankName = bankCache.getNameById(bankId);
//            thirdPartyFundBusinessDO.setConfThirdPartyBankName(bankName);
//        }


        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }

        UniversalInfoVO universalInfoVO = loanQueryDOMapper.selectUniversalInfo(orderId);
        UniversalCarInfoVO carInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(orderId);

        recombinationVO.setInfo(universalInfoVO);
        recombinationVO.setCar(carInfoVO);
        recombinationVO.setFinancial(financialSchemeVO);
        recombinationVO.setCustomers(customers);
        recombinationVO.setPartyFundBusinessVO(thirdPartyFundBusinessDO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 异常还款
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean abnormalRepay(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");

        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();

        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());

        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "异常还款跟新失败");

        return ResultBean.ofSuccess("保存成功");
    }

    /**
     * 金投行息费登记
     *
     * @param param
     * @return
     */
    @Override
    public ResultBean repayInterestRegister(AccommodationApplyParam param) {
        Preconditions.checkNotNull(param, "参数有误");
        Preconditions.checkNotNull(param.getIdPair().getOrderId(), "业务单号不能为空");
        Preconditions.checkNotNull(param.getIdPair().getBridgeProcessId(), "流程ID不能为空");


        ThirdPartyFundBusinessDO thirdPartyFundBusinessDO = new ThirdPartyFundBusinessDO();
        BeanUtils.copyProperties(param, thirdPartyFundBusinessDO);
        thirdPartyFundBusinessDO.setOrderId(param.getIdPair().getOrderId());
        thirdPartyFundBusinessDO.setBridgeProcecssId(param.getIdPair().getBridgeProcessId());
        int count = thirdPartyFundBusinessDOMapper.updateByPrimaryKeySelective(thirdPartyFundBusinessDO);
        Preconditions.checkArgument(count > 0, "金投行还款登记失败");

        return ResultBean.ofSuccess("保存成功");
    }
}
