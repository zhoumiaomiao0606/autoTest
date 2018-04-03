package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.BankCardRecordDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.BankCardRecordVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.BankCardRecordDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankCardRecordService;
import com.yunche.loan.service.LoanProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
@Service
@Transactional
public class BankCardRecordServiceImpl implements BankCardRecordService {


    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    BankCardRecordDOMapper bankCardRecordDOMapper ;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    LoanProcessService loanProcessService;
    @Override
    public ResultBean importFile(String pathFileName) {
        Preconditions.checkNotNull(pathFileName,"文件名不能为空（包含绝对路径）");

        List<String[]>  returnList;
        try {
            //客户姓名、身份证号、账单日、首月账单日、还款日、首月还款日、还款卡号、接收日期、接收人
            returnList = POIUtil.readExcel(0,1,pathFileName);
            BankCardRecordDO bankCardRecordDO = new BankCardRecordDO();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for(String[] tmp :returnList){

                Long orderId =  loanQueryDOMapper.selectOrderIdByIDCard(tmp[1].trim());
                bankCardRecordDO.setOrderId(orderId);
                bankCardRecordDO.setUserName(tmp[0].trim());//客户姓名
                bankCardRecordDO.setIdCard(tmp[1].trim());//身份证号
                bankCardRecordDO.setBillingDate(df.parse(tmp[2].trim()));//账单日
                bankCardRecordDO.setFirstBillingDate(df.parse(tmp[3].trim()));//首月账单日
                bankCardRecordDO.setRepayDate(df.parse(tmp[4].trim()));//还款日
                bankCardRecordDO.setFirstRepaymentDate(df.parse(tmp[5].trim()));//首月还款日
                bankCardRecordDO.setRepayCardId( tmp[6].trim());//还款卡号
                bankCardRecordDO.setReceiveDate(df.parse(tmp[7].trim()));//接收日期
                bankCardRecordDO.setSendee( tmp[8].trim());//接收人
                bankCardRecordDO.setStatus(Byte.valueOf("0"));

                //兼容重复导入
                BankCardRecordDO tmpBankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
                if(tmpBankCardRecordDO == null){
                    int count  =  bankCardRecordDOMapper.insert(bankCardRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:"+tmp[1].trim()+",对应记录导入出错");
                }else{
                    int count  =  bankCardRecordDOMapper.updateByPrimaryKey(bankCardRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:"+tmp[1].trim()+",对应记录更新出错");
                }

                bankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId,null);
                loanOrderDO.setBankCardRecordId((long)bankCardRecordDO.getId());
                int count = loanOrderDOMapper.updateByPrimaryKey(loanOrderDO);
                Preconditions.checkArgument(count > 0, "业务单号为:"+orderId+",对应记录更新出错");

                // 对应记录更新出错
                ApprovalParam approvalParam =  new ApprovalParam();
                approvalParam.setOrderId(orderId);
                approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_CARD_RECORD.getCode());
                approvalParam.setAction(Byte.valueOf("1"));
                ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
            }

        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        }

        return ResultBean.ofSuccess("导入成功");
    }

    @Override
    public ResultBean<BankCardRecordVO> query(BankCardRecordVO bankCardRecordVO) {
        Preconditions.checkNotNull(bankCardRecordVO.getOrderId(),"业务单号不能为空");
        BankCardRecordDO bankCardRecordDO =  bankCardRecordDOMapper.selectByOrderId(Long.valueOf(bankCardRecordVO.getOrderId()));
        BeanUtils.copyProperties(bankCardRecordDO,bankCardRecordVO);
        return ResultBean.ofSuccess(bankCardRecordVO);
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId,"业务单号不能为空");
        RecombinationVO recombinationVO=  new RecombinationVO();
        BankCardRecordVO bankCardRecordVO = loanQueryDOMapper.selectBankCardRecordDetail(orderId);
        recombinationVO.setInfo(bankCardRecordVO);
        //共贷人信息查询
        List<UniversalCustomerVO> customers =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        recombinationVO.setCustomers(customers);
        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public ResultBean input(BankCardRecordVO bankCardRecordVO) {
        Preconditions.checkNotNull(bankCardRecordVO,"银行卡登记信息不能为空");
        BankCardRecordDO bankCardRecordDO =  new BankCardRecordDO();
        BeanUtils.copyProperties(bankCardRecordVO,bankCardRecordDO);
        bankCardRecordDOMapper.insert(bankCardRecordDO);
        return ResultBean.ofSuccess("录入成功",bankCardRecordDO.toString());
    }



}
