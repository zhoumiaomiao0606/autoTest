package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.FtpUtil;
import com.yunche.loan.config.util.ImageUtil;
import com.yunche.loan.domain.entity.BankInterfaceSerialDO;
import com.yunche.loan.domain.entity.LoanFileDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.BankOpenCardParam;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerDetailVO;
import com.yunche.loan.mapper.BankInterfaceSerialDOMapper;
import com.yunche.loan.mapper.LoanFileDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.service.BankOpenCardService;
import com.yunche.loan.service.BankSolutionService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanFileEnum.*;

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

        boolean b = mergeUpload(bankOpenCardParam);
        if(!b){
            throw new BizException("图片上传失败");
        }
        return bankSolutionService.creditcardapply(bankOpenCardParam);
    }





    /**
     * 合并资料并上传至中间服务器
     * @param bankOpenCardParam
     * @return
     */
    private boolean mergeUpload(BankOpenCardParam bankOpenCardParam) {

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
        specialQuotaApply.stream().filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e->{
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            keys.addAll(list);
        });

        String mergerFilePath1 = ImageUtil.mergeImage2Pic(keys);//合成图片本地路径

        String fileName = mergerFilePath1.substring(mergerFilePath1.lastIndexOf(File.separator) + 1);
        BankOpenCardParam.Picture picture1 = new BankOpenCardParam.Picture();
        picture1.setPicid(IDict.K_PIC_ID.SPECIAL_QUOTA_APPLY);
        picture1.setPicname(fileName);




        //开卡】开卡申请表(和身份证正反面合并成一张图片)
        List<String> openCardTypesStr = Lists.newArrayList();
        openCardTypes.stream().filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e->{
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            openCardTypesStr.addAll(list);
        });
        String mergerFilePath2 = ImageUtil.mergeImage2Pic(openCardTypesStr);

        String fileName2 = mergerFilePath2.substring(mergerFilePath2.lastIndexOf(File.separator) + 1);
        BankOpenCardParam.Picture picture2 = new BankOpenCardParam.Picture();
        picture2.setPicid(IDict.K_PIC_ID.OPEN_CARD_DATA);
        picture2.setPicname(fileName2);

        bankOpenCardParam.getPictures().add(picture1);
        bankOpenCardParam.getPictures().add(picture2);
        boolean b1 = FtpUtil.icbcUpload(mergerFilePath1);
        boolean b2 = FtpUtil.icbcUpload(mergerFilePath2);

        return b1&&b2;
    }


}
