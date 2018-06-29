package com.yunche.loan.config.util;

import com.google.common.base.Preconditions;

import java.io.*;
import java.util.List;

public class FileUtil{

    public static void moveFile(String fromFile, String toFile) {
        try {
            DataInputStream in = null;
            DataOutputStream out = null;
            try {
                in = new DataInputStream(new BufferedInputStream(new FileInputStream(fromFile)));

                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(toFile, false)));
                while (in.available() != 0) {
                    out.writeByte(in.readByte());
                }
            }finally{
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
            deleteFile(fromFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFile(String fromFile, String toFile) {
        try {
            DataInputStream in = null;
            DataOutputStream out = null;
            try {
                in = new DataInputStream(new BufferedInputStream(new FileInputStream(fromFile)));

                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(toFile, false)));
                while (in.available() != 0) {
                    out.writeByte(in.readByte());
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        } catch (FileNotFoundException e){
            e.printStackTrace();
            Preconditions.checkArgument(false,"文件未找到");
        }
        catch (Exception e) {
            e.printStackTrace();
            Preconditions.checkArgument(false,e.getMessage());

        }
    }

    public static boolean deleteFile(String fileName) {
        try {
            File file = new File(fileName);
            if ((file.exists()) && (file.isFile())){
                if (file.delete()) {
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFile(String path, String fileName){
        try {
            String fullFileName = path;
            if ((fullFileName.endsWith("/")) || (fullFileName.endsWith("\\"))) {
                fullFileName = fullFileName + fileName;
            } else {
                fullFileName = fullFileName + "/" + fileName;
            }
            File file = new File(fullFileName);
            if ((file.exists()) && (file.isFile())){
                if (file.delete()) {
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFile(String path, List fileNameList){
        try{
            String fullFileName = path;
            if ((!fullFileName.endsWith("/")) && (!fullFileName.endsWith("\\"))) {
                fullFileName = fullFileName + "/";
            }
            for (int i = 0; i < fileNameList.size(); i++){
                File file = new File(fullFileName + fileNameList.get(i));
                if ((file.exists()) && (file.isFile()) &&
                        (!file.delete())) {
                    return false;
                }
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    public static void writeFile(String filePath, String fileName, String message)
//    {
//        String tmpPath = filePath;
//        if (!filePath.endsWith("/")) {
//            tmpPath = tmpPath + "/";
//        }
//        DataOutputStream out = null;
//        try
//        {
//            DirectoryUtil.mkDir(tmpPath);
//            out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tmpPath + fileName, true)));
//
//            out.write(message.getBytes());
//            out.writeBytes("\n");
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if (out != null) {
//                try
//                {
//                    out.close();
//                    out = null;
//                }
//                catch (IOException e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

//    public static List<String> filterFile(File dir, String fileNameFilterStr)
//    {
//        List<String> fileList = new ArrayList();
//        if (!dir.isDirectory()) {
//            return fileList;
//        }
//        boolean startFlag = false;boolean endFlag = false;
//        if ((!DataUtil.isNullStr(fileNameFilterStr)) && (fileNameFilterStr.startsWith("*")))
//        {
//            startFlag = true;
//            fileNameFilterStr = fileNameFilterStr.substring(1);
//        }
//        if ((!DataUtil.isNullStr(fileNameFilterStr)) && (fileNameFilterStr.endsWith("*")))
//        {
//            endFlag = true;
//            fileNameFilterStr = fileNameFilterStr.substring(0, fileNameFilterStr.length() - 1);
//        }
//        File[] files = dir.listFiles();
//        for (int i = 0; (files != null) && (i < files.length); i++) {
//            if ((DataUtil.isNullStr(fileNameFilterStr)) || ((startFlag) && (endFlag) && (DataUtil.contains(files[i].getName(), fileNameFilterStr))) || ((!startFlag) && (files[i].getName().startsWith(fileNameFilterStr))) || ((!endFlag) && (files[i].getName().endsWith(fileNameFilterStr)))) {
//                fileList.add(files[i].getName());
//            }
//        }
//        return fileList;
//    }
}
