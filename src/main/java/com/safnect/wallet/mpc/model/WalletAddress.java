package com.safnect.wallet.mpc.model;

/**
 * 钱包地址
 * @author shiwe
 *
 */
public class WalletAddress {
	
	/** 钱包id，网络，地址 */
	String walletId, network, address;
	
	/** 有效的 */
	Boolean valid;

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
}
