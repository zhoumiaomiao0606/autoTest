package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class BeanPlasticityUtills {

    public static <T> T copy(Class<T> t, Object orig) {
        try {
            T result = t.newInstance();
            org.apache.commons.beanutils.BeanUtils.copyProperties(result, orig);
            return result;
        } catch (IllegalAccessException e) {
            throw new BizException("copy bean throw IllegalAccessException");
        } catch (InstantiationException e) {
            throw new BizException("copy bean throw InstantiationException");
        } catch (InvocationTargetException e) {
            throw new BizException("copy bean throw InvocationTargetException");
        }
    }

    static {
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class type, Object value) {
                if (value == null) {
                    return null;
                }
                if (!(value instanceof String)) {
                    throw new ConversionException("类型不匹配String");
                }
                if (StringUtils.isBlank((String) value)) {
                    throw new ConversionException("要转成date的value不能为空");
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    return df.parse((String) value);
                } catch (ParseException e) {
                    throw new RuntimeException("日期转化失败");
                }
            }
        }, java.util.Date.class);
    }
}


