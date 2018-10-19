package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.BankFileListDO;
import com.yunche.loan.domain.entity.BankFileListRecordDO;
import com.yunche.loan.domain.vo.BankFileListRecordVO;
import com.yunche.loan.mapper.BankRecordQueryDOMapper;
import com.yunche.loan.service.BankListFileService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankListFileServiceImpl implements BankListFileService {
    @Autowired
    BankRecordQueryDOMapper bankRecordQueryDOMapper;

    @Override
    public ResultBean batchFileList(Integer pageIndex, Integer pageSize, String fileName, String startDate, String endDate, String fileType) {
        PageHelper.startPage(pageIndex, pageSize, true);
        List<BankFileListDO> list = bankRecordQueryDOMapper.selectBankImpRecord(fileName, startDate, endDate, fileType);
        PageInfo<BankFileListDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public ResultBean detail(Integer pageIndex, Integer pageSize, Long listId, String userName, String idCard, Byte isCustomer) {

        PageHelper.startPage(pageIndex, pageSize, true);

        //查询客户详细信息
        List<BankFileListRecordDO> bankFileListRecordDOS = bankRecordQueryDOMapper.selectBankRecordDetail(listId, userName, idCard, isCustomer);

        PageInfo<BankFileListRecordDO> pageInfo = new PageInfo<>(bankFileListRecordDOS);

        List<BankFileListRecordVO> collect =
                bankFileListRecordDOS.stream()
                        .map(e -> {

                            BankFileListRecordVO bankFileListRecordVO = new BankFileListRecordVO();
                            BeanUtils.copyProperties(e, bankFileListRecordVO);
                            bankFileListRecordVO.setOrderId(String.valueOf(e.getOrderId()));

                            return bankFileListRecordVO;
                        })
                        .collect(Collectors.toList());

        return ResultBean.ofSuccess(collect, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }


}
