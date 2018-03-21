package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.RepaymentRecordDO;
import com.yunche.loan.domain.entity.RepaymentRecordDOKey;
import com.yunche.loan.domain.vo.RepaymentRecordVO;
import com.yunche.loan.mapper.RepaymentRecordDOMapper;
import com.yunche.loan.service.RepaymentRecordService;
import com.yunche.loan.config.util.POIUtil;
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
    @Override
    public ResultBean<RepaymentRecordVO> query(RepaymentRecordDOKey repaymentRecordDOKey) {
        Preconditions.checkNotNull(repaymentRecordDOKey,"请求数据不能为空");
        Preconditions.checkNotNull(repaymentRecordDOKey.getIdCard(),"身份证号不能为空");
        Preconditions.checkNotNull(repaymentRecordDOKey.getRepayCardId(),"还款卡号不能为空");
        Preconditions.checkNotNull(repaymentRecordDOKey.getCurrentOverdueTimes(),"当前逾期期数不能为空");
        RepaymentRecordVO repaymentRecordVO= new RepaymentRecordVO();
        RepaymentRecordDO repaymentRecordDO= repaymentRecordDOMapper.selectByPrimaryKey(repaymentRecordDOKey);
        BeanUtils.copyProperties(repaymentRecordDO,repaymentRecordVO);
        //TODO 界面展示相关字段组装
        return ResultBean.ofSuccess(repaymentRecordVO);
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


                //TODO 先查询是否存在记录,存在则更新,不存在就插入
                RepaymentRecordDOKey repaymentRecordDOKey=new RepaymentRecordDOKey();
                BeanUtils.copyProperties(repaymentRecordDO,repaymentRecordDOKey);

                RepaymentRecordDO repaymentRecordDOCheck= repaymentRecordDOMapper.selectByPrimaryKey(repaymentRecordDOKey);
                if(repaymentRecordDOCheck==null){
                    repaymentRecordDO.setGmtCreate(new Date());
                    int count= repaymentRecordDOMapper.insert(repaymentRecordDO);
                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录导入出错");
                }else{
                    repaymentRecordDO.setGmtCreate(repaymentRecordDOCheck.getGmtCreate());
                    repaymentRecordDO.setGmtModify(new Date());
                    int count= repaymentRecordDOMapper.updateByPrimaryKeySelective(repaymentRecordDO);
                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录更新出错");
                }
            }

        } catch (Exception e) {
            Preconditions.checkArgument(false, e.getMessage());
        }






//            // 解析每行结果在listener中处理
//            ExcelListener listener = new ExcelListener();
//
//            ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLSX, null, listener);
//
//            excelReader.read(new Sheet(1, 1, RepaymentRecordExcelVO.class));
//            List<Object> list =  listener.getDatas();
//            RepaymentRecordDO repaymentRecordDO =new RepaymentRecordDO();
//            if(list==null){
//                return ResultBean.ofSuccess("文件为空");
//            }
//            for(int i=0;i<list.size();i++){
//                RepaymentRecordExcelVO repaymentRecordExcelVO =  (RepaymentRecordExcelVO)list.get(i);
//                BeanUtils.copyProperties(repaymentRecordExcelVO,repaymentRecordDO);
//                //TODO 先查询是否存在记录,存在则更新,不存在就插入
//                RepaymentRecordDOKey repaymentRecordDOKey=new RepaymentRecordDOKey();
//                BeanUtils.copyProperties(repaymentRecordDO,repaymentRecordDOKey);
//
//                RepaymentRecordDO repaymentRecordDOCheck= repaymentRecordDOMapper.selectByPrimaryKey(repaymentRecordDOKey);
//                if(repaymentRecordDOCheck==null){
//                    int count= repaymentRecordDOMapper.insert(repaymentRecordDO);
//                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录导入出错");
//                }else{
//                    int count= repaymentRecordDOMapper.updateByPrimaryKeySelective(repaymentRecordDO);
//                    Preconditions.checkArgument(count > 0, "IDCard:"+repaymentRecordDO.getIdCard()+",对应记录更新出错");
//                }
//
//
//
//            }
//
//        } catch (Exception e) {
//
//        } finally {
//            try {
//                inputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        return ResultBean.ofSuccess("导入成功");
    }

    private InputStream getInputStream(String fileName) {
        File f= new File(fileName) ;
        InputStream input = null ;
        try {
            input = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

}
