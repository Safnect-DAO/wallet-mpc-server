package com.safnect.wallet.mpc.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safnect.wallet.mpc.mapper.WalletAccountMapper;
import com.safnect.wallet.mpc.mapper.WalletAddressMapper;
import com.safnect.wallet.mpc.mapper.WalletMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;
import com.safnect.wallet.mpc.model.WalletAddress;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.JsonUtil;
import com.safnect.wallet.mpc.util.TextUtil;

@Service
public class WalletService {

	@Autowired
	WalletAddressMapper walletAddressMapper;
	
	@Autowired
	WalletMapper walletMapper;
	
	@Autowired
	WalletAccountMapper walletAccountMapper;
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void addWalletAddress(String addressJson, String walletId) {
		Map<String, Object> map = JsonUtil.fromJson2Map(addressJson);
		Set<String> set = map.keySet();
		List<WalletAddress> waList = new ArrayList<>();
		for (String key : set) {
			Object valueObj = map.get(key);
			if (valueObj instanceof String) {
				String address = valueObj.toString();
				waList.add(new WalletAddress(TextUtil.generateId(), walletId, Constants.NETWORK_MAINNET, address, key, 0, new Date()));
			} else if (valueObj instanceof Map) {
				Map<String, Object> addressMap = (Map<String, Object>) valueObj;
				String address = MapUtils.getString(addressMap, "address");
				waList.add(new WalletAddress(TextUtil.generateId(), walletId, Constants.NETWORK_MAINNET, address, key, 0, new Date()));
			} else {
				// 解析多地址类型，如Btc、FB
				List<Map<String, Object>> mapList = (List<Map<String, Object>>) valueObj;
				for (Map<String, Object> addrMap : mapList) {
					String address = (String) addrMap.get("address");
					String type = (String) addrMap.get("type");
					Integer addressType = convertAddressType(type);
					String network = Constants.NETWORK_MAINNET;
					if (StringUtils.endsWith(key, "Testnet")) {
						network = Constants.NETWORK_TESTNET;
					}
					waList.add(new WalletAddress(TextUtil.generateId(), walletId, network, address, key, addressType, new Date()));
				}
			}
		}
		this.walletAddressMapper.insertList(waList);
	}
	
	public static Integer convertAddressType(String type) {
		switch(type) {
		case "Legacy": return 1;
		case "Nested Segwit": return 2;
		case "Native Segwit": return 3;
		case "Taproot": return 4;
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void updateIndex(List<Map<String, Object>> mapList) {
		for (int i=0; i<mapList.size(); i++) {
			Map<String, Object> map = mapList.get(i);
			String walletId = MapUtils.getString(map, "walletId");
			Wallet wallet = new Wallet();
			wallet.setWalletId(walletId);
			wallet.setSno(i);
			this.walletMapper.updateByPrimaryKeySelective(wallet);
			List<Object> objList = (List<Object>) map.get("accounts");
			for (int j=0; j<objList.size(); j++) {
				Object obj = objList.get(j);
				Integer accountIndex = Integer.parseInt(obj.toString());
				WalletAccount wa = new WalletAccount(walletId, accountIndex, null, null, j);
				this.walletAccountMapper.updateByPrimaryKeySelective(wa);
			}
		}
	}
}
