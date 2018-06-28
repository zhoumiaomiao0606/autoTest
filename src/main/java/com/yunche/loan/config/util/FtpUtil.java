package com.yunche.loan.config.util;

import com.google.common.base.Preconditions;
import com.yunche.loan.config.exception.BizException;

import java.io.File;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class FtpUtil {
    private static String servierIP;
    private static String userName;
    private static String password;
    private static String serverpath;
    private static String port;

    static{
        ResourceBundle bundle = PropertyResourceBundle.getBundle("sysconfig");
        servierIP = bundle.containsKey("servierIP") == false ? "" : bundle.getString("servierIP");
        userName = bundle.containsKey("userName") == false? "" : bundle.getString("userName");
        password = bundle.containsKey("password") == false ? "" : bundle.getString("password");
        serverpath = bundle.containsKey("serverpath") == false ? "" : bundle.getString("serverpath");
        port = bundle.containsKey("port") == false ? "" : bundle.getString("port");
    }

    /**
     *
     * @param localFilePath
     * @param serverPath
     * @param serverIp
     * @param port
     * @param userName
     * @param password
     * @param fileName
     */

    public static void download(String localFilePath,String serverPath,String serverIp,int port ,String userName, String password,String fileName){
        Preconditions.checkNotNull(localFilePath,"本地存放路径未配置");
        Preconditions.checkNotNull(serverPath,"服务器文件路径未配置");
        Preconditions.checkNotNull(serverIp,"服务器IP未配置");
        Preconditions.checkNotNull(port,"端口未配置");
        Preconditions.checkNotNull(password,"密码未配置");
        FtpImpl ftp = new FtpImpl();
        try {
            ftp.connect(serverIp,port,userName,password);
            if(!ftp.existDirectory(serverPath)){
                ftp.mkdir(serverPath);
            }
            ftp.cd(serverPath);
            ftp.bin();
            boolean download = ftp.download(fileName, localFilePath + fileName);
//            boolean b = ftp.uploadFile(localFilePath+fileName,fileName);

            if(!download){
                Preconditions.checkArgument(false,"文件下载失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
               Preconditions.checkNotNull(false,e.getMessage());
            }
        }
    }

    /**
     * 上传文件
     * @param localFilePath
     * @param serverPath
     * @param serverIp
     * @param port
     * @param userName
     * @param password
     * @param fileName
     */
    public static void upload(String localFilePath,String serverPath,String serverIp,int port ,String userName, String password,String fileName){
        Preconditions.checkNotNull(localFilePath,"本地存放路径未配置");
        Preconditions.checkNotNull(serverPath,"服务器文件路径未配置");
        Preconditions.checkNotNull(serverIp,"服务器IP未配置");
        Preconditions.checkNotNull(port,"端口未配置");
        Preconditions.checkNotNull(password,"密码未配置");
        FtpImpl ftp = new FtpImpl();
        try {
            ftp.connect(serverIp,port,userName,password);
            if(!ftp.existDirectory(serverPath)){
                ftp.mkdir(serverPath);
            }
            ftp.cd(serverPath);
            ftp.bin();
            boolean b = ftp.uploadFile(localFilePath+fileName,fileName);
            if(!b ){
                Preconditions.checkArgument(false,"文件上传失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                Preconditions.checkNotNull(false,e.getMessage());
            }
        }
    }


    /**
     *
     * @param localFilePath
     * @param serverPath
     * @param serverIp
     * @param port
     * @param userName
     * @param password
     * @param fileName
     */

    public static void icbcDownload(String localFilePath,String serverPath,String serverIp,int port ,String userName, String password,String fileName){
        Preconditions.checkNotNull(localFilePath,"本地存放路径未配置");
        Preconditions.checkNotNull(serverPath,"服务器文件路径未配置");
        Preconditions.checkNotNull(serverIp,"服务器IP未配置");
        Preconditions.checkNotNull(port,"端口未配置");
        Preconditions.checkNotNull(password,"密码未配置");
        FtpImpl ftp = new FtpImpl();
        try {
            ftp.connect(serverIp,port,userName,password);
            if(!ftp.existDirectory(serverPath)){
                ftp.mkdir(serverPath);
            }
            ftp.cd(serverPath);
            ftp.bin();
            boolean download = ftp.download(fileName, localFilePath + fileName);
//            boolean b = ftp.uploadFile(localFilePath+fileName,fileName);

            if(!download){
                Preconditions.checkArgument(false,"文件下载失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                Preconditions.checkNotNull(false,e.getMessage());
            }
        }
    }

    /**
     * 上传文件
     * @param localFilePath
     */
    public static boolean icbcUpload(String localFilePath){
        Preconditions.checkNotNull(localFilePath,"本地存放路径未配置");
        Preconditions.checkNotNull(serverpath,"服务器文件路径未配置");
        Preconditions.checkNotNull(servierIP,"服务器IP未配置");
        Preconditions.checkNotNull(port,"端口未配置");
        Preconditions.checkNotNull(password,"密码未配置");
        String fileName = localFilePath.substring(localFilePath.lastIndexOf(File.separator) + 1);
        FtpImpl ftp = new FtpImpl();

        boolean flag=false;
        try {
            password = DesEncryptUtil.decryptBasedDes(password);

            ftp.connect(servierIP,Integer.parseInt(port),userName,password);
            if(!ftp.existDirectory(serverpath)){
                ftp.mkdir(serverpath);
            }
            ftp.cd(serverpath);
            ftp.bin();
            flag = ftp.uploadFile(localFilePath,fileName);

        } catch (IOException e) {
            throw new BizException("文件上传失败");
        }finally {
            try {
                ftp.disconnect();
            } catch (IOException e) {
                throw new BizException("ftp关闭连接异常");
            }
        }
        return flag;
    }
}
