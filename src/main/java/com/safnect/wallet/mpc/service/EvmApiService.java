package com.safnect.wallet.mpc.service;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.safnect.wallet.mpc.config.chain.ChainApiConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

@Component
public class EvmApiService {

	static final Integer TIMEOUT = 7000;
	
	/**
	 * Eth API
	 * @param endpoint
	 * @param path
	 * @param paramMap
	 * @return
	 */
	public ResponseModel executeGet(String network, Map<String, Object> paramMap, Class<? extends ChainApiConfig> clazz) {
		ChainApiConfig chainApiConfig = ChainApiConfig.getApiConfig(clazz); 
		paramMap.put("apikey", chainApiConfig.getKey());
		String endpoint = chainApiConfig.getEndpoint(network);
		String result = HttpClientUtil.httpGet(endpoint, paramMap, null, TIMEOUT);
		return toRm(result);
	}
	
	public ResponseModel executeGet(String endpoint, Map<String, Object> paramMap) {
		String result = HttpClientUtil.httpGet(endpoint, paramMap, null, TIMEOUT);
		return toRm(result);
	}

	public static ResponseModel toRm(String result) {
		try {
			Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
			String status = MapUtils.getString(resultMap, "status");
			if (StringUtils.equals(status, "1")) {
				return ResponseModel.successData(resultMap.get("result"));
			} else {
				String msg = MapUtils.getString(resultMap, "message");
				return ResponseModel.fail(701, String.format("code: %s, msg: %s", status, msg));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseModel.fail(result);
		}
	}
}
