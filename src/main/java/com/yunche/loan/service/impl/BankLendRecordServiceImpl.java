package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.LoanProcessEnum;
import com.yunche.loan.config.constant.ProcessApprovalConst;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.BankLendRecordDO;
import com.yunche.loan.domain.entity.LoanFinancialPlanDO;
import com.yunche.loan.domain.entity.LoanOrderDO;
import com.yunche.loan.domain.param.ApprovalParam;
import com.yunche.loan.domain.vo.BankLendRecordVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalCustomerFileVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.BankLendRecordDOMapper;
import com.yunche.loan.mapper.LoanFinancialPlanDOMapper;
import com.yunche.loan.mapper.LoanOrderDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.BankLendRecordService;
import com.yunche.loan.service.LoanProcessService;
import com.yunche.loan.service.LoanQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankLendRecordServiceImpl implements BankLendRecordService {

    private static final Logger LOG = LoggerFactory.getLogger(BankLendRecordService.class);

    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    BankLendRecordDOMapper bankLendRecordDOMapper;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    LoanProcessService loanProcessService;

    @Autowired
    private LoanQueryService loanQueryService;

    @Autowired
    LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {

        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        RecombinationVO recombinationVO = new RecombinationVO();
        BankLendRecordVO bankLendRecordVO = loanQueryDOMapper.selectBankLendRecordDetail(orderId);

        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        Preconditions.checkNotNull(loanOrderDO, "订单不存在");
        Long loanFinancialPlanId = loanOrderDO.getLoanFinancialPlanId();
        LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanFinancialPlanId);

        if (loanFinancialPlanDO != null) {
            bankLendRecordVO.setBankPeriodPrincipal(loanFinancialPlanDO.getBankPeriodPrincipal());
        }

        recombinationVO.setInfo(bankLendRecordVO);


        //共贷人信息查询
        List<UniversalCustomerVO> customers = loanQueryDOMapper.selectUniversalCustomer(orderId);

        for (UniversalCustomerVO universalCustomerVO : customers) {
            List<UniversalCustomerFileVO> files = loanQueryService.selectUniversalCustomerFile(Long.valueOf(universalCustomerVO.getCustomer_id()));
            List<UniversalCustomerFileVO> tmpfiles = files.parallelStream().map(file -> {
                if (file.getUrls() == null) {
                    file.setUrls("[]");
                }
                return file;
            }).collect(Collectors.toList());
            universalCustomerVO.setFiles(tmpfiles);
        }
        recombinationVO.setCustomers(customers);
        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public ResultBean importFile(String key) {
        Preconditions.checkNotNull(key, "文件名不能为空（包含绝对路径）");

        List<String[]> returnList;
        List<String> unusualRecord = Lists.newArrayList();
        String idCard=null;

        try{
            returnList = POIUtil.readExcelFromOSS(0, 1, key);


            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            for (String[] tmp : returnList) {
//                tmp[0].trim();//客户姓名
//                tmp[1].trim();//身份证号
//                tmp[2].trim();//放款日期
//                tmp[3].trim();//放款金额
                if (tmp.length != 4) {
                    continue;
                }
                idCard=tmp[1].trim();
                Long orderId = loanQueryDOMapper.selectOrderIdByIDCard(idCard);
                if (orderId == null) {
                    continue;
                }
                BankLendRecordDO bankLendRecordDO = new BankLendRecordDO();
                bankLendRecordDO.setLoanOrder(orderId);
                bankLendRecordDO.setLendDate(df.parse(tmp[2].trim()));
                bankLendRecordDO.setLendAmount(new BigDecimal(tmp[3].trim()));
                bankLendRecordDO.setRecordStatus(Byte.valueOf("1"));
                bankLendRecordDO.setStatus(Byte.valueOf("0"));
                bankLendRecordDO.setGmtCreate(new Date());
                //兼容重复导入
                BankLendRecordDO tmpBankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
                if (tmpBankLendRecordDO == null) {
                    int count = bankLendRecordDOMapper.insert(bankLendRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:" + idCard + ",对应记录导入出错");
                } else {
                    bankLendRecordDO.setId(tmpBankLendRecordDO.getId());
                    int count = bankLendRecordDOMapper.updateByPrimaryKey(bankLendRecordDO);
                    Preconditions.checkArgument(count > 0, "身份证号:" + idCard + ",对应记录更新出错");
                }

                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
                loanOrderDO.setBankLendRecordId((long) bankLendRecordDO.getId());
                int count = loanOrderDOMapper.updateByPrimaryKey(loanOrderDO);
                Preconditions.checkArgument(count > 0, "业务单号为:" + orderId + ",对应记录更新出错");


                ApprovalParam approvalParam = new ApprovalParam();
                approvalParam.setOrderId(orderId);
                approvalParam.setTaskDefinitionKey(LoanProcessEnum.BANK_LEND_RECORD.getCode());
                approvalParam.setAction(ProcessApprovalConst.ACTION_PASS);

                approvalParam.setNeedLog(true);
                approvalParam.setCheckPermission(false);
                try{
                    ResultBean<Void> approvalResultBean = loanProcessService.approval(approvalParam);
                    Preconditions.checkArgument(approvalResultBean.getSuccess(), approvalResultBean.getMsg());
                }catch (Exception e){
                    unusualRecord.add(idCard+":"+e.getMessage());
                    LOG.info("导入失败:" + idCard+" "+ e.getMessage());
                }

            }
        }catch (Exception e){
            unusualRecord.add(idCard+":"+e.getMessage());
            LOG.info("导入失败:" + idCard+" "+ e.getMessage());
        }

        return ResultBean.ofSuccess("导入成功，存在"+unusualRecord.size()+"条记录导入失败,失败原因：["+unusualRecord.toString()+"]");
    }

    @Override
    /**
     * 业务员手工录入银行放款记录
     */
    public ResultBean manualInput(BankLendRecordVO bankLendRecordVO) {

        Preconditions.checkNotNull(bankLendRecordVO, "银行放款记录不能为空");
        Preconditions.checkNotNull(bankLendRecordVO.getLendAmount(), "银行放款金额不能为空");
        Preconditions.checkNotNull(bankLendRecordVO.getLendDate(), "银行放款日期不能为空");
        BankLendRecordDO bankLendRecordDO = new BankLendRecordDO();
        bankLendRecordDO.setLoanOrder(Long.valueOf(bankLendRecordVO.getOrderId()));
        bankLendRecordDO.setLendAmount(bankLendRecordVO.getLendAmount());
        bankLendRecordDO.setLendDate(bankLendRecordVO.getLendDate());
        bankLendRecordDO.setRecordStatus(Byte.valueOf("1"));//正常
        bankLendRecordDO.setStatus(Byte.valueOf("0"));
        bankLendRecordDO.setGmtCreate(new Date());
        BankLendRecordDO tmpBankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(Long.valueOf(bankLendRecordVO.getOrderId()));
        if (tmpBankLendRecordDO == null) {
            bankLendRecordDOMapper.insert(bankLendRecordDO);
        } else {
//            bankLendRecordDOMapper.updateByPrimaryKeySelective(bankLendRecordDO);
            bankLendRecordDOMapper.updateByOrderId(bankLendRecordDO);
        }
        Long orderId = Long.valueOf(bankLendRecordVO.getOrderId());
        bankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId);
        loanOrderDO.setBankLendRecordId((long) bankLendRecordDO.getId());
        int count = loanOrderDOMapper.updateByPrimaryKey(loanOrderDO);
        Preconditions.checkArgument(count > 0, "业务单号为:" + orderId + ",对应记录更新出错");
        LoanFinancialPlanDO loanFinancialPlanDO = new LoanFinancialPlanDO();
        loanFinancialPlanDO.setBankPeriodPrincipal(bankLendRecordVO.getBankPeriodPrincipal());
        loanFinancialPlanDO.setId(loanOrderDO.getLoanFinancialPlanId());
        loanFinancialPlanDOMapper.updateByPrimaryKeySelective(loanFinancialPlanDO);
        return ResultBean.ofSuccess("录入成功");
    }

    @Override
    public ResultBean<BankLendRecordDO> querySave(Long orderId) {

        Preconditions.checkNotNull(orderId, "业务单号不能为空");
        BankLendRecordDO bankLendRecordDO = bankLendRecordDOMapper.selectByLoanOrder(orderId);
        return ResultBean.ofSuccess(bankLendRecordDO);
    }


}
