package com.yunche.loan;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/** 
 * HttpClient4.5.1工具类 
 * @author ldc 
 * 
 */
@Slf4j
public class HttpUtils {



	public static void main(String[] args) {
		doPostJson("http://192.168.0.140:8888/api/car/evaluate","{\n" +
				"    \"trimId\": \"128004\",\n" +
				"    \"mileage\":\"15379\",\n" +
				"    \"buyCarDate\": \"2016-11-10\",\n" +
				"    \"colorId\": \"1\",\n" +
				"    \"carCard\":\"皖AK487\",\n" +
				"    \"cityId\":\"杭州\"\n" +
				"}");
	}

	public static String doPostJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Post请求
			HttpPost httpPost = new HttpPost(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPost.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPost);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}

		return resultString;
	}

	/**
	 * PUT基础请求
	 */
	public static String doPutJson(String url, String json) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		String resultString = "";
		try {
			// 创建Http Put请求
			HttpPut httpPut = new HttpPut(url);
			// 创建请求内容
			StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
			httpPut.setEntity(entity);
			// 执行http请求
			response = httpClient.execute(httpPut);
			resultString = EntityUtils.toString(response.getEntity(), "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return resultString;
	}
      
}  