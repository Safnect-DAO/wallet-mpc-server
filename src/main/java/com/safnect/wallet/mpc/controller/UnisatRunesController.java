package com.safnect.wallet.mpc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

/**
 * 符文
 * @author shiwe
 *
 */
@RestController
@RequestMapping("runes")
@SuppressWarnings({"unchecked", "rawtypes" })
public class UnisatRunesController {
	
//	@Value("${app.rune-api-key}")
	String apiKey = "937fc3e56a7d264cfd5a32ab4a757416701737f23d20470ff84fd7f4696caa80";
	
	static String endPoint = "https://open-api%s.unisat.io";
	
	@Autowired
    RedisTemplate<String, String> redisTemplate;
	
	@Value("${signature.server}")
	String signatureServer;
	
	final String blockstream = "https://blockstream.info/";
	
	/**
	 * 获取符文余额列表
	 * @param network
	 * @param address
	 * @return、
	 */
	@GetMapping("balance-list")
	public ResponseModel balanceList(String network, String address) {
		String key = "runebalance_" + network + "_" + address;
		String value = this.redisTemplate.opsForValue().get(key);
		List<Map<String, Object>> mapList = null;
		if (StringUtils.isBlank(value)) {
			String url = endPoint + "/v1/indexer/address/%s/runes/balance-list";
			String networkStr = getUrlNetworkStr(network);
			String result = HttpClientUtil.httpGet(String.format(url, networkStr, address), null, getHeaders(), 5000);
			Map<String, Object> map = JsonUtil.fromJson2Map(result);
			Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
			mapList = (List<Map<String, Object>>) dataMap.get("detail");
			for (Map<String, Object> balMap : mapList) {
				balMap.put("runeId", MapUtils.getString(balMap, "runeid"));
				balMap.remove("runeid");
				String imagePrefix = this.getRuneImagePrefix(network);
				balMap.put("image", imagePrefix + MapUtils.getString(balMap, "spacedRune"));
			}
			this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(mapList), 30, TimeUnit.SECONDS);
		} else {
			mapList = JsonUtil.fromJson(value, new TypeReference<List<Map<String, Object>>>() {});
		}
		return ResponseModel.sucessData(mapList);
	}
	
	
	
	
	/**
	 * 获取符文余额列表
	 * @param network
	 * @param address
	 * @return
	 */
//	@GetMapping("balance-list")
	public ResponseModel balanceList2(String network, String address) {
		String networkStr = "";
		if (StringUtils.equals(network, "testnet")) {
			networkStr = "testnet/";
		}
		String result = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/address/" + address + "/utxo", null);
		List<Map> mapList = JsonUtil.fromJsonList(result, Map.class);
		Map<String, Map<String, Object>> mapMap = new HashMap<>();
		for (Map map : mapList) {
			Integer value546 = MapUtils.getInteger(map, "value");
			if (value546 == 546) {
				String txid = MapUtils.getString(map, "txid");
				try {
					Map<String,Object> runeMap = this.getUtxoRune(network, networkStr, txid);
					String key = MapUtils.getString(runeMap, "runeId");
					Map<String,Object> existsRuneMap = mapMap.get(key);
					if (existsRuneMap == null) {
						ResponseModel rm = this.runesInfo(network, key);
						Map<String, Object> infoMap = (Map<String, Object>) rm.getData();
						runeMap.put("rune", MapUtils.getString(infoMap, "rune"));
						runeMap.put("spacedRune", MapUtils.getString(infoMap, "spacedRune"));
						runeMap.put("symbol", MapUtils.getString(infoMap, "symbol"));
						runeMap.put("divisibility", MapUtils.getInteger(infoMap, "divisibility"));
						runeMap.put("image", MapUtils.getString(infoMap, "image"));
						mapMap.put(key, runeMap);
					} else {
						Long amount = MapUtils.getLong(existsRuneMap, "amount");
						Long amount2 = MapUtils.getLong(runeMap, "amount");
						existsRuneMap.put("amount", amount + amount2);
					}
				} catch (Exception e) {
					System.out.println("getTransRuneId fail txid -> " + txid);
				}
			}
		}
		List<Map<String, Object>> resultList = new ArrayList<>(mapMap.size());
		mapMap.values().forEach(map -> {
			map.put("amount", MapUtils.getLong(map, "amount").toString());
			resultList.add(map);
		});
		return ResponseModel.sucessData(resultList);
	}

	private String getUrlNetworkStr(String network) {
		String networkStr = "";
		if (StringUtils.equals(network, "testnet")) {
			networkStr = "-testnet";
		}
		return networkStr;
	}
	
	/**
	 * 获取符文信息
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("runes-info")
	public ResponseModel runesInfo(String network, String runeId) {
		String redisKey = "rune_info_" + network + "_" + runeId;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		Map<String, Object> dataMap = null;
		if (StringUtils.isBlank(value)) {
			String url = endPoint + "/v1/indexer/runes/%s/info";
			String networkStr = getUrlNetworkStr(network);
			String result = HttpClientUtil.httpGet(String.format(url, networkStr, runeId), null, getHeaders(), 5000);
			Map<String, Object> map = JsonUtil.fromJson2Map(result);
			dataMap = (Map<String, Object>) map.get("data");
			this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(dataMap));
		} else {
			dataMap = JsonUtil.fromJson2Map(value);
		}
		String imagePrefix = this.getRuneImagePrefix(network);
		dataMap.put("image", imagePrefix + MapUtils.getString(dataMap, "spacedRune"));
		return ResponseModel.sucessData(dataMap);
	}

	private String getRuneImagePrefix(String network) {
		String imagePrefix = null;
		if (StringUtils.equals(network, "testnet")) {
			imagePrefix = "https://api-t2.unisat.io/icon-v1/icon/runes/";
		} else {
			imagePrefix = "https://icon.unisat.io/icon/runes/";
		}
		return imagePrefix;
	}
	
	/**
	 * 获取符文历史记录
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("bill-list/get")
	public ResponseModel getBillList(String network, String address) {
		String networkStr = "";
		if (StringUtils.equals(network, "testnet")) {
			networkStr = "testnet/";
		}
		String key = "rune_billlist_" + network + "_" + address;
		String keyLimit = "rune_billlist_" + network + "_" + address + "_limit";
		List<Map> resultList = null;
		try {
			String valueLimit = this.redisTemplate.opsForValue().get(keyLimit);
			if (StringUtils.isBlank(valueLimit)) {
				String result = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/address/" + address + "/txs", null);
				List<Map> mapList = JsonUtil.fromJsonList(result, Map.class);
				resultList = new ArrayList<>(mapList.size());
				for (Map map : mapList) {
					List<Map> voutList = (List<Map>) map.get("vout");
					String scriptpubkey_type = voutList.get(0).get("scriptpubkey_type").toString();
					if (StringUtils.equals(scriptpubkey_type, "op_return")) {
						Integer value546 = Integer.parseInt(voutList.get(1).get("value").toString());
						if (value546 == 546) {
							List<Map> vinList = (List<Map>) map.get("vin");
							Integer direction = 1;
							for (Map inMap : vinList) {
								String addr = MapUtils.getMap(inMap, "prevout").get("scriptpubkey_address").toString();
								if (StringUtils.equals(address, addr)) {
									direction = 0;
									break;
								}
							}
							Map<String, Object> reMap = new HashMap<>();
							String txid = MapUtils.getString(map, "txid");
							reMap.put("txid", txid);
							reMap.put("gasFee", MapUtils.getInteger(map, "fee"));
							
							String redisKey = "rune2_" + network + "_" + txid;
							String json = this.redisTemplate.opsForValue().get(redisKey);
							Map baMap = null;
							if (StringUtils.isBlank(json)) {
								try {
									// 自解析
									baMap = getRuneAmount(network, txid);
								} catch (Exception e) {
									e.printStackTrace();
									// 解析失败取uniSat API
									String url = endPoint + "/v1/indexer/runes/utxo/%s/1/balance";
									networkStr = getUrlNetworkStr(network);
									String runeResult = HttpClientUtil.httpGet(String.format(url, networkStr, txid), null, getHeaders(), 5000);
									Map runeMap = JsonUtil.fromJson2Map(runeResult);
									List<Map> dataList = (List<Map>) runeMap.get("data");
									if (CollectionUtils.isNotEmpty(dataList)) {
										baMap = dataList.get(0);
										this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(baMap));
									} else {
										continue;
									}
								}
							} else {
								baMap = JsonUtil.fromJson2Map(json);
							}
							
							Long amount = MapUtils.getLong(baMap, "amount");
							reMap.put("amount", amount);
							String senderAddress = null;
							String receiveAddress = null;
							if (direction == 0) {
								senderAddress = address;
							} else {
								receiveAddress = address;
							}
							for (Map outMap : voutList) {
								if (MapUtils.getInteger(outMap, "value") == 546) {
									String outAddress = MapUtils.getString(outMap, "scriptpubkey_address");
									if (!StringUtils.equals(address, outAddress)) {
										if (direction == 0) {
											receiveAddress = outAddress;
										} else {
											senderAddress = outAddress;
										}
									}
								}
							}
							String runeName = MapUtils.getString(baMap, "spacedRune");
							if (StringUtils.isBlank(runeName)) {
								runeName = MapUtils.getString(baMap, "runeName");
							}
							reMap.put("runeName", runeName);
							reMap.put("runeId", MapUtils.getString(baMap, "runeid"));
							reMap.put("symbol", MapUtils.getString(baMap, "symbol"));
							reMap.put("divisibility", MapUtils.getInteger(baMap, "divisibility"));
							reMap.put("receiverAddress", receiveAddress);
							reMap.put("senderAddress", senderAddress);
							reMap.put("totalAmount", amount);
							reMap.put("direction", direction);
							Map<String, Object> statusMap = (Map<String, Object>) map.get("status");
							reMap.put("confirmed", MapUtils.getBoolean(statusMap, "confirmed"));
							reMap.put("time", MapUtils.getLong(statusMap, "block_time"));
							resultList.add(reMap);
						}
					}
				}
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(resultList));
				this.redisTemplate.opsForValue().set(keyLimit, "A", 30, TimeUnit.SECONDS);
			} else {
				resultList = getCacheBillList(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			resultList = getCacheBillList(key);
		}
		return ResponseModel.sucessData(resultList);
	}
	
	private List<Map> getCacheBillList(String key) {
		List<Map> resultList;
		String value = this.redisTemplate.opsForValue().get(key);
		resultList = JsonUtil.fromJsonList(value, Map.class);
		return resultList;
	}

	private Map<String, Object> getRuneAmount(String network, String txid) {
		Map<String, Object> resultMap = null;
		String redisKey = "rune2_amount_v1_" + network + "_" + txid;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		if (StringUtils.isBlank(value)) {
			String networkStr = "";
			if (StringUtils.equals(network, "testnet")) {
				networkStr = "testnet/";
			}
			String hex = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/tx/" + txid + "/hex", null);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("hex", hex);
			String str = HttpClientUtil.httpPost(signatureServer + "resolver-rune", paramMap);
			Map<String, Object> map = JsonUtil.fromJson2Map(str);
			
			String runeId = (String) map.get("runeId");
			resultMap = new HashMap<>();
			resultMap.put("amount", map.get("amount"));
			resultMap.put("runeid", map.get("runeId"));
			
			ResponseModel responseModel = this.runesInfo(network, runeId);
			Map runeMap = (Map) responseModel.getData();
			resultMap.put("runeName", MapUtils.getString(runeMap, "spacedRune"));
			resultMap.put("symbol", MapUtils.getString(runeMap, "symbol"));
			resultMap.put("divisibility", MapUtils.getInteger(runeMap, "divisibility"));
			this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(resultMap));
		} else {
			resultMap = JsonUtil.fromJson2Map(value);
		}
		return resultMap;
	}
	
	/**
	 * 获取符文UTXO(指定符文的utxo+BTC的UTXO)
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("rune-utxo/get")
	public ResponseModel getRuneUtxo(String network, String address, String runeId) {
		String networkStr = "";
		if (StringUtils.equals(network, "testnet")) {
			networkStr = "testnet/";
		}
		String result = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/address/" + address + "/utxo", null);
		List<Map> mapList = JsonUtil.fromJsonList(result, Map.class);
		List<Map> resultList = new ArrayList<>(mapList.size());
		for (Map map : mapList) {
			Integer value546 = MapUtils.getInteger(map, "value");
			if (value546 == 546) {
				String txid = MapUtils.getString(map, "txid");
				try {
					String txruneId = this.getTransRuneId(network, networkStr, txid);
					if (StringUtils.equals(runeId, txruneId)) {
						// utxo是当前符文的utxo，加入
						resultList.add(map);
					}
				} catch (Exception e) {
					System.out.println("getTransRuneId fail txid -> " + txid);
				}
				
			}
		}
		
		/* unisat get utxo API
		String url = endPoint + "/v1/indexer/address/%s/runes/%s/utxo";
		String networkStr = getUrlNetworkStr(network);
		String runeResult = HttpClientUtil.httpGet(String.format(url, networkStr, address, runeId), null, getHeaders(), 5000);
		Map<String, Object> runeResultMap = JsonUtil.fromJson2Map(runeResult);
		Map<String, Object> dataMap = (Map<String, Object>) runeResultMap.get("data");
		List<Map> runeList = (List<Map>) dataMap.get("utxo");

		List<Map> resultList = new ArrayList<>();
		for (Map map : runeList) {
			Map<String, Object> utxoMap = new HashMap<>();
			utxoMap.put("txid", MapUtils.getString(map, "txid"));
			utxoMap.put("vout", MapUtils.getInteger(map, "vout"));
			utxoMap.put("value", MapUtils.getLong(map, "satoshi"));
			resultList.add(utxoMap);
		}
		*/
		return ResponseModel.sucessData(resultList);
	}

	private String getTransRuneId(String network, String networkStr, String txid) {
		String redisKey = "runeid_" + network + "_" + txid;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		String txruneId = null;
		if (StringUtils.isBlank(value)) {
			String hex = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/tx/" + txid + "/hex", null);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("hex", hex);
			String str = HttpClientUtil.httpPost(signatureServer + "resolver-rune", paramMap);
			Map<String, Object> resultMap = JsonUtil.fromJson2Map(str);
			txruneId = (String) resultMap.get("runeId");
			this.redisTemplate.opsForValue().set(redisKey, txruneId);
		} else {
			txruneId = value;
		}
		return txruneId;
	}
	
	private Map<String, Object> getUtxoRune(String network, String networkStr, String txid) {
		String redisKey = "utxo_rune_" + network + "_" + txid;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		Map<String, Object> runeMap = new HashMap<>();
		if (StringUtils.isBlank(value)) {
			String hex = HttpClientUtil.httpGet(this.blockstream + networkStr + "api/tx/" + txid + "/hex", null);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("hex", hex);
			String str = HttpClientUtil.httpPost(signatureServer + "resolver-rune", paramMap);
			runeMap = JsonUtil.fromJson2Map(str);
			this.redisTemplate.opsForValue().set(redisKey, str);
		} else {
			runeMap = JsonUtil.fromJson2Map(value);
		}
		return runeMap;
	}
	
	
	private Header[] getHeaders() {
		return new Header[] { new BasicHeader("Authorization", "Bearer " + apiKey) };
	}
}
