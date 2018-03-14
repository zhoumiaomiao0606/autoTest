package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;

public class BeanPlasticityUtills {

    public static <T>T copy(Class<T> t,Object orig)  {
        try {
            T result = t.newInstance();
            BeanUtils.copyProperties(result,orig);
            return result;
        } catch (IllegalAccessException e) {
            throw new BizException("copy bean throw IllegalAccessException");
        } catch (InvocationTargetException e) {
            throw new BizException("copy bean throw InvocationTargetException");
        } catch (InstantiationException e) {
            throw new BizException("copy bean throw InstantiationException");
        }
    }
}
