package com.yunche.loan;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Test {
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        JSONObject res = new JSONObject();
        JSONObject pub = new JSONObject();
        pub.put("name1", 1234);
        res.put("name2", 5678);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pub", pub);
        jsonObject.put("res", res);

        String reqparam = jsonObject.toString();
        System.out.println("reqparam--"+reqparam);
        String result =
                sendPost("http://wwzkdci.hk1.mofasuidao.cn/api/v1/loanorder/icbc/creditresult",
                        "reqparam="+ URLEncoder.encode(reqparam, "UTF-8"));
        System.out.println("result--"+result);
    }

}


