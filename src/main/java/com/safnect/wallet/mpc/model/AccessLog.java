package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

/**
 * 访问日志
 * @author shiwe
 *
 */
public class AccessLog {
	
	public AccessLog() {
		super();
	}

	public AccessLog(String id, String walletId, String ipAddress, Date createDatetime) {
		super();
		this.id = id;
		this.walletId = walletId;
		this.ipAddress = ipAddress;
		this.createDatetime = createDatetime;
	}

	@Id
	String id;
	
	String walletId, ipAddress;
	
	Date createDatetime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
}
