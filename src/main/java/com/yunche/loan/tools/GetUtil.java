package com.yunche.loan.tools;


import com.yunche.loan.system.core.ExceptionEnum;
import com.yunche.loan.system.core.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Created by WangGang on 2017/6/22 0022.
 * E-mail userbean@outlook.com
 * The final interpretation of this procedure is owned by the author
 */
public class GetUtil {
    public static <T>T get(Map map, String value){
        if(StringUtils.isBlank(value)){
            throw new ServiceException(ExceptionEnum.EL00000002);
        }

        if(map == null){
            throw new ServiceException(ExceptionEnum.EL00000001);
        }

        if(map.isEmpty()){
            throw new ServiceException(ExceptionEnum.EL00000001);
        }

        Object obj = map.get(value);
        if(obj == null){
            throw new ServiceException(ExceptionEnum.EL00000003);
        }
        T t = (T)obj ;
        if(t == null){
            throw new ServiceException(ExceptionEnum.EL00000003);
        }

        return t;
    }
}
