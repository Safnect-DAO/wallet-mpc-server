package com.safnect.wallet.mpc.model;

import javax.persistence.Id;

public class TransactionFailure {
	
	public TransactionFailure() {
		super();
	}

	public TransactionFailure(String transId, String exInfo) {
		super();
		this.transId = transId;
		this.exInfo = exInfo;
	}

	@Id
	String transId;
	
	String exInfo;

	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getExInfo() {
		return exInfo;
	}

	public void setExInfo(String exInfo) {
		this.exInfo = exInfo;
	}

}
