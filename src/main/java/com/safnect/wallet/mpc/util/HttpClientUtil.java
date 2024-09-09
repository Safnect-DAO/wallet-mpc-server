/*
 * Copyright (c) 2013, FPX and/or its affiliates. All rights reserved.
 * Use, Copy is subject to authorized license.
 */
package com.safnect.wallet.mpc.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @author Jecky
 * @date 2015年9月2日
 */
public class HttpClientUtil {

    public static String httpPost(MediaType mediaType, URI uri, String requestContent) throws Exception {
        return httpPost(mediaType, uri, requestContent, null);
    }

    public static String httpPost(MediaType mediaType, String uri, String requestContent) {
        try {
			return httpPost(mediaType, new URI(uri), requestContent, null);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
    }

    public static String httpPost(CloseableHttpClient httpClient, MediaType mediaType, String uri, String requestContent) {
    	CloseableHttpResponse response = null;
        try {
        	Integer timeOut = 20000;
        	HttpPost post = new HttpPost(uri);
            post.setHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
            post.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
            if(StringUtils.isNotBlank(requestContent)){
            	post.setEntity(new StringEntity(requestContent, Consts.UTF_8));
            }
            String reponseStr = null;
            response = httpClient.execute(post);
            HttpEntity resEntity = response.getEntity();
            reponseStr = EntityUtils.toString(resEntity, Consts.UTF_8);
            return reponseStr;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static String httpPost(MediaType mediaType, String uri, String requestContent, Integer timeOut) {
    	 try {
    		 return httpPost(mediaType, new URI(uri), requestContent, timeOut);
 		} catch (Exception e) {
 			throw new IllegalStateException(e);
 		}
    }

    public static String httpPost(MediaType mediaType, URI uri, String requestContent, Integer timeOut) throws Exception {
        HttpPost post = new HttpPost(uri);
        post.setHeader("Connection", "close");
        post.setHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        if (timeOut != null) {
            post.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
        }
        if(StringUtils.isNotBlank(requestContent)){
        	post.setEntity(new StringEntity(requestContent, Consts.UTF_8));
        }
        return httpRequest(post);
    }
    public static String httpPost(MediaType mediaType, URI uri, String requestContent, Map<String, String> headerMap, Integer timeOut) throws Exception {
    	HttpPost post = new HttpPost(uri);
    	post.setHeader("Connection", "close");
    	for(Entry<String, String> entry : headerMap.entrySet()){
			post.setHeader(entry.getKey(), entry.getValue());
    	}
    	if (StringUtils.isNotBlank(mediaType.toString())) {
    		post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
    	}
    	if (timeOut != null) {
    		post.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
    	}
    	if(StringUtils.isNotBlank(requestContent)){
    		post.setEntity(new StringEntity(requestContent, Consts.UTF_8));
    	}
    	return httpRequest(post);
    }

    public static String httpPost(String uri, Map<String, String> paramMap) {
    	if (uri.startsWith("https")) {
    		trustAllHttpsCertificates();
    		HttpsURLConnection.setDefaultHostnameVerifier(hv);
    	}
    	URI uriAddress;
		try {
			uriAddress = new URI(uri);
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
        return httpPost(uriAddress, paramMap, 8000000);
    }

    public static String httpPost(URI uri, Map<String, String> paramMap) throws Exception {
        return httpPost(uri, paramMap, null);
    }

    public static String httpPost(String uri, Map<String, String> paramMap, Integer timeOut) {
    	try {
    		return httpPost(new URI(uri), paramMap, timeOut);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
    }

    public static String httpPost(URI uri, Map<String, String> paramMap, Integer timeOut) {
        HttpPost post = new HttpPost(uri);
        post.setHeader("Connection", "close");
        if (timeOut != null) {
            post.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
        }
        List<BasicNameValuePair> list = map2NameValuePairList(paramMap);
        if (list != null && !list.isEmpty()) {
            post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
        }
        return httpRequest(post);
    }

    public static String httpGet(String uri, Map<String, String> paramMap) {
    	if (uri.startsWith("https")) {
    		trustAllHttpsCertificates();
    		HttpsURLConnection.setDefaultHostnameVerifier(hv);
    	}
        return httpGet(uri, paramMap, null, 80000);
    }

    public static String httpGet(String uri, Map<String, String> paramMap, Header[] headers, Integer timeOut) {
        HttpGet get = new HttpGet(uri);
        if (timeOut != null) {
            get.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
        }
        if (headers != null && headers.length > 0) {
        	get.setHeaders(headers);
        }
        try {
	        List<BasicNameValuePair> list = map2NameValuePairList(paramMap);
	        if (list != null && !list.isEmpty()) {
	            String requestParam = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));
	            get.setURI(new URI(uri + "?" + requestParam));
	        }
	        return httpRequest(get);
        } catch (Exception e) {
			throw new IllegalStateException(e);
		}
    }

    public static String httpDelete(String uri, Map<String, String> paramMap, Integer timeOut, List<Header> headerList) {
        HttpDelete delete = new HttpDelete(uri);
        if (CollectionUtils.isNotEmpty(headerList)) {
        	for (Header header : headerList) {
        		delete.addHeader(header);
        	}
        }
        if (timeOut != null) {
        	delete.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
        }
        try {
	        List<BasicNameValuePair> list = map2NameValuePairList(paramMap);
	        if (CollectionUtils.isNotEmpty(list)) {
	            String requestParam = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));
	            delete.setURI(new URI(uri + "?" + requestParam));
	        }
	        return httpRequest(delete);
        } catch (Exception e) {
			throw new IllegalStateException(e);
		}
    }
    public static String httpGet(String uri, Map<String, String> paramMap, Integer timeOut, List<Header> headerList) {
    	HttpGet get = new HttpGet(uri);
    	if (CollectionUtils.isNotEmpty(headerList)) {
    		for (Header header : headerList) {
    			get.addHeader(header);
    		}
    	}
    	if (timeOut != null) {
    		get.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
    	}
    	try {
    		List<BasicNameValuePair> list = map2NameValuePairList(paramMap);
    		if (CollectionUtils.isNotEmpty(list)) {
    			String requestParam = EntityUtils.toString(new UrlEncodedFormEntity(list, Consts.UTF_8));
    			get.setURI(new URI(uri + "?" + requestParam));
    		}
    		return httpRequest(get);
    	} catch (Exception e) {
    		throw new IllegalStateException(e);
    	}
    }
    public static String httpPost(String uri,  Map<String, String> paramMap, Integer timeOut, Map<String, String> headerMap, HttpHost proxy) {
    	HttpPost post = new HttpPost(uri);
    	post.setHeader("Connection", "close");
    	for(Entry<String, String> entry : headerMap.entrySet()){
			post.setHeader(entry.getKey(), entry.getValue());
    	}

    	if (timeOut != null) {
    		post.setConfig(RequestConfig.custom().setProxy(proxy).setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
    	}
    	 List<BasicNameValuePair> list = map2NameValuePairList(paramMap);
         if (list != null && !list.isEmpty()) {
             post.setEntity(new UrlEncodedFormEntity(list, Consts.UTF_8));
         }
        return httpRequest(post);
    }
    public static String httpPost(String uri, String bodyJson, Integer timeOut, List<Header> headerList) {
    	HttpPost post = new HttpPost(uri);
    	if (CollectionUtils.isNotEmpty(headerList)) {
    		for (Header header : headerList) {
    			post.addHeader(header);
    		}
    	}
    	if (timeOut != null) {
    		post.setConfig(RequestConfig.custom().setSocketTimeout(timeOut).setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).build());
    	}
    	try {
    		if (StringUtils.isNotBlank(bodyJson)) {
    			StringEntity se = new StringEntity(bodyJson);
    			se.setContentType("text/json");
    			se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
    			post.setEntity(se);
    		}
    		return httpRequest(post);
    	} catch (Exception e) {
    		throw new IllegalStateException(e);
    	}
    }

    public static String httpRequest(HttpRequestBase request) {
        String reponseStr = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            HttpEntity resEntity = response.getEntity();
            reponseStr = EntityUtils.toString(resEntity, Consts.UTF_8);
        } catch (Exception e) {
        	throw new IllegalStateException(e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
        return reponseStr;
    }

    /**
     * 根据url下载文件
     *
     * @param url
     * @return HttpResponse
     */
    public static HttpResponse download(MediaType mediaType,String url, String requestContent) {
        try {
            HttpPost post = new HttpPost(url);
            post.setHeader("Connection", "close");
            post.setHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
            if(StringUtils.isNotBlank(requestContent)){
                post.setEntity(new StringEntity(requestContent, Consts.UTF_8));
            }
            HttpClient client = HttpClients.createDefault();
            return client.execute(post);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<BasicNameValuePair> map2NameValuePairList(Map<String, String> paramMap) {
        if (paramMap == null) {
            return null;
        }
        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();
        Iterator<String> it = paramMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            list.add(new BasicNameValuePair(key, (String) paramMap.get(key)));
        }
        return list;
    }

    static HostnameVerifier hv = new HostnameVerifier() {
        public boolean verify(String urlHostName, SSLSession session) {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
                    + session.getPeerHost());
            return true;
        }
    };

    private static void trustAllHttpsCertificates() {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        try {
        	javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
        			.getInstance("SSL");
        	sc.init(null, trustAllCerts, null);
        	javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                    .getSocketFactory());
        } catch (Exception e) {
        	new IllegalStateException(e);
        }
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }

        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
            return;
        }
    }
}
