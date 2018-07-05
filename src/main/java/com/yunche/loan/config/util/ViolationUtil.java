package com.yunche.loan.config.util;

import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Component
public class ViolationUtil {

    @Resource
    private LocalValidatorFactoryBean validator;

    public  <T>void  violation(T object, Class<?>... groups){
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if(violations.size() > 0){
            throw new ConstraintViolationException(violations);
        }
    }

    public  <T>void  violation(T object){
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if(violations.size() > 0){
            throw new ConstraintViolationException(violations);
        }
    }
}
