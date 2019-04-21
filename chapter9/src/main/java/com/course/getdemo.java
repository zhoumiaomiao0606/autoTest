package com.course;

import com.sun.deploy.nativesandbox.comm.Response;
import com.sun.deploy.net.HttpUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;
import sun.net.www.http.HttpClient;

import javax.imageio.IIOException;
import javax.swing.text.html.parser.Entity;
import java.io.IOException;

public class getdemo {
    @Test
    public void testhttpdemo() throws IOException {

       HttpGet httpGet=new HttpGet("http://www.baidu.com");
        CloseableHttpClient httpclient = HttpClients.createDefault();
      CloseableHttpResponse response=httpclient.execute(httpGet);

      String result=   EntityUtils.toString(response.getEntity());
        System.out.println(result);
        response.close();




    }
}
