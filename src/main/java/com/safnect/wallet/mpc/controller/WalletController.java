package com.safnect.wallet.mpc.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.WalletAccountMapper;
import com.safnect.wallet.mpc.mapper.WalletMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;
import com.safnect.wallet.mpc.service.WalletService;
import com.safnect.wallet.mpc.util.JsonUtil;

@RestController
@RequestMapping("wallet")
public class WalletController {

	@Autowired
	WalletMapper walletMapper;
	
	@Autowired
	WalletAccountMapper walletAccountMapper;
	
	@Autowired
	WalletService walletService;
	
	@PostMapping("update")
	public ResponseModel update(String walletId, String alias) {
		Wallet wallet = new Wallet();
		wallet.setAlias(alias);
		wallet.setWalletId(walletId);
		this.walletMapper.updateByPrimaryKeySelective(wallet);
		return ResponseModel.success();
	}
	
	@PostMapping("account-update")
	public ResponseModel updateAccount(String walletId, Integer accountIndex, String alias) {
		WalletAccount wa = new WalletAccount(walletId, accountIndex, alias, null, null);
		this.walletAccountMapper.updateByPrimaryKeySelective(wa);
		return ResponseModel.success();
	}
	
	@GetMapping("get")
	public ResponseModel get(String walletId) {
		Wallet wallet = this.walletMapper.selectByPrimaryKey(walletId);
		if (wallet == null) {
			return ResponseModel.fail602();
		}
		List<WalletAccount> waList = this.walletAccountMapper.select(new WalletAccount(walletId));
		wallet.setPublicKey(null);
		wallet.setPkSharding(null);
		wallet.setWaList(waList);
		return ResponseModel.successData(wallet);
	}
	
	@GetMapping("get-all")
	public ResponseModel getall(String walletIds) {
		if (StringUtils.isBlank(walletIds)) {
			return ResponseModel.fail601();
		}
		
		String[] arr = walletIds.split(",");
		List<String> idList = Arrays.asList(arr);
		List<Wallet> wList = this.walletMapper.getWallets(idList);
		
		List<WalletAccount> waList = this.walletMapper.getWalletAccounts(idList);
		Map<String, List<WalletAccount>> map = new HashMap<>();
		waList.forEach(item -> {
			String key = item.getWalletId();
			List<WalletAccount> list = map.get(key);
			if (list == null) {
				list = new ArrayList<>();
			}
			list.add(item);
			map.put(key, list);
		});
		wList.forEach(item -> {
			String key = item.getWalletId();
			List<WalletAccount> waList2 = map.get(key);
			if (CollectionUtils.isNotEmpty(waList2)) {
				Collections.sort(waList2);
			}
			item.setWaList(waList2);
			item.setPkSharding(null);
			item.setPublicKey(null);
		});
		Collections.sort(wList);
		return ResponseModel.successData(wList);
	}
	
	@PostMapping("index-update")
	public ResponseModel updateIndex(String jsonData) {
		if (StringUtils.isBlank(jsonData)) {
			return ResponseModel.fail601();
		}
		List<Map<String, Object>> mapList = JsonUtil.fromJson(jsonData, new TypeReference<List<Map<String, Object>>>() {});
		this.walletService.updateIndex(mapList);
		return ResponseModel.success();
	}
	
	@PostMapping("account-delete")
	public ResponseModel deleteAccount(Integer accountIndex, String walletId) {
		if (StringUtils.isAnyBlank(walletId) || accountIndex == null) {
			return ResponseModel.fail601();
		}
		this.walletAccountMapper.delete(new WalletAccount(walletId, accountIndex));
		return ResponseModel.success();
	}
}
