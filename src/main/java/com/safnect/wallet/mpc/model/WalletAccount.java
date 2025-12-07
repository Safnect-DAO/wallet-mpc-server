package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

public class WalletAccount implements Comparable<WalletAccount> {

	public WalletAccount(String walletId, Integer accountIndex) {
		super();
		this.walletId = walletId;
		this.accountIndex = accountIndex;
	}

	public WalletAccount(String walletId) {
		super();
		this.walletId = walletId;
	}

	public WalletAccount() {
		super();
	}

	public WalletAccount(String walletId, Integer accountIndex, String alias, Date createDatetime, Integer sno) {
		super();
		this.walletId = walletId;
		this.accountIndex = accountIndex;
		this.alias = alias;
		this.createDatetime = createDatetime;
		this.sno = sno;
	}

	@Id
	String walletId;
	
	@Id
	Integer accountIndex;
	
	String alias;
	
	Date createDatetime;
	
	Integer sno;

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public Integer getAccountIndex() {
		return accountIndex;
	}

	public void setAccountIndex(Integer accountIndex) {
		this.accountIndex = accountIndex;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}
	
	@Override
	public int compareTo(WalletAccount o) {
		if (this.sno == null || o.getSno() == null) {
			return 0;
		}
		return Integer.compare(this.sno, o.getSno());
	}
}
