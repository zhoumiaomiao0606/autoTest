package com.yunche.loan.service.impl;

import com.yunche.loan.config.cache.DictCache;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.config.util.StringUtil;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;

/**
 * @author liuzhe
 * @date 2018/7/10
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private DictCache dictCache;


    @Override
    public ResultBean<DataDictionaryVO> dictionary(String field) {

        DataDictionaryVO dataDictionaryVO = dictCache.get();

        // 返回所有
        if (StringUtils.isBlank(field) || null == dataDictionaryVO) {
            return ResultBean.ofSuccess(dataDictionaryVO);
        }

        // 返回单个字段
        try {
            Class<? extends DataDictionaryVO> clazz = dataDictionaryVO.getClass();

            // get Val
            String getMethodName = "get" + StringUtil.firstLetter2UpperCase(field);
            Method getMethod = clazz.getMethod(getMethodName);
            DataDictionaryVO.Detail fieldDict = (DataDictionaryVO.Detail) getMethod.invoke(dataDictionaryVO);

            // set Val
            DataDictionaryVO dataDictionaryVO_ = new DataDictionaryVO();
            String setMethodName = "set" + StringUtil.firstLetter2UpperCase(field);
            Method setMethod = clazz.getMethod(setMethodName, DataDictionaryVO.Detail.class);
            Object result_ = setMethod.invoke(dataDictionaryVO_, fieldDict);

            return ResultBean.ofSuccess(dataDictionaryVO_);

        } catch (NoSuchMethodException e) {
            return ResultBean.ofError("字段不存在：" + field);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

}
