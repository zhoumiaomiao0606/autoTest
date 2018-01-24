package com.yunche.loan.domain.viewObj;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

/**
 * @author liuzhe
 * @date 2018/1/23
 */
@Data
public class EmployeeVO {

    private Long id;

    private String name;

    private String idCard;

    private String mobile;

    private String email;

    private String dingDing;

    private Long departmentId;

    private String title;

    private Date entryDate;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
    /**
     * 直接上级
     */
    private Leader leader;

    @Data
    public static class Leader {
        private Long employeeId;
        private Long employeeName;
    }
}
