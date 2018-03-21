package com.yunche.loan.config.common;

public interface ProcessBind<T> {
    String process(T t);
}
