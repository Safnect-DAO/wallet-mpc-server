package com.safnect.wallet.mpc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

/**
 * 符文
 * @author shiwe
 *
 */
@RestController
@RequestMapping("runes-scrypt")
public class RunesController {
	
	@Value("${app.rune-api-key}")
	String apiKey;
	
	static String endPoint = "https://open-api.scrypt.io/v1/btc/";
	
	/**
	 * 获取符文余额列表
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("balance-list")
	public ResponseModel balanceList(String network, String address) {
		String url = endPoint + network + "/runes/addresses/" + address + "/balances";
		String result = HttpClientUtil.httpGet(url, null, getHeaders(), 5000);
		Map<String, Object> map = JsonUtil.fromJson2Map(result);
		return ResponseModel.successData(map);
	}
	
	/**
	 * 获取符文信息
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("runes-info")
	public ResponseModel runesInfo(String network, String runeId) {
		String url = endPoint + network + "/runes/" + runeId + "/info";
		String result = HttpClientUtil.httpGet(url, null, getHeaders(), 5000);
		Map<String, Object> map = JsonUtil.fromJson2Map(result);
		return ResponseModel.successData(map);
	}
	
	/**
	 * 获取符文历史记录
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("bill-list/get")
	public ResponseModel getBillList(String network, String address) {
		String url = endPoint + network + "/runes/addresses/" + address + "/history";
		String result = HttpClientUtil.httpGet(url, null, getHeaders(), 5000);
		Map<String, Object> map = JsonUtil.fromJson2Map(result);
		return ResponseModel.successData(map);
	}
	
	/**
	 * 获取符文UTXO(指定符文的utxo+BTC的UTXO)
	 * @param network
	 * @param address
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("rune-utxo/get")
	public ResponseModel getRuneUtxo(String network, String address, String runeId) {
		String result = HttpClientUtil.httpGet("https://blockstream.info/" + network + "/api/address/" + address + "/utxo", null);
		List<Map> utxoList = JsonUtil.fromJsonList(result, Map.class);
		String url = endPoint + network + "/runes/addresses/" + address + "/utxos";
		String runeResult = HttpClientUtil.httpGet(url, null, getHeaders(), 5000);
		Map<String, Object> runeResultMap = JsonUtil.fromJson2Map(runeResult);
		List<Map> runeList = (List<Map>) runeResultMap.get("data");

		Map<String, Map> runeMap = new HashMap<>();
		for (Map map : runeList) {
			String outpoint = MapUtils.getString(map, "outpoint");
			String tempTxid = outpoint.split(":")[0];
			runeMap.put(tempTxid, map);
		}
		List<Map> resultList = new ArrayList<>();
		for (Map utxoMap : utxoList) {
			String txid = MapUtils.getString(utxoMap, "txid");
			Map rune = runeMap.get(txid);
			if (rune == null) {// 非符文的utxo，加入
				resultList.add(utxoMap);
			} else {
				String tempRuneId = MapUtils.getString(rune, "rune_id");
				if (StringUtils.equals(tempRuneId, runeId)) {
					// 是当前转账的符文UTXO，加入
					resultList.add(utxoMap);
				}
			}
		}
		
		return ResponseModel.successData(resultList);
	}
	
	/**
	 * 获取符文UTXO
	 * @param network
	 * @param address
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("rune-utxo/v2/get")
	public ResponseModel getRuneUtxoV2(String network, String address, String runeId) {
		String result = HttpClientUtil.httpGet("https://blockstream.info/" + network + "/api/address/" + address + "/utxo", null);
		List<Map> utxoList = JsonUtil.fromJsonList(result, Map.class);
		String url = endPoint + network + "/runes/addresses/" + address + "/utxos";
		String runeResult = HttpClientUtil.httpGet(url, null, getHeaders(), 5000);
		Map<String, Object> runeResultMap = JsonUtil.fromJson2Map(runeResult);
		List<Map> runeList = (List<Map>) runeResultMap.get("data");

		Map<String, Map> runeMap = new HashMap<>();
		for (Map map : runeList) {
			String outpoint = MapUtils.getString(map, "outpoint");
			String tempTxid = outpoint.split(":")[0];
			runeMap.put(tempTxid, map);
		}
		List<Map> resultList = new ArrayList<>();
		for (Map utxoMap : utxoList) {
			String txid = MapUtils.getString(utxoMap, "txid");
			Map rune = runeMap.get(txid);
			if (rune != null) {
				String tempRuneId = MapUtils.getString(rune, "rune_id");
				if (StringUtils.equals(tempRuneId, runeId)) {
					// 是当前转账的符文UTXO，加入
					resultList.add(utxoMap);
				}
			}
		}
		
		return ResponseModel.successData(resultList);
	}
	
	private Header[] getHeaders() {
		return new Header[] { new BasicHeader("Authorization", "Bearer " + apiKey) };
	}
}
