package com.safnect.wallet.mpc.controller.mobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.safnect.wallet.mpc.dto.ResponseModel;
import com.safnect.wallet.mpc.mapper.WalletAccountMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;
import com.safnect.wallet.mpc.model.WalletCard;
import com.safnect.wallet.mpc.service.MobileAppService;
import com.safnect.wallet.mpc.util.Constants;
import com.safnect.wallet.mpc.util.TextUtil;

/**
 * 插件钱包
 * @author shiwe
 *
 */
@RestController
@RequestMapping("mobile-app")
public class MobileAppController {

	@Autowired
	MobileAppService mobileAppService;
	
	@Autowired
	WalletAccountMapper walletAccountMapper;
	
	@PostMapping("signup")
	public ResponseModel signup(String walletId, String publicKey, String walletAlias, String cardSn1, String cardSn2, String cardSn3, String accountAlias, String addressJson, Integer sno) {
		Date now = new Date();
		Integer accountIndex = 0;
		
		if (StringUtils.isAnyBlank(walletId, cardSn1, cardSn2, addressJson)) {
			return ResponseModel.fail601();
		}
		
		if (StringUtils.isBlank(walletAlias)) {
			walletAlias = walletId.substring(walletId.length()-8, walletId.length());
		}
		
		if (StringUtils.isBlank(accountAlias)) {
			accountAlias = "Account 01";
		}
		
		if (sno == null) {
			sno = 0;
		}
		
		Wallet wallet = new Wallet();
		wallet.setAccountIndex(accountIndex);
		wallet.setAlias(walletAlias);
		wallet.setCreateDatetime(now);
		wallet.setPublicKey(publicKey);
		wallet.setSourceApp(Constants.SOURCE_APP_ANDROID);
		wallet.setWalletId(walletId);
		wallet.setSno(sno);
		
		List<WalletCard> wcList = new ArrayList<>();
		wcList.add(new WalletCard(TextUtil.generateId(), walletId, cardSn1));
		wcList.add(new WalletCard(TextUtil.generateId(), walletId, cardSn2));
		if (StringUtils.isNotBlank(cardSn3)) {
			wcList.add(new WalletCard(TextUtil.generateId(), walletId, cardSn3));
		}
		
		WalletAccount wa = new WalletAccount(walletId, accountIndex, accountAlias, now, accountIndex);
		this.mobileAppService.saveSignup(wallet, wa, wcList, addressJson);
		return ResponseModel.success();
	}
	
	@PostMapping("account-add")
	public ResponseModel addAccount(String walletId, String alias, Integer accountIndex) {
		Date now = new Date();
		if (StringUtils.isAnyBlank(walletId) || accountIndex == null) {
			return ResponseModel.fail601();
		}
		
		if (StringUtils.isBlank(alias)) {
			alias = "Account " + TextUtil.appendZero(new Long(accountIndex + 1), 2);
		}
		WalletAccount wa = new WalletAccount(walletId, accountIndex, alias, now, accountIndex);
		WalletAccount existsWa = this.walletAccountMapper.selectByPrimaryKey(wa);
		if (existsWa != null) {
			return ResponseModel.fail603();
		}
		this.mobileAppService.addAccount(wa);
		return ResponseModel.success();
	}
}
