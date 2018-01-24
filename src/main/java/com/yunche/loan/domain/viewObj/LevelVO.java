package com.yunche.loan.domain.viewObj;

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
public class LevelVO {

    private Long id;

    private String name;

    private boolean hasChild;

    private List<LevelVO> childList;

    public boolean getHasChild() {
        if (CollectionUtils.isEmpty(childList)) {
            return false;
        } else {
            return true;
        }
    }
}
