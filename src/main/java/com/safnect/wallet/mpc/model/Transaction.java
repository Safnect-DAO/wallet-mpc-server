package com.safnect.wallet.mpc.model;

import java.util.Date;

import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;

public class Transaction {
	
	@Id
	String id;
	
	String chain, network, sendAddress, toAddress, walletId, tokenName, transHex, amount, gasFee, totalAmount;

	Date sendTime;
	
	Boolean successed;

	
	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public String getTransHex() {
		return transHex;
	}

	public void setTransHex(String transHex) {
		this.transHex = transHex;
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

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getWalletId() {
		return walletId;
	}

	public void setWalletId(String walletId) {
		this.walletId = walletId;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getGasFee() {
		return gasFee;
	}

	public void setGasFee(String gasFee) {
		this.gasFee = gasFee;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Boolean getSuccessed() {
		return successed;
	}

	public void setSuccessed(Boolean successed) {
		this.successed = successed;
	}
	
	public boolean isEmpty() {
		return StringUtils.isAnyBlank(this.chain, this.network, this.sendAddress, this.toAddress, this.walletId,
				this.tokenName, this.transHex, this.amount, this.gasFee, this.totalAmount);
	}
}
