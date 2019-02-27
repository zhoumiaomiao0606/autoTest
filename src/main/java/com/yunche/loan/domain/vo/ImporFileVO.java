package com.yunche.loan.domain.vo;


import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ImporFileVO {

    private int success;

    private int error;

    private List<String> info = Collections.EMPTY_LIST;
}
