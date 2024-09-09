package com.safnect.wallet.mpc.model;

import javax.persistence.Id;

public class TransactionSuccess {
	
	public TransactionSuccess() {
		super();
	}

	public TransactionSuccess(String transId, String txid) {
		super();
		this.transId = transId;
		this.txid = txid;
	}

	@Id
	String transId;
	
	public String getTransId() {
		return transId;
	}

	public void setTransId(String transId) {
		this.transId = transId;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	String txid;
}
