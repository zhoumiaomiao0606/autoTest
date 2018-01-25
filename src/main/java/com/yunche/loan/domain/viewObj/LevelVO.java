package com.yunche.loan.domain.viewObj;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
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
public class LevelVO {
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

    private List<LevelVO> children;

    public boolean getHasChild() {
        if (CollectionUtils.isEmpty(children)) {
            return false;
        } else {
            return true;
        }
    }
}
