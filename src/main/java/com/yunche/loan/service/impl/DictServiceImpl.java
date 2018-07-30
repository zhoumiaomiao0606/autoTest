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
    public Map<String, String> getKVMap(String field) {
        Preconditions.checkArgument(StringUtils.isNotBlank(field), "field不能为空");

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

    @Override
    public Map<String, String> getVKMap(String field) {
        Preconditions.checkArgument(StringUtils.isNotBlank(field), "field不能为空");

        Map<String, String> vkMap = Maps.newHashMap();

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

                            vkMap.put(v, k);
                        });
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return vkMap;
    }

    @Override
    public Map<String, String> getKCodeMap(String field) {

        Map<String, String> kCodeMap = Maps.newHashMap();

        DataDictionaryVO.Detail fieldDetail = getFieldDetail(field);

        if (null != fieldDetail) {

            JSONArray attr = fieldDetail.getAttr();

            attr.stream()
                    .filter(Objects::nonNull)
                    .forEach(e -> {

                        JSONObject eJObj = (JSONObject) e;

                        String k = eJObj.getString("k");
                        String code = eJObj.getString("code");

                        kCodeMap.put(k, code);
                    });
        }

        return kCodeMap;
    }

    @Override
    public Map<String, String> getCodeKMap(String field) {

        Map<String, String> codeKMap = Maps.newHashMap();

        DataDictionaryVO.Detail fieldDetail = getFieldDetail(field);

        if (null != fieldDetail) {

            JSONArray attr = fieldDetail.getAttr();

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


    @Override
    public String getCodeByKey(String field, String key) {
        Preconditions.checkArgument(StringUtils.isNotBlank(field), "field不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(key), "code不能为空");

        Map<String, String> kCodeMap = getKCodeMap(field);

        String code = kCodeMap.get(key);

        return code;
    }

    @Override
    public String getKeyByCode(String field, String code) {
        Preconditions.checkArgument(StringUtils.isNotBlank(field), "field不能为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(code), "code不能为空");

        Map<String, String> codeKMap = getCodeKMap(field);

        String key = codeKMap.get(code);

        return key;
    }


    private DataDictionaryVO.Detail getFieldDetail(String field) {
        // getAll
        DataDictionaryVO dataDictionaryVO = dictCache.get();

        Class<? extends DataDictionaryVO> clazz = dataDictionaryVO.getClass();

        String methodName = "get" + StringUtil.firstLetter2UpperCase(field);

        try {
            Method method = clazz.getMethod(methodName);

            DataDictionaryVO.Detail fieldDetail = (DataDictionaryVO.Detail) method.invoke(dataDictionaryVO);

            return fieldDetail;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
