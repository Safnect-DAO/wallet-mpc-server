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
import com.safnect.wallet.mpc.mapper.WalletAccountMapper;
import com.safnect.wallet.mpc.mapper.WalletMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;
import com.safnect.wallet.mpc.service.WalletService;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.HttpClientUtil;

/**
 * 插件钱包
 * @author shiwe
 *
 */
@RestController
@RequestMapping("extensions")
public class ExtensionsController {
	
	@Autowired
	WalletMapper walletMapper;
	
	@Autowired
	WalletService walletService;
	
	@Autowired
	WalletAccountMapper walletAccountMapper;
	
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
		wallet.setAccountIndex(0);
		wallet.setSourceApp(Constants.SOURCE_APP_EXTENSION);
		wallet.setShardType(1);
		wallet.setType(2);
		wallet.setAlias("Wallet 01");
		if (!this.walletMapper.existsWithPrimaryKey(walletId)) {
			wallet.setCreateDatetime(new Date());
			this.walletMapper.insertSelective(wallet);
		} else {
			this.walletMapper.updateByPrimaryKeySelective(wallet);
		}
		return ResponseModel.success();
	}
	
	/**
	 * 钱包登记
	 * @param walletId
	 * @param publicKey
	 * @param pkSharding
	 * @return
	 */
	@PostMapping("v2/signup")
	public ResponseModel signup(String walletId, String publicKey, String pkSharding, String addressJson, Integer shardType, String alias, Integer sno) {
		if (StringUtils.isAnyBlank(walletId, publicKey, pkSharding, addressJson)) {
			return ResponseModel.fail601();
		}
		if (sno == null) {
			sno = 0;
		}
		Wallet wallet = new Wallet(walletId, publicKey, pkSharding, null);
		wallet.setAccountIndex(0);
		wallet.setShardType(shardType);
		wallet.setSourceApp(Constants.SOURCE_APP_EXTENSION);
		wallet.setSno(sno);
		if (StringUtils.isBlank(alias)) {
			alias = walletId.substring(walletId.lastIndexOf("-") + 1);
		}
		wallet.setAlias(alias);
		if (shardType == 1 || shardType == 3) {
			wallet.setType(2);
		} else {
			wallet.setType(1);
		}
		if (!this.walletMapper.existsWithPrimaryKey(walletId)) {
			Date now = new Date();
			wallet.setCreateDatetime(now);
			this.walletMapper.insertSelective(wallet);
			this.walletService.addWalletAddress(addressJson, walletId);
			WalletAccount wa = new WalletAccount(walletId, 0, "Account 01", now, 0);
			this.walletAccountMapper.insertSelective(wa);
		} else {
			this.walletMapper.updateByPrimaryKeySelective(wallet);
		}
		return ResponseModel.success();
	}
	
	/**
	 * 获取分片
	 * @param walletId
	 * @param randomStr
	 * @param signatureHex
	 * @return
	 */
	@PostMapping("sharding-verify")
	public ResponseModel shardingVerify(String walletId, String randomStr, String signatureHex) {
		Wallet wallet = this.walletMapper.selectByPrimaryKey(walletId);
		if (wallet == null) {
			return ResponseModel.fail602();
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("publicKey", wallet.getPublicKey());
		paramMap.put("signatureHex", signatureHex);
		paramMap.put("message", randomStr);
		String result = HttpClientUtil.httpPost(signatureServer + "verify-msg", paramMap);
		if (BooleanUtils.toBoolean(result)) {
			return ResponseModel.successData(wallet.getPkSharding());
		}
		return ResponseModel.fail("Invalid signature");
	}
	
	/**
	 * 签名验证
	 * @param walletId
	 * @param randomStr
	 * @param signatureHex
	 * @return
	 */
	@PostMapping("signature-verify")
	public ResponseModel signatureVerify(String walletId, String randomStr, String signatureHex) {
		Wallet wallet = this.walletMapper.selectByPrimaryKey(walletId);
		if (wallet == null) {
			return ResponseModel.fail602();
		}
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("publicKey", wallet.getPublicKey());
		paramMap.put("signatureHex", signatureHex);
		paramMap.put("message", randomStr);
		String result = HttpClientUtil.httpPost(signatureServer + "verify-msg", paramMap);
		return ResponseModel.successData(BooleanUtils.toBoolean(result));
	}
	
	/**
	 * 更新公钥（修改用户密码后调用）
	 * @param walletId
	 * @param publicKey
	 * @param pkSharding
	 * @return
	 */
	@PostMapping("pk-update")
	public ResponseModel updatePublicKey(String walletId, String publicKey) {
		if (StringUtils.isAnyBlank(walletId, publicKey)) {
			return ResponseModel.fail601();
		}
		Wallet wallet = new Wallet(walletId, publicKey, null, null);
		this.walletMapper.updateByPrimaryKeySelective(wallet);
		return ResponseModel.success();
	}
}
