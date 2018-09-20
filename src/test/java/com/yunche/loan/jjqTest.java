package com.yunche.loan;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class jjqTest {
    public static void main(String[] args) {
        List<Integer> strs = Arrays.asList(1,2,null,4,5,null);

        System.out.println(strs.stream().reduce((x,y)->x+y));

    }
}
