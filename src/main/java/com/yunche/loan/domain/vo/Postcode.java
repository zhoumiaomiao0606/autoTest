package com.yunche.loan.domain.vo;


import lombok.Data;

import java.util.List;

@Data
public class Postcode {
    List<PostcodeDetail> result;
}

class PostcodeDetail{
    String ADDR;

    String POSTCODE;
}
