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

	public AccessLog(String id, String walletId, String ipAddress, Date createDatetime, String sourceApp) {
		super();
		this.id = id;
		this.walletId = walletId;
		this.ipAddress = ipAddress;
		this.createDatetime = createDatetime;
		this.sourceApp = sourceApp;
	}

	@Id
	String id;
	
	String walletId, ipAddress, sourceApp;

	Date createDatetime;
	
	public String getSourceApp() {
		return sourceApp;
	}

	public void setSourceApp(String sourceApp) {
		this.sourceApp = sourceApp;
	}

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
