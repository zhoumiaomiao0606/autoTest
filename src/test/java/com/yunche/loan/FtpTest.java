package com.yunche.loan;

import com.yunche.loan.config.util.FtpUtil;
import org.junit.Test;

import java.io.File;

public class FtpTest {
    private static final String ServerIP = "122.225.203.102";
    private static final String UserName = "YCFTP";
    private static final String PassWord = "YC^&981F&*T90P1@#";
    private static final int Port = 21;
    private static final String ServerPath = "E:/ZSFTP/LocalUser/YCFTP/yunche/";
    private static final String localFilePath = "/Users/zhengdu/Downloads/yunche/";
    private static final String fileName = "20180627085149135470.docx";

//    private static final String  fileName="20180625173629979759.jpg";
    @Test
    public void hdajhsak(){
       String s = "云车-3123123";
       System.out.println(s.substring(3));
    }

    //@Test
    public void func1() {
//        FtpImpl ftp = new FtpImpl();
        try {
//            ftp.connect(ServerIP,Port,UserName,PassWord);
//            if(!ftp.existDirectory(ServerPath)){
//                ftp.mkdir(ServerPath);
//            }
//            ftp.cd(ServerPath);
//            ftp.bin();
////            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
//            boolean b = ftp.uploadFile(localFilePath+fileName,fileName);
//            if(b){
//                System.out.println("======succ");
//            }else{
//                System.out.println("======fail");
//            }
//            ftp.disconnect();

//
//            Thread thread1 = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("A.......1");
//                    FtpUtil.icbcUpload("/tmp/8888.jpg");
//                    System.out.println("A.......2");
//                }
//            });
//            Thread thread2 = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("B.......1");
//                    FtpUtil.icbcUpload("/tmp/9999.jpg");
//                    System.out.println("B.......2");
//                }
//            });
//            Thread thread3 = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    System.out.println("C.......1");
//                    FtpUtil.icbcUpload("/tmp/9898.jpg");
//                    System.out.println("C.......2");
//                }
//            });
//            thread1.start();
//            thread2.start();
//            thread3.start();
//            FtpUtil.icbcUpload("/tmp/9999.jpg");

            FtpUtil.icbcUpload("/tmp/aaa.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
