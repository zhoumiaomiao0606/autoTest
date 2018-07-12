package com.yunche.loan.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.constant.IDict;
import com.yunche.loan.config.util.OSSUnit;
import com.yunche.loan.domain.entity.BankFileListDO;
import com.yunche.loan.domain.entity.BankFileListRecordDO;
import com.yunche.loan.mapper.BankFileListDOMapper;
import com.yunche.loan.mapper.BankFileListRecordDOMapper;
import com.yunche.loan.service.UnsecuredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class UnsecuredServiceImpl implements UnsecuredService{

    @Autowired
    BankFileListRecordDOMapper  bankFileListRecordDOMapper;

    @Autowired
    BankFileListDOMapper bankFileListDOMapper;
    @Override
    public boolean autoUnsecuredImp(String ossKey) {

            List<BankFileListRecordDO> recordLists = Lists.newArrayList();
            try {
                InputStream in = OSSUnit.getOSS2InputStream(ossKey);
                InputStreamReader inReader = null;
                inReader = new InputStreamReader(in, "UTF-8");


                BufferedReader bufReader = new BufferedReader(inReader);
                int bankFileListId = recordImportBatch(ossKey); //记录导入记录
                bankFileListRecordDOMapper.deleteBylistId(Long.valueOf(bankFileListId));
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
                return false;
            }
            return true;
    }

    /**
     *
     * @param bankFileListId
     * @param split
     * @return
     */
    private BankFileListRecordDO packObject(int bankFileListId, String[] split) {
        //地区号、平台编号、担保单位编号、订单号、卡号、姓名、证件类型、证件号码、分期金额、汇总笔数、汇总金额
        BankFileListRecordDO bankFileListRecordDO = new BankFileListRecordDO();
        bankFileListRecordDO.setAreaId(split[0].trim());
        bankFileListRecordDO.setPlatNo(split[1].trim());
        bankFileListRecordDO.setGuarantyUnit(split[2].trim());
        bankFileListRecordDO.setOrderId(Long.valueOf(StringUtil.isEmpty(split[3].trim())?"0":split[3].trim()));
        bankFileListRecordDO.setCardNumber(split[4].trim());
        bankFileListRecordDO.setName(split[5].trim());
        bankFileListRecordDO.setCardType(split[6].trim());
        bankFileListRecordDO.setCredentialNo(split[7].trim());
        bankFileListRecordDO.setInstalmentAmount(new BigDecimal(split[8].trim()));
        bankFileListRecordDO.setSumNumber(Integer.parseInt(split[9].trim()));
        bankFileListRecordDO.setSumAmount(new BigDecimal(split[10].trim()));
        return bankFileListRecordDO;

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
        bankFileListDO.setFileType(IDict.K_WJLX.WJLX_2);
        bankFileListDO.setGmtCreate(new Date());
        bankFileListDO.setOperator("auto");
        int count = bankFileListDOMapper.insertSelective(bankFileListDO);
        Preconditions.checkArgument(count>0,"插入失败");
        int bankFileListId =  bankFileListDO.getId().intValue();
        return bankFileListId;
    }
}
