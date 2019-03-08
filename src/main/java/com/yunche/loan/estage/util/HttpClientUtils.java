/**
 * Copyright (C) 2014-2016, hrfax and/or its affiliates. All rights reserved.
 * hrfax PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.yunche.loan.estage.util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpClientUtils {

	private static final Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

	public static String doPost(String url, HttpEntity httpEntity) {

		HttpPost request = new HttpPost(url);
		request.setEntity(httpEntity);
		
		return executeInternal(request);
	}
	
	public static String executeInternal(HttpUriRequest request) {
		try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultRequestConfig(get()).build();) {
			try (CloseableHttpResponse response = httpclient.execute(request);) {
				int statusCode = response.getStatusLine().getStatusCode();
//				logger.info("请求【{}】返回状态吗【{}】", request.getURI(), statusCode);
				if (HttpStatus.SC_OK == statusCode) {
					String result =  IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8.name());
//					logger.info("请求【{}】返回结果:{}", request.getURI(),result);
					return result;
				}
				throw new RuntimeException(String.format("请求【%s】返回状态吗【%s】", request.getURI(), statusCode));
			}
		} catch (Exception e) {
			throw new RuntimeException(String.format("请求%s异常", request.getURI()),e);
		}
	}

	private static RequestConfig get() {
		return RequestConfig.custom()
			    .setSocketTimeout(60000)
			    .setConnectTimeout(60000)
			    .setConnectionRequestTimeout(60000)
			    .build();
	}

	/**
	 * post请求 参数key-value形式
	 * @param url
	 * @param params (key,value)
	 * @return
	 */
	public static String doPost(String url, Map<String, Object> params) {
		List<BasicNameValuePair> nvps = new ArrayList<>(params.size());
		for (String key : params.keySet()) {
			Object value = params.get(key);
			if (value != null) {
				nvps.add(new BasicNameValuePair(key, value.toString()));
			}
		}
		return doPost(url, new UrlEncodedFormEntity(nvps, StandardCharsets.UTF_8));
	}
	
	public static String get(String url) {
		
		HttpGet request = new HttpGet(url);
		return executeInternal(request);
	}

	/**
	 * post Json参数
	 * @param url
	 * @param data
	 * @return
	 */
	public static String post(String url, String data) {
		
		HttpEntity httpEntity = new StringEntity(data, StandardCharsets.UTF_8);
		return doPost(url, httpEntity);
	}

}
