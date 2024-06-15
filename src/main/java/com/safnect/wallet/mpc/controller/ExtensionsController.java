package com.safnect.wallet.mpc.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.WalletMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.util.HttpClientUtil;

@RestController
@RequestMapping("extensions")
public class ExtensionsController {
	
	@Autowired
	WalletMapper walletMapper;
	
	@Value("${signature.server}")
	String signatureServer;
	
	/**
	 * 钱包登记
	 * @param walletId
	 * @param publicKey
	 * @param pkSharding
	 * @return
	 */
	@PostMapping("signup")
	public ResponseModel signup(String walletId, String publicKey, String pkSharding) {
		if (StringUtils.isAnyBlank(walletId, publicKey, pkSharding)) {
			return ResponseModel.fail601();
		}
		Wallet wallet = new Wallet(walletId, publicKey, pkSharding, null);
		if (!this.walletMapper.existsWithPrimaryKey(walletId)) {
			wallet.setCreateDatetime(new Date());
			this.walletMapper.insertSelective(wallet);
		} else {
			this.walletMapper.updateByPrimaryKeySelective(wallet);
		}
		return ResponseModel.sucess();
	}
	
	/**
	 * 获取分片
	 * @param walletId
	 * @param randomStr
	 * @param signatureHex
	 * @return
	 */
	@PostMapping("sharding-get")
	public ResponseModel getSharding(String walletId, String randomStr, String signatureHex) {
		Wallet wallet = this.walletMapper.selectByPrimaryKey(walletId);
		if (wallet == null) {
			return ResponseModel.fail602();
		}
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("publicKey", wallet.getPublicKey());
		paramMap.put("signatureHex", signatureHex);
		paramMap.put("message", randomStr);
		String result = HttpClientUtil.httpPost(signatureServer + "verify-msg", paramMap);
		if (BooleanUtils.toBoolean(result)) {
			return ResponseModel.sucessData(wallet.getPkSharding());
		}
		return ResponseModel.fail("Invalid signature");
	}
}
