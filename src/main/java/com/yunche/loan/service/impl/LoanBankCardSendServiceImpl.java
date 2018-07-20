package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.LoanBankCardSendDO;
import com.yunche.loan.domain.vo.UniversalBankCardSendVO;
import com.yunche.loan.mapper.LoanBankCardSendDOMapper;
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


    @Override
    public ResultBean<Void> save(LoanBankCardSendDO loanBankCardSendDO) {
        Preconditions.checkNotNull(loanBankCardSendDO.getOrderId(), "订单号不能为空");

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

            if (!CollectionUtils.isEmpty(rowList)) {
                // parse
                rowList.stream()
                        .filter(ArrayUtils::isNotEmpty)
                        .forEach(row -> {

                            LoanBankCardSendDO loanBankCardSendDO = new LoanBankCardSendDO();

                            loanBankCardSendDO.setOrderId(Long.valueOf(row[0]));
                            loanBankCardSendDO.setCardholderName(row[1]);
                            loanBankCardSendDO.setCardholderPhone(row[2]);
                            loanBankCardSendDO.setCardholderAddress(row[3]);
                            loanBankCardSendDO.setRepayCardNum(row[4]);
                            loanBankCardSendDO.setExpressSendAddress(row[5]);
                            loanBankCardSendDO.setExpressSendNum(row[6]);
                            loanBankCardSendDO.setExpressSendDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row[7]));

                            loanBankCardSendDO.setGmtCreate(new Date());
                            loanBankCardSendDO.setGmtModify(new Date());

                            loanBankCardSendDOList.add(loanBankCardSendDO);
                        });
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
