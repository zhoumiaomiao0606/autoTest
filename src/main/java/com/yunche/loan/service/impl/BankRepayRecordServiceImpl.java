package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankRepayParam;
import com.yunche.loan.domain.vo.BankRepayRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankRepayRecordService;
import com.yunche.loan.service.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.BankUrgeConst.URGE_NO;
import static com.yunche.loan.config.constant.BankUrgeConst.URGE_YES;
import static com.yunche.loan.config.constant.BaseConst.*;


@Service
@Transactional
public class BankRepayRecordServiceImpl implements BankRepayRecordService {

    @Autowired
    BankRepayRecordDOMapper bankRepayRecordDOMapper;

    @Autowired
    BankRepayImpRecordDOMapper bankRepayImpRecordDOMapper;

    @Autowired
    BankRepayQueryDOMapper bankRepayQueryDOMapper;

    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    LoanRepayPlanDOMapper loanRepayPlanDOMapper;

    @Autowired

    LoanFinancialPlanDOMapper loanFinancialPlanDOMapper;

    @Autowired
    LoanOrderDOMapper loanOrderDOMapper;

    @Autowired
    BankUrgeRecordDOMapper bankUrgeRecordDOMapper;

    @Autowired
    BankRepayRecordService bankRepayRecordService;

    @Autowired
    CollectionService collectionService;
    @Override
    public ResultBean query() {
//        //查询客户详细信息
//        PageHelper.startPage(pageIndex, pageSize, true);
//        List<BankRepayRecordVO> bankRepayRecordVOList = bankRepayQueryDOMapper.selectBankRepayRecordDetail(bankRepayImpRecordId);
//        PageInfo<BankRepayRecordVO> pageInfo = new PageInfo<>(bankRepayRecordVOList);
//        return ResultBean.ofSuccess(bankRepayRecordVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
        return null;
    }

    @Override
    public ResultBean batchFileList(Integer pageIndex, Integer pageSize,String fileName,String startDate,String endDate) {

        PageHelper.startPage(pageIndex, pageSize, true);
        List<BankRepayImpRecordDO> list = bankRepayQueryDOMapper.selectBankRepayImpRecord(fileName,startDate,endDate);
        PageInfo<BankRepayImpRecordDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }


    public ResultBean detail(Integer pageIndex, Integer pageSize,Long bankRepayImpRecordId,String userName,String idCard, Byte isCustomer) {
        //查询客户详细信息
        PageHelper.startPage(pageIndex, pageSize, true);
        List<BankRepayRecordVO> bankRepayRecordVOList = bankRepayQueryDOMapper.selectBankRepayRecordDetail(bankRepayImpRecordId,userName,idCard,isCustomer);
        PageInfo<BankRepayRecordVO> pageInfo = new PageInfo<>(bankRepayRecordVOList);
        return ResultBean.ofSuccess(bankRepayRecordVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    @Override
    public ResultBean importFile(String ossKey) {
        Preconditions.checkNotNull(ossKey,"文件名不能为空");
        List<BankRepayRecordDO> list = importOverdueRecord(ossKey);//导入
        //更新还款计划
        adjustBankRepayPlanRecord(list);
        //更新催收记录
        adjustUrgeRecord(list);
        //催收自动分配
        collectionService.autoDistribution();
        return ResultBean.ofSuccess("导入成功");
    }

    /**
     * 更新还款计划表
     */
    private void adjustBankRepayPlanRecord(List<BankRepayRecordDO> list) {
        if(list==null){
            return ;
        }

        list.stream().filter(e-> K_YORN_YES.equals(e.getIsCustomer())).forEach(e->{

            if(e.getOverdueAmount().intValue()>0){
                //逾期金额大于0
                Long orderId =e.getOrderId();
//                List<LoanRepayPlanDO> loanRepayPlanDOS = bankRepayQueryDOMapper.selectRepayPlanListByOrderId(e.getOrderId());
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(e.getOrderId(), null);
                LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                BigDecimal ableRepay = loanFinancialPlanDO.getEachMonthRepay();//每月还款
                Double  tmpTimes = Math.ceil(e.getOverdueAmount().divide(ableRepay,10,RoundingMode.HALF_UP).doubleValue());
                int overdueTimes = tmpTimes.intValue();
                //0-降序  1-升序
                 List<LoanRepayPlanDO> overdueRepayPlanList = bankRepayQueryDOMapper.selectOverdueRepayPlanList(orderId, e.getBatchDate(), overdueTimes);
                overdueRepayPlanList.stream().forEach(r->{
                    r.setOverdueAmount(r.getPayableAmount());
                    r.setCheckDate(e.getBatchDate());
                    r.setIsOverdue(K_YORN_YES);
                    loanRepayPlanDOMapper.updateByPrimaryKeySelective(r);
                });


                List<LoanRepayPlanDO> lastRepayPlanLists = bankRepayQueryDOMapper.selectOverdueRepayPlanList(orderId, e.getBatchDate(), 1);
                if(!CollectionUtils.isEmpty(lastRepayPlanLists)){
                    Integer nper = lastRepayPlanLists.get(0).getNper();
                    Long lastOrderId = lastRepayPlanLists.get(0).getOrderId();
                    int num = nper-overdueTimes+1;
                    LoanRepayPlanDO loanRepayPlanDO = bankRepayQueryDOMapper.selectRepayPlanByNper(lastOrderId, num);
                    BigDecimal actual = loanRepayPlanDO
                            .getPayableAmount().multiply(new BigDecimal(overdueTimes)).subtract(e.getOverdueAmount());

                    loanRepayPlanDO.setActualRepayAmount(actual);
                    loanRepayPlanDO.setOverdueAmount(loanRepayPlanDO.getPayableAmount().subtract(loanRepayPlanDO.getActualRepayAmount()));
                    loanRepayPlanDOMapper.updateByPrimaryKeySelective(loanRepayPlanDO);
                }


            }else{
                List<LoanRepayPlanDO> overdueRepayPlanList = bankRepayQueryDOMapper.selectOverdueRepayPlanList(e.getOrderId(), e.getBatchDate(), null);
                overdueRepayPlanList.stream().forEach(noOverdue->{
                    noOverdue.setIsOverdue(K_YORN_NO);
                    noOverdue.setActualRepayAmount(noOverdue.getPayableAmount());
                    noOverdue.setCheckDate(e.getBatchDate());
                    loanRepayPlanDOMapper.updateByPrimaryKeySelective(noOverdue);
                });
            }

        });





    }

    /**
     * 更新催收记录
     */
    private void adjustUrgeRecord(List<BankRepayRecordDO> list) {
        if(list==null){
            return;
        }
        list.stream().filter(e-> e.getIsCustomer().equals(K_YORN_YES)).forEach(e->{
            BankUrgeRecordDO bankUrgeRecordDO = bankUrgeRecordDOMapper.selectByPrimaryKey(e.getOrderId());

            if(bankUrgeRecordDO==null){
                if(e.getOverdueAmount().intValue()>0){
                    BankUrgeRecordDO newUrge = new BankUrgeRecordDO();
                    newUrge.setOrderId(e.getOrderId());
                    newUrge.setOperator(SessionUtils.getLoginUser().getName());
                    newUrge.setBankRepayImpRecordId(e.getBankRepayImpRecordId());
                    newUrge.setUrgeStatus(URGE_NO);
                    bankUrgeRecordDOMapper.insertSelective(newUrge);
                }
            }else{
                bankUrgeRecordDO.setBankRepayImpRecordId(e.getBankRepayImpRecordId());
                bankUrgeRecordDO.setOperator(SessionUtils.getLoginUser().getName());
                if(e.getOverdueAmount().intValue()>0){
                    bankUrgeRecordDO.setUrgeStatus(URGE_NO);//未催
                }else{
                    bankUrgeRecordDO.setUrgeStatus(URGE_YES);//已催
                }
                bankUrgeRecordDOMapper.updateByPrimaryKeySelective(bankUrgeRecordDO);
            }

        });




    }

    /**
     *导入银行逾期记录
     */
    private List<BankRepayRecordDO> importOverdueRecord(String ossKey) {
        List<String[]>  returnList;
        List<BankRepayRecordDO> bankRepayList = Lists.newArrayList();
        try {

            returnList = POIUtil.readExcelFromOSS(0,1,ossKey);

            bankRepayList = Lists.newArrayList();
            for(String[] tmp :returnList){
                BankRepayRecordDO bankRepayRecordDO =new BankRepayRecordDO();
                if(tmp.length!=8){
                    continue;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                bankRepayRecordDO.setUserName(tmp[0].trim()); //用户姓名
                bankRepayRecordDO.setIdCard(StringUtil.isEmpty(tmp[1].trim())?null:tmp[1].trim()); //身份证号
                bankRepayRecordDO.setRepayCard(StringUtil.isEmpty(tmp[2].trim())?null:tmp[2].trim());//还款卡号
                bankRepayRecordDO.setCardBalance(new BigDecimal(tmp[3].trim()));//卡余额
                bankRepayRecordDO.setOverdueAmount(new BigDecimal(tmp[4].trim()));//逾期金额
                bankRepayRecordDO.setOverdueTimes(Integer.parseInt(tmp[5].trim()));//连续违约次数
                bankRepayRecordDO.setMaxOverdueTimes(Integer.parseInt(tmp[6].trim()));//最大违约次数
                bankRepayRecordDO.setBatchDate(formatter.parse(tmp[7].trim()));//批次日期
                bankRepayRecordDO.setGmtCreate(new Date());
                bankRepayRecordDO.setStatus(VALID_STATUS);
                bankRepayList.add(bankRepayRecordDO);
            }
            String fileName = ossKey.split(File.separator)[ossKey.split(File.separator).length-1];
            //获取批次号
            BankRepayImpRecordDO bankRepayImpRecordDO = new BankRepayImpRecordDO();
            bankRepayImpRecordDO.setBankFileMark(fileName);
            bankRepayImpRecordDO.setGmtCreate(new Date());
            bankRepayImpRecordDO.setOperator(SessionUtils.getLoginUser().getName());
            bankRepayImpRecordDO.setStatus(VALID_STATUS);
            bankRepayImpRecordDO.setFileKey(ossKey);
            int count = bankRepayImpRecordDOMapper.insert(bankRepayImpRecordDO);
            Preconditions.checkNotNull(count>0,"导入文件出错");
            Long batchId = bankRepayImpRecordDO.getId();

            bankRepayList.stream().filter(f-> (f.getIdCard()!=null || f.getRepayCard()!=null)).forEach(e->{


                BankRepayParam bankRepayParam = bankRepayQueryDOMapper.selectByIdCardOrRepayCard(e.getIdCard(), e.getRepayCard());
                if(bankRepayParam!=null){
                    e.setOrderId(bankRepayParam.getOrderId());
                    e.setRepayCard(bankRepayParam.getRepayCard());
                    e.setIdCard(bankRepayParam.getIdCard());
                    e.setIsCustomer(K_YORN_YES);
                    e.setBankRepayImpRecordId(batchId);
                    e.setStatus(VALID_STATUS);
                }else{
                    e.setIsCustomer(K_YORN_NO);
                    e.setIdCard(e.getIdCard()==null?"-1":e.getIdCard());
                    e.setRepayCard(e.getRepayCard()==null?"-1":e.getRepayCard());
                    e.setBankRepayImpRecordId(batchId);
                    e.setOrderId((long)-1);//不存在的记录
                    e.setStatus(INVALID_STATUS);
                }
                int insertCount = bankRepayRecordDOMapper.insertSelective(e);
                Preconditions.checkNotNull(insertCount>0,"导入记录出错");

            });


        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        }

        return bankRepayList;
    }


}
