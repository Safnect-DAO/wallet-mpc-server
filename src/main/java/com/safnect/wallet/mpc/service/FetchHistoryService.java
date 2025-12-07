package com.safnect.wallet.mpc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.config.chain.BscApiConfig;
import com.safnect.wallet.mpc.config.chain.ChainApiConfig;
import com.safnect.wallet.mpc.config.chain.EthApiConfig;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.TokenInfoMapper;
import com.safnect.wallet.mpc.mapper.TransactionMapper;
import com.safnect.wallet.mpc.model.TokenInfo;
import com.safnect.wallet.mpc.model.Transaction;
import com.safnect.wallet.mpc.util.BcUtils;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@Component
@SuppressWarnings("unchecked")
public class FetchHistoryService {
	
	@Autowired
	TransactionMapper transactionMapper;
	
	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	EvmApiService evmApiService;
	
	@Autowired
	TokenInfoMapper tokenInfoMapper;
	
	@Autowired
	OklinkService oklinkService;
	
	static final String TXS_LASTTIME_KEY_FORMAT = "ADDR_TX_LASTTIME_%s_%s_%s";
	
	static final Integer TIMEOUT = 5000;

	@Async
	public void asyncFetchHistory(String network, Map<String, Object> addressMap) {
		List<Map<String, Object>> btcAddressMap = null;
		List<Map<String, Object>> fbAddressMap = null;
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) { // mainnet
			btcAddressMap = (List<Map<String, Object>>) addressMap.get("bitcoin");
			fbAddressMap = (List<Map<String, Object>>) addressMap.get("fractalBitcoin");
		} else {
			// testnet
			btcAddressMap = (List<Map<String, Object>>) addressMap.get("bitcoinTestnet");
			fbAddressMap = (List<Map<String, Object>>) addressMap.get("fractalBitcoinTestnet");
		}
		
		this.asyncFetchBitcoinHistory(network, btcAddressMap);
		this.asyncFetchFractalBitcoinHistory(network, fbAddressMap);
		this.asyncFetchEthereumHistory(network, getAddress(addressMap, "ethereum"), "Ethereum", EthApiConfig.class, "ETH", "ETH", 1, null);
		this.asyncFetchEthereumHistory(network, getAddress(addressMap, "BSC"), "BSC", BscApiConfig.class, "BNB", "BSC", 56, null);
		this.asyncFetchTronHistory(network, getAddress(addressMap, "tron"));
		this.asyncFetchLtcHistory(network, getAddress(addressMap, "LTC"));
		this.fetchDogeTranslist(network, getAddress(addressMap, "DOGE"), "DOGE");
		this.asyncFetchChainHistory(network, getAddress(addressMap, "BCH"), "BCH");
		this.asyncFetchBsvHistory(network, getAddress(addressMap, "BSV"));
		this.asyncFetchXrpHistory(network, getAddress(addressMap, "XRP"));
		this.asyncFetchChainHistory(network, getAddress(addressMap, "SUI"), "SUI");
		this.asyncFetchChainHistory(network, getAddress(addressMap, "SOLANA"), "SOLANA");
		this.asyncFetchCardanoHistory(network, getAddress(addressMap, "CARDANO"));
		this.fetchAptosTranslist(network, getAddress(addressMap, "APTOS"), "APTOS");
		this.asyncFetchChainHistory(network, getAddress(addressMap, "COSMOS"), "COSMOS");
		this.asyncFetchChainHistory(network, getAddress(addressMap, "ETC"), "ETC");
		
		String evmAddress = getAddress(addressMap, "EVM");
		this.asyncFetchEthereumHistory(network, evmAddress, "OPTIMISM", null, "OP_ETH", "OPTIMISM", 10, "https://optimism.blockscout.com/api");
		this.asyncFetchEthereumHistory(network, evmAddress, "BASE", null, "BASE_ETH", "BASE", 8453, "https://base.blockscout.com/api");
		this.asyncFetchEthereumHistory(network, evmAddress, "POLYGON", null, "POL", "POLYGON", 137, "https://polygon.blockscout.com/api");
		this.asyncFetchEthereumHistory(network, evmAddress, "ARBITRUM_ONE", null, "ARB_ETH", "ARBITRUM_ONE", 42161, "https://arbitrum.blockscout.com/api");
		this.asyncFetchEthereumHistory(network, evmAddress, "FILECOIN", null, "FIL", "FILECOIN", 314, "https://filecoin.blockscout.com/api");
		this.asyncFetchEthereumHistory(network, evmAddress, "ZKSYNC", null, "ZK_ETH", "ZKSYNC", 324, "https://zksync.blockscout.com/api");
	}

	private String getAddress(Map<String, Object> addressMap, String code) {
		Map<String, Object> ethAddressMap = (Map<String, Object>) addressMap.get(code);
		String ethAddress = MapUtils.getString(ethAddressMap, "address");
		return ethAddress;
	}
	
	@Async
	public void asyncFetchBitcoinHistory(String network, List<Map<String, Object>> btcAddressMap) {
		try {
			for (Map<String, Object> addressMap : btcAddressMap) {
				String address = MapUtils.getString(addressMap, "address");
			
				String networkPrefix = "";
				if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
					networkPrefix = "/testnet4";
				}
				String url = String.format(Constants.BITCOIN_MEMPOOL_SPACE + "%s/api/address/%s/txs", networkPrefix, address);
				fetchMempoolTxs(network, address, url, "Bitcoin", "BTC");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Async
	public void asyncFetchFractalBitcoinHistory(String network, List<Map<String, Object>> btcAddressMap) {
		try {
			for (Map<String, Object> addressMap : btcAddressMap) {
				String address = MapUtils.getString(addressMap, "address");
			
				String networkPrefix = "";
				if (StringUtils.equals(network, Constants.NETWORK_TESTNET)) {
					networkPrefix = "-testnet";
				}
				String url = String.format(Constants.FRACTALBITCOIN_MEMPOOL_SPACE + "/api/address/%s/txs", networkPrefix, address);
				fetchMempoolTxs(network, address, url, "Fractal Bitcoin", "FB");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fetchMempoolTxs(String network, String address, String url, String chainName, String tokenName) {
		String result = HttpClientUtil.httpGet(url, null, TIMEOUT);
		List<Map<String, Object>> txMapList = JsonUtil.fromJson(result, new TypeReference<List<Map<String, Object>>>() {});
		String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName.replace(" ", "_"), network, address);
		Date oldLastDate = this.getOldLastDate(redisKey);
		Date lastDate = null;
		for (Map<String, Object> txMap : txMapList) {
			Map<String, Object> statusMap = (Map<String, Object>) txMap.get("status");
			Boolean confirmed = MapUtils.getBoolean(statusMap, "confirmed");
			Date sendTime = null;
			if (confirmed) {
				Long blockTime = MapUtils.getLong(statusMap, "block_time");
				sendTime = new Date(blockTime * 1000);
			} else {
				sendTime = new Date();
			}
			if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
				continue;
			}
			
			String txid = MapUtils.getString(txMap, "txid");
			Long fee = MapUtils.getLong(txMap, "fee");
			String sendAddress = null;
			String toAddress = null;
			String lastInAddress = null;
			boolean in = true;
			List<Map<String, Object>> vinList = (List<Map<String, Object>>) txMap.get("vin");
			for (Map<String, Object> vinMap : vinList) {
				Map<String, Object> prevoutMap = (Map<String, Object>) vinMap.get("prevout");
				String inAddress = MapUtils.getString(prevoutMap, "scriptpubkey_address");
				if (StringUtils.equals(address, inAddress)) {
					in = false;
					sendAddress = address;
					break;
				}
				lastInAddress = inAddress;
			}
			if (in) {
				sendAddress = lastInAddress;
			}
			Long amount = 0l;
			List<Map<String, Object>> voutList = (List<Map<String, Object>>) txMap.get("vout");
			for (Map<String, Object> voutMap : voutList) {
				Long value = MapUtils.getLong(voutMap, "value");
				if (value > 546) {
					String outAddress = MapUtils.getString(voutMap, "scriptpubkey_address");
					toAddress = outAddress;
					amount = value;
					break;
				}
			}
			Long totalAmount = amount + fee;
			Transaction trans = this.buildTransaction(network, txid, BcUtils.toBtcStr(fee), confirmed, sendTime, sendAddress, toAddress,
					in, BcUtils.toBtcStr(amount), chainName, BcUtils.toBtcStr(totalAmount), tokenName, tokenName, null);
			Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
			List<Transaction> list = this.transactionMapper.select(cond);
			if (CollectionUtils.isEmpty(list)) {
				this.transactionMapper.insertSelective(trans);
			} else {
				Transaction exists = list.get(0);
				if (!exists.getConfirmed() && confirmed) {
					Transaction update = new Transaction();
					update.setId(exists.getId());
					update.setConfirmed(true);
					this.transactionMapper.updateByPrimaryKeySelective(update);
				}
			}
			if (confirmed) {
				if (lastDate == null) {
					lastDate = sendTime;
				} else if (lastDate.before(sendTime)) { 
					lastDate = sendTime;
				}
			}
		}
		if (lastDate != null) {
			this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
		}
	}

	private Transaction buildTransaction(String network, String txid, String fee, Boolean confirmed, Date sendTime,
			String sendAddress, String toAddress, boolean in, String amount, String chainName, String totalAmount, String tokenName, String symbol, String contractAddress) {
		Transaction trans = new Transaction();
		trans.setAmount(amount);
		trans.setChain(chainName);
		trans.setConfirmed(confirmed);
		trans.setDirection(in ? 1 : 0);
		trans.setGasFee(fee);
		trans.setId(TextUtil.generateId());
		trans.setInternal(false);
		trans.setNetwork(network);
		trans.setSendAddress(sendAddress);
		trans.setSendTime(sendTime);
		trans.setSuccessed(true);
		trans.setToAddress(toAddress);
		trans.setTokenName(tokenName);
		trans.setTotalAmount(totalAmount);
		trans.setTransHex("0");
		trans.setTxid(txid);
		trans.setWalletId(Constants.DEFAULT_WALLET_ID);
		trans.setSymbol(symbol);
		trans.setContractAddress(contractAddress);
		return trans;
	}
	
	@Async
	public void asyncFetchEthereumHistory(String network, String ethAddress, String chainName, Class<? extends ChainApiConfig> clazz, String nativeCoinName, String chainShortName, Integer chainId, String endpoint) {
		try {
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("module", "account");
			paramMap.put("action", "txlist");
			paramMap.put("page", "1");
			paramMap.put("offset", "10");
			paramMap.put("address", ethAddress);
			paramMap.put("sort", "desc");
			
			List<Map<String, Object>> txMapList = null;
			if (StringUtils.isBlank(endpoint)) {
				ResponseModel rm = this.evmApiService.executeGet(network, paramMap, clazz);
				txMapList = (List<Map<String, Object>>) rm.getData();
			} else {
				ResponseModel rm = this.evmApiService.executeGet(endpoint, paramMap);
				txMapList = (List<Map<String, Object>>) rm.getData();
			}
			if (CollectionUtils.isEmpty(txMapList)) {
				return ;
			}
			String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, ethAddress);
			Date oldLastDate = this.getOldLastDate(redisKey);
			Date lastDate = null;
			for (Map<String, Object> txMap : txMapList) {
				String methodId = MapUtils.getString(txMap, "methodId");
				if (!BcUtils.isEvmTransfer(methodId)) {
					continue;
				}
				String timeStamp = MapUtils.getString(txMap, "timeStamp");
				Date sendTime = new Date(Long.parseLong(timeStamp) * 1000);
				if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
					continue;
				}
				String txid = MapUtils.getString(txMap, "hash");
				String sendAddress = MapUtils.getString(txMap, "from");
				String gasUsed = MapUtils.getString(txMap, "gasUsed");
				String gasPrice = MapUtils.getString(txMap, "gasPrice");
				Long gasUsedLong = Long.parseLong(gasUsed);
				Long gasPriceLong = Long.parseLong(gasPrice);
				Long fee = gasPriceLong * gasUsedLong;
				boolean in = !StringUtils.equals(ethAddress, sendAddress);
				String value = null, toAddress = null, contractAddress = null, tokenName = null, symbol = null, amount = null, totalAmount = null;
				if (BcUtils.isEvmNative(methodId)) {
					toAddress = MapUtils.getString(txMap, "to");
					value = MapUtils.getString(txMap, "value");
					tokenName = nativeCoinName;
					symbol = nativeCoinName;
					Long valueLong = Long.parseLong(value);
					amount = BcUtils.toEther(valueLong);
					totalAmount = BcUtils.toEther(valueLong + fee);
				} else {
					String input = MapUtils.getString(txMap, "input");
					toAddress = BcUtils.getEvmAddress(input);
					value = BcUtils.getEvmAmount(input);
					Long valueLong = Long.parseLong(value);
					contractAddress = MapUtils.getString(txMap, "to"); // contract address
					TokenInfo tokenInfo = this.tokenInfoMapper.selectOne(new TokenInfo(contractAddress, network, chainShortName));
					if (tokenInfo == null) {
						Integer chain = 1;
						tokenInfo = getTokenInfoByApi(network, contractAddress, chainShortName, chain, chainShortName);
					}
					tokenName = tokenInfo.getFullName();
					symbol = tokenInfo.getSymbol();
					amount = BcUtils.convert(valueLong, tokenInfo.getDecimals()).toString();
					totalAmount = amount;
				}
				
				Transaction trans = this.buildTransaction(network, txid, BcUtils.toEther(fee), true, sendTime, sendAddress, toAddress,
						in, amount, chainName, totalAmount, tokenName, symbol, contractAddress);
				Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
				List<Transaction> list = this.transactionMapper.select(cond);
				if (CollectionUtils.isEmpty(list)) {
					this.transactionMapper.insertSelective(trans);
				}
				if (lastDate == null) {
					lastDate = sendTime;
				} else if (lastDate.before(sendTime)) { 
					lastDate = sendTime;
				}
			}
			
			if (lastDate != null) {
				this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private TokenInfo getTokenInfoByApi(String network, String contractAddress, String chainShortName, Integer chain, String chainName) {
		Map<String, Object> paramMap2 = new HashMap<>();
		paramMap2.put("chainShortName", chainShortName);
		paramMap2.put("tokenContractAddress", contractAddress);
		ResponseModel rm2 = this.oklinkService.executeGet("token/token-list", paramMap2);
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) rm2.getData();
		Map<String, Object> tokenMap = dataList.get(0);
		List<Map<String, Object>> tokenList = (List<Map<String, Object>>) tokenMap.get("tokenList");
		Map<String, Object> tokenInfoMap = tokenList.get(0);
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setChain(chain);
		tokenInfo.setChainName(chainName);
		tokenInfo.setContractAddress(contractAddress);
		tokenInfo.setDecimals(MapUtils.getInteger(tokenInfoMap, "precision"));
		tokenInfo.setFullName(MapUtils.getString(tokenInfoMap, "tokenFullName"));
		tokenInfo.setImg(MapUtils.getString(tokenInfoMap, "logoUrl"));
		tokenInfo.setInvisable(false);
		tokenInfo.setNetwork(network);
		tokenInfo.setSno(999);
		tokenInfo.setSymbol(MapUtils.getString(tokenInfoMap, "token"));
		tokenInfo.setTotalSupply(MapUtils.getString(tokenInfoMap, "totalSupply"));
		this.tokenInfoMapper.insertSelective(tokenInfo);
		return tokenInfo;
	}

	private Date getOldLastDate(String redisKey) {
		String redisValue = this.redisTemplate.opsForValue().get(redisKey);
		Date oldLastDate = null;
		if (StringUtils.isNotBlank(redisValue)) {
			oldLastDate = new Date(Long.parseLong(redisValue));
		}
		return oldLastDate;
	}
	
	@SuppressWarnings("rawtypes")
	@Async
	public void asyncFetchTronHistory(String network, String tronAddress) {
		try {
			String chainName = "Tron";
			String url = null;
			if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				url = "https://api.trongrid.io/v1/accounts/%s/transactions";
			} else {
				url = "https://api.shasta.trongrid.io/v1/accounts/%s/transactions";
			}
			String result = HttpClientUtil.httpGet(String.format(url, tronAddress), null, TIMEOUT);
			Map<String, Object> map = JsonUtil.fromJson2Map(result);
			List<Map> mapList = (List<Map>) map.get("data");
			String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, tronAddress);
			Date oldLastDate = this.getOldLastDate(redisKey);
			Date lastDate = null;
			for (Map txMap : mapList) {
				String txid = MapUtils.getString(txMap, "txID");
				Map<String, Object> rawMap = (Map<String, Object>) txMap.get("raw_data");
				Long timestamp = MapUtils.getLong(rawMap, "timestamp");
				Date sendTime = new Date(timestamp);
				if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
					continue;
				}
				List<Map<String, Object>> contractList = (List<Map<String, Object>>) rawMap.get("contract");
				Map<String, Object> contractMap = contractList.get(0);
				String type = MapUtils.getString(contractMap, "type");
				Map<String, Object> paramMap = (Map<String, Object>) contractMap.get("parameter");
				Map<String, Object> valueMap = (Map<String, Object>) paramMap.get("value");
				String sendAddressHex = MapUtils.getString(valueMap, "owner_address");
				String sendAddress = BcUtils.hex2Base58(sendAddressHex);
				boolean in = !StringUtils.equals(tronAddress, sendAddress);
				List<Map<String, Object>> retList = (List<Map<String, Object>>) txMap.get("ret");
				Map<String, Object> retMap = retList.get(0);
				Long fee = MapUtils.getLong(retMap, "fee");
				String toAddress = null, contractAddress = null, tokenName = null, symbol = null, amount = null, totalAmount = null;
				if (StringUtils.equals(type, "TransferContract")) { // Native
					String toAddressHex = MapUtils.getString(valueMap, "to_address");
					toAddress = BcUtils.hex2Base58(toAddressHex);
					Long value = MapUtils.getLong(valueMap, "amount");
					tokenName = "TRX";
					symbol = "TRX";
					amount = BcUtils.toTrx(value);
					totalAmount = BcUtils.toTrx(value + fee);
				} else { // Token
					String contractAddressHex = MapUtils.getString(valueMap, "contract_address");
					contractAddress = BcUtils.hex2Base58(contractAddressHex);
					String data = "0x" + MapUtils.getString(valueMap, "data");
					String toAddressHex = BcUtils.getEvmAddress(data).substring(2);
					toAddress = BcUtils.hex2Base58("0x" + toAddressHex);
					String value = BcUtils.getEvmAmount(data);
					Long valueLong = Long.parseLong(value);
					TokenInfo tokenInfo = this.tokenInfoMapper.selectOne(new TokenInfo(contractAddress, network, "TRON"));
					if (tokenInfo == null && StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
						tokenInfo = getTokenInfoByApi(network, contractAddress, "TRON", 195, "TRON");
					}
					if (tokenInfo == null) {
						continue;
					}
					tokenName = tokenInfo.getFullName();
					symbol = tokenInfo.getSymbol();
					amount = BcUtils.convert(valueLong, tokenInfo.getDecimals()).toString();
					totalAmount = amount;
				}
				
				Transaction trans = this.buildTransaction(network, txid, BcUtils.toTrx(fee), true, sendTime, sendAddress, toAddress,
						in, amount, "Tron", totalAmount, tokenName, symbol, contractAddress);
				Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
				List<Transaction> list = this.transactionMapper.select(cond);
				if (CollectionUtils.isEmpty(list)) {
					this.transactionMapper.insertSelective(trans);
				}
				if (lastDate == null) {
					lastDate = sendTime;
				} else if (lastDate.before(sendTime)) { 
					lastDate = sendTime;
				}
			}
			if (lastDate != null) {
				this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Async
	public void asyncFetchSidraHistory(String network, String sidraAddress) {
		if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) { // only support sidra chain mainnet
//			String json = HttpClientUtil.httpGet("https://ledger.sidrachain.com/api/v2/addresses/" + sidraAddress + "/transactions", null);
		}
	}
	
	@Async
	public void asyncFetchLtcHistory(String network, String ltcAddress) {
		try {
			if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				String url = String.format(Constants.LITECOIN_MEMPOOL_SPACE + "/api/address/%s/txs", ltcAddress);
				fetchMempoolTxs(network, ltcAddress, url, "LTC ", "LTC");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Async
	public void asyncFetchChainHistory(String network, String address, String chainName) {
		try {
			this.fetchOklinkTranslist(network, address, chainName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void fetchOklinkTranslist(String network, String address, String chainName) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("chainShortName", chainName);
		paramMap.put("address", address);
		ResponseModel rm = oklinkService.executeGet("address/transaction-list", paramMap);
		if (rm.isSuccess()) {
			List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) rm.getData();
			Map<String, Object> dataMap = dataMapList.get(0);
			List<Map<String, Object>> transList = (List<Map<String, Object>>) dataMap.get("transactionLists");
			String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, address);
			Date oldLastDate = this.getOldLastDate(redisKey);
			Date lastDate = null;
			for (Map<String, Object> tranMap : transList) {
				String transactionTime = MapUtils.getString(tranMap, "transactionTime");
				Date sendTime = new Date(Long.parseLong(transactionTime));
				if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
					continue;
				}
				String txid = MapUtils.getString(tranMap, "txId");
				String sendAddress = MapUtils.getString(tranMap, "from");
				boolean in = !StringUtils.equals(address, sendAddress);
				String to = MapUtils.getString(tranMap, "to");
				String arr[] = to.split(",");
				String toAddress = null;
				for (String t : arr) {
					if (!StringUtils.equals(t, address)) {
						toAddress = t;
						break;
					}
				}
				if (toAddress == null) {
					toAddress = arr[0];
				}
				
				String symbol = MapUtils.getString(tranMap, "transactionSymbol");
				String txFee = MapUtils.getString(tranMap, "txFee");
				String amount = MapUtils.getString(tranMap, "amount");
				String totalAmount = amount;
				if (!in) {
					totalAmount = (Double.parseDouble(amount) + Double.parseDouble(txFee)) + "";
				}
				Transaction trans = this.buildTransaction(network, txid, txFee, true, sendTime, sendAddress, toAddress,
						in, amount, chainName, totalAmount, symbol, symbol, null);
				Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
				List<Transaction> list = this.transactionMapper.select(cond);
				if (CollectionUtils.isEmpty(list)) {
					this.transactionMapper.insertSelective(trans);
				}
				if (lastDate == null) {
					lastDate = sendTime;
				} else if (lastDate.before(sendTime)) { 
					lastDate = sendTime;
				}
			}
			if (lastDate != null) {
				this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
			}
		}
	}
	
	private void fetchAptosTranslist(String network, String address, String chainName) {
		String result = HttpClientUtil.httpGet(Constants.APTOS_API_ENDPOINT + "/v1/accounts/" + address + "/transactions" + address, null, 5000);
		List<Map<String, Object>> transList = JsonUtil.fromJson(result, new TypeReference<List<Map<String, Object>>>() {});
		String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, address);
		Date oldLastDate = this.getOldLastDate(redisKey);
		Date lastDate = null;
		for (Map<String, Object> tranMap : transList) {
			Map<String, Object> payloadMap = (Map<String, Object>) tranMap.get("payload");
			String function = MapUtils.getString(payloadMap, "function");
			if (!StringUtils.equals(function, "0x1::aptos_account::transfer")) {
				continue;
			}
			
			String transactionTime = MapUtils.getString(tranMap, "timestamp");
			Date sendTime = new Date(Long.parseLong(transactionTime));
			if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
				continue;
			}
			String txid = MapUtils.getString(tranMap, "hash");
			String sendAddress = MapUtils.getString(payloadMap, "sender");
			boolean in = !StringUtils.equals(address, sendAddress);
			List<Object> arguments = (List<Object>) payloadMap.get("arguments");
			String toAddress = arguments.get(0).toString();
			Long feeLong = MapUtils.getLong(tranMap, "gas_unit_price") * MapUtils.getLong(tranMap, "gas_used");
			String txFee = BcUtils.convert(feeLong, 8).toString();
			String amount = BcUtils.convert(Long.parseLong(arguments.get(1).toString()), 8).toString();
			String totalAmount = amount;
			if (!in) {
				totalAmount = (Double.parseDouble(amount) + Double.parseDouble(txFee)) + "";
			}
			String symbol = "APT";
			Transaction trans = this.buildTransaction(network, txid, txFee, true, sendTime, sendAddress, toAddress,
					in, amount, chainName, totalAmount, symbol, symbol, null);
			Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
			List<Transaction> list = this.transactionMapper.select(cond);
			if (CollectionUtils.isEmpty(list)) {
				this.transactionMapper.insertSelective(trans);
			}
			if (lastDate == null) {
				lastDate = sendTime;
			} else if (lastDate.before(sendTime)) { 
				lastDate = sendTime;
			}
		}
		if (lastDate != null) {
			this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
		}
	}
	
	private void fetchDogeTranslist(String network, String address, String chainName) {
		String result = HttpClientUtil.httpGet(Constants.TATUM_API_ENDPOINT + "/v3/dogecoin/transaction/address/" + address + "?pageSize=10", null, new Header[] { new BasicHeader("x-api-key", Constants.TATUM_APIKEY) }, 5000);
		List<Map<String, Object>> transList = JsonUtil.fromJson(result, new TypeReference<List<Map<String, Object>>>() {});
		String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, address);
		Date oldLastDate = this.getOldLastDate(redisKey);
		Date lastDate = null;
		for (Map<String, Object> tranMap : transList) {
			String transactionTime = MapUtils.getString(tranMap, "time");
			Date sendTime = new Date(Long.parseLong(transactionTime) * 1000);
			if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
				continue;
			}
			String txid = MapUtils.getString(tranMap, "hash");
			List<Map<String, Object>> inputs = (List<Map<String, Object>>) tranMap.get("inputs");
			Map<String, Object> inputMap = inputs.get(0);
			Map<String, Object> coinMap = (Map<String, Object>) inputMap.get("coin");
			String sendAddress = MapUtils.getString(coinMap, "address");
			boolean in = !StringUtils.equals(address, sendAddress);
			List<Map<String, Object>> voutList = (List<Map<String, Object>>) tranMap.get("outputs");
			Map<String, Object> outMap = voutList.get(0);
			String toAddress = MapUtils.getString(outMap, "address");
			String amount = MapUtils.getString(outMap, "value");
			String symbol = "DOGE";
			String txFee = MapUtils.getString(tranMap, "fee");
			String totalAmount = amount;
			if (!in) {
				totalAmount = (Double.parseDouble(amount) + Double.parseDouble(txFee)) + "";
			}
			Transaction trans = this.buildTransaction(network, txid, txFee, true, sendTime, sendAddress, toAddress,
					in, amount, chainName, totalAmount, symbol, symbol, null);
			Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
			List<Transaction> list = this.transactionMapper.select(cond);
			if (CollectionUtils.isEmpty(list)) {
				this.transactionMapper.insertSelective(trans);
			}
			if (lastDate == null) {
				lastDate = sendTime;
			} else if (lastDate.before(sendTime)) { 
				lastDate = sendTime;
			}
		}
		if (lastDate != null) {
			this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
		}
	}
	
	@Async
	public void asyncFetchBsvHistory(String network, String bsvAddress) {
		try {
			String chainName = "BSV";
			if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				String result = HttpClientUtil.httpGet("https://api.bitails.io/address/" + bsvAddress + "/history", null, TIMEOUT);
				Map<String, Object> map = JsonUtil.fromJson2Map(result);
				List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get("history");
				String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, bsvAddress);
				Date oldLastDate = this.getOldLastDate(redisKey);
				Date lastDate = null;
				for (Map<String, Object> tranMap : mapList) {
					Long time = MapUtils.getLong(tranMap, "time");
					Date sendTime = new Date(time * 1000);
					if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
						continue;
					}
					String txid = MapUtils.getString(tranMap, "txid");
					String json = HttpClientUtil.httpGet("https://api.bitails.io/tx/" + txid, null, TIMEOUT);
					Map<String, Object> infoMap = JsonUtil.fromJson2Map(json);
					Long fee = MapUtils.getLong(infoMap, "fee");
					List<Map<String, Object>> inputList = (List<Map<String, Object>>) infoMap.get("inputs");
					Map<String, Object> inputMap = inputList.get(0);
					Map<String, Object> sourceMap = (Map<String, Object>) inputMap.get("source");
					String script = MapUtils.getString(sourceMap, "script");
					String sendAddress = BcUtils.toBtcAddr(script);
					String toAddress = null;
					Long satoshis = null;
					List<Map<String, Object>> outputList = (List<Map<String, Object>>) infoMap.get("outputs");
					for (Map<String, Object> outputMap : outputList) {
						String toscript = MapUtils.getString(outputMap, "script");
						String to = BcUtils.toBtcAddr(toscript);
						if (!StringUtils.equals(sendAddress, to)) {
							toAddress = to;
							satoshis = MapUtils.getLong(outputMap, "satoshis");
							break;
						}
					}
					if (toAddress == null) {
						Map<String, Object> outputMap = outputList.get(0);
						String toscript = MapUtils.getString(outputMap, "script");
						toAddress = BcUtils.toBtcAddr(toscript);
						satoshis = MapUtils.getLong(outputMap, "satoshis");
					}
					boolean in = !StringUtils.equals(bsvAddress, sendAddress);
					String amount = BcUtils.toBtcStr(satoshis);
					String totalAmount = amount;
					if (!in) {
						totalAmount = BcUtils.toBtcStr(satoshis + fee);
					}
					
					Transaction trans = this.buildTransaction(network, txid, BcUtils.toBtcStr(fee), true, sendTime, sendAddress, toAddress,
							in, amount, chainName, totalAmount, chainName, chainName, null);
					Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
					List<Transaction> list = this.transactionMapper.select(cond);
					if (CollectionUtils.isEmpty(list)) {
						this.transactionMapper.insertSelective(trans);
					}
					if (lastDate == null) {
						lastDate = sendTime;
					} else if (lastDate.before(sendTime)) { 
						lastDate = sendTime;
					}
				}
				if (lastDate != null) {
					this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static final String XRP_DATETIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	static final String XRP_TESTNET_API = "https://s.altnet.rippletest.net:51234/";
	static final String XRP_MAINNET_API = "https://xrplcluster.com/";
	
	@Async
	public void asyncFetchXrpHistory(String network, String xrpAddress) {
		try {
			String chainName = "XRP";
			String url = null;
			if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				url = XRP_MAINNET_API;
			} else {
				url = XRP_TESTNET_API;
			}
			Map<String, Object> paramMap = new HashMap<>();
			paramMap.put("method", "account_tx");
			List<Map<String, Object>> mapList = new ArrayList<>();
			Map<String, Object> childParamMap = new HashMap<>();
			childParamMap.put("account", xrpAddress);
			childParamMap.put("ledger_index_min", -1);
			childParamMap.put("ledger_index_max", -1);
			childParamMap.put("limit", 10);
			childParamMap.put("api_version", 2);
			mapList.add(childParamMap);
			paramMap.put("params", mapList);
			String result = HttpClientUtil.httpPost(MediaType.APPLICATION_JSON, url, JsonUtil.toJson(paramMap), TIMEOUT);
			Map<String, Object> map = JsonUtil.fromJson2Map(result);
			Map<String, Object> resultMap = (Map<String, Object>) map.get("result");
			List<Map<String, Object>> transList = (List<Map<String, Object>>) resultMap.get("transactions");
			if (CollectionUtils.isNotEmpty(transList)) {
				String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, chainName, network, xrpAddress);
				Date oldLastDate = this.getOldLastDate(redisKey);
				Date lastDate = null;
				for (Map<String, Object> tranMap : transList) {
					Map<String, Object> metaMap = (Map<String, Object>) tranMap.get("meta");
					String TransactionResult = MapUtils.getString(metaMap, "TransactionResult");
					if (!StringUtils.equals(TransactionResult, "tesSUCCESS")) {
						continue;
					}
					String close_time_iso = MapUtils.getString(tranMap, "close_time_iso");
					Date sendTime = DateUtils.parseDate(close_time_iso, XRP_DATETIME_FORMAT);
					if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
						continue;
					}
					String txid = MapUtils.getString(tranMap, "hash");
					Long delivered_amount = MapUtils.getLong(metaMap, "delivered_amount");
					Map<String, Object> tx_jsonMap = (Map<String, Object>) tranMap.get("tx_json");
					String sendAddress = MapUtils.getString(tx_jsonMap, "Account");
					String toAddress = MapUtils.getString(tx_jsonMap, "Destination");
					Long fee = MapUtils.getLong(tx_jsonMap, "Fee");
					boolean in = !StringUtils.equals(xrpAddress, sendAddress);
					Long totalAmount = delivered_amount;
					if (!in) {
						totalAmount += fee;
					}
					Transaction trans = this.buildTransaction(network, txid, BcUtils.toXrp(fee), true, sendTime, sendAddress, toAddress,
							in, BcUtils.toXrp(delivered_amount), chainName, BcUtils.toXrp(totalAmount), chainName, chainName, null);
					Transaction cond = new Transaction(trans.getChain(), trans.getNetwork(), trans.getTxid());
					List<Transaction> list = this.transactionMapper.select(cond);
					if (CollectionUtils.isEmpty(list)) {
						this.transactionMapper.insertSelective(trans);
					}
					if (lastDate == null) {
						lastDate = sendTime;
					} else if (lastDate.before(sendTime)) { 
						lastDate = sendTime;
					}
				}
				if (lastDate != null) {
					this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Async
	public void asyncFetchCardanoHistory(String network, String address) {
		try {
			if (StringUtils.equals(network, Constants.NETWORK_MAINNET)) {
				Header[] headerArr = new Header[] { new BasicHeader("Project_id", Constants.Cardano.PROJECT_ID) };
				String json = HttpClientUtil.httpGet(String.format("%s/addresses/%s/transactions", Constants.Cardano.API_ENDPOINT, address), null, headerArr, TIMEOUT);
				List<Map> mapList = JsonUtil.fromJsonList(json, Map.class);
				String redisKey = String.format(TXS_LASTTIME_KEY_FORMAT, Constants.Cardano.CHAIN_NAME, network, address);
				Date oldLastDate = this.getOldLastDate(redisKey);
				Date lastDate = null;
				for (Map map : mapList) {
					Long block_time = MapUtils.getLong(map, "block_time");
					Date sendTime = new Date(block_time * 1000);
					if (oldLastDate != null && sendTime.getTime() <= oldLastDate.getTime()) { // tx has been got, ignore and continue.
						continue;
					}
					String txid = MapUtils.getString(map, "tx_hash");
					Transaction cond = new Transaction(Constants.Cardano.CHAIN_NAME, network, txid);
					List<Transaction> list = this.transactionMapper.select(cond);
					if (CollectionUtils.isNotEmpty(list)) {
						continue;
					}
					json = HttpClientUtil.httpGet(String.format("%s/txs/%s/utxos", Constants.Cardano.API_ENDPOINT, txid), null, headerArr, TIMEOUT);
					Map<String, Object> transMap = JsonUtil.fromJson2Map(json);
					List<Map<String, Object>> inputList = (List<Map<String, Object>>) transMap.get("inputs");
					Map<String, Object> inputMap = inputList.get(0);
					String sendAddress = MapUtils.getString(inputMap, "address");
					boolean in = !StringUtils.equals(address, sendAddress);
					List<Map<String, Object>> outputList = (List<Map<String, Object>>) transMap.get("outputs");
					Map<String, Object> outputMap = outputList.get(0);
					String toAddress = MapUtils.getString(outputMap, "address");
					List<Map<String, Object>> amountList = (List<Map<String, Object>>) outputMap.get("amount");
					Map<String, Object> amountMap = amountList.get(0);
					String amount = MapUtils.getString(amountMap, "quantity");
					Long amountLong = Long.parseLong(amount);
					json = HttpClientUtil.httpGet(String.format("%s/txs/%s", Constants.Cardano.API_ENDPOINT, txid), null, headerArr, TIMEOUT);
					Map<String, Object> feeMap = JsonUtil.fromJson2Map(json);
					String fee = MapUtils.getString(feeMap, "fees");
					Long feeLong = Long.parseLong(fee);
					Long totalAmount = amountLong;
					if (!in) {
						totalAmount += feeLong;
					}
					Transaction trans = this.buildTransaction(network, txid, BcUtils.toXrp(feeLong), true, sendTime, sendAddress, toAddress,
							in, BcUtils.toXrp(amountLong), Constants.Cardano.CHAIN_NAME, BcUtils.toXrp(totalAmount), Constants.Cardano.SYMBOL, Constants.Cardano.SYMBOL, null);
					this.transactionMapper.insertSelective(trans);
					if (lastDate == null) {
						lastDate = sendTime;
					} else if (lastDate.before(sendTime)) { 
						lastDate = sendTime;
					}
				}
				if (lastDate != null) {
					this.redisTemplate.opsForValue().set(redisKey, String.valueOf(lastDate.getTime()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
