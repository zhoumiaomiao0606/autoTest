package com.course.model;

import lombok.Data;

@Data

public class User {
    private  int id;
    private  String name;
    private  String sex;
    private  String permition;
    private  String password;
    private  int age;
    private  String isdelete;
    private  String expected;
}
