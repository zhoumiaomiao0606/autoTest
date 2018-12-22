package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.BankCardRecordDO;
import com.yunche.loan.domain.entity.LoanCustomerDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.BankCardRecordVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.BankCardRecordDOMapper;
import com.yunche.loan.mapper.LoanCustomerDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankCardRecordService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanQueryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BankCardRecordServiceImpl implements BankCardRecordService {


    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    BankCardRecordDOMapper bankCardRecordDOMapper;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    LoanProcessService loanProcessService;

    @Autowired
    LoanCustomerDOMapper loanCustomerDOMapper;


    @Override
    public ResultBean importFile(String key) {
        Preconditions.checkNotNull(key, "文件名不能为空（包含绝对路径）");

        List<String[]> returnList;
        List<String> logList = Lists.newArrayList();

        try {
            //客户姓名、身份证号、账单日、首月账单日、还款日、首月还款日、还款卡号、接收日期、接收人
//            returnList = POIUtil.readExcel(0,1,pathFileName);
            returnList = POIUtil.readExcelFromOSS(0, 1, key);


            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            int line=1;
            for (String[] tmp : returnList) {
                BankCardRecordDO bankCardRecordDO = new BankCardRecordDO();


                if (tmp.length != 9) {
                    logList.add("文件第【"+line+"】行，列数异常,正确格式：{客户姓名、身份证号、账单日、首月账单日、还款日、首月还款日、还款卡号、接收日期、接收人}");
                    line++;
                    continue;
                }
                line++;
                Long orderId = loanQueryDOMapper.selectOrderIdByIDCard(tmp[1].trim());
                if (null == orderId) {
                    logList.add("文件第【"+line+"】行，"+tmp[1].trim()+":该客户订单不存在或存在多笔订单，请确认");
                    continue;
                }
                bankCardRecordDO.setOrderId(orderId);
                bankCardRecordDO.setUserName(tmp[0].trim());//客户姓名
                bankCardRecordDO.setIdCard(tmp[1].trim());//身份证号
                bankCardRecordDO.setBillingDate(tmp[2].trim());//账单日
                try{
                    bankCardRecordDO.setFirstBillingDate(df.parse(tmp[3].trim()));//首月账单日
                }catch (Exception e){
                    logList.add("文件第【"+line+"】行，第4列{首月账单日}格式异常，正确格式（文本）：YYYY-MM-DD");
                }

                bankCardRecordDO.setRepayDate(tmp[4].trim());//还款日
                try{
                    bankCardRecordDO.setFirstRepaymentDate(df.parse(tmp[5].trim()));//首月还款日
                }catch (Exception e){
                    logList.add("文件第【"+line+"】行，第6列{首月还款日}格式异常，正确格式（文本）：YYYY-MM-DD");
                }
                bankCardRecordDO.setRepayCardId(tmp[6].trim());//还款卡号
                try{
                    if (StringUtils.isNotBlank(tmp[7].trim())) {
                        bankCardRecordDO.setReceiveDate(df.parse(tmp[7].trim()));//接收日期
                    }
                }catch (Exception e){
                    logList.add("文件第【"+line+"】行，第8列{接收日期}格式异常，正确格式（文本）：YYYY-MM-DD");
                }

                bankCardRecordDO.setSendee(tmp[8].trim());//接收人
                bankCardRecordDO.setStatus(Byte.valueOf("0"));

                //兼容重复导入
                BankCardRecordDO tmpBankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
                if (tmpBankCardRecordDO == null) {
                    int count = bankCardRecordDOMapper.insertSelective(bankCardRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:" + tmp[1].trim() + ",对应记录导入出错");
                } else {
                    bankCardRecordDO.setId(tmpBankCardRecordDO.getId());
                    int count = bankCardRecordDOMapper.updateByPrimaryKeySelective(bankCardRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:" + tmp[1].trim() + ",对应记录更新出错");
                }

                bankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
                loanOrderDO.setBankCardRecordId((long) bankCardRecordDO.getId());
                int count = loanOrderDOMapper.updateByPrimaryKey(loanOrderDO);
                Preconditions.checkArgument(count > 0, "业务单号为:" + orderId + ",对应记录更新出错");
                //数据同步
                LoanOrderDO orderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
                LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(orderDO.getLoanCustomerId(), null);
                customerDO.setLendCard(bankCardRecordDO.getRepayCardId());
                loanCustomerDOMapper.updateByPrimaryKeySelective(customerDO);
                // 对应记录更新出错
                ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setOrderId(orderId);
                approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_CARD_RECORD.getCode());
                approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);
                approvalParam.setNeedLog(true);
                approvalParam.setCheckPermission(false);
                ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
            }

        } catch (Exception e) {

            Preconditions.checkArgument(false, e.getMessage());
        }

        return ResultBean.ofSuccess("导入成功，存在"+logList.size()+"笔记录处理异常，"+logList.toString());
    }

    @Override
    public ResultBean<BankCardRecordDO> query(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        BankCardRecordDO bankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
        return ResultBean.ofSuccess(bankCardRecordDO);
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        RecombinationVO recombinationVO = new RecombinationVO();
        BankCardRecordVO bankCardRecordVO = loanQueryDOMapper.selectBankCardRecordDetail(orderId);
        if(bankCardRecordVO.getRepayCardId()==null){

            LoanCustomerDO customerDO = loanCustomerDOMapper.selectByPrimaryKey(loanOrderDO.getLoanCustomerId(), null);
            bankCardRecordVO.setRepayCardId(customerDO.getLendCard());
        }
        recombinationVO.setInfo(bankCardRecordVO);
        //共贷人信息查询
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);
        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            universalCustomerVO.setFiles(files);
        }
        recombinationVO.setCustomers(customers);
        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public ResultBean input(BankCardRecordVO bankCardRecordVO) {
        Preconditions.checkNotNull(bankCardRecordVO, "银行卡登记信息不能为空");
        BankCardRecordDO bankCardRecordDO = new BankCardRecordDO();
        BeanUtils.copyProperties(bankCardRecordVO, bankCardRecordDO);
        bankCardRecordDO.setOrderId(Long.valueOf(bankCardRecordVO.getOrderId()));
        BankCardRecordDO tmpBankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(Long.valueOf(bankCardRecordVO.getOrderId()));
        if (tmpBankCardRecordDO == null) {
            bankCardRecordDOMapper.insert(bankCardRecordDO);
        } else {
            bankCardRecordDOMapper.updateByOrderId(bankCardRecordDO);
        }
        Long orderId = Long.valueOf(bankCardRecordVO.getOrderId());
        bankCardRecordDO = bankCardRecordDOMapper.selectByOrderId(orderId);
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        loanOrderDO.setBankCardRecordId((long) bankCardRecordDO.getId());
        int count = loanOrderDOMapper.updateByPrimaryKey(loanOrderDO);
        Preconditions.checkArgument(count > 0, "业务单号为:" + orderId + ",对应记录更新出错");
        return ResultBean.ofSuccess("录入成功", bankCardRecordDO.toString());
    }


}
