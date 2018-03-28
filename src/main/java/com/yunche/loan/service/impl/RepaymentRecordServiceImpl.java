package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.RepaymentRecordDO;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.domain.param.RepaymentRecordParam;
import com.yunche.loan.domain.vo.RepaymentRecordVO;
import com.yunche.loan.domain.vo.UniversalCustomerVO;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.mapper.RepaymentRecordDOMapper;
import com.yunche.loan.service.RepaymentRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class RepaymentRecordServiceImpl implements RepaymentRecordService {

    @Autowired
    RepaymentRecordDOMapper repaymentRecordDOMapper;

    @Autowired
    LoanQueryDOMapper loanQueryDOMapper;
    @Override
    public ResultBean<List<RepaymentRecordVO>> query() {
        //TODO 界面展示相关字段组装
        return ResultBean.ofSuccess(repaymentRecordDOMapper.selectCustomerOverdueRepayList());
    }

//    @Override
    //TODO  接口增加详情查询
    public ResultBean<RepaymentRecordParam> detail(Long orderId) {

        //TODO 界面展示相关字段组装
        //查询客户详细信息
        RepaymentRecordParam repaymentRecordparam = repaymentRecordDOMapper.selectCustomerOverdueRepayDetail(orderId);
        List<UniversalCustomerVO> universalCustomerVOS =  loanQueryDOMapper.selectUniversalCustomer(orderId);
        repaymentRecordparam.setUniversalCustomerVOS(universalCustomerVOS);
        return ResultBean.ofSuccess(repaymentRecordparam);
    }

    @Override
    public ResultBean importFile(String pathFileName) {
        Preconditions.checkNotNull(pathFileName,"文件名不能为空（包含绝对路径）");

        List<String[]>  returnList;
        try {

            returnList = POIUtil.readExcel(0,1,pathFileName);
            RepaymentRecordDO repaymentRecordDO =new RepaymentRecordDO();
            for(String[] tmp :returnList){
                repaymentRecordDO.setUserName(tmp[0].trim());
                repaymentRecordDO.setIdCard(tmp[1].trim());
                repaymentRecordDO.setRepayCardId(tmp[2].trim());
                repaymentRecordDO.setOptimalReturn(new BigDecimal(tmp[3].trim()));
                repaymentRecordDO.setMinPayment(new BigDecimal(tmp[4].trim()));
                repaymentRecordDO.setPastDue(new BigDecimal(tmp[5].trim()));
                repaymentRecordDO.setCurrentOverdueTimes(Integer.parseInt(tmp[6].trim()));
                repaymentRecordDO.setCumulativeOverdueTimes(Integer.parseInt(tmp[7].trim()));
                repaymentRecordDO.setCardBalance(new BigDecimal(tmp[8].trim()));
                repaymentRecordDO.setGmtCreate(new Date());


                //TODO 根据身份证号+贷款账号查询单号 repaymentRecordDO.setBizOrder();


                //TODO
                RepaymentRecordDOKey repaymentRecordDOKey=new RepaymentRecordDOKey();
                BeanUtils.copyProperties(repaymentRecordDO,repaymentRecordDOKey);

                RepaymentRecordDO repaymentRecordDOCheck= repaymentRecordDOMapper.selectByPrimaryKey(repaymentRecordDOKey);
                if(repaymentRecordDOCheck==null){
                    repaymentRecordDO.setGmtCreate(new Date());
                    int count= repaymentRecordDOMapper.insert(repaymentRecordDO);
                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录导入出错");
                }else{
                    int count= repaymentRecordDOMapper.updateByPrimaryKeySelective(repaymentRecordDO);
                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录更新出错");
                }
            }

        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        }

        return ResultBean.ofSuccess("导入成功");
    }



}
