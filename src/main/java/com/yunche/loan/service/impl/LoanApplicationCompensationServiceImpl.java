package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.exception.BizException;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateUtil;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.BankUrgeRecordDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDO;
import com.yunche.loan.domain.entity.LoanApplyCompensationDOKey;
import com.yunche.loan.domain.entity.LoanProcessDO;
import com.yunche.loan.domain.param.UniversalCompensationParam;
import com.yunche.loan.domain.query.UniversalCompensationQuery;
import com.yunche.loan.domain.vo.FinancialSchemeVO;
import com.yunche.loan.domain.vo.RecombinationVO;
import com.yunche.loan.domain.vo.UniversalInfoVO;
import com.yunche.loan.mapper.BankUrgeRecordDOMapper;
import com.yunche.loan.mapper.LoanApplyCompensationDOMapper;
import com.yunche.loan.mapper.LoanProcessDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanApplicationCompensationService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.yunche.loan.config.constant.BankUrgeConst.URGE_NO;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_DONE;
import static com.yunche.loan.config.constant.LoanOrderProcessConst.TASK_PROCESS_TODO;


@Service
public class LoanApplicationCompensationServiceImpl implements LoanApplicationCompensationService {


    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    LoanApplyCompensationDOMapper loanApplyCompensationDOMapper;

    @Autowired
    BankUrgeRecordDOMapper bankUrgeRecordDOMapper;

    @Autowired
    LoanProcessDOMapper loanProcessDOMapper;
    /**
     * 导入文件
     * @param key oss key
     * @return
     */
    @Override
    @Transactional
    public void batchInsert(String key) {
        Preconditions.checkNotNull(key,"文件key不能为空");
        List<LoanApplyCompensationDO> loanApplyCompensationDOList= Lists.newArrayList();

        try {

            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, key);
            if (!CollectionUtils.isEmpty(rowList)) {
                Preconditions.checkArgument(rowList.size()<=2000,"最大支持导入2000条数据，当前条数：" + rowList.size());

                for (int i = 0; i < rowList.size(); i++) {

                    // 当前行数
                    int rowNum = i + 1;

                    String[] row = rowList.get(i);
                    // 空行跳过
                    if (ArrayUtils.isEmpty(row) || StringUtils.isEmpty(row[2])) {
                        continue;
                    }

                    if(!StringUtils.isEmpty(isHasCustomer(row[2]))){
                        LoanApplyCompensationDO compensationDO = new LoanApplyCompensationDO();
                        compensationDO.setOrderId(isHasCustomer(row[2]));//业务单号

                        try {
                            compensationDO.setCurrArrears(new BigDecimal(row[4]));//当前欠款
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第5列格式有误：" + row[4]);
                        }

                        try {
                            compensationDO.setLoanBanlance(new BigDecimal(row[5]));//贷款余额
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第6列格式有误：" + row[5]);
                        }

                        try {
                            compensationDO.setAdvancesBanlance(new BigDecimal(row[6]));//垫款金额
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第7列格式有误：" + row[6]);
                        }
                        try {
                            compensationDO.setOverdueNumber(new Integer(row[7].trim()));//逾期次数
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第8列格式有误：" + row[7]);
                        }
                        try {
                            compensationDO.setAdvancesNumber(new Integer(row[8].trim()));//已垫款次数
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第9列格式有误：" + row[8]);
                        }
                        try {
                            compensationDO.setOverdueDays(new Integer(row[10].trim()));//逾期天数
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第11列格式有误：" + row[10]);
                        }
                        try {
                            compensationDO.setRiskTakingRatio(new BigDecimal(row[13].trim()));//风险承担比例
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第14列格式有误：" + row[13]);
                        }
                        try {
                            compensationDO.setCompensationCause(row[14].trim());//代偿原因
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第15列格式有误：" + row[14]);
                        }
                        try {
                            compensationDO.setApplyCompensationDate(DateUtil.getDate10(row[15].trim()));//申请代偿时间
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第16列格式有误：" + row[15]);
                        }
                        try {
                            compensationDO.setRemark(row[16].trim());//备注
                        } catch (Exception e) {
                            throw new BizException("第" + rowNum + "行，第17列格式有误：" + row[16]);
                        }

                        compensationDO.setGmtCreate(new Date());
                        compensationDO.setStatus(TASK_PROCESS_TODO);//2:未提交
                        //添加数据
                        loanApplyCompensationDOList.add(compensationDO);
                    }
                }


                //插入数据库
                loanApplyCompensationDOList.stream()
                        .filter(Objects::nonNull)
                        .forEach(e->{
                            LoanApplyCompensationDO tmpDO = loanApplyCompensationDOMapper.selectByPrimaryKey(e);

                            if(tmpDO==null){
                                int count = loanApplyCompensationDOMapper.insertSelective(e);
                                Preconditions.checkArgument(count>0,"插入记录出错");
                            } else if(!tmpDO.getStatus().equals(TASK_PROCESS_DONE)){
                                e.setGmtModify(new Date());
                                int count = loanApplyCompensationDOMapper.updateByPrimaryKeySelective(e);
                                Preconditions.checkArgument(count>0,"更新记录出错");
                            }
                            //客户逾期
                            if(IDict.K_DCYY.K_DCYY_A.equals(e.getCompensationCause())){
                                dealBankUrgeRecord(e.getOrderId());
                            }
                        });
            }

        } catch (IOException e) {
            throw new BizException("文件解析失败");
        }
    }

    /**
     * 手工录入
     * @param param
     * @return
     */
    @Override
    @Transactional
    public void manualInsert(UniversalCompensationParam param) {
        Preconditions.checkNotNull(param,"参数有误");
        LoanApplyCompensationDO tmpDO = loanApplyCompensationDOMapper.selectByPrimaryKey(param);

        if(tmpDO !=null){
            LoanProcessDO processDO = loanProcessDOMapper.selectByPrimaryKey(param.getOrderId());
            // TODO 代偿申请 字段
//            Preconditions.checkArgument(tmpDO.getStatus().equals(TASK_PROCESS_TODO),"订单已提交，禁止修改");
            int count = loanApplyCompensationDOMapper.updateByPrimaryKeySelective(param);
            Preconditions.checkArgument(count>0,"参数错误，保存失败");
        }else {
            int count = loanApplyCompensationDOMapper.insertSelective(param);
            Preconditions.checkArgument(count>0,"参数错误，保存失败");
        }
    }

    /**
     * 详情页
     * @param applicationCompensationQuery
     * @return
     */
    @Override
    public ResultBean detail(UniversalCompensationQuery applicationCompensationQuery) {
        Preconditions.checkNotNull(applicationCompensationQuery,"参数有误");
        Preconditions.checkNotNull(applicationCompensationQuery.getOrderId(),"业务单号不能为空");
        Preconditions.checkNotNull(applicationCompensationQuery.getApplyCompensationDate(),"申请代偿日期不能为空");

        RecombinationVO<Object> result = new RecombinationVO<>();

        //数据查询
        UniversalInfoVO infoVO = loanQueryDOMapper.selectUniversalInfo(applicationCompensationQuery.getOrderId());
        FinancialSchemeVO financialSchemeVO = loanQueryDOMapper.selectFinancialScheme(applicationCompensationQuery.getOrderId());

        LoanApplyCompensationDOKey doKey = new LoanApplyCompensationDOKey();
        doKey.setOrderId(applicationCompensationQuery.getOrderId());
        doKey.setApplyCompensationDate(applicationCompensationQuery.getApplyCompensationDate());
        LoanApplyCompensationDO loanApplyCompensationDO = loanApplyCompensationDOMapper.selectByPrimaryKey(doKey);

        //赋值
        result.setInfo(infoVO);
        result.setFinancial(financialSchemeVO);
        result.setApplyCompensation(loanApplyCompensationDO);

        return ResultBean.ofSuccess(result);
    }

    /**
     * 判断是否系统客户
     * @param idCard
     * @return
     */
    private Long isHasCustomer(String idCard){
        return loanQueryDOMapper.selectOrderIdByIDCard(idCard);
    }

    /**
     * 催保原因为 A:客户逾期 生成待催收记录
     */
    public void  dealBankUrgeRecord(Long orderId){
        BankUrgeRecordDO newUrge = new BankUrgeRecordDO();
        newUrge.setOrderId(orderId);
        newUrge.setOperator(SessionUtils.getLoginUser().getName());
        newUrge.setUrgeStatus(URGE_NO);
        bankUrgeRecordDOMapper.insertSelective(newUrge);
    }

}
