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
public class DepartmentVO {
    private Long id;

    private String name;

    private Long parentId;

    private Long employeeId;

    private String tel;

    private String fax;

    private Long areaId;

    private String address;

    private String openBank;

    private String receiveUnit;

    private String bankAccount;

    private Date gmtCreate;

    private Date gmtModify;

    private Byte status;

    private String feature;
}
