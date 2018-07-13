package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.yunche.loan.config.cache.DepartmentCache;
import com.yunche.loan.config.cache.DictCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.LoanDataFlowDO;
import com.yunche.loan.domain.vo.BaseVO;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.domain.vo.UniversalDataFlowDetailVO;
import com.yunche.loan.mapper.LoanDataFlowDOMapper;
import com.yunche.loan.mapper.LoanQueryDOMapper;
import com.yunche.loan.service.ActivitiVersionService;
import com.yunche.loan.service.DictService;
import com.yunche.loan.service.LoanDataFlowService;
import com.yunche.loan.service.LoanRejectLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.yunche.loan.config.constant.LoanDataFlowConst.FLOW_END_NO;

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

        // kvMap
        Map<String, String> kvMap = dictService.getKVMapOfLoanDataFlowType();
        // type -> typeText
        String typeText = kvMap.get(String.valueOf(universalDataFlowDetailVO.getType()));
        universalDataFlowDetailVO.setTypeText(typeText);

        return ResultBean.ofSuccess(universalDataFlowDetailVO);
    }

    @Override
    @Transactional
    public ResultBean create(LoanDataFlowDO loanDataFlowDO) {
        Preconditions.checkArgument(null != loanDataFlowDO && null != loanDataFlowDO.getType(), "type不能为空");

        loanDataFlowDO.setStatus(FLOW_END_NO);
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
    public ResultBean<Object> key() {

        Set<String> loginUserOwnDataFlowNodes = activitiVersionService.getLoginUserOwnDataFlowNodes();

        return ResultBean.ofSuccess(loginUserOwnDataFlowNodes);
    }

    @Override
    public ResultBean<Object> key_get_type(String key) {

        DataDictionaryVO dataDictionaryVO = dictCache.get();

        DataDictionaryVO.Detail loanDataFlowTypes = dataDictionaryVO.getLoanDataFlowType();

        JSONArray attr = loanDataFlowTypes.getAttr();

        final String[] val = {null};

        attr.stream()
                .forEach(e -> {

                    JSONObject jsonObj = (JSONObject) e;

                    String k = jsonObj.getString("k");
                    String v = jsonObj.getString("v");
                    String code = jsonObj.getString("code");

                    if (key.equals(code)) {
                        val[0] = v;
                    }

                });

        return ResultBean.ofSuccess(val[0]);
    }

    @Override
    public ResultBean<Object> type_get_key(String type) {

        DataDictionaryVO dataDictionaryVO = dictCache.get();

        DataDictionaryVO.Detail loanDataFlowTypes = dataDictionaryVO.getLoanDataFlowType();

        JSONArray attr = loanDataFlowTypes.getAttr();

        final String[] val = {null};

        attr.stream()
                .forEach(e -> {

                    JSONObject jsonObj = (JSONObject) e;

                    String k = jsonObj.getString("k");
                    String v = jsonObj.getString("v");
                    String code = jsonObj.getString("code");

                    if (type.equals(k)) {
                        val[0] = code;
                    }

                });

        return ResultBean.ofSuccess(val[0]);
    }

}