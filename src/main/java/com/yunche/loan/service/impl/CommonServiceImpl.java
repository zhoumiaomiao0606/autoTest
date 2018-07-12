package com.yunche.loan.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.yunche.loan.config.result.ResultBean;
import com.yunche.loan.domain.entity.ConfDictDO;
import com.yunche.loan.domain.vo.DataDictionaryVO;
import com.yunche.loan.mapper.ConfDictDOMapper;
import com.yunche.loan.service.CommonService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.List;


/**
 * @author liuzhe
 * @date 2018/7/10
 */
@Service
public class CommonServiceImpl implements CommonService {

    @Autowired
    private ConfDictDOMapper confDictDOMapper;


    @Override
    public ResultBean<DataDictionaryVO> dictionary() throws Exception {

        DataDictionaryVO dataDictionaryVO = new DataDictionaryVO();

        // getAll
        List<ConfDictDO> confDictDOList = confDictDOMapper.getAll();

        // 反射赋值
        Class<? extends DataDictionaryVO> dataDictionaryClass = dataDictionaryVO.getClass();

        if (!CollectionUtils.isEmpty(confDictDOList)) {

            confDictDOList.stream()
                    .filter(e -> null != e && StringUtils.isNotBlank(e.getField()))
                    .forEach(e -> {

                        String field = e.getField();

                        String endStr = field.substring(1, field.length());

                        char startChar = field.charAt(0);

                        String methodName = "set" + Character.toUpperCase(startChar) + endStr;

                        try {
                            Method method = dataDictionaryClass.getMethod(methodName, DataDictionaryVO.Detail.class);

                            DataDictionaryVO.Detail detail = new DataDictionaryVO.Detail();
                            BeanUtils.copyProperties(e, detail);

                            JSONArray attr = detail.getAttr();

                            Object result = method.invoke(dataDictionaryVO, detail);

                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
        }

        return ResultBean.ofSuccess(dataDictionaryVO);
    }

}
