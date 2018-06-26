package com.yunche.loan.config.util;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

public abstract interface Ftp
{

    public abstract void connect(String paramString1, int paramInt, String paramString2, String paramString3)
            throws SocketException, IOException;

    public abstract void connect(String paramString1, int paramInt, String paramString2, String paramString3, String paramString4)
            throws SocketException, IOException;

    public abstract void bin()
            throws IOException;

    public abstract void asc()
            throws IOException;

    public abstract void disconnect()
            throws IOException;

    public abstract boolean cd(String paramString)
            throws IOException;

    public abstract boolean mkdir(String paramString)
            throws IOException;

    public abstract boolean rm(String paramString)
            throws IOException;

    public abstract boolean rm(String paramString, boolean paramBoolean)
            throws IOException;

    public abstract boolean existDirectory(String paramString)
            throws IOException;

    public abstract List<String> getFileList(String paramString)
            throws IOException;

    public abstract boolean deleteFile(String paramString)
            throws IOException;

    public abstract boolean uploadFile(String paramString1, String paramString2)
            throws IOException;

    public abstract boolean uploadFile(String paramString)
            throws IOException;

    public abstract boolean download(String paramString1, String paramString2)
            throws IOException;

    public abstract boolean renameFile(String paramString1, String paramString2)
            throws IOException;
}