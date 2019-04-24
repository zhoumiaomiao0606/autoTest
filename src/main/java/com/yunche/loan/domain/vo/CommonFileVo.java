package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class CommonFileVo
{
    private Long orderId;
    private List<FileVO> files = Collections.EMPTY_LIST;
}
