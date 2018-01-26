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

    private String title;

    private Date entryDate;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;
    /**
     * 所属部门
     */
    private BaseVO department;
    /**
     * 直接上级
     */
    private BaseVO leader;
}
