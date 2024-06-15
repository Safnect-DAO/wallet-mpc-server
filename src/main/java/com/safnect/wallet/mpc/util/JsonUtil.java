/*
 * Copyright (c) 2016, FPX and/or its affiliates. All rights reserved.
 * Use, Copy is subject to authorized license.
 */
package com.safnect.wallet.mpc.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author shiw
 * @date 2016年5月24日
 */
public class JsonUtil {
	
	private static ObjectMapper mapper = new ObjectMapper();

    static {
        // 反序列化时忽略掉多余的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * 将对象转换成json格式字符串
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toJson(Object object) {
        try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * 从json格式字符串转换成对象
     * @param json
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
    	try {
    		return (T) mapper.readValue(json, clazz);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    /**
     * 从json格式字符串转换成对象
     * @param json
     * @param clazz
     * @return
     * @throws Exception
     */
    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        try {
			return mapper.readValue(json, valueTypeRef);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
    }

    /**
     * 将json转换成list对象
     * @param json
     * @param clazz
     * @return
     * @throws Exception 
     */
    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        JavaType t = mapper.getTypeFactory().constructParametricType(List.class, clazz);
        try {
			return mapper.readValue(json, t);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
    }
    
    /**
     * 转换为map
     * @param json
     * @return
     */
    public static Map<String, Object> fromJson2Map(String json) {
        try {
			return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
    }
}
