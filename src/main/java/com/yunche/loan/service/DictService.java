package com.yunche.loan.service;

import java.util.Map;

/**
 * @author liuzhe
 * @date 2018/7/12
 */
public interface DictService {

    /**
     * K-V  map
     *
     * @param field 字典filed
     * @return
     */
    Map<String, String> getKVMap(String field);

    /**
     * V-K  map
     *
     * @param field 字典filed
     * @return
     */
    Map<String, String> getVKMap(String field);

    /**
     * K-CODE  map
     *
     * @param field 字典filed
     * @return
     */
    Map<String, String> getKCodeMap(String field);

    /***
     * CODE-K  map
     *
     * @param field 字典filed
     * @return
     */
    Map<String, String> getCodeKMap(String field);


    /**
     * K -> CODE
     *
     * @param field
     * @param code
     * @return
     */
    String getKeyByCode(String field, String code);

    /**
     * CODE -> K
     *
     * @param field
     * @param key
     * @return
     */
    String getCodeByKey(String field, String key);

    /**
     * 资料流转
     * code -> k
     *
     * @param code
     * @return
     */
    String getKeyByCodeOfLoanDataFlowType(String code);
}
