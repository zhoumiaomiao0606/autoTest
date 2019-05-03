package com.course.utils;

import com.course.model.interfacename;

import java.util.Locale;
import java.util.ResourceBundle;

public class configfile {

    public static ResourceBundle bundle =ResourceBundle.getBundle("application", Locale.CHINA);
    private  static  String url=bundle.getString("test.url");
    public  static  String uri=null;
    public  static  String testurl=null;
    public static  String getUrl(interfacename interfacename){

        if(interfacename.GETUSERLIST==interfacename){
            uri=bundle.getString("getuserlist.uri");
       testurl=url +uri;

        }
        else if(interfacename.GETUSERINFO==interfacename){
            uri=bundle.getString("getuserinfo.uri");
            testurl=url +uri;
        }
        else if(interfacename.LOGIN==interfacename){
            uri=bundle.getString("login.uri");
            testurl=url +uri;
        }
        else if(interfacename.ADDUSERINFO==interfacename){
            uri=bundle.getString("adduserinfo.uri");
            testurl=url +uri;
        }
        else if(interfacename.UPDATEUSERINFO==interfacename){
            uri=bundle.getString("updateuserinfo.uri");
            testurl=url +uri;
        }

        return  testurl;



    }

}
