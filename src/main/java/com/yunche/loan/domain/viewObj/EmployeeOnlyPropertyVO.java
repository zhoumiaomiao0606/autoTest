package com.yunche.loan.domain.viewObj;

import lombok.Data;

import java.util.List;

/**
 * @author liuzhe
 * @date 2018/2/5
 */
@Data
public class EmployeeOnlyPropertyVO {

    private List<String> idCardList;

    private List<String> mobileList;

    private List<String> emailList;

    private List<String> dingDingList;
}
