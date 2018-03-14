package com.yunche.loan.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class RecombinationVO<T> {
        private T info;

        private List<UniversalCustomerVO> customers;

}
