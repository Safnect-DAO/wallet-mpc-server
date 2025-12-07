package com.safnect.wallet.mpc.dto;

public class TxToConfirm {

	public TxToConfirm() {
		super();
	}
	
	public TxToConfirm(String transId, String txid, String network, String chain) {
		super();
		this.transId = transId;
		this.txid = txid;
		this.network = network;
		this.chain = chain;
	}

	String transId, txid, network, chain;

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

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getChain() {
		return chain;
	}

	public void setChain(String chain) {
		this.chain = chain;
	}
}
