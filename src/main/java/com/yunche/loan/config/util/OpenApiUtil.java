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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yunche.loan.config.common.GpsConfig;
import com.yunche.loan.domain.vo.GetTokenVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static com.yunche.loan.config.util.SignUtils.signTopRequest;


/**
 * @FileName OpenApiDemo.java
 * @Description: 
 *
 * @Date 2018.7.17
 * @author jjq
 * @version 1.0
 */
public class OpenApiUtil {

	private static final Logger logger = LoggerFactory.getLogger(OpenApiUtil.class);
	@Autowired
	private static GetTokenVO getTokenVO;

	@Resource
	private static GpsConfig gpsConfig;

	private static  String openapi_url = "";
	
	// 申请来的appKey和appSecret
	private static  String app_key = "";
	private static  String app_secret = "";

	static{
		ResourceBundle bundle = PropertyResourceBundle.getBundle("gpsconfig");
		openapi_url = bundle.getString("jimiUrl");
		app_key = bundle.getString("jimiKey");
		app_secret = bundle.getString("jimiSecret");
	}
	private static Map<String, String> getCommonMap(){
		Map<String, String> paramMap = new HashMap<String, String>();
		// 公共参数
		paramMap.put("app_key", app_key);
		paramMap.put("v", "1.0");
		paramMap.put("timestamp", getCurrentDate());
		paramMap.put("sign_method", "md5");
		paramMap.put("format", "json");
		return paramMap;
	}
	private static String getSign(Map<String, String> paramMap){
		// 计算签名
		String sign = "";
		try {
			sign = signTopRequest(paramMap, app_secret, "md5");
			paramMap.put("sign", sign);
		} catch (IOException e) {
			logger.error("签名失败",e);
		}
		return sign;
	}

	//获取Token
	public static String[] getToken() throws Exception {
		String[] result = new String[2];
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();
		paramMap.put("method", "jimi.oauth.token.get");
		paramMap.put("user_id", "浙江鑫宝行");
		paramMap.put("user_pwd_md5", DigestUtils.md5Hex("fxglb0571"));
		paramMap.put("expires_in", "7200");
		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		if(!StringUtil.isEmpty(returnStr)){
		   getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
		   if("0".equals(getTokenVO.getCode())){
		   	   result[0] = ((Map<String,String>)getTokenVO.getResult()).get("accessToken");
		   	   result[1] = ((Map<String,String>)getTokenVO.getResult()).get("refreshToken");
		   }else if("1006".equals(getTokenVO.getCode())){
			   result[0] = "1006";
		   }else{
			   throw new Exception("获取Token信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
		   }
        }else{
			throw new Exception("获取时与GPS系统通讯异常");
		}
        return result;
	}
	//刷新Token
	public static String[] refreshToken(String accToken,String refToken) throws Exception {
		String[] result = new String[2];
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();

		paramMap.put("method", "jimi.oauth.token.refresh");
		paramMap.put("access_token", accToken);
		paramMap.put("refresh_token", refToken);
		paramMap.put("expires_in", "7200");
		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		if(!StringUtil.isEmpty(returnStr)){
			getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
			if("0".equals(getTokenVO.getCode())){
				result[0] = ((Map<String,String>)getTokenVO.getResult()).get("accessToken");
				result[1] = ((Map<String,String>)getTokenVO.getResult()).get("refreshToken");
			}else if("1006".equals(getTokenVO.getCode())){
				result[0] = "1006";
			}else{
				throw new Exception("刷新Token信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
			}
		}else{
			throw new Exception("刷新时与GPS系统通讯异常");
		}
		return result;
	}
	//获取GPS
	public static List<Map<String,Object>> getGpsInfo(String accToken ,String target) throws Exception {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();

		paramMap.put("method", "jimi.user.device.list");
		paramMap.put("access_token", accToken);
		paramMap.put("target", target);
		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		if(!StringUtil.isEmpty(returnStr)){
			getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
			if("0".equals(getTokenVO.getCode())){
				if(getTokenVO.getResult() instanceof Map){
					result.add((Map<String,Object>)getTokenVO.getResult());
				}else if (getTokenVO.getResult() instanceof List){
					result = (List<Map<String,Object>>)getTokenVO.getResult();
				}
			}else if("1004".equals(getTokenVO.getCode())){
				Map<String,Object> codeMap = new HashMap<String, Object>();
				codeMap.put("code","1004");
				result.add(codeMap);
			}else{
				throw new Exception("获取GPS信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
			}
		}else{
			throw new Exception("与GPS系统通讯异常");
		}
		return result;
	}
	//获取gps详细信息
	public static List<Map<String,Object>> getGpsDetailInfo(String accToken,String imeis)throws Exception{
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();
		paramMap.put("method", "jimi.open.device.getDetails");
		paramMap.put("access_token", accToken);
		paramMap.put("imeis", imeis);
		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		logger.info("康凯斯返回："+returnStr);
		if(!StringUtil.isEmpty(returnStr)){
			getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
			if("0".equals(getTokenVO.getCode())){
				if(getTokenVO.getResult() instanceof Map){
					result.add((Map<String,Object>)getTokenVO.getResult());
				}else if (getTokenVO.getResult() instanceof List){
					result = (List<Map<String,Object>>)getTokenVO.getResult();
				}
			}else if("1004".equals(getTokenVO.getCode())){
				Map<String,Object> codeMap = new HashMap<String, Object>();
				codeMap.put("code","1004");
				result.add(codeMap);
			}else if("1002".equals(getTokenVO.getCode())){

			}else{
				throw new Exception("获取GPS信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
			}
		}else{
			throw new Exception("与GPS系统通讯异常");
		}
		return result;
	}
	//更新GPS
	public static String updateGpsInfo(String accToken,String imei,String vehicleName,String driverName) throws Exception {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();
		String result ="";
		paramMap.put("method", "jimi.open.device.update");
		paramMap.put("access_token", accToken);
		paramMap.put("imei", imei);
		paramMap.put("vehicle_name", vehicleName);
		paramMap.put("driver_name", driverName);

		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		if(!StringUtil.isEmpty(returnStr)){
			getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
			if("0".equals(getTokenVO.getCode())){
				result = "0";
			}else if("1004".equals(getTokenVO.getCode())){
				result = "1004";
			}else{
				throw new Exception("更新GPS信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
			}
		}else{
			throw new Exception("更新时与GPS系统通讯异常");
		}
		return result;
	}
	//获取子账户
	public static List<Map<String,Object>> getChildTarget(String accToken) throws Exception {
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap = getCommonMap();

		paramMap.put("method", "jimi.user.child.list");
		paramMap.put("access_token", accToken);
		paramMap.put("target", "浙江鑫宝行");
		getSign(paramMap);
		String returnStr = sendPost(paramMap);
		if(!StringUtil.isEmpty(returnStr)){
			getTokenVO = formJson2Obj(returnStr,GetTokenVO.class);
			if("0".equals(getTokenVO.getCode())){
				if(getTokenVO.getResult() instanceof Map){
					result.add((Map<String,Object>)getTokenVO.getResult());
				}else if (getTokenVO.getResult() instanceof List){
					result = (List<Map<String,Object>>)getTokenVO.getResult();
				}
			}else if("1004".equals(getTokenVO.getCode())){
				Map<String,Object> codeMap = new HashMap<String, Object>();
				codeMap.put("code","1004");
				result.add(codeMap);
			}else{
				throw new Exception("获取GPS信息异常："+getTokenVO.getCode()+" "+getTokenVO.getMessage());
			}
		}else{
			throw new Exception("与GPS系统通讯异常");
		}
		return result;
	}
	private static String sendPost(Map<String, String> paramMap) throws IOException {
	    String result = "";
		CloseableHttpClient httpClient = null;
		HttpResponse response = null ;
		try {
			Map<String, String> headerMap = new HashMap<String, String>();
			headerMap.put("Content-Type", "application/x-www-form-urlencoded");
			HttpPost post = new HttpPost(openapi_url);
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for (String key : paramMap.keySet()) {
				list.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
			post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
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
