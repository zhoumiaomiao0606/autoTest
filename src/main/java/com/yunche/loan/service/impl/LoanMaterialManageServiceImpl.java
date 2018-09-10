package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.domain.entity.BaseAreaDO;
import com.yunche.loan.domain.entity.LoanBaseInfoDO;
import com.yunche.loan.domain.entity.LoanMaterialManageDO;
import com.yunche.loan.domain.vo.*;
import com.yunche.loan.mapper.BaseAreaDOMapper;
import com.yunche.loan.mapper.LoanBaseInfoDOMapper;
import com.yunche.loan.mapper.LoanMaterialManageDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.LoanMaterialManageService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static com.yunche.loan.config.constant.BaseConst.VALID_STATUS;

/**
 * @author liuzhe
 * @date 2018/7/16
 */
@Service
public class LoanMaterialManageServiceImpl implements LoanMaterialManageService {

    @Autowired
    private LoanMaterialManageDOMapper loanMaterialManageDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private LoanBaseInfoDOMapper loanBaseInfoDOMapper;

    @Autowired
    private BaseAreaDOMapper baseAreaDOMapper;


    @Override
    public ResultBean<Void> save(LoanMaterialManageDO loanMaterialManageDO) {
        Preconditions.checkNotNull(loanMaterialManageDO.getOrderId(), "订单号不能为空");

        LoanMaterialManageDO existDO = loanMaterialManageDOMapper.selectByPrimaryKey(loanMaterialManageDO.getOrderId());

        if (null == existDO) {
            // create
            loanMaterialManageDO.setGmtCreate(new Date());
            loanMaterialManageDO.setGmtModify(new Date());
            int count = loanMaterialManageDOMapper.insertSelective(loanMaterialManageDO);
            Preconditions.checkArgument(count > 0, "插入失败");
        } else {
            // update
            loanMaterialManageDO.setGmtModify(new Date());
            int count = loanMaterialManageDOMapper.updateByPrimaryKeySelective(loanMaterialManageDO);
            Preconditions.checkArgument(count > 0, "编辑失败");
        }

        return ResultBean.ofSuccess(null, "保存成功");
    }

    @Override
    public ResultBean<RecombinationVO> detail(Long orderId) {
        Preconditions.checkNotNull(orderId, "订单号不能为空");

        UniversalCarInfoVO universalCarInfoVO = loanQueryDOMapper.selectUniversalCarInfo(orderId);

        LoanBaseInfoDO loanBaseInfoDO = loanBaseInfoDOMapper.getTotalInfoByOrderId(orderId);
        String tmpApplyLicensePlateArea = null;
        if (loanBaseInfoDO.getAreaId()!=null) {
            BaseAreaDO baseAreaDO = baseAreaDOMapper.selectByPrimaryKey(loanBaseInfoDO.getAreaId(), VALID_STATUS);
            //（个性化）如果上牌地是区县一级，则返回形式为 省+区
            if("3".equals(String.valueOf(baseAreaDO.getLevel()))){
                Long parentAreaId = baseAreaDO.getParentAreaId();
                BaseAreaDO cityDO = baseAreaDOMapper.selectByPrimaryKey(parentAreaId, null);
                baseAreaDO.setParentAreaId(cityDO.getParentAreaId());
                baseAreaDO.setParentAreaName(cityDO.getParentAreaName());
            }
            if (baseAreaDO != null) {
                if (baseAreaDO.getParentAreaName() != null) {
                    tmpApplyLicensePlateArea = baseAreaDO.getParentAreaName() + baseAreaDO.getAreaName();
                } else {
                    tmpApplyLicensePlateArea = baseAreaDO.getAreaName();
                }
            }
        }
        universalCarInfoVO.setVehicle_apply_license_plate_area(tmpApplyLicensePlateArea);

        RecombinationVO recombinationVO = new RecombinationVO();
        recombinationVO.setMaterialManage(loanQueryDOMapper.selectUniversalMaterialManage(orderId));
        recombinationVO.setCustomers(loanQueryDOMapper.selectUniversalCustomer(orderId));
        recombinationVO.setCar(universalCarInfoVO);
        recombinationVO.setFinancial(loanQueryDOMapper.selectFinancialScheme(orderId));

        return ResultBean.ofSuccess(recombinationVO);
    }

    @Override
    public ResultBean<Integer> imp(String ossKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ossKey), "ossKey不能为空");

        // 收集数据
        List<LoanMaterialManageDO> loanMaterialManageDOList = Lists.newArrayList();

        try {
            // readFile
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, ossKey);

            if (!CollectionUtils.isEmpty(rowList)) {
                // parse
                rowList.stream()
                        .filter(ArrayUtils::isNotEmpty)
                        .forEach(row -> {

                            LoanMaterialManageDO loanMaterialManageDO = new LoanMaterialManageDO();

                            loanMaterialManageDO.setOrderId(Long.valueOf(row[0]));
                            loanMaterialManageDO.setMaterialNum(row[1]);
                            loanMaterialManageDO.setCompleteDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row[2]));
                            loanMaterialManageDO.setInfo(row[3]);

                            loanMaterialManageDO.setGmtCreate(new Date());
                            loanMaterialManageDO.setGmtModify(new Date());

                            loanMaterialManageDOList.add(loanMaterialManageDO);
                        });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // batchInsert
        int count = batchInsert(loanMaterialManageDOList);

        return ResultBean.ofSuccess(count, "导入成功");
    }

    /**
     * 批量导入
     *
     * @param loanMaterialManageDOList
     * @return
     */
    private int batchInsert(List<LoanMaterialManageDO> loanMaterialManageDOList) {

        if (CollectionUtils.isEmpty(loanMaterialManageDOList)) {
            return 0;
        }

        // batchInsert
        int count = loanMaterialManageDOMapper.batchInsert(loanMaterialManageDOList);

        return count;
    }
}
