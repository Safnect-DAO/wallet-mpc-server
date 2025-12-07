package com.safnect.wallet.mpc.service;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

import com.safnect.wallet.mpc.config.CryptoApiConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

@Component
public class CryptoApiService {

	static final Integer TIMEOUT = 30000;
	static final Integer POST_TIMEOUT = 60000;
	
	public ResponseModel executeGet(String path, Map<String, Object> paramMap) {
		String result = HttpClientUtil.httpGet(CryptoApiConfig.ENDPOINT + path, paramMap, getHeader(), TIMEOUT);
		return toRm(result);
	}
	
	public ResponseModel executePost(String path, Map<String, Object> paramMap) {
		String result = HttpClientUtil.httpPost(CryptoApiConfig.ENDPOINT + path, JsonUtil.toJson(paramMap), POST_TIMEOUT, getHeader());
		return toRm(result);
	}
	
	@SuppressWarnings("unchecked")
	public static ResponseModel toRm(String result) {
		try {
			Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
			Object errorObj = resultMap.get("error");
			if (errorObj != null) {
				Map<String, Object> errorMap = (Map<String, Object>) errorObj;
				String code = MapUtils.getString(errorMap, "code");
				String errorMsg = MapUtils.getString(errorMap, "message");
				return ResponseModel.fail(701, String.format("code: %s, msg: %s", code, errorMsg));
			} else {
				Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
				return ResponseModel.successData(dataMap.get("item"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseModel.fail(result);
		}
	}
	
	public static Header[] getHeader() {
		return new Header[] { new BasicHeader("X-API-Key", CryptoApiConfig.getKey()) };
	}
}
