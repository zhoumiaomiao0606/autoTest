package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
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

import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;
import static com.yunche.loan.config.constant.LoanFileEnum.*;

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
        // TODO 图片合成
        //1001 【开卡】专项额度核定申请表 Special quota apply
        //1002  【开卡】开卡申请表(和身份证正反面合并成一张图片)
//        loanFileDOMapper
        List typets = Lists.newArrayList();
        typets.add(ID_CARD_FRONT);
        typets.add(ID_CARD_BACK);
        typets.add(OPEN_CARD_DATA);
        List<LoanFileDO> loanFileDOS = loanFileDOMapper.selectByCustomerAndTypes(bankOpenCardParam.getCustomerId(), typets);
        List<LoanFileDO> loanFileDOS1 = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), ID_CARD_FRONT.getType(), (byte) 1);
        List<LoanFileDO> loanFileDOS2 = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), ID_CARD_BACK.getType(), (byte) 1);
        List<LoanFileDO> loanFileDOS3 = loanFileDOMapper.listByCustomerIdAndType(bankOpenCardParam.getCustomerId(), ID_CARD_BACK.getType(), (byte) 1);

        List<String> keys = Lists.newArrayList();
        loanFileDOS.stream().filter(e -> StringUtils.isNotBlank(e.getPath())).forEach(e->{
            String path = e.getPath();
            List<String> list = JSONArray.parseArray(path, String.class);
            keys.addAll(list);
        });
        String mergerFilePath = ImageUtil.mergeImage2Pic(keys);//合成图片本地路径
        boolean b = FtpUtil.icbcUpload(mergerFilePath);
        if(!b){
            return ResultBean.of("图片上传失败",false,null);
        }
        return bankSolutionService.creditcardapply(bankOpenCardParam);
    }


}
