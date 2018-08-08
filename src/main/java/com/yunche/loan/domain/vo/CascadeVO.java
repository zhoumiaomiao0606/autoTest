package com.yunche.loan.domain.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * 级联对象
 * <p>
 * 可无限递归
 *
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class CascadeVO {
    /**
     * id
     */
    private Long value;
    /**
     * name
     */
    private String label;
    /**
     * 等级
     */
    private Integer level;

    private boolean hasChild;

    private Long parentId;

    private List<CascadeVO> children;

    public boolean getHasChild() {
        if (CollectionUtils.isEmpty(children)) {
            return false;
        } else {
            return true;
        }
    }
}
