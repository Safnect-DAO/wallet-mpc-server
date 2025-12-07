package com.safnect.wallet.mpc.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Utils;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.springframework.data.redis.core.RedisTemplate;

public class BcUtils {
	
	static final DecimalFormat BTC_DF = new DecimalFormat("0.########");
	static final DecimalFormat ETH_DF = new DecimalFormat("0.############");
	static final DecimalFormat TRX_DF = new DecimalFormat("0.######");

	public static List<Map<String, Object>> removeMarkUtxo(RedisTemplate<String, String> redisTempalte, String network, List<Map<String, Object>> mapList, String chain, String sortName) {
		List<Map<String, Object>> newList = new ArrayList<>();
		for (int i=0; i<mapList.size(); i++) {
			Map<String, Object> utxoMap = mapList.get(i);
			String txid = MapUtils.getString(utxoMap, "txid");
			Integer vout = MapUtils.getInteger(utxoMap, "vout");
			String key = String.format(Constants.CHAIN_UTXO_KEY_FORMAT, chain, network, txid, vout);
			if (!redisTempalte.hasKey(key)) {
				newList.add(utxoMap);
			}
		}
		newList.sort(Comparator.comparing(itemMap -> MapUtils.getLong(itemMap, sortName)));
		return newList;
	}
	
	public static List<Map<String, Object>> pickUtxo(Long amount, List<Map<String, Object>> mapList, String key) {
		// 指定Amount，匹配近似数额
		List<Map<String, Object>> dataList = new ArrayList<>();
		for (Map<String, Object> utxoMap : mapList) {
			Long satoshi = MapUtils.getLong(utxoMap, key);
			if (satoshi - amount > 0) {
				dataList.add(utxoMap);
				break;
			}
		}
		
		if (CollectionUtils.isEmpty(dataList)) {
			// 没有匹配到合适的单个UTXO
			Long totalSatoshi = 0l;
			for (Map<String, Object> utxoMap : mapList) {
				Long satoshi = MapUtils.getLong(utxoMap, key);
				totalSatoshi += satoshi;
				dataList.add(utxoMap);
				if (totalSatoshi - amount > 0) {
					break;
				}
			}
		}
		return dataList;
	}
	
	public static Double toBtc(Long satoshi) {
		return satoshi / 100_000_000.0;
	}
	
	public static String toBtcStr(Long satoshi) {
		Double d = toBtc(satoshi);
		return BTC_DF.format(d);
	}
	
	public static String toEther(Long wei) {
		Double d = wei / 1_000_000_000_000_000_000.0;
		return ETH_DF.format(d);
	}
	
	public static String hex2Base58(String hex) {
		int index = 0x41;
		byte[] bytes = Utils.HEX.decode(hex.substring(2));
		return Base58.encodeChecked(index, bytes);
	}
	
	public static String toTrx(Long sun) {
		Double d = sun / 1_000_000.0;
		return TRX_DF.format(d);
	}
	
	public static String toXrp(Long sun) {
		Double d = sun / 1_000_000.0;
		return TRX_DF.format(d);
	}
	
	public static boolean isEvmTransfer(String methodId) {
		return StringUtils.equals(methodId, "0x") || StringUtils.equals(methodId, Constants.EVM_METHOD_ID);
	}
	
	public static boolean isEvmNative(String methodId) {
		return StringUtils.equals(methodId, "0x");
	}
	
	public static boolean isEvmToken(String methodId) {
		return StringUtils.equals(methodId, Constants.EVM_METHOD_ID);
	}
	
	public static String getEvmAddress(String data) {
        String paramsData = data.substring(10);
        String toAddressHex = paramsData.substring(0, 64).replaceFirst("^0+", "");
        return "0x" + toAddressHex;
	}
	
	public static String getEvmAmount(String data) {
		return Long.valueOf(data.substring(74, 138), 16).toString();
	}
	
	public static Double convert(Long amount, int decimals) {
		return amount / Math.pow(10, decimals);
	}
	
	public static String toBtcAddr(String hexString) {
		Script script = new Script(hexStringToByteArray(hexString));
		return script.getToAddress(MainNetParams.get()).toString();
	}
	
	public static byte[] hexStringToByteArray(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        int len = hexString.length();
        byte[] bytes = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            // 提取两个字符并转换为字节
            String hexByte = hexString.substring(i, i + 2);
            bytes[i / 2] = (byte) Integer.parseInt(hexByte, 16);
        }

        return bytes;
    }
}
