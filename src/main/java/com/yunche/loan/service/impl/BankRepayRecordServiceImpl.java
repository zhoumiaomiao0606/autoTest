package com.yunche.loan.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.*;
import com.yunche.loan.domain.param.BankRepayParam;
import com.yunche.loan.domain.vo.BankRepayRecordVO;
import com.yunche.loan.mapper.*;
import com.yunche.loan.service.BankRepayRecordService;
import com.yunche.loan.service.CollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yunche.loan.config.constant.BankUrgeConst.URGE_NO;
import static com.yunche.loan.config.constant.BankUrgeConst.URGE_YES;
import static com.yunche.loan.config.constant.BaseConst.*;


@Service
@Transactional
public class BankRepayRecordServiceImpl implements BankRepayRecordService {
    private static final Logger LOG = LoggerFactory.getLogger(BankRepayRecordService.class);
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

    @Autowired
    BankFileListRecordDOMapper bankFileListRecordDOMapper;

    @Autowired
    BankFileListDOMapper bankFileListDOMapper;
    @Override
    public ResultBean query() {
//        //查询客户详细信息
//        PageHelper.startPage(pageIndex, pageSize, true);
//        List<BankRepayRecordVO> bankRepayRecordVOList = bankRepayQueryDOMapper.selectBankRepayRecordDetail(bankRepayImpRecordId);
//        PageInfo<BankRepayRecordVO> pageInfo = new PageInfo<>(bankRepayRecordVOList);
//        return ResultBean.ofSuccess(bankRepayRecordVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
        return null;
    }

    /**
     *
     * @param pageIndex
     * @param pageSize
     * @param fileName
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public ResultBean batchFileList(Integer pageIndex, Integer pageSize,String fileName,String startDate,String endDate) {

        PageHelper.startPage(pageIndex, pageSize, true);
        List<BankFileListDO> list = bankRepayQueryDOMapper.selectBankRepayImpRecord(fileName,startDate,endDate);
        PageInfo<BankFileListDO> pageInfo = new PageInfo<>(list);
        return ResultBean.ofSuccess(list, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    /**
     *
     * @param pageIndex
     * @param pageSize
     * @param bankRepayImpRecordId
     * @param userName
     * @param idCard
     * @param isCustomer
     * @return
     */
    public ResultBean detail(Integer pageIndex, Integer pageSize,Long bankRepayImpRecordId,String userName,String idCard, Byte isCustomer) {
        //查询客户详细信息
        PageHelper.startPage(pageIndex, pageSize, true);
        List<BankRepayRecordVO> bankRepayRecordVOList = bankRepayQueryDOMapper.selectBankRepayRecordDetail(bankRepayImpRecordId,userName,idCard,isCustomer);
        PageInfo<BankRepayRecordVO> pageInfo = new PageInfo<>(bankRepayRecordVOList);
        return ResultBean.ofSuccess(bankRepayRecordVOList, new Long(pageInfo.getTotal()).intValue(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    /**
     *
     * @param ossKey
     * @return
     */
    @Override
    public ResultBean importFile(String ossKey) {
        Preconditions.checkNotNull(ossKey,"文件名不能为空");
        List<BankFileListRecordDO> list = importOverdueRecord(ossKey);//导入
        //更新还款计划
        adjustBankRepayPlanRecord(list);
        //更新催收记录
        adjustUrgeRecord(list);
        //催收自动分配
        collectionService.autoDistribution();
        return ResultBean.ofSuccess("导入成功");
    }

    /**
     *
     * @param ossKey
     * @return
     */
    @Override
    public boolean autoImportFile(String ossKey) {
        try{
            Preconditions.checkNotNull(ossKey,"文件名不能为空");
            LOG.info("【"+ossKey+"：银行逾期文件自动导入开始...】");
            List<BankFileListRecordDO> list = autoImportOverdueRecord(ossKey);//导入
            LOG.info("【"+ossKey+"：银行逾期文件自动导入结束...】");
            LOG.info("【"+ossKey+"：更新还款计划开始】");
            adjustBankRepayPlanRecord(list);
            LOG.info("【"+ossKey+"：更新还款计划结束】");
            LOG.info("【"+ossKey+"：更新催收记录开始】");
            adjustUrgeRecord(list);
            LOG.info("【"+ossKey+"：更新催收记录结束】");
            LOG.info("【"+ossKey+"：自动分单开始】");
            collectionService.autoDistribution();
            LOG.info("【"+ossKey+"：自动分单结束】");
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     *
     * @param ossKey
     * @return
     */
    private List<BankFileListRecordDO> autoImportOverdueRecord(String ossKey) {
        List<BankFileListRecordDO> recordLists = Lists.newArrayList();
        try {
            InputStream in = OSSUnit.getOSS2InputStream(ossKey);
            InputStreamReader inReader = null;
            inReader = new InputStreamReader(in, "UTF-8");


            BufferedReader bufReader = new BufferedReader(inReader);
            int bankFileListId = recordImportBatch(ossKey); //记录导入记录
            bankFileListRecordDOMapper.deleteByPrimaryKey(Long.valueOf(bankFileListId));
            /**
             * 数据日期(yyyymmdd)、地区号、平台编号、担保单位编号、订单号、卡号、姓名、证件类型、
             * 证件号码、卡余额、最优还款额、累计违约次数、连续违约次数、经办支行、分期业务种类
             */
            String line="";
            while((line = bufReader.readLine()) != null){
                String[] split = line.split("\\|");
                BankFileListRecordDO bankFileListRecordDO = packObject(bankFileListId,split);
                recordLists.add(bankFileListRecordDO);
            }
            if (!CollectionUtils.isEmpty(recordLists)) {
                int count = bankFileListRecordDOMapper.insertBatch(recordLists);
                Preconditions.checkArgument(count == recordLists.size(), "批量插入失败");
            }
        } catch (Exception e) {
           throw new BizException(e.getMessage());
        }
        return recordLists;
    }

    /**
     * 记录导入批次记录
     * @param ossKey
     * @return
     */
    private int recordImportBatch(String ossKey) {
        BankFileListDO bankFileListDO = new BankFileListDO();
        String[] split1 = ossKey.split(File.separator);
        String fileName =ossKey;
        if(split1.length>0){
            fileName = split1[split1.length-1].trim();
        }
        bankFileListDO.setFileName(fileName);
        bankFileListDO.setFileKey(ossKey);
        bankFileListDO.setFileType(IDict.K_WJLX.WJLX_1);
        bankFileListDO.setGmtCreate(new Date());
        bankFileListDO.setOperator("auto");
        int count = bankFileListDOMapper.insertSelective(bankFileListDO);
        Preconditions.checkArgument(count>0,"插入失败");
        int bankFileListId =  bankFileListDO.getId().intValue();
        return bankFileListId;
    }

    /**
     * @param bankFileListId
     * @param split
     * @return
     */
    private BankFileListRecordDO packObject(int bankFileListId ,String[] split) {
        BankFileListRecordDO bankFileListRecordDO = new BankFileListRecordDO();
        Date date = DateUtil.getDate(split[0].trim());//数据日期
        bankFileListRecordDO.setBatchDate(date);
        bankFileListRecordDO.setAreaId(split[1].trim());//地区号
        bankFileListRecordDO.setPlatNo(split[2].trim());//平台编号
        bankFileListRecordDO.setGuarantyUnit(split[3].trim());;//担保单位编号
        bankFileListRecordDO.setOrderId(Long.valueOf(split[4].trim()));;//订单号
        bankFileListRecordDO.setCardNumber(split[5].trim());//卡号
        bankFileListRecordDO.setName(split[6].trim());//姓名
        bankFileListRecordDO.setCardType(split[7].trim());//证件类型
        bankFileListRecordDO.setCredentialNo(split[8].trim());//证件号码
        bankFileListRecordDO.setCardBalance(new BigDecimal(split[9].trim()));//卡余额
        bankFileListRecordDO.setOptimalReturn(new BigDecimal(split[10].trim()));//最优还款额(逾期金额)
        bankFileListRecordDO.setCumulativeBreachNumber(Integer.parseInt(split[11].trim()));//累计违约次数
        bankFileListRecordDO.setConsecutiveBreachNumber(Integer.parseInt(split[12].trim()));//连续违约次数
        bankFileListRecordDO.setRunBank(split[13].trim());//经办支行
        bankFileListRecordDO.setInstalmentTypes(split[14].trim());//分期业务种类
        bankFileListRecordDO.setBankFileListId(Long.valueOf(bankFileListId));
        bankFileListRecordDO.setIsCustomer(isCustomer(Long.valueOf(split[4].trim())));
        return bankFileListRecordDO;

    }

    /**
     *
     * @param orderId
     * @return
     */
    private Byte isCustomer(Long orderId){
        LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(orderId, VALID_STATUS);
        if(loanOrderDO != null){
           return K_YORN_YES;
        }else{
            return K_YORN_NO;
        }
    }
    /**
     * 更新还款计划表
     */
    private void adjustBankRepayPlanRecord(List<BankFileListRecordDO> list) {
        if(list==null){
            return ;
        }
        list.stream().filter(e-> K_YORN_YES.equals(e.getIsCustomer())).forEach(e->{

            if(e.getOptimalReturn().doubleValue()>0){
                //逾期金额大于0
                Long orderId =e.getOrderId();
                LoanOrderDO loanOrderDO = loanOrderDOMapper.selectByPrimaryKey(e.getOrderId(), null);
                LoanFinancialPlanDO loanFinancialPlanDO = loanFinancialPlanDOMapper.selectByPrimaryKey(loanOrderDO.getLoanFinancialPlanId());
                BigDecimal ableRepay = loanFinancialPlanDO.getEachMonthRepay();//每月还款
                Double  tmpTimes = Math.ceil(e.getOptimalReturn().divide(ableRepay,10,RoundingMode.HALF_UP).doubleValue());
                int overdueTimes = tmpTimes.intValue();
                List<LoanRepayPlanDO> lastRepayPlanLists = bankRepayQueryDOMapper.selectOverdueRepayPlanList(orderId, e.getBatchDate(), 1);
                List<LoanRepayPlanDO> overdueRepayPlanList = bankRepayQueryDOMapper.selectOverdueRepayPlanList(orderId, e.getBatchDate(), overdueTimes);
                //如果逾期多期先将逾期记录中的逾期金额全部更新成应还金额
               if(overdueTimes>1){
                   overdueRepayPlanList.stream().filter(Objects::nonNull).forEach(r->{
                       r.setOverdueAmount(r.getPayableAmount());
                       r.setCheckDate(e.getBatchDate());
                       r.setIsOverdue(K_YORN_YES);
                       r.setActualRepayAmount(new BigDecimal(0));
                       loanRepayPlanDOMapper.updateByPrimaryKeySelective(r);
                   });
               }
                if(!CollectionUtils.isEmpty(lastRepayPlanLists)){
                    Integer nper = lastRepayPlanLists.get(0).getNper();
                    Long lastOrderId = lastRepayPlanLists.get(0).getOrderId();
                    int num = nper-overdueTimes+1;
                    LoanRepayPlanDO loanRepayPlanDO = bankRepayQueryDOMapper.selectRepayPlanByNper(lastOrderId, num);
                    if(loanRepayPlanDO!=null){
                        BigDecimal actual = loanRepayPlanDO
                                .getPayableAmount().multiply(new BigDecimal(overdueTimes)).subtract(e.getOptimalReturn());

                        loanRepayPlanDO.setActualRepayAmount(actual);
                        loanRepayPlanDO.setOverdueAmount(loanRepayPlanDO.getPayableAmount().subtract(loanRepayPlanDO.getActualRepayAmount()));
                        if(loanRepayPlanDO.getOverdueAmount().doubleValue()<=0){
                            loanRepayPlanDO.setIsOverdue(K_YORN_NO);
                        }else{
                            loanRepayPlanDO.setIsOverdue(K_YORN_YES);
                        }
                        loanRepayPlanDOMapper.updateByPrimaryKeySelective(loanRepayPlanDO);
                    }

                }
                if(lastRepayPlanLists!=null){
                    LoanRepayPlanDO loanRepayPlanDO = bankRepayQueryDOMapper.selectRepayPlanByNper(e.getOrderId(), lastRepayPlanLists.get(0).getNper() - overdueTimes);
                    if(loanRepayPlanDO!=null){
                        List<LoanRepayPlanDO> loanRepayPlanDOS = bankRepayQueryDOMapper.selectOverdueRepayPlanList(e.getOrderId(), loanRepayPlanDO.getRepayDate(), null);
                        loanRepayPlanDOS.stream().filter(old-> old.getIsOverdue().equals(K_YORN_YES)).forEach(old->{
                            old.setActualRepayAmount(old.getPayableAmount());
                            old.setIsOverdue(K_YORN_NO);
                            old.setCheckDate(e.getBatchDate());
                            old.setOverdueAmount(new BigDecimal(0));
                            loanRepayPlanDOMapper.updateByPrimaryKeySelective(old);
                        });
                    }

                }
            }else{
                List<LoanRepayPlanDO> overdueRepayPlanList = bankRepayQueryDOMapper.selectOverdueRepayPlanList(e.getOrderId(), e.getBatchDate(), null);
                overdueRepayPlanList.stream().filter(noOverdue-> noOverdue.getIsOverdue().equals(K_YORN_NO)).forEach(noOverdue->{
                    noOverdue.setIsOverdue(K_YORN_NO);
                    noOverdue.setActualRepayAmount(noOverdue.getPayableAmount());
                    noOverdue.setCheckDate(e.getBatchDate());
                    noOverdue.setOverdueAmount(new BigDecimal(0));
                    loanRepayPlanDOMapper.updateByPrimaryKeySelective(noOverdue);
                });
            }
        });
    }


    /**
     * 更新催收记录
     */
    private void adjustUrgeRecord(List<BankFileListRecordDO> list) {
        if(list==null){
            return;
        }
        list.stream().filter(e-> e.getIsCustomer().equals(K_YORN_YES)).forEach(e->{
            BankUrgeRecordDO bankUrgeRecordDO = bankUrgeRecordDOMapper.selectByPrimaryKey(e.getOrderId());

            if(bankUrgeRecordDO==null){
                if(e.getOptimalReturn().doubleValue()>0){
                    BankUrgeRecordDO newUrge = new BankUrgeRecordDO();
                    newUrge.setOrderId(e.getOrderId());
                    newUrge.setOperator(SessionUtils.getLoginUser().getName());
                    newUrge.setBankRepayImpRecordId(e.getBankFileListId());
                    newUrge.setUrgeStatus(URGE_NO);
                    bankUrgeRecordDOMapper.insertSelective(newUrge);
                }
            }else{
                bankUrgeRecordDO.setBankRepayImpRecordId(e.getBankFileListId());
                bankUrgeRecordDO.setOperator(SessionUtils.getLoginUser().getName());
                if(e.getOptimalReturn().doubleValue()>0){
                    bankUrgeRecordDO.setUrgeStatus(URGE_NO);//未催
                }else{
                    bankUrgeRecordDO.setUrgeStatus(URGE_YES);//已催
                }
                bankUrgeRecordDOMapper.updateByPrimaryKeySelective(bankUrgeRecordDO);
            }

        });
    }

    /**
     * 导入银行逾期记录
     */
    private List<BankFileListRecordDO> importOverdueRecord(String ossKey) {
        List<String[]>  returnList;
        List<BankFileListRecordDO> bankRepayList = Lists.newArrayList();
        try {

            returnList = POIUtil.readExcelFromOSS(0,1,ossKey);

            bankRepayList = Lists.newArrayList();
            for(String[] tmp :returnList){
                BankFileListRecordDO bankFileListRecordDO =new BankFileListRecordDO();
                if(tmp.length!=8){
                    continue;
                }
                bankFileListRecordDO.setName(tmp[0].trim());
                bankFileListRecordDO.setCredentialNo(StringUtil.isEmpty(tmp[1].trim())?null:tmp[1].trim());
                bankFileListRecordDO.setCardNumber(StringUtil.isEmpty(tmp[2].trim())?null:tmp[2].trim());
                bankFileListRecordDO.setCardBalance(new BigDecimal(tmp[3].trim()));
                bankFileListRecordDO.setOptimalReturn(new BigDecimal(tmp[4].trim()));
                bankFileListRecordDO.setConsecutiveBreachNumber(Integer.parseInt(tmp[5].trim()));
                bankFileListRecordDO.setCumulativeBreachNumber(Integer.parseInt(tmp[6].trim()));
                bankFileListRecordDO.setBatchDate(DateUtil.getDate10(tmp[7].trim()));
                bankFileListRecordDO.setStatus(VALID_STATUS);
                bankRepayList.add(bankFileListRecordDO);
            }
            int batchId = recordImportBatch(ossKey);
            //获取批次号
            List bankRepayList2 =  bankRepayList.stream().filter(f-> (f.getCredentialNo()!=null || f.getCardNumber()!=null)).map(e->{
                BankRepayParam bankRepayParam = bankRepayQueryDOMapper.selectByIdCardOrRepayCard(e.getCredentialNo(), e.getCardNumber());
                if(bankRepayParam!=null){
                    e.setOrderId(bankRepayParam.getOrderId());
                    e.setCardNumber(bankRepayParam.getRepayCard());
                    e.setCredentialNo(bankRepayParam.getIdCard());
                    e.setIsCustomer(K_YORN_YES);
                    e.setBankFileListId(Long.valueOf(batchId));
                    e.setStatus(VALID_STATUS);
                }else{
                    e.setIsCustomer(K_YORN_NO);
                    e.setCredentialNo(e.getCredentialNo()==null?"-1":e.getCredentialNo());
                    e.setCardNumber(e.getCardNumber()==null?"-1":e.getCardNumber());
                    e.setBankFileListId(Long.valueOf(batchId));
                    e.setOrderId((long)-1);//不存在的记录
                    e.setStatus(INVALID_STATUS);
                }
                return e;

            }).collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(bankRepayList2)) {
                int count = bankFileListRecordDOMapper.insertBatch(bankRepayList2);
                Preconditions.checkArgument(count == bankRepayList2.size(), "批量插入失败");
            }
        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        }
        return bankRepayList;
    }
}
