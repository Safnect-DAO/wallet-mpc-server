package com.safnect.wallet.mpc.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.AccessLogMapper;
import com.safnect.wallet.mpc.model.AccessLog;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@Component
public class AccessInterceptor implements HandlerInterceptor {
	
	static final String KEY = "";
	
	@Autowired
	AccessLogMapper accessLogMapper;
	
	@Autowired
    RedisTemplate<String, String> redisTemplate;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String token = request.getHeader("token");
		if (StringUtils.isAnyBlank(token)) {
			this.writeMessage(response, ResponseModel.fail601());
			return false;
		}
		
		
		String encryptText = this.getEncryptText(request);
		if (!StringUtils.equals(token, encryptText)) {
			try{
				this.writeMessage(response, ResponseModel.fail(603, "Invalid token"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
		
		// 5分钟记录一次访问日志
		String walletId = request.getParameter("walletId");
		if (StringUtils.isNotBlank(walletId) && !this.redisTemplate.hasKey(walletId)) {
			this.redisTemplate.opsForValue().set(walletId, "on", 5, TimeUnit.MINUTES);
			// 获取代理IP（如果存在）
	        String ipAddress = getIpAddress(request);
	        String sourceApp = detectClientType(request);
	        if (!StringUtils.equals(sourceApp, "Unknown")) {
	        	this.accessLogMapper.insertSelective(new AccessLog(TextUtil.getUUID2(), walletId, ipAddress, new Date(), sourceApp));
	        }
		}
		return true;
    }
	
	public static String detectClientType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        // 检查是否是 Chrome 浏览器
        if (userAgent.contains("Chrome/") && !userAgent.contains("CriOS/")) {
            return Constants.SOURCE_APP_EXTENSION;
        }

        // 检查是否是 Chrome for iOS
        if (userAgent.contains("CriOS/")) {
            return Constants.SOURCE_APP_EXTENSION;
        }

        // 检查是否是 Android 设备
        if (userAgent.contains("Android")) {
            return Constants.SOURCE_APP_ANDROID;
        }

        // 检查是否是 iOS 设备
        if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iPod")) {
            return Constants.SOURCE_APP_IOS;
        }

        // 检查是否是桌面浏览器
        if (userAgent.contains("Windows") || userAgent.contains("Macintosh") || userAgent.contains("X11")) {
        	return Constants.SOURCE_APP_EXTENSION;
        }

        // 如果无法识别，返回未知
        return "Unknown";
    }

	private String getEncryptText(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		List<String> list = new ArrayList<>();
		while (names.hasMoreElements()) {
			list.add(names.nextElement());
		}
		Collections.sort(list);
		StringBuilder sb = new StringBuilder();
		list.forEach((item) -> {
			String value = request.getParameter(item);
			sb.append(value);
		});
		sb.append(KEY);
		String text = sb.toString();
		String encryptText = TextUtil.sha1(TextUtil.base64(text));
		return encryptText;
	}

	private void writeMessage(HttpServletResponse response, ResponseModel rm) throws IOException {
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		PrintWriter out = response.getWriter();
		out.write(JsonUtil.toJson(rm));
		out.flush();
		out.close();
	}

	private static String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		
		if (ipAddress == null) {
		    // 如果没有代理，则获取客户端的IP地址
		    ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
