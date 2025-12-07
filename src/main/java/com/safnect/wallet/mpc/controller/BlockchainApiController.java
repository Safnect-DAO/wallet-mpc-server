package com.safnect.wallet.mpc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.config.chain.BscApiConfig;
import com.safnect.wallet.mpc.config.chain.ChainApiConfig;
import com.safnect.wallet.mpc.config.chain.EthApiConfig;
import com.safnect.wallet.mpc.config.unisat.UnisatBitcoinMainnetConfig;
import com.safnect.wallet.mpc.config.unisat.UnisatFractalMainnetConfig;
import com.safnect.wallet.mpc.config.unisat.UnisatFractalTestnetConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.CoinMapper;
import com.safnect.wallet.mpc.model.Coin;
import com.safnect.wallet.mpc.service.CryptoApiService;
import com.safnect.wallet.mpc.service.EvmApiService;
import com.safnect.wallet.mpc.service.OklinkService;
import com.safnect.wallet.mpc.service.UnisatApiService;
import com.safnect.wallet.mpc.util.BcUtils;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

@RestController
@RequestMapping("bca")
public class BlockchainApiController {

	@Autowired
	OklinkService oklinkService;
	
	@Autowired
	EvmApiService evmApiService;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	CryptoApiService cryptoApiService;
	
	@Autowired
	CoinMapper coinMapper;
	
	@Autowired
	UnisatApiService unisatApiService;
	
	final String BSV_FEES_URL_FORMAT = "https://api.whatsonchain.com/v1/bsv/main/miner/fees?from=%s&to=%s";
	
	@GetMapping("get")
	public ResponseModel oklinkGet(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		Integer cacheTime = 5; // default cache expire time is 3 seconds 
		Map<String, Object> paramMap = new HashMap<>();
		String path = null;
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getParameter(name);
			if (StringUtils.equals(name, "path")) {
				path = value;
			} else if (StringUtils.equals(name, "cache")) {
				if (StringUtils.isNotBlank(value)) {
					cacheTime = Integer.parseInt(value);
				}
			} else {
				paramMap.put(name, value);
			}
		}
		String chainShortName = paramMap.get("chainShortName").toString();
		if (StringUtils.equals(path, "address/token-balance") && StringUtils.equals(chainShortName, "BSC_TESTNET")) {
			// BSV TESTNET token list
			return this.getBscTestnetTokenBalance(paramMap);
		}
		
		StringBuilder keySb = this.getCacheKey(paramMap, path);
		String key = keySb.toString();
		String cacheValue = this.redisTemplate.opsForValue().get(key);
		ResponseModel rm = null;
		if (StringUtils.isBlank(cacheValue)) {
			rm = this.oklinkService.executeGet(path, paramMap);
			if (rm.isSuccess()) {
				Object data = rm.getData();
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(data), cacheTime, TimeUnit.SECONDS);
			}
		} else {
			Object obj = JsonUtil.fromJson(cacheValue, Object.class);
			rm = ResponseModel.successData(obj);
		}
		return rm;
	}

	private ResponseModel getBscTestnetTokenBalance(Map<String, Object> paramMap) {
		String address = paramMap.get("address").toString();
		Map<String, Object> paramMap2 = new HashMap<>();
		paramMap2.put("module", "account");
		paramMap2.put("action", "tokenbalance");
		paramMap2.put("contractaddress", "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd");
		paramMap2.put("address", address);
		ResponseModel rm = this.evmApiService.executeGet(Constants.NETWORK_TESTNET, paramMap2, BscApiConfig.class);
		String amount = (String) rm.getData();
		List<Map<String, Object>> mapList = new ArrayList<>();
		Map<String, Object> map1 = new HashMap<>();
		List<Map<String, Object>> tokenList = new ArrayList<>();
		Map<String, Object> map2 = new HashMap<>();
		map2.put("symbol", "USDT");
		map2.put("tokenContractAddress", "0x337610d27c682E347C9cD60BD4b3b107C9d34dDd");
		map2.put("tokenType", "BEP20");
		map2.put("holdingAmount", BcUtils.toEther(Long.parseLong(amount)));
		tokenList.add(map2);
		map1.put("tokenList", tokenList);
		mapList.add(map1);
		rm.setData(mapList);
		return rm;
	}
	
	@GetMapping("ethapi-get")
	public ResponseModel ethapiget(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		Integer cacheTime = 5; // default cache expire time is 3 seconds 
		Map<String, Object> paramMap = new HashMap<>();
		String network = null;
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getParameter(name);
			if (StringUtils.equals(name, "network")) {
				network = value;
			} else if (StringUtils.equals(name, "cache")) {
				if (StringUtils.isNotBlank(value)) {
					cacheTime = Integer.parseInt(value);
				}
			} else {
				paramMap.put(name, value);
			}
		}
		StringBuilder keySb = this.getCacheKey(paramMap, "ethapi_" + network);
		String key = keySb.toString();
		String cacheValue = this.redisTemplate.opsForValue().get(key);
		ResponseModel rm = null;
		if (StringUtils.isBlank(cacheValue)) {
			rm = this.evmApiService.executeGet(network, paramMap, EthApiConfig.class);
			if (rm.isSuccess()) {
				Object data = rm.getData();
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(data), cacheTime, TimeUnit.SECONDS);
			}
		} else {
			Object obj = JsonUtil.fromJson(cacheValue, Object.class);
			rm = ResponseModel.successData(obj);
		}
		return rm;
	}
	
	public Class<? extends ChainApiConfig> getChainApiConfigClass(String chainName) {
		switch (chainName) {
		case "ETH": return EthApiConfig.class;
		case "SEPOLIA_TESTNET": return EthApiConfig.class;
		case "BSC": return BscApiConfig.class;
		case "BSC_TESTNET": return BscApiConfig.class;
		default: return null;
		}
	}

	private StringBuilder getCacheKey(Map<String, Object> paramMap, String path) {
		Object[] nameArr = paramMap.keySet().toArray();
		Arrays.sort(nameArr);
		StringBuilder keySb = new StringBuilder(path);
		for (Object name : nameArr) {
			if (!StringUtils.equals(name.toString(), "cache")) {
				keySb.append("_").append(paramMap.get(name));
			}
		}
		return keySb;
	}
	
	@PostMapping("post")
	public ResponseModel oklinkPost(HttpServletRequest request) {
		Enumeration<String> names = request.getParameterNames();
		
		Map<String, Object> paramMap = new HashMap<>();
		String path = null;
		while (names.hasMoreElements()) {
			String name = names.nextElement();
			String value = request.getParameter(name);
			if (StringUtils.equals(name, "path")) {
				path = value;
			} else {
				paramMap.put(name, value);
			}
		}
		return this.oklinkService.executePost(path, paramMap);
	}
	
	@GetMapping("fb-utxo")
	public ResponseModel getFbUtxo(String network, String address, Long amount) {
		if (StringUtils.isAnyBlank(network, address)) {
			return ResponseModel.fail601();
		}
		String endpoint = Constants.Unisat.FB_TEST_ENDPOINT_PREFIX;
		Header[] headerArr = Constants.Unisat.FB_TESTNET_HEADER_ARR;
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			endpoint = Constants.Unisat.FB_MAIN_ENDPOINT_PREFIX;
			headerArr = Constants.Unisat.FB_MAINNET_HEADER_ARR;
		}
		return this.fetchUtxo(network, address, amount, endpoint, headerArr, Constants.ChainName.FRACTAL_BITCOIN);
	}
	
	@GetMapping("btc-utxo")
	public ResponseModel getBtcUtxo(String network, String address, Long amount) {
		if (StringUtils.isAnyBlank(network, address)) {
			return ResponseModel.fail601();
		}
		
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			String endpoint = Constants.Unisat.BTC_MAIN_ENDPOINT_PREFIX;
			Header[] headerArr = Constants.Unisat.BTC_MAINNET_HEADER_ARR;
			return this.fetchUtxo(network, address, amount, endpoint, headerArr, Constants.ChainName.BITCOIN);
		} else { // testnet4
			String re = HttpClientUtil.httpGet("https://wallet-api-testnet4.unisat.io/v5/address/btc-utxo?address=" + address, null);
			Map<String, Object> map = JsonUtil.fromJson2Map(re);
			return this.pickUtxo(network, amount, Constants.ChainName.BITCOIN, map, address);
		}
	}

	@GetMapping("cat-utxo")
	public ResponseModel getCatUtxo(String network, String address, Long amount) {
		if (StringUtils.isAnyBlank(network, address)) {
			return ResponseModel.fail601();
		}

		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
			String endpoint = Constants.Unisat.BTC_MAIN_ENDPOINT_PREFIX;
			Header[] headerArr = Constants.Unisat.BTC_MAINNET_HEADER_ARR;
			return this.fetchUtxo(network, address, amount, endpoint, headerArr, Constants.ChainName.BITCOIN);
		} else { // testnet4
			String re = HttpClientUtil.httpGet("https://wallet-api-testnet4.unisat.io/v5/address/btc-utxo?address=" + address, null);
			Map<String, Object> map = JsonUtil.fromJson2Map(re);
			return this.pickUtxo(network, amount, Constants.ChainName.BITCOIN, map, address);
		}
	}

	
	private ResponseModel fetchUtxo(String network, String address, Long amount, String endpoint, Header[] headerArr, String chain) {
		String result = HttpClientUtil.httpGet(String.format("%s/v1/indexer/address/%s/utxo-data?cursor=0&size=100", endpoint, address), null, headerArr, 7000);
		Map<String, Object> map = JsonUtil.fromJson2Map(result);
		return pickUtxo(network, amount, chain, map, address);
	}

	@SuppressWarnings("unchecked")
	private ResponseModel pickUtxo(String network, Long amount, String chain, Map<String, Object> map, String address) {
		Integer code = MapUtils.getInteger(map, "code");
		if (code == 0) {
			List<Map<String, Object>> oldmapList = null;
			if (StringUtils.equals(chain, Constants.ChainName.BITCOIN) && StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
				// BTC testnet4
				oldmapList = (List<Map<String, Object>>) map.get("data");
				oldmapList.forEach(itemMap -> {
					itemMap.put("satoshi", itemMap.get("satoshis"));
					itemMap.put("address", address);
				});
			} else {
				Map<String, Object> dataMap = MapUtils.getMap(map, "data");
				oldmapList = (List<Map<String, Object>>) dataMap.get("utxo");
			}
			
			List<Map<String, Object>> mapList = BcUtils.removeMarkUtxo(this.redisTemplate, network, oldmapList, chain, "satoshi");
			if (amount == null || amount <= 0) { // 转账额<=0时，表示需要全部的UTXO交易
				return ResponseModel.successData(mapList);
			} else {
				List<Map<String, Object>> dataList = BcUtils.pickUtxo(amount, mapList, "satoshi");
				return ResponseModel.successData(dataList);
			}
		} else {
			String msg = MapUtils.getString(map, "msg");
			return ResponseModel.fail(msg);
		}
	}

	@PostMapping("cat-utxo-mark")
	public ResponseModel catmarkUtxo(String network, String utxoJson) {
		Integer days = 7;
		return utxoMarkSave(Constants.ChainName.CATCOIN, network, utxoJson, Constants.CHAIN_UTXO_KEY_FORMAT, days);
	}

	@PostMapping("fb-utxo-mark")
	public ResponseModel fbmarkUtxo(String network, String utxoJson) {
		Integer days = 7;
		return utxoMarkSave(Constants.ChainName.FRACTAL_BITCOIN, network, utxoJson, Constants.CHAIN_UTXO_KEY_FORMAT, days);
	}

	private ResponseModel utxoMarkSave(String chain, String network, String utxoJson, String keyFormat, Integer days) {
		if (StringUtils.isAnyBlank(network, utxoJson)) {
			return ResponseModel.fail601();
		}
		List<Map<String, Object>> utxoList = JsonUtil.fromJson(utxoJson, new TypeReference<List<Map<String, Object>>>() {});
		for (Map<String, Object> utxoMap : utxoList) {
			String txid = MapUtils.getString(utxoMap, "txid");
			Integer vout = MapUtils.getInteger(utxoMap, "vout");
			String key = String.format(keyFormat, chain, network, txid, vout);
			this.redisTemplate.opsForValue().set(key, "1", days, TimeUnit.DAYS);
		}
		return ResponseModel.success();
	}
	
	@PostMapping("btc-utxo-mark")
	public ResponseModel btcmarkUtxo(String network, String utxoJson) {
		Integer days = 30;
		return utxoMarkSave(Constants.ChainName.BITCOIN, network, utxoJson, Constants.CHAIN_UTXO_KEY_FORMAT, days);
	}
	
	@PostMapping("utxo-mark")
	public ResponseModel markUtxo(String chain, String network, String utxoJson) {
		Integer days = 30;
		return utxoMarkSave(chain, network, utxoJson, Constants.CHAIN_UTXO_KEY_FORMAT, days);
	}
	
	@GetMapping("coins")
	public ResponseModel coins(String network) {
		List<Coin> coinList = this.coinMapper.get(network);
		return ResponseModel.successData(coinList);
	}
	
	@PostMapping("rpc-api")
	public ResponseModel rpcApi(String chain, String network, String method, String param, String type) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("jsonrpc", "2.0");
		paramMap.put("id", "1");
		paramMap.put("method", method);
		List<Object> list = new ArrayList<>();
		if (StringUtils.equals(type, "String")) {
			list.add(param);
		} else if (StringUtils.equals(type, "Object")) {
			Map<String, Object> map = JsonUtil.fromJson2Map(param);
			list.add(map);
		} else if (StringUtils.equals(type, "Array")) {
			list = JsonUtil.fromJsonList(param, Object.class);
		} 
		paramMap.put("params", list);
		String endpoint = Constants.EVM_RPC_ENDPOINT.getEndpoint(chain, network);
		if (StringUtils.isBlank(endpoint)) {
			return ResponseModel.fail602();
		}
		String result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, endpoint, JsonUtil.toJson(paramMap));
		Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
		return ResponseModel.successData(resultMap);
	}
	
	@GetMapping("fees")
	public ResponseModel fees(String chain, String network) {
		String key = String.format("%s_%s_FEE_RECOMM", network, chain);
		String keyPermanent = String.format("%s_%s_FEE_RECOMM_PERMANENT", network, chain);
		ResponseModel rm = null;
		try {
			String value = this.redisTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				if (StringUtils.equalsIgnoreCase(chain, Constants.ChainName.BitcoinSV)) {
					// bsv
					Map<String, Object> newMap = getBsvRecommFee(network);
					rm = ResponseModel.successData(newMap);
				} else {
					String path = String.format("/blockchain-data/%s/%s/mempool/fees", chain, network);
					rm = this.cryptoApiService.executeGet(path, null);
				}
				
				if (rm.isSuccess()) {
					Object data = rm.getData();
					String dataJson = JsonUtil.toJson(data);
					this.redisTemplate.opsForValue().set(key, dataJson, 30, TimeUnit.SECONDS);
					this.redisTemplate.opsForValue().set(keyPermanent, dataJson);
				} 
			} else {
				rm = ResponseModel.successData(JsonUtil.fromJson2Map(value));
			}
		} catch (Exception e) {
			e.printStackTrace(); // 异常的时候取永久备用
		}
		if (rm == null) {
			String value = this.redisTemplate.opsForValue().get(keyPermanent);
			if (StringUtils.isNotBlank(value)) {
				rm = ResponseModel.successData(JsonUtil.fromJson2Map(value));
			} else {
				return ResponseModel.fail("unknow");
			}
		}
		return rm;
	}

	@SuppressWarnings("rawtypes")
	private Map<String, Object> getBsvRecommFee(String network) {
		String cachekey = "bsv_fees_" + network;
		String cacheValue = this.redisTemplate.opsForValue().get(cachekey);
		Map<String, Object> newMap = null;
		if (StringUtils.isBlank(cacheValue)) {
			long currentTime = System.currentTimeMillis() / 1000;
			long startTime = currentTime - 60 * 30;
			String json = HttpClientUtil.httpGet(String.format(BSV_FEES_URL_FORMAT, startTime, currentTime), null);
			List<Map> mapList = JsonUtil.fromJsonList(json, Map.class);
			Map map = mapList.get(0);
			double minFeeRate = MapUtils.getDouble(map, "min_fee_rate");
			Integer fee = (int) Math.ceil(minFeeRate);
			newMap = new HashMap<>();
			newMap.put("unit", "satoshi");
			newMap.put("fast", fee.toString());
			newMap.put("slow", fee.toString());
			newMap.put("standard", fee.toString());
			this.redisTemplate.opsForValue().set(cachekey, JsonUtil.toJson(newMap), 2, TimeUnit.MINUTES);
		} else {
			newMap = JsonUtil.fromJson2Map(cacheValue);
		}
		return newMap;
	}

	@GetMapping("unisat-balance")
	public ResponseModel getUnisatApiBalance(String chainName, String network, String address) {
		String cachekey = "unisat_" + chainName + "_" + network + "_" + address;
		String cacheValue = this.redisTemplate.opsForValue().get(cachekey);
		ResponseModel rm = null;
		if (StringUtils.isBlank(cacheValue)) {
			String endpoint = null;
			String apiKey = null;
			if (StringUtils.equals(chainName, Constants.ChainName.BITCOIN) && StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				endpoint = UnisatBitcoinMainnetConfig.ENDPOINT;
				apiKey = UnisatBitcoinMainnetConfig.getKey();
			} else if (StringUtils.equals(chainName, Constants.ChainName.FRACTAL_BITCOIN) && StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				endpoint = UnisatFractalMainnetConfig.ENDPOINT;
				apiKey = UnisatFractalMainnetConfig.getKey();
			} else if (StringUtils.equals(chainName, Constants.ChainName.FRACTAL_BITCOIN) && StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
				endpoint = UnisatFractalTestnetConfig.ENDPOINT;
				apiKey = UnisatFractalTestnetConfig.getKey();
			} 
//			System.out.println("chainName -> " + chainName + " network -> " + network + " address -> " + address + " apikey -> " + apiKey);
			rm = this.unisatApiService.executeGet(endpoint, apiKey, "/v1/indexer/address/" + address + "/balance", null);
			if (rm.isSuccess()) {
				Object data = rm.getData();
				this.redisTemplate.opsForValue().set(cachekey, JsonUtil.toJson(data), 60, TimeUnit.SECONDS);
			}
		} else {
			Object obj = JsonUtil.fromJson(cacheValue, Object.class);
			rm = ResponseModel.successData(obj);
		}
		return rm;
	}
}
