package com.chinasofti.ark.bdadp.quartz.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.chinasofti.ark.bdadp.quartz.dto.HttpContent;

/**
 * @Author : water
 * @Date : 2016年9月20日
 * @Desc : post get请求工具类
 * @version: V1.0
 */
public class HttpUtil {

	/**
	 * 处理get请求.
	 * 
	 * @param url
	 *            请求路径
	 * @return json
	 */
	public static HttpContent get(String url) {
		HttpContent response = null;
		HttpGet httpGet = new HttpGet(url);
		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

			System.out.println("执行get请求:...." + httpGet.getURI());
			httpGet.setHeader("Content-Type", "application/json;charset=utf-8");
			httpGet.setHeader("Encoding", "UTF-8");
			// get.setHeader("Authorization", authorization);
			try (CloseableHttpResponse chr = httpClient.execute(httpGet)) {
				response = getHttpResponse(chr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpGet.releaseConnection();
		}
		return response;
	}

	/**
	 * 处理post请求.
	 * 
	 * @param url
	 *            请求路径
	 * @param params
	 *            参数
	 * @return json
	 */

	public static String post(String url, Map<String, String> params) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Set<String> keySet = params.keySet();
		for (String key : keySet) {
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		CloseableHttpResponse response = null;
		// HttpContent httpContent;
		String content = "";
		try {
			// 提交的参数
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
			// 将参数给post方法
			httpPost.setEntity(uefEntity);
			// 执行post方法
			response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");
				System.err.println(content);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public static String post(String url) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		CloseableHttpResponse response = null;
		// HttpContent httpContent;
		String content = "";
		try {
			// 提交的参数
			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(nvps, "UTF-8");
			// 将参数给post方法
			httpPost.setEntity(uefEntity);
			// 执行post方法
			response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				content = EntityUtils.toString(response.getEntity(), "utf-8");
				System.err.println(content);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}

	public static HttpContent getHttpResponse(CloseableHttpResponse httpResponse) throws IOException {
		HttpContent res = new HttpContent();

		if (httpResponse != null) {
			res.setStatusCode(httpResponse.getStatusLine().getStatusCode());
			res.setResponseBody(EntityUtils.toString(httpResponse.getEntity()));
		}
		return res;
	}

}