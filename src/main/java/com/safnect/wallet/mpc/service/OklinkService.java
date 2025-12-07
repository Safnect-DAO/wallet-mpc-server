package com.safnect.wallet.mpc.service;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.stereotype.Component;

import com.safnect.wallet.mpc.config.OklinkConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

@Component
public class OklinkService {

	static final Integer TIMEOUT = 5000;
	static final Integer POST_TIMEOUT = 60000;
	
	
	
	public ResponseModel executeGet(String path, Map<String, Object> paramMap) {
		String result = HttpClientUtil.httpGet(OklinkConfig.ENDPOINT + path, paramMap, getHeader(), TIMEOUT);
		return toRm(result);
	}
	
	public ResponseModel executePost(String path, Map<String, Object> paramMap) {
		String result = HttpClientUtil.httpPost(OklinkConfig.ENDPOINT + path, JsonUtil.toJson(paramMap), POST_TIMEOUT, getHeader());
		return toRm(result);
	}
	
	public static ResponseModel toRm(String result) {
		try {
			Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
			String code = MapUtils.getString(resultMap, "code");
			String status = MapUtils.getString(resultMap, "status");
			if (StringUtils.equals(code, "0")) {
				return ResponseModel.successData(resultMap.get("data"));
			} else if (StringUtils.equals(status, "1")) {
				return ResponseModel.successData(resultMap.get("result"));
			} else {
				String msg = MapUtils.getString(resultMap, "msg");
				if (StringUtils.isBlank(msg)) {
					msg = MapUtils.getString(resultMap, "message");
				}
				return ResponseModel.fail(701, String.format("code: %s, msg: %s", StringUtils.defaultIfBlank(code, status), msg));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseModel.fail(result);
		}
	}
	
	public static Header[] getHeader() {
		return new Header[] { new BasicHeader("Ok-Access-Key", OklinkConfig.getKey()) };
	}
}
