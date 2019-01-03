package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class DistributorVO {

    private String resultCode;
    private String message;
    private List<Distributor> datas;


    @Data
    public static class Distributor{
        private String id;
        private String name;
    }

}
