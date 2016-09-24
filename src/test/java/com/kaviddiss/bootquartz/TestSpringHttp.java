package com.kaviddiss.bootquartz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

/** 
* @Author : water  
* @Date   : 2016年9月20日 
* @Desc   : TODO
* @version: V1.0
*/
public class TestSpringHttp {

	public static void main(String[] args) {
		testHttp();
	}
	
	
	public static void testHttp(){
		try {
			URI uri = new URI("http://localhost:8080/schedule/swagger-ui.html#/");
			SimpleClientHttpRequestFactory schr = new SimpleClientHttpRequestFactory();
			ClientHttpRequest chr = schr.createRequest(uri, HttpMethod.POST);
            //chr.getBody().write(param.getBytes());//body中设置请求参数
			ClientHttpResponse res = chr.execute();
			InputStream is = res.getBody(); //获得返回数据,注意这里是个流
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String str = "";
			while((str = br.readLine())!=null){
				System.out.println(str);//获得页面内容或返回内容
			}
			
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
