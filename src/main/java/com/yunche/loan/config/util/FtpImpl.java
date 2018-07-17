package com.yunche.loan.config.util;


import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * FTP 服务
 */
@Service
public class FtpImpl implements Ftp {
    private FTPClient ftpClient;
    public static final int BINARY = 2;
    public static final int ASCII = 0;

    public FtpImpl() {
        this.ftpClient = new FTPClient();
        this.ftpClient.setControlEncoding("UTF-8");
        this.ftpClient.setBufferSize(1024*1024*10);
    }

    public void setControlEncoding(String encoding) {
        this.ftpClient.setControlEncoding(encoding);
    }

    public void connect(String server, int port, String user, String password) throws SocketException, IOException {
        connect(server, port, user, password, "");
    }

    public void connect(String server, int port, String user, String password, String path) throws SocketException, IOException {
        this.ftpClient.setConnectTimeout(60000);
        this.ftpClient.connect(server, port);

        this.ftpClient.setSoTimeout(60000);

        int reply = this.ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            this.ftpClient.disconnect();
            throw new SocketException("FTP服务拒绝链接...");
        }
        boolean flag = this.ftpClient.login(user, password);
        if (!flag) {
            throw new RuntimeException("FTP登录失败，用户名或密码错误");
        }
        if (path.length() != 0) {
            this.ftpClient.changeWorkingDirectory(path);
        }
        try{
            String pasvMode = System.getProperty("yunche.ftp.client.pasv.mode", "true");
//            if ("false".equals(pasvMode)) {
                this.ftpClient.enterLocalActiveMode();
//            } else {
//                this.ftpClient.enterLocalPassiveMode();//233580
//            }
        }catch (Exception e) {
            this.ftpClient.enterLocalPassiveMode();
        }
    }

    public void setFileType(int fileType) throws IOException {
        this.ftpClient.setFileType(fileType);
    }

//    public void setCache() throws IOException {
//        this.ftpClient.setBufferSize(100000);
//    }
    public void bin() throws IOException {
        setFileType(2);
    }

    public void asc() throws IOException {
        setFileType(0);
    }

    public void disconnect() throws IOException {
        if (this.ftpClient.isConnected()) {
            this.ftpClient.logout();
            this.ftpClient.disconnect();
        }
    }

    public boolean cd(String path) throws IOException {
        return this.ftpClient.changeWorkingDirectory(path);
    }

    public boolean mkdir(String pathName) throws IOException {
//        if (!existDirectory(pathName)) {
//            StringTokenizer st = new StringTokenizer(pathName, "/");
//            String path = "";
//            while (st.hasMoreTokens())
//            {
//                path = path + "/" + st.nextToken();
//                if ((!existDirectory(path)) &&
//                        (!this.ftpClient.makeDirectory(path))) {
//                    return false;
//                }
//            }
//        }
        this.ftpClient.makeDirectory(pathName);
        return true;
    }

    public boolean rm(String path) throws IOException {
        return this.ftpClient.removeDirectory(path);
    }

    public boolean rm(String path, boolean isAll) throws IOException {
        if (!isAll) {
            return rm(path);
        }
        FTPFile[] ftpFileArr = this.ftpClient.listFiles(path);
        if ((ftpFileArr == null) || (ftpFileArr.length == 0)) {
            return rm(path);
        }
        for (FTPFile ftpFile : ftpFileArr) {
            String name = ftpFile.getName();
            if (ftpFile.isDirectory()) {
                rm(path + "/" + name, true);
            } else if (ftpFile.isFile()) {
                deleteFile(path + "/" + name);
            } else if (!ftpFile.isSymbolicLink()) {
                if (!ftpFile.isUnknown()) {}
            }
        }
        return this.ftpClient.removeDirectory(path);
    }

    public boolean existDirectory(String path) throws IOException {
        String currWorkingPath = this.ftpClient.printWorkingDirectory();
        try {
            if (cd(path)) {
                return true;
            }
        } catch (IOException e){
            throw e;
        }finally{
            cd(currWorkingPath);
        }
        return false;
    }

    public List<String> getFileList(String path) throws IOException {
        FTPFile[] ftpFiles = this.ftpClient.listFiles(path);

        List<String> retList = new ArrayList();
        if ((ftpFiles == null) || (ftpFiles.length == 0)) {
            return retList;
        }
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.isFile()) {
                retList.add(ftpFile.getName());
            }
        }
        return retList;
    }

    public boolean deleteFile(String pathName) throws IOException {
        return this.ftpClient.deleteFile(pathName);
    }

    public boolean uploadFile(String fileName, String newName) throws IOException {
        boolean flag = false;
        InputStream iStream = null;
        try {
            BufferedInputStream  input = new BufferedInputStream(new FileInputStream(fileName));
            iStream = new FileInputStream(fileName);
            this.ftpClient.enterLocalPassiveMode();
            flag = this.ftpClient.storeFile(newName, input);
        }catch (IOException e){
            flag = false;
            return flag;
        }finally{
            if (iStream != null) {
                iStream.close();
            }
        }
        return flag;
    }

    public boolean uploadFile(String fileName) throws IOException {
        return uploadFile(fileName, fileName);
    }

    public boolean download(String remoteFileName, String localFileName) throws IOException {
        boolean flag = false;
        File outfile = new File(localFileName);
        OutputStream oStream = null;
        try {
            oStream = new FileOutputStream(outfile);
            flag = this.ftpClient.retrieveFile(remoteFileName, oStream);
        }catch (IOException e){
            flag = false;
            return flag;
        }finally{
            if (oStream != null) {
                oStream.close();
            }
        }
        if (!flag) {
            FileUtil.deleteFile(localFileName);
        }
        return flag;
    }

    public boolean renameFile(String fileName, String newName) throws IOException {
        boolean flag = false;
        flag = this.ftpClient.rename(fileName, newName);
        return flag;
    }
}
