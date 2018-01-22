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
    private Head head;

    @Data
    public static class Head {
        private Long employeeId;
        private String employeeName;
    }

    @Data
    public static class BizArea {

        private Long id;

        private String name;

        private Integer level;

        private boolean hasChild;

        private List<BizArea> childList;

        public boolean getHasChild() {
            if (CollectionUtils.isEmpty(childList)) {
                return false;
            } else {
                return true;
            }
        }
    }

}
