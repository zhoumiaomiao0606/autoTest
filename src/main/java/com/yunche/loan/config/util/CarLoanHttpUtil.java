/*
 * COPYRIGHT. ShenZhen JiMi Technology Co., Ltd. 2018.
 * ALL RIGHTS RESERVED.
 *
 * No part of this publication may be reproduced, stored in a retrieval system, or transmitted,
 * on any form or by any means, electronic, mechanical, photocopying, recording, 
 * or otherwise, without the prior written permission of ShenZhen JiMi Network Technology Co., Ltd.
 *
 * Amendment History:
 * 
 * Date                   By              Description
 * -------------------    -----------     -------------------------------------------
 * 2018年5月18日    yaojianping         Create the class
 * http://www.jimilab.com/
*/

package com.yunche.loan.config.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunche.loan.domain.vo.CarLoanResultVO;
import com.yunche.loan.domain.vo.GetTokenVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yunche.loan.config.util.SignUtils.signTopRequest;


/**
 * @FileName OpenApiDemo.java
 * @Description: 
 *
 * @Date 2018.7.17
 * @author jjq
 * @version 1.0
 */
public class CarLoanHttpUtil {

	private static final Logger logger = LoggerFactory.getLogger(CarLoanHttpUtil.class);
	@Autowired
	private static CarLoanResultVO carLoanResultVO;

	private static String openapi_url = "http://114.55.174.190:8280/vehicle_client";
	
	// 申请来的appKey和appSecret
	private static final String app_key = "836923d25d2290f15aae11a74acac69a";
	//机构新增修改
	public static void modifyClient(String tName,String tId,String sName,String sId) throws Exception {
		String url = openapi_url+"/api/modifyClient";
		String sign =MD5Utils.md5Hex(tId+app_key);
		url +="?tName="+tName+"&tId="+tId+
				"&sName="+sName+"&sId="+sId+"&sp=9&sign="+sign;
		String returnInfo = sendGet(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){

			}
		}
	}

	//机构删除
	public static void delClient(String tId) throws Exception {
		String url = openapi_url+"/api/delClient";
		String sign =MD5Utils.md5Hex(tId+app_key);
		url +="?id="+tId+"&sp=9&sign="+sign;
		String returnInfo = sendGet(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){

			}
		}
	}

	//客户新建修改
	public static void modifyCustomer(String cId,String orderId,String customerName,String vehicleNo) throws Exception {
		String url = openapi_url+"/api/modifyCustomer";
		String sign =MD5Utils.md5Hex(orderId+app_key);
		url +="cid="+cId+"&orderId="+orderId+"&customerName="+customerName+"&vehicleNo="+vehicleNo
				+"&sp=9&sign="+sign;
		String returnInfo = sendGet(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){

			}
		}
	}

	//客户删除
	public static void delCustomer(String orderId) throws Exception {
		String url = openapi_url+"/api/delCustomer";
		String sign =MD5Utils.md5Hex(orderId+app_key);
		url +="?orderId="+orderId+"&sp=9&sign="+sign;
		String returnInfo = sendGet(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){

			}
		}
	}


	//绑定gps
	public static boolean bindGps(String gpsCode,String orderId) throws Exception {
		boolean flag = false;
		String url = openapi_url+"/api/bindGps";
		String sign =MD5Utils.md5Hex(gpsCode+app_key);
		url += "?orderId="+orderId+"&gprsCode="+gpsCode+"&sp=9&sign="+sign;

		String returnInfo = sendPost(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){
				flag = true;
			}else{
				logger.error("该GPS:"+gpsCode+"绑定失败，原因:"+carLoanResultVO.getMsg());
			}
		}
		return flag;
	}
	//获取gps信息
	public static List<Map<String,Object>> getGpsInfo(String gpsCode) throws Exception {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		String url = openapi_url+"/api/getGpsData";
		String sign =MD5Utils.md5Hex(gpsCode+app_key);
		url += "?gprsCode="+gpsCode+"&sp=9&sign="+sign;
		System.out.println(url);
		String returnInfo = sendPost(url);
		if(!StringUtil.isEmpty(returnInfo)){
			carLoanResultVO = formJson2Obj(returnInfo,CarLoanResultVO.class);
			if("true".equals(carLoanResultVO.getSuccess())){
				if(carLoanResultVO.getData() instanceof Map){
					result.add((Map<String, Object>) carLoanResultVO.getData());
				}else if(carLoanResultVO.getData() instanceof  List){
					result = (List<Map<String,Object>>)carLoanResultVO.getData();
				}
			}else{
				logger.error("该GPS:"+gpsCode+"查询失败，原因:"+carLoanResultVO.getMsg());
			}
		}
		return result;
	}
	private static String sendPost(String url) throws IOException {
	    String result = "";
		CloseableHttpClient httpClient = null;
		HttpResponse response = null ;
		try {
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json charset=utf-8");
			HttpPost post = new HttpPost(url);
			if (null != headerMap) {
				post.setHeaders(assemblyHeader(headerMap));
			}
			httpClient = HttpClients.createDefault();

//			RequestConfig requestConfig = RequestConfig.custom()
//					.setConnectTimeout(30000).setConnectionRequestTimeout(5000)
//					.setSocketTimeout(30000).build();
			response = httpClient.execute(post);
			HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "utf-8");
            System.out.println(result);
		} catch (IOException e) {
			logger.error("系统链接失败",e);
		}finally {
			if (httpClient != null)
				httpClient.close();
			if (response !=null)
				((CloseableHttpResponse) response).close();

		}
		return result;
	}
	private static String sendGet(String url) throws IOException {
		String result = "";
		CloseableHttpClient httpClient = null;
		HttpResponse response = null ;
		try {
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/json charset=utf-8");
			HttpGet get = new HttpGet(url);
			if (null != headerMap) {
				get.setHeaders(assemblyHeader(headerMap));
			}
			httpClient = HttpClients.createDefault();

//			RequestConfig requestConfig = RequestConfig.custom()
//					.setConnectTimeout(30000).setConnectionRequestTimeout(5000)
//					.setSocketTimeout(30000).build();
			response = httpClient.execute(get);
			HttpEntity entity = response.getEntity();
			result = EntityUtils.toString(entity, "utf-8");
			System.out.println(result);
		} catch (IOException e) {
			logger.error("系统链接失败",e);
		}finally {
			if (httpClient != null)
				httpClient.close();
			if (response !=null)
				((CloseableHttpResponse) response).close();

		}
		return result;
	}


	private static <T>T formJson2Obj(String json,Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return objectMapper.readValue(json,clazz);

    }
	
	/**
	 * 组装头部信息
	 * 
	 * @param headers
	 * @return
	 */
	private static Header[] assemblyHeader(Map<String, String> headers) {
		Header[] allHeader = new BasicHeader[headers.size()];
		int i = 0;
		for (String str : headers.keySet()) {
			allHeader[i] = new BasicHeader(str, headers.get(str));
			i++;
		}
		return allHeader;
	}

	public static String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = formatter.format(new Date());
		return result;
	}
}
