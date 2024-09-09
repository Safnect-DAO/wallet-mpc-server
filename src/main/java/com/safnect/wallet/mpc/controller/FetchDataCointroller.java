package com.safnect.wallet.mpc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.TokenInfoMapper;
import com.safnect.wallet.mpc.model.TokenInfo;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

/**
 * 抓抓抓
 * 
 * @author shiwe
 *
 */
@RestController
@RequestMapping("fetch-data")
@SuppressWarnings({"rawtypes", "unchecked"})
public class FetchDataCointroller {

	@Autowired
	RedisTemplate<String, String> redisTemplate;

	@Autowired
	TokenInfoMapper tokenInfoMapper;
	
	static final String DATE_FORMAT = "yyyy-MM-dd H:mm:ss";
	
	@GetMapping("price")
	public ResponseModel price(String chainNames) throws IOException {
		String redisKey = "coingecko_price_list";
		String value = this.redisTemplate.opsForValue().get(redisKey);
		Map priceMap;
		if (StringUtils.isBlank(value)) {
			String str = "";
			if (StringUtils.isNotBlank(chainNames)) {
				str = "," + chainNames;
			}
			String result = HttpClientUtil.httpGet(
					"https://api.coingecko.com/api/v3/simple/price?ids=ethereum,bitcoin,solana,litecoin,dogecoin,conflux-token,arbitrum,Filecoin,WETH,usd-coin"
							+ str + "&vs_currencies=usd&include_24hr_change=true",
					null);
			this.redisTemplate.opsForValue().set(redisKey, result, 5, TimeUnit.SECONDS);
			priceMap = JsonUtil.fromJson2Map(result);
		} else {
			priceMap = JsonUtil.fromJson2Map(value);
		}
		// 添加USDT行情
		Map<String, Object> usdtMap = new HashMap<>();
		usdtMap.put("usd", 1.0);
		usdtMap.put("usd_24h_change", 0.0);
		priceMap.put("usdt", usdtMap);
		return ResponseModel.sucessData(priceMap);
	}
	
	@GetMapping("eth-tx-record")// 0x604ca12bc0728d9e8202da97d592d149bfa9faca
	public ResponseModel getEthTxRecord(String network, String address) throws IOException {
		String detailKeyPrefix = "eth_txdetail_" + network;
		String key = "eth_txlist_" + network + "_" + address;
		String value = this.redisTemplate.opsForValue().get(key);
		List<Map<String, Object>> resultList = null;
		if (StringUtils.isBlank(value)) {
			String chainName = "ETH";
			if (StringUtils.equals(network, "testnet")) {
				chainName = "SEPOLIA_TESTNET";
			}
			List<Map<String, Object>> transactionList = this.getEthTransList(address, chainName);
			transactionList.addAll(getEthTokenTransList(address, chainName));
			transactionList.sort(Comparator.comparing(map -> (Comparable) map.get("transactionTime").toString()));
			Collections.reverse(transactionList);
			resultList = new ArrayList<>();
			List<String> noCacheTxidList = new ArrayList<>(); 
			for (Map<String, Object> map : transactionList) {
				String amount = null;
				if (this.isEthTrans(map)) {
					amount = map.get("amount").toString();
					if (StringUtils.equals(amount, "0")) {
						// 转账给合约（Token转账）数据忽略
						continue;
					}
				} else {
					amount = "0";
				}
				Map<String, Object> rsMap = new HashMap<>();
				rsMap.put("amount", amount + " ETH");
				String txid = map.get("txId").toString();
				
				
				if (!this.isEthTrans(map)) {
					String detailKey = detailKeyPrefix + txid;
					String detailValue = this.redisTemplate.opsForValue().get(detailKey);
					if (StringUtils.isNotBlank(detailValue)) {
						Map<String, Object> detailMap = JsonUtil.fromJson2Map(detailValue);
						rsMap.put("gasFee", detailMap.get("txFee"));
					} else {
						noCacheTxidList.add(txid);
					}
				} else {
					rsMap.put("gasFee", map.get("txFee"));
				}
				
				rsMap.put("receiveAddress", map.get("to"));
				rsMap.put("txid", txid);
				String from = map.get("from").toString();
				rsMap.put("fromAddress", from);
				String transactionTimeStr = map.get("transactionTime").toString();
				String time = DateFormatUtils.format(new Date(Long.parseLong(transactionTimeStr)), DATE_FORMAT);
				rsMap.put("time", time);
				String directory = "IN";
				if (StringUtils.equals(address, from)) {
					directory = "OUT";
				}
				rsMap.put("type", "0");
				if (!this.isEthTrans(map)) {
					rsMap.put("type", "1");
				}
				
				rsMap.put("directory", directory);
				if (!this.isEthTrans(map)) {
					Map<String, Object> tokenInfo = new HashMap<>();
					tokenInfo.put("tokenSymbol", map.get("symbol"));
					tokenInfo.put("tokenAmount", map.get("amount"));
					tokenInfo.put("tokenReceiveAddress", map.get("tokenContractAddress"));
					rsMap.put("tokenInfo", tokenInfo);
				}
				resultList.add(rsMap);
			}
			if (CollectionUtils.isNotEmpty(noCacheTxidList)) {
				String ids = StringUtils.join(noCacheTxidList, ",");
				String url = String.format("%stransaction/transaction-multi?chainShortName=%s&txId=" + ids, Constants.OKLINK_ENDPOINT, chainName);
				List<Map<String, Object>> dataList = getOkxApiResult(url);
				Map<String, Map<String, Object>> txDetailMap = new HashMap<>();
				for (Map<String, Object> detailMap : dataList) {
					String txid = detailMap.get("txId").toString();
					this.redisTemplate.opsForValue().set(detailKeyPrefix + txid, JsonUtil.toJson(detailMap));
					txDetailMap.put(txid, detailMap);
				}
				for (Map<String, Object> txMap : resultList) {
					Object gasFeeObj = txMap.get("gasFee");
					if (gasFeeObj == null || StringUtils.isBlank(gasFeeObj.toString())) {
						String txid = (String) txMap.get("txid");
						Map<String, Object> txValueMap = txDetailMap.get(txid);
						txMap.put("gasFee", txValueMap.get("txFee"));
					}
				}
			}
			this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(resultList), 45, TimeUnit.SECONDS);
		} else {
			resultList = JsonUtil.fromJson(value, new TypeReference<List<Map<String, Object>>>() {});
		}
		return ResponseModel.sucessData(resultList);
	}
	
	private boolean isEthTrans(Map<String, Object> map) {
		Object transactionSymbol = map.get("transactionSymbol");
		return transactionSymbol != null && StringUtils.equals(transactionSymbol.toString(), "ETH");
	}

	private List<Map<String, Object>> getEthTokenTransList(String address, String chainName) {
		String url = String.format("%saddress/token-transaction-list?chainShortName=%s&address=%s", Constants.OKLINK_ENDPOINT, chainName, address);
		List<Map<String, Object>> dataList = getOkxApiResult(url);
		return (List<Map<String, Object>>) dataList.get(0).get("transactionList");
	}

	private List<Map<String, Object>> getOkxApiResult(String url) {
		Header header = new BasicHeader("Ok-Access-Key", Constants.OKLINE_API_KEY);
		String json = HttpClientUtil.httpGet(url, null, new Header[] { header }, 5000);
		Map<String, Object> map = JsonUtil.fromJson2Map(json);
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) map.get("data");
		return dataList;
	}

	private List<Map<String, Object>> getEthTransList(String address, String chainName) {
		String url = String.format("%saddress/transaction-list?chainShortName=%s&address=%s&limit=50", Constants.OKLINK_ENDPOINT, chainName, address);
		List<Map<String, Object>> dataList = getOkxApiResult(url);
		return (List<Map<String, Object>>) dataList.get(0).get("transactionLists");
	}
	
	@GetMapping("v1/eth-tx-record")
	public ResponseModel getEthTxRecordV1(String network, String address) throws IOException {
		String redisKey = "eth_" + network + "_" + address;
		String redisKeyLimit = redisKey + "_limit";
		List<Map<String, Object>> mapList;
		try {
			String valueLimit = this.redisTemplate.opsForValue().get(redisKeyLimit);
			if (StringUtils.isBlank(valueLimit)) {
				String prefix = getUrlNetworkPrefix(network);
				Document doc = Jsoup.connect("https://" + prefix + "etherscan.io/address/" + address + "?r=" + System.currentTimeMillis()).timeout(7000).get();
				Elements els = doc.select("#transactions table tbody tr");
				mapList = new ArrayList<>(els.size());
				for (Element el : els) {
					Elements tds = el.select("td");
					if (tds.size() > 10) {
						String txid = tds.get(1).select("div>a").text();
						String time = tds.get(5).select("span").attr("data-bs-title");
						String fromAddress = tds.get(7).select("div>*>span").attr("data-highlight-target");
						String directory = tds.get(8).select("span").text();
						String receiveAddress = tds.get(9).select("div>*>span").attr("data-highlight-target");
						
						String amount = tds.get(10).select("span").get(0).text();
						String gasFee = tds.get(11).text().replace(" ETH", "");
						Map<String, Object> map = new HashMap<>();
						map.put("txid", txid);
						map.put("time", time);
						map.put("fromAddress", fromAddress);
						map.put("directory", directory);
						map.put("receiveAddress", receiveAddress);
						map.put("amount", amount);
						map.put("gasFee", gasFee);
						Integer type = 0;
						if (tds.get(9).select("i.text-secondary").size() > 0) {
							// ERC-20转账
							String amountStr = amount.replace(" ETH", "");
							if (Double.parseDouble(amountStr) > 0) {
								type = 2;
							} else {
								type = 1;
								String key = "eth_tx_" + network + "_" + txid;
								String tokenValue = this.redisTemplate.opsForValue().get(key);
								Map<String, Object> tokenInfoMap = null;
								if (StringUtils.isBlank(tokenValue)) {
									doc = Jsoup.connect("https://" + prefix + "etherscan.io/tx/" + txid).timeout(7000).get();
									tokenInfoMap = new HashMap<>();
									String tokenAmount = doc.select("#ContentPlaceHolder1_maintable .row .d-inline-flex span.me-1").get(1).text();
									String tokenSymbol = doc.select("#ContentPlaceHolder1_maintable .row .d-inline-flex a[data-highlight-value]").get(2).select("span.text-muted span[data-bs-toggle='tooltip']").text();
									String tokenReceiveAddress = doc.select("#ContentPlaceHolder1_maintable .row .d-inline-flex a[data-highlight-value]:eq(1)").attr("data-highlight-target");
									tokenInfoMap.put("tokenAmount", tokenAmount);
									tokenInfoMap.put("tokenReceiveAddress", tokenReceiveAddress);
									tokenInfoMap.put("tokenSymbol", tokenSymbol);
									this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(tokenInfoMap));
								} else {
									tokenInfoMap = JsonUtil.fromJson2Map(tokenValue);
								}
								map.put("tokenInfo", tokenInfoMap);
							}
						}
						map.put("type", type);
						mapList.add(map);
					}
				}
				this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(mapList));
				this.redisTemplate.opsForValue().set(redisKeyLimit, "A", 45, TimeUnit.SECONDS);
			} else {
				mapList = this.getCacheEthTxRecord(redisKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
			mapList = this.getCacheEthTxRecord(redisKey);
		}
		return ResponseModel.sucessData(mapList);
	}

	private List<Map<String, Object>> getCacheEthTxRecord(String redisKey) {
		List<Map<String, Object>> mapList;
		String value = this.redisTemplate.opsForValue().get(redisKey);
		mapList = JsonUtil.fromJson(value, new TypeReference<List<Map<String, Object>>>() {});
		return mapList;
	}
	
	@RequestMapping("token-list")
	public ResponseModel getEthTokenBalanceList(String network, String address) throws IOException {
		String key = "eth_token_" + network + "_" + address;
		String keyLimit = "eth_token_" + network + "_" + address + "_limit";
		List<TokenInfo> list = null;
		try {
			String valueLimit = this.redisTemplate.opsForValue().get(keyLimit);
			if (StringUtils.isBlank(valueLimit)) {
				String chainName = "ETH";
				if (StringUtils.equals(network, "testnet")) {
					chainName = "SEPOLIA_TESTNET";
				}
				list = new ArrayList<>();
				String url = String.format("%saddress/token-balance?chainShortName=%s&address=%s&protocolType=token_20", Constants.OKLINK_ENDPOINT, chainName, address);
				List<Map<String, Object>> dataList = getOkxApiResult(url);
				List<Map<String, Object>> tokenList = (List<Map<String, Object>>) dataList.get(0).get("tokenList");
				for (Map<String, Object> tokenMap : tokenList) {
					String amount = tokenMap.get("holdingAmount").toString();
					String contractAddress = tokenMap.get("tokenContractAddress").toString();
					TokenInfo ti = this.tokenInfoMapper.selectByPrimaryKey(contractAddress);
					if (ti == null) {
						ti = new TokenInfo();
						ti.setContractAddress(contractAddress);
						ti.setImg("/token/empty-token.png");
						ti.setNetwork(network);
					}
					ti.setSymbol(tokenMap.get("symbol").toString());
					ti.setPriceUsd(tokenMap.get("priceUsd").toString());
					ti.setValueUsd(tokenMap.get("valueUsd").toString());
					ti.setAmount(amount);
					list.add(ti);
				}
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(list));
				this.redisTemplate.opsForValue().set(keyLimit, "A", 45, TimeUnit.SECONDS);
			} else {
				list = getCacheTokenList(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = getCacheTokenList(key);
		}
		return ResponseModel.sucessData(list);
	}
	
	@RequestMapping("v1/token-list")
	public ResponseModel getEthTokenBalanceListV1(String network, String address) throws IOException {
		String prefix = getUrlNetworkPrefix(network);
		String key = "eth_token_" + network + "_" + address;
		String keyLimit = "eth_token_" + network + "_" + address + "_limit";
		List<TokenInfo> list = null;
		try {
			String valueLimit = this.redisTemplate.opsForValue().get(keyLimit);
			if (StringUtils.isBlank(valueLimit)) {
				list = new ArrayList<>();
				Document doc = Jsoup.connect("https://" + prefix + "etherscan.io/address/" + address).timeout(7000).get();
				Elements eles = doc.select("#availableBalance ul li");
				for (int i=1; i<eles.size(); i++) {
					Element el = eles.get(i);
					String str = el.select("a").attr("href");
					String amountStr = el.select("a span.text-muted").text();
					String amount = amountStr.split(" ")[0];
					String contractAddress = str.substring(7, str.indexOf("?"));
					TokenInfo ti = this.tokenInfoMapper.selectByPrimaryKey(contractAddress);
					if (ti == null) {
						ti = new TokenInfo();
						ti.setContractAddress(contractAddress);
						ti.setImg("/token/empty-token.png");
						ti.setNetwork(network);
						ti.setSymbol(amountStr.split(" ")[1]);
					}
					ti.setAmount(amount);
					list.add(ti);
				}
				this.redisTemplate.opsForValue().set(key, JsonUtil.toJson(list));
				this.redisTemplate.opsForValue().set(keyLimit, "A", 45, TimeUnit.SECONDS);
			} else {
				list = getCacheTokenList(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
			list = getCacheTokenList(key);
		}
		return ResponseModel.sucessData(list);
	}

	private List<TokenInfo> getCacheTokenList(String key) {
		List<TokenInfo> list;
		String value = this.redisTemplate.opsForValue().get(key);
		list = JsonUtil.fromJsonList(value, TokenInfo.class);
		return list;
	}
	

	@GetMapping("abi-get")
	public ResponseModel getAbi(String network, String contractAddress) throws IOException {
		String key = "eth_abi_" + network + "_" + contractAddress;
		String value = this.redisTemplate.opsForValue().get(key);
		List<Map> mapList = null;
		if (StringUtils.isBlank(value)) {
			String prefix = getUrlNetworkPrefix(network);
			Document doc = Jsoup.connect("https://" + prefix + "etherscan.io/address/" + contractAddress + "?r=" + System.currentTimeMillis()).timeout(7000).get();
			String abi = doc.select("#dividcode .mb-4:nth-child(2) pre").text();
			this.redisTemplate.opsForValue().set(key, abi);
			mapList = JsonUtil.fromJsonList(abi, Map.class);
		} else {
			mapList = JsonUtil.fromJsonList(value, Map.class);
		}
		return ResponseModel.sucessData(mapList);
	}
	
	@GetMapping("eth-balance")
	public ResponseModel getEthBalance(String network, String address) {
		String balance = null;
		String key = "eth_balance_" + network + "_" + address;
		String value = this.redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(value)) {
			String chainName = "ETH";
			if (StringUtils.equals(network, "testnet")) {
				chainName = "SEPOLIA_TESTNET";
			}
			String url = String.format("%saddress/address-summary?chainShortName=%s&address=%s", Constants.OKLINK_ENDPOINT, chainName, address);
			Header header = new BasicHeader("Ok-Access-Key", Constants.OKLINE_API_KEY);
			String json = HttpClientUtil.httpGet(url, null, new Header[] { header }, 5000);
			Map<String, Object> map = JsonUtil.fromJson2Map(json);
			List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get("data");
			balance = (String) mapList.get(0).get("balance");
			this.redisTemplate.opsForValue().set(key, balance, 30, TimeUnit.SECONDS);
		} else {
			balance = value;
		}
		return ResponseModel.sucessData(balance);
	}

	private String getUrlNetworkPrefix(String network) {
		String prefix = null;
		if (StringUtils.equals(network, "mainnet")) {
			prefix = "";
		} else {
			prefix = "sepolia.";
		}
		return prefix;
	}

//	@GetMapping("set")
	public ResponseModel set() throws Exception {
		String url = "https://etherscan.io/token/%s";
		List<TokenInfo> list = this.tokenInfoMapper.selectAll();
		for (TokenInfo ti : list) {
			if (StringUtils.isNotBlank(ti.getTotalSupply()) && ti.getDecimals() != null) {
				continue;
			}
			TokenInfo u = new TokenInfo();
			u.setContractAddress(ti.getContractAddress());
			Document doc = null;
			try {
				doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
			} catch (Exception e) {
				try {
					Thread.sleep(3000l);
					doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
				} catch (Exception e1) {
					try {
						Thread.sleep(3000l);
						doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
					} catch (Exception e2) {
						Thread.sleep(3000l);
						doc = Jsoup.connect(String.format(url, ti.getContractAddress())).get();
					}
				}
			}
			try {
				String decimals = doc.select("#ContentPlaceHolder1_divSummary .g-3>div:eq(2) .card-body h4 b").text();
				u.setDecimals(Integer.parseInt(decimals));
				String totalSupply = doc
						.select("#ContentPlaceHolder1_divSummary .g-3>div:eq(0) .card-body div.align-items-center>span")
						.text();
				u.setTotalSupply(totalSupply);
				try {
					this.tokenInfoMapper.updateByPrimaryKeySelective(u);
				} catch (Exception e) {
					e.printStackTrace();
					this.tokenInfoMapper.updateByPrimaryKeySelective(u);
				}
			} catch (Exception e) {
				System.out.println(JsonUtil.toJson(ti));
			}
		}
		return ResponseModel.sucess();

	}

}
