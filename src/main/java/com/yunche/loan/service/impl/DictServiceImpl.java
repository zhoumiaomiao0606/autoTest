package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.yunche.loan.config.cache.DictCache;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.service.DictService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * @author liuzhe
 * @date 2018/7/12
 */
@Service
public class DictServiceImpl implements DictService {

    @Autowired
    private DictCache dictCache;


    @Override
    public String getCodeByKeyOfLoanDataFlowTypes(String key) {
        Preconditions.checkArgument(StringUtils.isNotBlank(key), "key不能为空");

        DataDictionaryVO dataDictionaryVO = dictCache.get();

        DataDictionaryVO.Detail loanDataFlowTypes = dataDictionaryVO.getLoanDataFlowTypes();

        final String[] code = {null};

        if (null != loanDataFlowTypes) {

            JSONArray attr = loanDataFlowTypes.getAttr();

            attr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        JSONObject eJObj = (JSONObject) e;

                        String k = eJObj.getString("k");
                        String code_ = eJObj.getString("code");

                        if (key.equals(k)) {
                            code[0] = code_;
                        }
                    });
        }

        return code[0];
    }

    @Override
    public String getKeyByCodeOfLoanDataFlowTypes(String code) {
        Preconditions.checkArgument(StringUtils.isNotBlank(code), "code不能为空");

        DataDictionaryVO dataDictionaryVO = dictCache.get();

        DataDictionaryVO.Detail loanDataFlowTypes = dataDictionaryVO.getLoanDataFlowTypes();

        final String[] key = {null};

        if (null != loanDataFlowTypes) {

            JSONArray attr = loanDataFlowTypes.getAttr();

            attr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        JSONObject eJObj = (JSONObject) e;

                        String k = eJObj.getString("k");
                        String code_ = eJObj.getString("code");

                        if (code.equals(code_)) {
                            key[0] = k;
                        }
                    });
        }

        return key[0];
    }

    @Override
    public Map<String, String> getKVMapOfLoanDataFlowTypes() {

        Map<String, String> kvMap = getKVMapOfDictField("loanDataFlowTypes");

        return kvMap;
    }

    @Override
    public Map<String, String> getCodeKMapOfLoanDataFlowTypes() {

        Map<String, String> codeKMap = Maps.newHashMap();

        // getAll
        DataDictionaryVO dataDictionaryVO = dictCache.get();

        DataDictionaryVO.Detail loanDataFlowTypes = dataDictionaryVO.getLoanDataFlowTypes();

        if (null != loanDataFlowTypes) {

            JSONArray attr = loanDataFlowTypes.getAttr();

            attr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        JSONObject eJObj = (JSONObject) e;

                        String k = eJObj.getString("k");
                        String code = eJObj.getString("code");

                        codeKMap.put(code, k);
                    });
        }

        return codeKMap;
    }

    /**
     * 反射获取  K/V map
     *
     * @param field
     * @return
     */
    private Map<String, String> getKVMapOfDictField(String field) {

        Map<String, String> kvMap = Maps.newHashMap();

        // getAll
        DataDictionaryVO dataDictionaryVO = dictCache.get();

        // 反射获取
        Class<? extends DataDictionaryVO> clazz = dataDictionaryVO.getClass();

        // getXX
        String methodName = "get" + StringUtil.firstLetter2UpperCase(field);

        try {
            Method method = clazz.getMethod(methodName);

            DataDictionaryVO.Detail result = (DataDictionaryVO.Detail) method.invoke(dataDictionaryVO);

            if (null != result) {

                JSONArray attr = result.getAttr();

                attr.stream()
                        .filter(Objects::nonNull)
                        .forEach(e -> {

                            JSONObject eJObj = (JSONObject) e;

                            String k = eJObj.getString("k");
                            String v = eJObj.getString("v");

                            kvMap.put(k, v);
                        });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return kvMap;
    }
}
