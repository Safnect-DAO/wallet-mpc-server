package com.safnect.wallet.mpc.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Id;
import javax.persistence.Transient;

/**
 * 钱包
 * @author shiwe
 *
 */
public class Wallet implements Comparable<Wallet> {
	
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
	String publicKey, pkSharding, sourceApp, alias;
	
	Integer accountIndex, shardType, type, sno;

	/** 创建时间 */
	Date createDatetime;
	
	@Transient
	List<WalletAccount> waList;
	
	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	public List<WalletAccount> getWaList() {
		return waList;
	}

	public void setWaList(List<WalletAccount> waList) {
		this.waList = waList;
	}

	public Integer getShardType() {
		return shardType;
	}

	public void setShardType(Integer shardType) {
		this.shardType = shardType;
	}
	
	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getAccountIndex() {
		return accountIndex;
	}

	public void setAccountIndex(Integer accountIndex) {
		this.accountIndex = accountIndex;
	}

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

	@Override
	public int compareTo(Wallet o) {
		if (this.sno == null || o.getSno() == null) {
			return 0;
		}
		return Integer.compare(this.sno, o.getSno());
	}
	
}
