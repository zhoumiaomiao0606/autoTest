package com.yunche.loan.domain.query;

import com.yunche.loan.domain.entity.BankDO;
import lombok.Data;

import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/5/15
 */
@Data
public class BankQuery extends BaseQuery {

    private String name;

    private String mnemonicCode;

    private String contact;

    private String tel;

    private String officePhone;

    private String fax;

    private String address;

    private Byte needVideoFace;

    private Byte videoFaceMachine;
    /**
     * 状态（0-启用;1-停用;2-删除;）
     */
    private Byte status;

    private Date gmtCreate;

    private Date gmtModify;
}
