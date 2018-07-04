package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.feign.request.ICBCApiRequest;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.*;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.BankSolutionService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanFileEnum.*;
import static com.yunche.loan.config.thread.ThreadPool.executorService;

@Service
public class BankOpenCardServiceImpl implements BankOpenCardService{

    @Autowired
    LoanQueryService loanQueryService;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    BankInterfaceSerialDOMapper bankInterfaceSerialDOMapper;

    @Autowired
    BankSolutionService bankSolutionService;

    @Autowired
    LoanFileDOMapper loanFileDOMapper;

    @Autowired
    MaterialDownHisDOMapper materialDownHisDOMapper;

    @Autowired
    BankFileListDOMapper bankFileListDOMapper;

    @Autowired
    BankFileListRecordDOMapper bankFileListRecordDOMapper;

    @Autowired
    FtpUtil ftpUtil;


    /**
     * 银行开卡详情页
     * @param orderId
     * @return
     */
    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        RecombinationVO recombinationVO = new RecombinationVO();
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, VALID_STATUS);
        Long customerId = loanOrderDO.getLoanCustomerId();
        UniversalCustomerDetailVO universalCustomerDetailVO = loanQueryService.universalCustomerDetail(customerId);
        BankInterfaceSerialDO serialDO = bankInterfaceSerialDOMapper.selectByCustomerIdAndTransCode(customerId, IDict.K_API.CREDITCARDAPPLY);

        recombinationVO.setInfo(universalCustomerDetailVO);
        recombinationVO.setBankSerial(serialDO);

        return ResultBean.ofSuccess(recombinationVO);
    }

    /**
     * 银行开卡
     * @param bankOpenCardParam
     * @return
     */
    @Override
    public ResultBean openCard(BankOpenCardParam bankOpenCardParam) {
        mergeUpload(bankOpenCardParam);
        bankSolutionService.creditcardapply(bankOpenCardParam);
        return ResultBean.ofSuccess(null);
    }

    /**
     * 导入银行开卡文件记录
     * @param ossKey
     * @return
     */
    @Override
    public boolean importFile(String ossKey) {

        try {
            InputStream in = OSSUnit.getOSS2InputStream(ossKey);
            InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
            BufferedReader bufReader = new BufferedReader(inReader);

            BankFileListDO bankFileListDO = new BankFileListDO();
            String[] split1 = ossKey.split(File.separator);
            String fileName =ossKey;
            if(split1.length>0){
                fileName = split1[split1.length-1].trim();
            }
            bankFileListDO.setFileName(fileName);
            bankFileListDO.setFileKey(ossKey);
            bankFileListDO.setFileType(IDict.K_WJLX.WJLX_0);
            bankFileListDO.setGmtCreate(new Date());
            bankFileListDO.setOperator(SessionUtils.getLoginUser().getName());
            int bankFileListId = bankFileListDOMapper.insertSelective(bankFileListDO);

            String line="";
            bankFileListRecordDOMapper.deleteByPrimaryKey(Long.valueOf(bankFileListId));
            /**
             * 地区号、平台编号、担保单位编号、订单号、开卡日期、卡号、姓名、证件类型、
             * 证件号码、发卡标志[0：开卡失败 1：开卡成功]、对账单日、还款日
             */
            List<BankFileListRecordDO> recordLists = Lists.newArrayList();
            while((line = bufReader.readLine()) != null){
                String[] split = line.split("\\|");
                BankFileListRecordDO bankFileListRecordDO = packObject(split);
                bankFileListRecordDO.setBankFileListId(Long.valueOf(bankFileListId));
                recordLists.add(bankFileListRecordDO);
            }
            if (!CollectionUtils.isEmpty(recordLists)) {
                int count = bankFileListRecordDOMapper.insertBatch(recordLists);
                Preconditions.checkArgument(count == recordLists.size(), "批量插入失败");
            }
        } catch (UnsupportedEncodingException e) {
            throw new BizException("导入文件失败");
        } catch (IOException e) {
            throw new BizException("导入文件失败");
        }
        return true;
    }

    /**
     *
     * @param split
     */
    private BankFileListRecordDO packObject(String[] split) {
        BankFileListRecordDO bankFileListRecordDO = new BankFileListRecordDO();

        String  areaId = split[0].trim();//地区号
        String  platNo = split[1].trim();//平台编号
        String  guarantyUnit = split[2].trim();//担保单位编号
        String  orderId = split[3].trim();//订单号
        String  openCardDate = split[4].trim();//开卡日期
        String  cardNumber = split[5].trim();//卡号
        String  name = split[6].trim();//姓名
        String  cardType = split[7].trim();//证件类型
        String  credentialNo = split[8].trim();//证件号码
        String  hairpinFlag = split[9].trim();//发卡标志
        String  accountStatement = split[10].trim();//对账单日
        String  repayDate = split[11].trim();//还款日
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(Long.valueOf(orderId), VALID_STATUS);
        Preconditions.checkNotNull(loanOrderDO,"订单不存在");
        bankFileListRecordDO.setAreaId(areaId);
        bankFileListRecordDO.setPlatNo(platNo);
        bankFileListRecordDO.setGuarantyUnit(guarantyUnit);
        bankFileListRecordDO.setOrderId(Long.valueOf(orderId));
        bankFileListRecordDO.setOpencardDate(DateUtil.getDate(openCardDate));
        bankFileListRecordDO.setCardNumber(cardNumber);
        bankFileListRecordDO.setName(name);
        bankFileListRecordDO.setCardType(cardType);
        bankFileListRecordDO.setCredentialNo(credentialNo);
        bankFileListRecordDO.setHairpinFlag(hairpinFlag);
        bankFileListRecordDO.setAccountStatement(accountStatement);
        bankFileListRecordDO.setRepayDate(repayDate);
        bankFileListRecordDO.setCustomerId(loanOrderDO.getLoanCustomerId());

        return bankFileListRecordDO;
    }


    /**
     * 合并资料并上传至中间服务器
     * @param bankOpenCardParam
     * @return
     */
    private void mergeUpload(BankOpenCardParam bankOpenCardParam) {

        List<LoanFileDO> idCardFront = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), ID_CARD_FRONT.getType(), (byte) 1);
        List<LoanFileDO> idCardback = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), ID_CARD_BACK.getType(), (byte) 1);
        List<LoanFileDO> specialQuotaApply = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), SPECIAL_QUOTA_APPLY.getType(), (byte) 1);
        List<LoanFileDO> openCardData = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), OPEN_CARD_DATA.getType(), (byte) 1);

        List<LoanFileDO> openCardTypes = Lists.newArrayList();
        openCardTypes.addAll(idCardFront);
        openCardTypes.addAll(idCardback);
        openCardTypes.addAll(openCardData);

        //【开卡】专项额度核定申请表
        List<String> keys = Lists.newArrayList();
        specialQuotaApply.stream().filter(Objects::nonNull).filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e->{
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            keys.addAll(list);
        });
        Preconditions.checkArgument(keys.size()>0,"专项额度核定申请表,不存在");
        String mergerFilePath1 = ImageUtil.mergeImage2Pic(keys);//合成图片本地路径
        Preconditions.checkNotNull(mergerFilePath1,"图片合成失败");

        String fileName = mergerFilePath1.substring(mergerFilePath1.lastIndexOf(File.separator) + 1);

        ICBCApiRequest.Picture picture1 = new ICBCApiRequest.Picture();
        picture1.setPicid(IDict.K_PIC_ID.SPECIAL_QUOTA_APPLY);
        picture1.setPicname(fileName);




        //开卡】开卡申请表(和身份证正反面合并成一张图片)
        List<String> openCardTypesStr = Lists.newArrayList();
        openCardTypes.stream().filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e->{
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            openCardTypesStr.addAll(list);
        });
        Preconditions.checkArgument(openCardTypesStr.size()>0,"开卡申请表(和身份证正反面合并成一张图片)");
        String mergerFilePath2 = ImageUtil.mergeImage2Pic(openCardTypesStr);
        Preconditions.checkNotNull(mergerFilePath2,"图片合成失败");

        String fileName2 = mergerFilePath2.substring(mergerFilePath2.lastIndexOf(File.separator) + 1);

        ICBCApiRequest.Picture picture2 = new ICBCApiRequest.Picture();
        picture2.setPicid(IDict.K_PIC_ID.OPEN_CARD_DATA);
        picture2.setPicname(fileName2);

        bankOpenCardParam.getPictures().add(picture1);
        bankOpenCardParam.getPictures().add(picture2);

        List<String> uploadFiles = Lists.newArrayList();
        uploadFiles.add(mergerFilePath1);
        uploadFiles.add(mergerFilePath2);
        asyncPush(uploadFiles);//文件上传

    }

    private void asyncPush(List<String> list){
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                list.parallelStream().forEach(e->{
                    ftpUtil.icbcUpload(e);
                });

            }
        });
    }


}
