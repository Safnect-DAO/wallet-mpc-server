package com.safnect.wallet.mpc.web;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.safnect.wallet.mpc.mapper.AccessLogMapper;
import com.safnect.wallet.mpc.model.AccessLog;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.TextUtil;

@Component
public class AccessInterceptor implements HandlerInterceptor {
	
	@Autowired
	AccessLogMapper accessLogMapper;
	
	@Autowired
    RedisTemplate<String, String> redisTemplate;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
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

	private static String getIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		
		if (ipAddress == null) {
		    // 如果没有代理，则获取客户端的IP地址
		    ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
