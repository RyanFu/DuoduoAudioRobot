package me.yongbo.DuoduoRingRobot;


import java.util.Map;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtil {
	public static final String CHARSET = "UTF-8";
	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	
	private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/27.0.1453.94 Safari/537.36";
	
	public static HttpClient getHttpClient() {
		HttpClient httpClient = new DefaultHttpClient();
		return httpClient;
	}	
	public static HttpGet getHttpGet(Map<String, String> request_headers) {
		HttpGet httpGet = new HttpGet();
		
		httpGet.setHeader("User-Agent", USER_AGENT);
		for(String k : request_headers.keySet()){
			httpGet.setHeader(k, request_headers.get(k));
		}
		
		return httpGet;
	}
}
