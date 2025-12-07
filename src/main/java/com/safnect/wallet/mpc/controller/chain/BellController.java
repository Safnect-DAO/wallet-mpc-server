package com.safnect.wallet.mpc.controller.chain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.BcUtils;

@RestController
@RequestMapping("chain-bell")
public class BellController {
	
	public static final String MAINNET_RPC_URL = "https://api.nintondo.io/api";
	public static final String TESTNET_RPC_URL = "https://testnet.nintondo.io/electrs";
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@GetMapping("utxo")
	public ResponseModel getUtxo(String network, String address, Long amount) {
		if (StringUtils.isAnyBlank(network, address)) {
			return ResponseModel.fail601();
		}
		String endpoint = TESTNET_RPC_URL;
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			endpoint = MAINNET_RPC_URL;
		}
		String result = HttpClientUtil.httpGet(String.format("%s/address/%s/utxo", endpoint, address), null, 7000);
		List<Map<String, Object>> oldmapList = JsonUtil.fromJson(result, new TypeReference<List<Map<String, Object>>>() {});
		List<Map<String, Object>> mapList = BcUtils.removeMarkUtxo(this.redisTemplate, network, oldmapList, Constants.ChainName.BELLCOIN, "value");
		mapList.forEach(da -> { da.put("address", address); });
		if (amount == null || amount <= 0) { // 转账额<=0时，表示需要全部的UTXO交易
			return ResponseModel.successData(mapList);
		} else {
			List<Map<String, Object>> dataList = BcUtils.pickUtxo(amount, mapList, "value");
			return ResponseModel.successData(dataList);
		}
	}
	
	@GetMapping("fee-recomm")
	public Object feeRecomm(String network) {
		Map<String, Object> resultMap = null;
		String key = network + "_BELL_FEE_RECOMM";
		String keyPermanent = network + "_BELL_FEE_RECOMM_permanent";
		String endpoint = TESTNET_RPC_URL;
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			endpoint = MAINNET_RPC_URL;
		}
		try {
			String value = this.redisTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				resultMap = new HashMap<>();
				String url = String.format("%s/fee-estimates", endpoint);
				String result = HttpClientUtil.httpGet(url, null, 5000);
				Map<String, Object> dataMap = JsonUtil.fromJson2Map(result);
				Double oneFee = MapUtils.getDouble(dataMap, "1");
				Double fastestFee = oneFee * 1.1;
				resultMap.put("fastestFee", fastestFee.intValue());
				resultMap.put("economyFee", oneFee);
				resultMap.put("minimumFee", oneFee);
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(resultMap), 7, TimeUnit.SECONDS);
				this.redisTemplate.opsForValue().set(keyPermanent, JsonUtil.toJson(resultMap)); // 永久缓存备用
			} else {
				resultMap = JsonUtil.fromJson2Map(value);
			}
		} catch (Exception e) {
			e.printStackTrace(); // 异常的时候取永久备用
			String value = this.redisTemplate.opsForValue().get(keyPermanent);
			resultMap = JsonUtil.fromJson2Map(value);
		}
		return resultMap;
	}
}
