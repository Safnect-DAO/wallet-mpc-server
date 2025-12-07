package com.safnect.wallet.mpc.init;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.safnect.wallet.mpc.config.chain.ChainApiConfig;
import com.safnect.wallet.mpc.config.chain.TronApiConfig;
import com.safnect.wallet.mpc.dto.TxToConfirm;
import com.safnect.wallet.mpc.mapper.TransactionFailureMapper;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.model.TransactionFailure;
import com.safnect.wallet.mpc.service.EvmApiService;
import com.safnect.wallet.mpc.service.OklinkService;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;

@Component
public class TxConfirmQueueComponent {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	EvmApiService evmApiService;
	
	@Autowired
	TransactionMapper transactionMapper;
	
	@Autowired
	TransactionFailureMapper transactionFailureMapper;
	
	@Autowired
	OklinkService oklinkService;
	
	@PostConstruct
	@SuppressWarnings("unchecked")
	public void start() {
		new Thread() {
			public void run() {
				while (true) {
					try {
						Date now = new Date();
						String json = redisTemplate.opsForList().rightPop(Constants.UNCONFIRMED_TXS_LIST, 20, TimeUnit.SECONDS);
						if (StringUtils.isNotBlank(json)) {
							TxToConfirm tt = JsonUtil.fromJson(json, TxToConfirm.class);
							boolean confirmed = false;
							boolean successed = true;
							String exInfo = null;
							String chain = tt.getChain();
							if (StringUtils.equals(chain, "Fractal Bitcoin")) { // FB
								String endpoint = Constants.Unisat.FB_TEST_ENDPOINT_PREFIX;
								Header[] headerArr = Constants.Unisat.FB_TESTNET_HEADER_ARR;
								if (StringUtils.equals(tt.getNetwork(), Constants.NETWORK_MAINNET)) {
									endpoint = Constants.Unisat.FB_MAIN_ENDPOINT_PREFIX;
									headerArr = Constants.Unisat.FB_MAINNET_HEADER_ARR;
								}
								String result = HttpClientUtil.httpGet(String.format("%s/v1/indexer/tx/%s", endpoint, tt.getTxid()), null, headerArr, 7000);
								Map<String, Object> map = JsonUtil.fromJson2Map(result);
								Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
								Integer confirmations = MapUtils.getInteger(dataMap, "confirmations");
								if (confirmations != null && confirmations > 0) {
									// tx confirmed
									confirmed = true;
								}
							} else if (StringUtils.equals(chain, "Bitcoin")) { // Bitcoin
								if (StringUtils.equals(tt.getNetwork(), Constants.NETWORK_MAINNET)) {
									String endpoint = Constants.Unisat.BTC_MAIN_ENDPOINT_PREFIX;
									Header[] headerArr = Constants.Unisat.BTC_MAINNET_HEADER_ARR;
									String result = HttpClientUtil.httpGet(String.format("%s/v1/indexer/tx/%s", endpoint, tt.getTxid()), null, headerArr, 7000);
									Map<String, Object> map = JsonUtil.fromJson2Map(result);
									Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
									Map<String, Object> detailMap = (Map<String, Object>) dataMap.get("detail");
									Integer confirmations = MapUtils.getInteger(detailMap, "confirmations");
									if (confirmations != null && confirmations > 0) {
										// tx confirmed
										confirmed = true;
									}
								} else { // bitcoin testnet4
									String result = HttpClientUtil.httpGet(String.format("%s/testnet4/api/tx/%s", Constants.BITCOIN_MEMPOOL_SPACE, tt.getTxid()), null);
									Map<String, Object> map = JsonUtil.fromJson2Map(result);
									Map<String, Object> statusMap = (Map<String, Object>) map.get("status");
									Boolean confirmedValue = MapUtils.getBoolean(statusMap, "confirmed");
									if (confirmedValue != null && confirmedValue) {
										// tx confirmed
										confirmed = true;
									}
								}
								
							} else if (StringUtils.equals(chain, "Tron")) { // Ethereum
								String apiUrl = ChainApiConfig.getApiConfig(TronApiConfig.class).getEndpoint(tt.getNetwork());
								Map<String, Object> paramMap = new HashMap<>();
								paramMap.put("value", tt.getTxid());
								String result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, apiUrl + "/walletsolidity/gettransactioninfobyid", JsonUtil.toJson(paramMap));
								Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
								String id = MapUtils.getString(resultMap, "id");
								String resultStatus = MapUtils.getString(resultMap, "result");
								if (StringUtils.isNotBlank(id)) {
									// tx confirmed
									confirmed = true;
								}
								if (StringUtils.equals(resultStatus, "FAILED")) { // transfer failed
									successed = false;
									Map<String, Object> receiptMap = (Map<String, Object>) resultMap.get("receipt");
									exInfo = receiptMap.get("result").toString();
								}
							} else if (StringUtils.equals(chain, "LITECOIN")) { // LTC
								String result = HttpClientUtil.httpGet(String.format("%s/api/tx/%s", Constants.LITECOIN_MEMPOOL_SPACE, tt.getTxid()), null);
								Map<String, Object> map = JsonUtil.fromJson2Map(result);
								Map<String, Object> statusMap = (Map<String, Object>) map.get("status");
								Boolean confirmedValue = MapUtils.getBoolean(statusMap, "confirmed");
								if (confirmedValue != null && confirmedValue) {
									// tx confirmed
									confirmed = true;
								}
							} else if (StringUtils.equals(chain, "DOGE")) {
								Map<String, Object> paramMap = new HashMap<>();
								paramMap.put("jsonrpc", "2.0");
								paramMap.put("method", "getrawtransaction");
								paramMap.put("id", 1);
								List<Object> list = new ArrayList<>();
								list.add(tt.getTxid());
								list.add(true);
								paramMap.put("params", list);
								String result = HttpClientUtil.httpPost(Constants.TATUM_DOGECOIN_RPC, JsonUtil.toJson(paramMap), 
										7000, new Header[] { new BasicHeader("x-api-key", Constants.TATUM_APIKEY) });
								Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
								Map<String, Object> resultDataMap = (Map<String, Object>) resultMap.get("result");
								Long confirmations = MapUtils.getLong(resultDataMap, "confirmations");
								if (confirmations != null && confirmations > 0) {
									confirmed = true;
								} 
							} else if (StringUtils.equals(chain, "BCH")) {
								String result = HttpClientUtil.httpGet("https://api.fullstack.cash/v5/electrumx/tx/data/" + tt.getTxid(), null);
								Map<String, Object> resultMap = JsonUtil.fromJson2Map(result);
								Map<String, Object> detailsMap = (Map<String, Object>) resultMap.get("details");
								Long confirmations = MapUtils.getLong(detailsMap, "confirmations");
								if (confirmations != null && confirmations > 0) {
									confirmed = true;
								} 
							} else if (StringUtils.equals(chain, "BSV")) {
								String result = HttpClientUtil.httpGet("https://api.bitails.io/tx/" + tt.getTxid(), null);
								Map<String, Object> map = JsonUtil.fromJson2Map(result);
								Long confirmations = MapUtils.getLong(map, "confirmations");
								if (confirmations != null && confirmations > 0) {
									confirmed = true;
								}
							} else if (StringUtils.equals(chain, "Cardano")) { // Cardano链超过2分钟自动确认
								Transaction existsTrans = transactionMapper.selectByPrimaryKey(tt.getTransId());
								if (existsTrans != null && now.getTime() - existsTrans.getSendTime().getTime() > 120000) { 
									confirmed = true;
								}
							} else if (StringUtils.equals(chain, "OPCAT Layer")) {
								String url = String.format(Constants.OPCAT_LAYER_TX_STATUS_ENDPOINT, tt.getTxid());
								String result = HttpClientUtil.httpGet(url, null);
								Map<String, Object> map = JsonUtil.fromJson2Map(result);
								Map<String, Object> dataMap = (Map<String, Object>) map.get("data");
								Boolean confirmedValue = MapUtils.getBoolean(dataMap, "confirmed");
								if (confirmedValue != null && confirmedValue) {
									confirmed = true;
								}
							}
							
							if (confirmed) { // 已确认，更新交易状态
								Transaction transaction = new Transaction();
								transaction.setId(tt.getTransId());
								transaction.setConfirmed(true);
								transaction.setSuccessed(successed);
								transactionMapper.updateByPrimaryKeySelective(transaction);
								
								if (!successed) {
									TransactionFailure tf = new TransactionFailure(tt.getTransId(), exInfo);
									transactionFailureMapper.insertSelective(tf);
								}
								
							} else { // 未确认，重新加入队列查询结果
								Timer timer = new Timer();
						        TimerTask task = new TimerTask() {
						            @Override
						            public void run() {
						                redisTemplate.opsForList().leftPush(Constants.UNCONFIRMED_TXS_LIST, JsonUtil.toJson(tt));
						            }
						        };
						        timer.schedule(task, 10000);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}
