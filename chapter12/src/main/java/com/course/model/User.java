package com.course.model;

import lombok.Data;

@Data
public class User {
    private  int id;
    private  String name;
    private  String sex;
    private  int age;
    private  String permition;
    private  String password;

    private  String isdelete;
    @Override
    public  String toString(){
        return ("{id"+ id +","+
                "name"+name+","+
                "sex"+sex+","+
                "permition"+permition+","+
                "password"+ password + ","+
                "age"+age+","+
                "isdelete"+isdelete+"}");
    }

}
