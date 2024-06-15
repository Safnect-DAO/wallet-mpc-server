package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

/**
 * 钱包
 * @author shiwe
 *
 */
public class Wallet {
	
	public Wallet() {
		super();
	}

	public Wallet(String walletId, String publicKey, String pkSharding, Date createDatetime) {
		super();
		this.walletId = walletId;
		this.publicKey = publicKey;
		this.pkSharding = pkSharding;
		this.createDatetime = createDatetime;
	}

	/* 钱包id */
	@Id
	String walletId;
	
	/** 公钥，私钥分片 */
	String publicKey, pkSharding;
	
	/** 创建时间 */
	Date createDatetime;

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPkSharding() {
		return pkSharding;
	}

	public void setPkSharding(String pkSharding) {
		this.pkSharding = pkSharding;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
}
