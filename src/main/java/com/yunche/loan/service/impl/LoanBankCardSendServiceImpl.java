package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.BaseConst;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;
import com.yunche.loan.mapper.LoanBankCardSendDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanBankCardSendService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Service
public class LoanBankCardSendServiceImpl implements LoanBankCardSendService {

    @Autowired
    private LoanBankCardSendDOMapper loanBankCardSendDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanCustomerDOMapper loanCustomerDOMapper;


    @Override
    public ResultBean<Void> save(LoanBankCardSendDO loanBankCardSendDO) {
        Preconditions.checkNotNull(loanBankCardSendDO.getOrderId(), "订单号不能为空");


        LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());
        if (orderDO != null) {
            LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(orderDO.getLoanCustomerId(), BaseConst.VALID_STATUS);
            customerDO.setBankCardTransmitAddress(loanBankCardSendDO.getExpressSendAddress());
            loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
        }
        LoanBankCardSendDO existDO = loanBankCardSendDOMapper.selectByPrimaryKey(loanBankCardSendDO.getOrderId());

        if (null == existDO) {
            // create
            loanBankCardSendDO.setGmtCreate(new Date());
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.insertSelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            loanBankCardSendDO.setGmtModify(new Date());
            int count = loanBankCardSendDOMapper.updateByPrimaryKeySelective(loanBankCardSendDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<UniversalBankCardSendVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        UniversalBankCardSendVO universalBankCardSendVO = loanQueryDOMapper.selectUniversalBankCardSend(orderId);

        return ResultBean.ofSuccess(universalBankCardSendVO);
    }

    @Override
    public ResultBean<Integer> imp(String ossKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ossKey), "ossKey不能为空");

        // 收集数据
        List<LoanBankCardSendDO> loanBankCardSendDOList = Lists.newArrayList();

        try {

            // readFile
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, ossKey);


            for (int i = 0; i < rowList.size(); i++) {

                // 当前行数
                int rowNum = i + 1;

                // 当前行数据
                String[] row = rowList.get(i);

                // 空行跳过
                if (ArrayUtils.isEmpty(row)) {
                    continue;
                }

                // 列
                int line = 0;
                // 当前行，具体列val
                String rowVal = "";

                LoanBankCardSendDO loanBankCardSendDO = new LoanBankCardSendDO();

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setOrderId(Long.valueOf(rowVal));
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setCardholderName(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setCardholderPhone(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setCardholderAddress(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setRepayCardNum(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setExpressSendAddress(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setExpressSendNum(rowVal);
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                try {
                    rowVal = row[line++];
                    loanBankCardSendDO.setExpressSendDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(rowVal));
                } catch (Exception e) {
                    throw new BizException("第" + rowNum + "行，第" + (line) + "列格式有误：" + rowVal);
                }

                loanBankCardSendDO.setGmtCreate(new Date());
                loanBankCardSendDO.setGmtModify(new Date());

                loanBankCardSendDOList.add(loanBankCardSendDO);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // batchInsert
        int count = batchInsert(loanBankCardSendDOList);

        return ResultBean.ofSuccess(count, "导入成功");
    }

    /**
     * 批量导入
     *
     * @param loanBankCardSendDOList
     * @return
     */
    private int batchInsert(List<LoanBankCardSendDO> loanBankCardSendDOList) {

        if (CollectionUtils.isEmpty(loanBankCardSendDOList)) {
            return 0;
        }

        // batchInsert
        int count = loanBankCardSendDOMapper.batchInsert(loanBankCardSendDOList);
        return count;
    }
}
