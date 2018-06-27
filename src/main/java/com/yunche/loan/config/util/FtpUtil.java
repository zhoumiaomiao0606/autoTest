package com.yunche.loan.config.util;

import com.google.common.base.Preconditions;

import java.io.IOException;

public class FtpUtil {

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
}
