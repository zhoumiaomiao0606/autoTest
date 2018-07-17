package com.yunche.loan.service.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.yunche.loan.config.cache.DepartmentCache;
import com.yunche.loan.config.cache.DictCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.DateTimeFormatUtils;
import com.yunche.loan.config.util.POIUtil;
import com.yunche.loan.config.util.SessionUtils;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.UniversalCustomerOrderVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;
import com.yunche.loan.mapper.LoanDataFlowDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.ActivitiVersionService;
import com.yunche.loan.service.DictService;
import com.yunche.loan.service.LoanDataFlowService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

import static com.yunche.loan.config.util.DateTimeFormatUtils.formatter_yyyyMMdd;

/**
 * @author liuzhe
 * @date 2018/7/4
 */
@Service
public class LoanDataFlowServiceImpl implements LoanDataFlowService {

    @Autowired
    private LoanDataFlowDOMapper loanDataFlowDOMapper;

    @Autowired
    private LoanQueryDOMapper loanQueryDOMapper;

    @Autowired
    private ActivitiVersionService activitiVersionService;

    @Autowired
    private DictCache dictCache;

    @Autowired
    private DepartmentCache departmentCache;

    @Autowired
    private DictService dictService;


    @Override
    public LoanDataFlowDO getLastByOrderIdAndType(Long orderId, Byte type) {
        Preconditions.checkNotNull(orderId, "orderId不能为空");
        Preconditions.checkNotNull(type, "type不能为空");

        LoanDataFlowDO loanDataFlowDO = loanDataFlowDOMapper.getLastByOrderIdAndType(orderId, type);
        return loanDataFlowDO;
    }

    @Override
    public ResultBean<UniversalDataFlowDetailVO> detail(Long id) {
        Preconditions.checkNotNull(id, "资料流转单ID不能为空");

        UniversalDataFlowDetailVO universalDataFlowDetailVO = loanQueryDOMapper.selectUniversalDataFlowDetail(id);

        // tyoe - kvMap
        Map<String, String> loanDataFlowTypeKVMap = dictService.getKVMap("loanDataFlowType");
        universalDataFlowDetailVO.setTypeText(loanDataFlowTypeKVMap.get(String.valueOf(universalDataFlowDetailVO.getType())));

        // expressCom - kvMap
        Map<String, String> loanDataFlowExpressComKVMap = dictService.getKVMap("loanDataFlowExpressCom");
        universalDataFlowDetailVO.setExpressComText(loanDataFlowExpressComKVMap.get(String.valueOf(universalDataFlowDetailVO.getExpressCom())));

        return ResultBean.ofSuccess(universalDataFlowDetailVO);
    }

    @Override
    @Transactional
    public ResultBean create(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getType(), "type不能为空");

        loanDataFlowDO.setGmtCreate(new Date());
        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.insertSelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "插入失败");

        return ResultBean.ofSuccess(loanDataFlowDO.getId(), "创建成功");
    }

    @Override
    @Transactional
    public ResultBean update(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getId(), "id不能为空");

        loanDataFlowDO.setGmtModify(new Date());
        int count = loanDataFlowDOMapper.updateByPrimaryKeySelective(loanDataFlowDO);
        Preconditions.checkArgument(count > 0, "编辑失败");

        return ResultBean.ofSuccess(null, "编辑成功");
    }

    @Override
    public ResultBean<List<BaseVO>> flowDept() {
        List<BaseVO> flowDept = departmentCache.getFlowDept();
        return ResultBean.ofSuccess(flowDept);
    }

    @Override
    public ResultBean<List<UniversalCustomerOrderVO>> queryDataFlowCustomerOrder(String customerName) {

        // telephone_verify = 1  &&   data_flow_mortgage_b2c = 0
        Long loginUserId = SessionUtils.getLoginUser().getId();
        List<UniversalCustomerOrderVO> universalCustomerOrderVOS = loanQueryDOMapper.selectUniversalDataFlowCustomerOrder(loginUserId, customerName);

        return ResultBean.ofSuccess(universalCustomerOrderVOS);
    }

    @Override
    public ResultBean<Integer> imp(String ossKey) {
        Preconditions.checkArgument(StringUtils.isNotBlank(ossKey), "ossKey不能为空");

        // 收集数据
        List<LoanDataFlowDO> loanDataFlowDOList = Lists.newArrayList();

        try {
            // readFile
            List<String[]> rowList = POIUtil.readExcelFromOSS(0, 1, ossKey);

            if (!CollectionUtils.isEmpty(rowList)) {
                // parse
                rowList.stream()
                        .filter(ArrayUtils::isNotEmpty)
                        .forEach(row -> {

                            LoanDataFlowDO loanDataFlowDO = new LoanDataFlowDO();

                            loanDataFlowDO.setOrderId(Long.valueOf(row[0]));
                            loanDataFlowDO.setType(Byte.valueOf(row[1]));
                            loanDataFlowDO.setExpressCom(Byte.valueOf(row[2]));
                            loanDataFlowDO.setExpressNum(row[3]);
                            loanDataFlowDO.setExpressSendDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row[4]));
                            loanDataFlowDO.setExpressReceiveDate(DateTimeFormatUtils.convertStrToDate_yyyyMMdd(row[5]));
                            loanDataFlowDO.setExpressReceiveMan(row[6]);
                            loanDataFlowDO.setHasMortgageContract(convertHasMortgageContract(row[7]));
                            loanDataFlowDO.setFlowOutDeptName(row[8]);
                            loanDataFlowDO.setFlowInDeptName(row[9]);

                            loanDataFlowDO.setGmtCreate(new Date());
                            loanDataFlowDO.setGmtModify(new Date());

                            loanDataFlowDOList.add(loanDataFlowDO);
                        });
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // batchInsert
        int count = batchInsert(loanDataFlowDOList);

        return ResultBean.ofSuccess(count, "导入成功");
    }

    /**
     * 批量导入
     *
     * @param loanDataFlowDOList
     * @return
     */
    private int batchInsert(List<LoanDataFlowDO> loanDataFlowDOList) {

        if (CollectionUtils.isEmpty(loanDataFlowDOList)) {
            return 0;
        }

        // batchInsert
        int count = loanDataFlowDOMapper.batchInsert(loanDataFlowDOList);
        return count;
    }

    private Byte convertHasMortgageContract(String field) {

        if ("否".equals(field)) {
            return 0;
        } else if ("是".equals(field)) {
            return 1;
        }

        return null;
    }

}