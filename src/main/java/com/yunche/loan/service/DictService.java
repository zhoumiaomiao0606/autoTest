package com.yunche.loan.service;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/7/12
 */
public interface DictService {

    /**
     * 资料流转
     * k -> code
     *
     * @param key
     * @return
     */
    String getCodeByKeyOfLoanDataFlowTypes(String key);

    /**
     * 资料流转
     * code -> k
     *
     * @param code
     * @return
     */
    String getKeyByCodeOfLoanDataFlowTypes(String code);

    /**
     * 资料流转
     * K-V  map
     *
     * @return
     */
    Map<String, String> getKVMapOfLoanDataFlowTypes();

    /**
     * 资料流转
     * code-K  map
     *
     * @return
     */
    Map<String, String> getCodeKMapOfLoanDataFlowTypes();
}
