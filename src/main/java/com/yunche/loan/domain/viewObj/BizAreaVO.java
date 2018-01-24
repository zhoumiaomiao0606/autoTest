package com.yunche.loan.domain.viewObj;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/19
 */
@Data
public class BizAreaVO {
    private Long id;

    private String name;

    private Long parentId;

    private Integer level;

    private String info;

    private Date gmtCreate;

    private Date gmtModify;
    /**
     * 部门负责人
     */
    private Leader leader;

    @Data
    public static class Leader {
        private Long employeeId;
        private String employeeName;
    }

    /**
     * 级联对象
     */
    @Data
    public static class Level extends LevelVO {
        private Integer level;
    }

}
