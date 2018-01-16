package com.yunche.loan.query.configure.info.address;

import com.yunche.loan.query.BaseQuery;
import lombok.Data;

/**
 * @author liuzhe
 * @date 2018/1/15
 */
@Data
public class BaseAreaQuery extends BaseQuery {

    private Integer id;

    private Long codeId;

    private Long parentCodeId;

    private String positionName;

    private Byte level;
}
