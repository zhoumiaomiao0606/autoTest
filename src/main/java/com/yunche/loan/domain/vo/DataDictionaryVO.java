package com.yunche.loan.domain.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * 数据字典
 *
 * @author liuzhe
 * @date 2018/7/10
 */
@Data
public class DataDictionaryVO {

    /**
     * 字段
     * 资料流转-类型
     */
    private Detail loanDataFlowTypes;
    /**
     * 资料流转-角色对应的节点列表
     */
    private Detail roleDataFlowNodeList;


    /**
     * 详情
     */
    @Data
    public static class Detail {

        private Long id;

        private String name;

        private String field;

        @JsonIgnore
        private String content;

        /**
         * K/V列表
         */
        private JSONArray attr;


        public JSONArray getAttr() {

            if (!CollectionUtils.isEmpty(attr)) {
                return attr;
            }

            if (StringUtils.isNotBlank(content)) {

                JSONArray jsonArray = JSON.parseArray(content);

                this.attr = jsonArray;

                return jsonArray;
            }

            return new JSONArray();
        }
    }
}
