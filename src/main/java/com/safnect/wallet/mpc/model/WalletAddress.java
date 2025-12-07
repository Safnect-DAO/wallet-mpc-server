package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

/**
 * 钱包地址
 * @author shiwe
 *
 */
public class WalletAddress {
	
	@Id
	String id;
	
	/** 钱包id，网络，地址 */
	String walletId, network, address, chain;
	
	/** 有效的 */
	Integer addressType;
	
	Date createDatetime;
	
	public WalletAddress() {
		super();
	}

	public WalletAddress(String id, String walletId, String network, String address, String chain, Integer addressType,
			Date createDatetime) {
		super();
		this.id = id;
		this.walletId = walletId;
		this.network = network;
		this.address = address;
		this.chain = chain;
		this.addressType = addressType;
		this.createDatetime = createDatetime;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChain() {
		return chain;
	}

	public void setChain(String chain) {
		this.chain = chain;
	}

	public Integer getAddressType() {
		return addressType;
	}

	public void setAddressType(Integer addressType) {
		this.addressType = addressType;
	}
	
}
