package com.safnect.wallet.mpc.model.chain;

import javax.persistence.Id;

public class WalletChain {

	public WalletChain() {
		super();
	}

	public WalletChain(String walletId, String chainId) {
		super();
		this.walletId = walletId;
		this.chainId = chainId;
	}

	@Id
	String walletId, chainId;

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getChainId() {
		return chainId;
	}

	public void setChainId(String chainId) {
		this.chainId = chainId;
	}
}
