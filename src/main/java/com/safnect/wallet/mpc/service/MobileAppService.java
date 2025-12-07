package com.safnect.wallet.mpc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.safnect.wallet.mpc.mapper.WalletAccountMapper;
import com.safnect.wallet.mpc.mapper.WalletCardMapper;
import com.safnect.wallet.mpc.mapper.WalletMapper;
import com.safnect.wallet.mpc.model.Wallet;
import com.safnect.wallet.mpc.model.WalletAccount;
import com.safnect.wallet.mpc.model.WalletCard;

@Service
public class MobileAppService {

	@Autowired
	WalletMapper walletMapper;
	
	@Autowired
	WalletAccountMapper walletAccountMapper;
	
	@Autowired
	WalletCardMapper walletCardMapper;
	
	@Autowired
	WalletService walletService;
	
	@Transactional
	public void saveSignup(Wallet wallet, WalletAccount wa, List<WalletCard> wc, String addressJson) {
		boolean exists = this.walletMapper.existsWithPrimaryKey(wallet.getWalletId());
		if (!exists) {
			this.walletMapper.insertSelective(wallet);
			this.walletAccountMapper.insertSelective(wa);
			this.walletCardMapper.insertList(wc);
			this.walletService.addWalletAddress(addressJson, wallet.getWalletId());
		}
	}
	
	@Transactional
	public void addAccount(WalletAccount wa) {
		this.walletAccountMapper.insertSelective(wa);
		Wallet wallet = new Wallet();
		wallet.setWalletId(wa.getWalletId());
		wallet.setAccountIndex(wa.getAccountIndex());
		this.walletMapper.updateByPrimaryKeySelective(wallet);
	}
}
