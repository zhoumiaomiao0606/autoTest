package com.yunche.loan;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class jjqTest {
    public static void main(String[] args) throws IOException {
        File file =new File("/Users/zhongmingxiao/Downloads/yc.mp4");

        System.out.println("====="+file.getPath());



        System.out.println("====++++++"+new StringBuilder(file.getPath()).append("_c"));

        System.out.println("====="+file.getName());
        System.out.println("====="+file.getAbsolutePath());
        System.out.println("====="+file.getAbsoluteFile());
        System.out.println("====="+file.getCanonicalPath());


    }
}
