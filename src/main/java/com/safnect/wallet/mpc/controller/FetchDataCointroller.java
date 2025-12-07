package com.safnect.wallet.mpc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.config.chain.EthApiConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.TokenInfoMapper;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.model.BaseTransaction;
import com.safnect.wallet.mpc.model.TokenInfo;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.service.EvmApiService;
import com.safnect.wallet.mpc.util.BcUtils;
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
	
	@Autowired
	TransactionMapper transactionMapper;
	
	static final String DATE_FORMAT = "yyyy-MM-dd H:mm:ss";
	
	static final String FB_TEST_API_ENDPOINT = "https://open-api-fractal-testnet.unisat.io";
	
	static final String FB_MAIN_API_ENDPOINT = "https://open-api-fractal.unisat.io";
	
	static final String TESTNET_FB_API_KEY = "c6b667f7f10282215c1eb8034261759796d47039bf5e589859c2ff4f8b36532f";
	
	static final String MAINNET_FB_API_KEY = "d7a197808d86c252d944a7b3e1b84dc93c6e82a47ec37192b452a6acedfc41ee";
	
	@Value("${cat20.server}")
	String cat20RPCEndpoing;
	
	static final String CAT20_V2_RPC_ENDPOING = "https://tracker2-fractal-mainnet.catprotocol.org/api";
	
	Double bitcoinMap;
	
	public static final String priceRedisKey = "gate_price_list";
	public static final String fbpriceRedisKey = "fb_price";
	
	final String cat20RedisKey = "cat20_price";
	
	@Autowired
	EvmApiService evmApiService;
	
	@PostConstruct
	public void init() {
		Timer timer1 = new Timer();
		
		TimerTask tt0 = new TimerTask() {
			
			@Override
			public void run() {
				try {
					Map priceMap = null;
					String value = redisTemplate.opsForValue().get(priceRedisKey);
					if (StringUtils.isBlank(value)) {
						priceMap = new HashMap<>();
					} else {
						priceMap = JsonUtil.fromJson2Map(value);
					}
					String result = HttpClientUtil.httpGet("https://data.gateapi.io/api2/1/tickers", null);
					Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
					Set<String> keySet = resultMap.keySet();
					for (String key : keySet) {
						if (key.endsWith("_usdt")) {
							String arr[] = key.split("_");
							priceMap.put(arr[0], getGateioPrice(resultMap, key));
						}
					}
					priceMap.put("bitcoin", getGateioPrice(resultMap, "btc_usdt"));
					priceMap.put("ethereum", getGateioPrice(resultMap, "eth_usdt"));
					priceMap.put("bellcoin", getGateioPrice(resultMap, "bells_usdt"));
					Map<String, Object> fbMap = getGateioPrice(resultMap, "fb_usdt");
					priceMap.put("fb", fbMap);
					redisTemplate.opsForValue().set(priceRedisKey, JsonUtil.toJson(priceMap));
					redisTemplate.opsForValue().set(fbpriceRedisKey, fbMap.get("usd").toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("-----------tt0 task executed.");
			}
		};
		timer1.schedule(tt0, 0, 5000);
		
		Timer timer2 = new Timer();
		TimerTask tt1 = new TimerTask() {
			
			@Override
			public void run() {
				try {
					String value = redisTemplate.opsForValue().get(priceRedisKey);
					if (StringUtils.isNotBlank(value)) {
						Map map = JsonUtil.fromJson2Map(value);
						Map<String, Object> bitcoinMap = (Map<String, Object>) map.get("bitcoin");
						Double btcPrice = MapUtils.getDouble(bitcoinMap, "usd");
						
						Map<String, Object> priceMap = new HashMap<>();
						// 45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0 cat
						String tokenId1 = "45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0";
						String result = HttpClientUtil.httpGet("https://www.coinex.com/res/quotes/real-time/assets?assets=OPCAT", null);
						Map<String, Object> catDataMap = JsonUtil.fromJson2Map(result);
						Map<String, Object> dataMap = MapUtils.getMap(catDataMap, "data");
						Map<String, Object> FBCATMap = MapUtils.getMap(dataMap, "OPCAT");
						Map<String, Object> cat20Map = new HashMap<>();
						cat20Map.put("usd", Double.parseDouble(MapUtils.getString(FBCATMap, "price_usd")));
						cat20Map.put("usd_24h_change", Double.parseDouble(MapUtils.getString(FBCATMap, "change_rate")) * 100);
						priceMap.put(tokenId1, cat20Map);
						priceMap.put("cat", cat20Map);
						// 暂时取Unisat market价格
//						String tokenId2 = "cc1b4c7e844c8a7163e0fccb79a9ade20b0793a2e86647825b7c05e8002b9f6a_0";
//						Map<String, Object> bullMap = getCatPrice(btcPrice, "FRACTAL BULL", tokenId2);
//						priceMap.put(tokenId2, bullMap);
//						
//						String tokenId3 = "59d566844f434e419bf5b21b5c601745fcaaa24482b8d68f32b2582c61a95af2_0";
//						Map<String, Object> cat20_pizza = getCatPrice(btcPrice, "cat20_pizza", tokenId3);
//						priceMap.put(tokenId3, cat20_pizza);
//						
//						String tokenId4 = "a004b19b6e52aba25546360acc11e8b650c2387a975c9f48ff74a8bd5f6c32e7_0";
//						Map<String, Object> EatTheCat = getCatPrice(btcPrice, "EatTheCat", tokenId4);
//						priceMap.put(tokenId4, EatTheCat);
//						
//						String tokenId5 = "c468e99ac3b533e503eac5ccf4f0e3362772f80cead8b7f71d802305d02f73d0_0";
//						Map<String, Object> Kibble = getCatPrice(btcPrice, "Kibble", tokenId5);
//						priceMap.put(tokenId5, Kibble);
//						
//						String tokenId6 = "34475c0c600acf665737ef4c8d97bade02e9c5472bcfc0be141184e244d7daaf_0";
//						Map<String, Object> KittyCash = getCatPrice(btcPrice, "KittyCash", tokenId6);
//						priceMap.put(tokenId6, KittyCash);
						
						Double fbPrice = getFbPrice();
						if (fbPrice != null) {
							String json = HttpClientUtil.httpGet("https://fractal-api.unisat.io/cat20-dex-v1/getMarketStats?sortField=volume24h&offset=0&limit=81", null);
							Map<String, Object> unisatMap = JsonUtil.fromJson2Map(json);
							Map<String, Object> unisatDataMap = (Map<String, Object>) unisatMap.get("data");
							List<Map<String, Object>> satsList = (List<Map<String, Object>>) unisatDataMap.get("stats");
							for (Map<String, Object> tokenMap : satsList) {
								String tokenId = MapUtils.getString(tokenMap, "tokenId");
								if (!StringUtils.equals(tokenId, "45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0")) {
									// 以上几种Token还是取dotswap数据
									Double tokenPrice = MapUtils.getDouble(tokenMap, "price");
									Double tokenPrice24h = MapUtils.getDouble(tokenMap, "price24h");
									Map<String, Object> cat20TokenMap = new HashMap<>();
									cat20TokenMap.put("usd", tokenPrice * fbPrice / 1000000);
									Double quoteChange24 = 0d;
									if (tokenPrice24h > 0) {
										quoteChange24 = (tokenPrice - tokenPrice24h) / tokenPrice24h;
									}
									cat20TokenMap.put("usd_24h_change", quoteChange24 * 100);
									priceMap.put(tokenId, cat20TokenMap);
								}
							}
						}
						
						redisTemplate.opsForValue().set(cat20RedisKey, JsonUtil.toJson(priceMap));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("-----------tt1 task executed.");
			}
		};
		timer2.schedule(tt1, 2000, 1000 * 60 * 2);
	}
	
	public static Map<String, Object> getGateioPrice(Map<String, Object> resultMap, String key) {
		Map<String, Object> map1 = (Map<String, Object>) resultMap.get(key);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("usd", MapUtils.getDouble(map1, "last"));
		map2.put("usd_24h_change", MapUtils.getDouble(map1, "percentChange"));
		return map2;
	}
	
	private Double getFbPrice() {
		String fbprice = this.redisTemplate.opsForValue().get(fbpriceRedisKey);
		if (StringUtils.isNotBlank(fbprice)) {
			return Double.parseDouble(fbprice);
		}
		return null;
	}
	
	@GetMapping("v2/price")
	public ResponseModel priceV2(String chainNames) throws IOException {
		Map<String, Object> priceMap = null;
		String value = this.redisTemplate.opsForValue().get(priceRedisKey);
		if (StringUtils.isNotBlank(value)) {
			priceMap = JsonUtil.fromJson2Map(value);
			value = this.redisTemplate.opsForValue().get(this.cat20RedisKey);
			priceMap.putAll(JsonUtil.fromJson2Map(value));
			Map<String, Object> usdtMap = new HashMap<>();
			usdtMap.put("usd", 1.0);
			usdtMap.put("usd_24h_change", 0.0);
			priceMap.put("usdt", usdtMap);
		}
		return ResponseModel.successData(priceMap);
	}
	
	@GetMapping("price")
	public ResponseModel price(String chainNames) throws IOException {
		String redisKey = "coingeckov1_price_list";
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
			
			priceMap = JsonUtil.fromJson2Map(result);
			result = HttpClientUtil.httpGet("https://www.gate.io/apiw/v2/market/tickers/FB_USDT", null);
			Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
			Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
			String rate = (String) dataMap.get("rate");
			String change = (String) dataMap.get("change");
			Map<String, Object> fbMap = new HashMap<>();
			fbMap.put("usd", Double.parseDouble(rate));
			fbMap.put("usd_24h_change", Double.parseDouble(change));
			priceMap.put("fb", fbMap);
			this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(priceMap), 5, TimeUnit.SECONDS);
		} else {
			priceMap = JsonUtil.fromJson2Map(value);
		}
		// 添加USDT行情 "usd-coin":{"usd":1.0,"usd_24h_change":0.001743185807381547}
		Map<String, Object> usdtMap = new HashMap<>();
		usdtMap.put("usd", 1.0);
		usdtMap.put("usd_24h_change", 0.0);
		priceMap.put("usdt", usdtMap);
		Map<String, Object> bitcoinMap = (Map<String, Object>) priceMap.get("bitcoin");
		Map<String, Object> catMap = this.getCatPrice(MapUtils.getDouble(bitcoinMap, "usd"));
		priceMap.put("cat", catMap);
		return ResponseModel.successData(priceMap);
	}
	
	private Map<String, Object> getCatPrice(Double btcPrice) {
		String redisKey = "quote_fb_cat";
		String value = this.redisTemplate.opsForValue().get(redisKey);
		Map<String, Object> catMap = null;
		if (StringUtils.isBlank(value)) {
			String result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, "https://api.swap.dotwallet.com/brc20swap/get_cat20_tick", "{\"tick\": \"cat:45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0\",\"coin_type\": \"cat20\"}");
			Map<String, Object> map = JsonUtil.fromJson2Map(result);
			Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
			String priceStr = dataMap.get("price").toString();
			Double price = Double.parseDouble(priceStr);
			Double catPrice = price / 100000000 * btcPrice;
			
			// 获取24小时涨跌
			result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, "https://api.swap.dotwallet.com/brc20swap/stat/trade_info_detail", "{\"tick1\": \"FB\",\"coin_type_1\":\"fractal\",\"tick2\":\"cat:45ee725c2c5993b3e4d308842d87e973bf1951f5f7a804b21e4dd964ecd12d6b_0\",\"coin_type_2\":\"cat20\"}");
			map = JsonUtil.fromJson2Map(result);
			dataMap = (Map<String, Object>) map.get("data");
			// 0.150717126864395
			Double quoteChange24 = MapUtils.getDouble(dataMap, "quote_change_24h");
			catMap = new HashMap<>();
			catMap.put("usd", catPrice);
			catMap.put("usd_24h_change", quoteChange24 * 100);
			this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(catMap), 1, TimeUnit.MINUTES);
		} else {
			catMap = JsonUtil.fromJson2Map(value);
		}
		return catMap;
	}
	
	private Map<String, Object> getCatPrice(Double btcPrice, String tick, String tokenId) {
		
		String params = String.format("{\"tick\": \"%s:%s\",\"coin_type\": \"cat20\"}", tick, tokenId);
		String result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, "https://api.swap.dotwallet.com/brc20swap/get_cat20_tick", params);
		Map<String, Object> map = JsonUtil.fromJson2Map(result);
		Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
		String priceStr = dataMap.get("price").toString();
		Double price = Double.parseDouble(priceStr);
		Double catPrice = price / 100000000 * btcPrice;
		
		// 获取24小时涨跌
		params = String.format("{\"tick1\": \"FB\",\"coin_type_1\":\"fractal\",\"tick2\":\"%s:%s\",\"coin_type_2\":\"cat20\"}", tick, tokenId);
		result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, "https://api.swap.dotwallet.com/brc20swap/stat/trade_info_detail", params);
		map = JsonUtil.fromJson2Map(result);
		dataMap = (Map<String, Object>) map.get("data");
		// 0.150717126864395
		Double quoteChange24 = MapUtils.getDouble(dataMap, "quote_change_24h");
		Map<String, Object> cat20Map = new HashMap<>();
		cat20Map.put("usd", catPrice);
		cat20Map.put("usd_24h_change", quoteChange24 * 100);
		return cat20Map;
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
		return ResponseModel.successData(resultList);
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
		return ResponseModel.successData(mapList);
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
		return ResponseModel.successData(list);
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
		return ResponseModel.successData(list);
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
		return ResponseModel.successData(mapList);
	}
	
	@GetMapping("eth-balance")
	public ResponseModel getEthBalance(String network, String address) {
		String balance = null;
		String key = "eth_balance_" + network + "_" + address;
		String value = this.redisTemplate.opsForValue().get(key);
		if (StringUtils.isBlank(value)) {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("module", "account");
			paramMap.put("action", "balance");
			paramMap.put("address", address);
			paramMap.put("tag", "latest");
			ResponseModel rm = this.evmApiService.executeGet(network, paramMap, EthApiConfig.class);
			Map<String, Object> resultMap = (Map<String, Object>) rm.getData();
			String result = resultMap.get("result").toString();
			BcUtils.toEther(Long.parseLong(result));
			this.redisTemplate.opsForValue().set(key, balance, 30, TimeUnit.SECONDS);
		} else {
			balance = value;
		}
		return ResponseModel.successData(balance);
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
	
	@GetMapping("cat20-billlist")
	public ResponseModel getCat20BillList(String network, String address, Integer start, Integer limit) {
		if (start == null) {
			start = 0;
		}
		if (limit == null) {
			limit = 10;
		}
		List<Transaction> list = this.transactionMapper.getTrans(network, "CAT", address, start, limit);
		List<BaseTransaction> btList = resetDataList(network, address, list);
		return ResponseModel.successData(btList);
	}

	private List<BaseTransaction> resetDataList(String network, String address, List<Transaction> list) {
		List<BaseTransaction> btList = new ArrayList<>();
		String apiKey = MAINNET_FB_API_KEY;
		String endpointPrefix = FB_MAIN_API_ENDPOINT;
		if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
			apiKey = TESTNET_FB_API_KEY;
			endpointPrefix = FB_TEST_API_ENDPOINT;
		}
		Header[] headerArr = new Header[] { new BasicHeader("Authorization", "Bearer " + apiKey) };
		for (int i=0; i<list.size(); i++) {
			Transaction da = list.get(i);
			try {
				da.put(address);
				String txid = da.getTxid();
				String redisKey = network + "_CAT20_confirmed_" + txid;
				String value = this.redisTemplate.opsForValue().get(redisKey);
				Integer confirmations = null;
				if (StringUtils.isBlank(value)) {
					String result = HttpClientUtil.httpGet(String.format("%s/v1/indexer/tx/%s", endpointPrefix, da.getTxid()), null, headerArr, 5000);
					Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
					Map<String, Object> dataMap = (Map<String, Object>) resultMap.get("data");
					confirmations = MapUtils.getInteger(dataMap, "confirmations");
					if (confirmations != null && confirmations > 0) {
						da.setConfirmed(true);
						this.redisTemplate.opsForValue().set(redisKey, confirmations.toString());
					} else {
						da.setConfirmed(false);
					}
				} else {
					da.setConfirmed(true);
				}
				btList.add(new BaseTransaction(da));
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		return btList;
	}
	
	@GetMapping("cat721-billlist")
	public ResponseModel getCat721BillList(String network, String address, Integer start, Integer limit) {
		if (start == null) {
			start = 0;
		}
		if (limit == null) {
			limit = 10;
		}
		List<Transaction> list = this.transactionMapper.getCat721Trans(network, address, start, limit);
		List<BaseTransaction> btList = resetDataList(network, address, list);
		return ResponseModel.successData(btList);
	}
	
	/**
	 * FB的cat20协议余额列表
	 * @param network
	 * @param address
	 * @return
	 */
	@GetMapping("cat20-balances")
	public ResponseModel cat20(String network, String address) {
		List<Map<String, Object>> baList = null;
		String redisKey0 = network + "_CAT20_balances_" + address;
		String value0 = this.redisTemplate.opsForValue().get(redisKey0);
		if (StringUtils.isBlank(value0)) {
			String json = HttpClientUtil.httpGet(String.format("%s/addresses/%s/balances", this.cat20RPCEndpoing, address), null);
			Map<String, Object> map = JsonUtil.fromJson2Map(json);
			Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
			baList = (List<Map<String, Object>>) dataMap.get("balances");
			baList.forEach(da -> {
				String tokenId = MapUtils.getString(da, "tokenId");
				Map<String, Object> tokenMap = null;
				String redisKey = network + "_CAT20_tokeninfo_" + tokenId;
				String value = this.redisTemplate.opsForValue().get(redisKey);
				if (StringUtils.isBlank(value)) {
					String tokenJson = HttpClientUtil.httpGet(String.format("%s/tokens/%s", this.cat20RPCEndpoing, tokenId), null);
					Map<String, Object> resultMap = JsonUtil.fromJson2Map(tokenJson);
					tokenMap = (Map<String, Object>) resultMap.get("data");
					this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(tokenMap));
				} else {
					tokenMap = JsonUtil.fromJson2Map(value);
				}
				da.put("minterAddr", MapUtils.getObject(tokenMap, "minterAddr"));
				da.put("tokenAddr", MapUtils.getObject(tokenMap, "tokenAddr"));
				da.put("name", MapUtils.getObject(tokenMap, "name"));
				da.put("symbol", MapUtils.getObject(tokenMap, "symbol"));
				da.put("decimals", MapUtils.getObject(tokenMap, "decimals"));
			});
			this.redisTemplate.opsForValue().set(redisKey0, JsonUtil.toJson(baList), 30, TimeUnit.SECONDS);
		} else {
			baList = JsonUtil.fromJson(value0, new TypeReference<List<Map<String, Object>>>() {});
		}
		baList.forEach(daMap -> {
			String tokenId = MapUtils.getString(daMap, "tokenId");
			if (StringUtils.equals(tokenId, Constants.TOKEN_ID_OPCAT)) {
				daMap.put("name", "opcat");
				daMap.put("symbol", "OPCAT");
			}
		});
		return ResponseModel.successData(baList);
	}
	
	@GetMapping("cat20v2-balances")
	public ResponseModel cat20V2(String network, String address) {
		List<Map<String, Object>> baList = null;
		String redisKey0 = network + "_CAT20V2_balances_" + address;
		String value0 = this.redisTemplate.opsForValue().get(redisKey0);
		if (StringUtils.isBlank(value0)) {
			String json = HttpClientUtil.httpGet(String.format("%s/addresses/%s/balances", CAT20_V2_RPC_ENDPOING, address), null);
			Map<String, Object> map = JsonUtil.fromJson2Map(json);
			Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
			baList = (List<Map<String, Object>>) dataMap.get("balances");
			baList.forEach(da -> {
				String tokenId = MapUtils.getString(da, "tokenId");
				Map<String, Object> tokenMap = null;
				String redisKey = network + "_CAT20V2_tokeninfo_" + tokenId;
				String value = this.redisTemplate.opsForValue().get(redisKey);
				if (StringUtils.isBlank(value)) {
					String tokenJson = HttpClientUtil.httpGet(String.format("%s/tokens/%s", CAT20_V2_RPC_ENDPOING, tokenId), null);
					Map<String, Object> resultMap = JsonUtil.fromJson2Map(tokenJson);
					tokenMap = (Map<String, Object>) resultMap.get("data");
					this.redisTemplate.opsForValue().set(redisKey, JsonUtil.toJson(tokenMap));
				} else {
					tokenMap = JsonUtil.fromJson2Map(value);
				}
				da.put("minterAddr", MapUtils.getObject(tokenMap, "minterAddr"));
				da.put("tokenAddr", MapUtils.getObject(tokenMap, "tokenAddr"));
				da.put("name", MapUtils.getObject(tokenMap, "name"));
				da.put("symbol", MapUtils.getObject(tokenMap, "symbol"));
				da.put("decimals", MapUtils.getObject(tokenMap, "decimals"));
			});
			this.redisTemplate.opsForValue().set(redisKey0, JsonUtil.toJson(baList), 30, TimeUnit.SECONDS);
		} else {
			baList = JsonUtil.fromJson(value0, new TypeReference<List<Map<String, Object>>>() {});
		}
		return ResponseModel.successData(baList);
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
		return ResponseModel.success();

	}
	
	@GetMapping("fb-fee-recomm")
	public Object fbFeeRecomm(String network) {
		Map<String, Object> resultMap = null;
		String key = network + "_FB_FEE_RECOMM";
		String keyPermanent = network + "_FB_FEE_RECOMM_permanent";
		String networkPrefix = "";
		if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
			networkPrefix = "-testnet";
		}
		try {
			String value = this.redisTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				String url = String.format(Constants.FRACTALBITCOIN_MEMPOOL_SPACE + "/api/v1/fees/recommended", networkPrefix);
				String result = HttpClientUtil.httpGet(url, null, 5000);
				resultMap = JsonUtil.fromJson2Map(result);
				Double fastestFee = MapUtils.getDouble(resultMap, "fastestFee");
				fastestFee += fastestFee * 0.2;
				resultMap.put("fastestFee", fastestFee.intValue());
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
	
	@GetMapping("btc-fee-recomm")
	public Object btcFeeRecomm(String network) {
		Map<String, Object> resultMap = null;
		String key = network + "_BTC_FEE_RECOMM";
		String keyPermanent = network + "_BTC_FEE_RECOMM_permanent";
		String networkPrefix = "";
		if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
			networkPrefix = "testnet4/";
		}
		try {
			String value = this.redisTemplate.opsForValue().get(key);
			if (StringUtils.isBlank(value)) {
				String url = String.format(Constants.BITCOIN_MEMPOOL_SPACE + "/%sapi/v1/fees/recommended", networkPrefix);
				String result = HttpClientUtil.httpGet(url, null, 5000);
				resultMap = JsonUtil.fromJson2Map(result);
				Double fastestFee = MapUtils.getDouble(resultMap, "fastestFee");
				fastestFee += fastestFee * 0.2;
				resultMap.put("fastestFee", fastestFee.intValue());
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
