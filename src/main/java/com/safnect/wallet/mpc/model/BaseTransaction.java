package com.safnect.wallet.mpc.model;

import java.util.Date;

public class BaseTransaction {

	String sendAddress, toAddress, tokenName;
	
	Double amount, gasFee, totalAmount;

	Date sendTime;
	
	String txid, transHex;
	
	Integer direction;
	
	Boolean confirmed;
	
	public BaseTransaction(Transaction trans) {
		this(trans.getSendAddress(), trans.getToAddress(), trans.getTokenName(), Double.parseDouble(trans.getAmount()),
				Double.parseDouble(trans.getGasFee()), Double.parseDouble(trans.getTotalAmount()), trans.getSendTime(),
				trans.getTxid(), trans.getDirection(), trans.getConfirmed(), trans.getTransHex());
	}

	public BaseTransaction() {
		super();
	}

	public BaseTransaction(String sendAddress, String toAddress, String tokenName, Double amount, Double gasFee,
			Double totalAmount, Date sendTime, String txid, Integer direction, Boolean confirmed, String transHex) {
		super();
		this.sendAddress = sendAddress;
		this.toAddress = toAddress;
		this.tokenName = tokenName;
		this.amount = amount;
		this.gasFee = gasFee;
		this.totalAmount = totalAmount;
		this.sendTime = sendTime;
		this.txid = txid;
		this.direction = direction;
		this.confirmed = confirmed;
		this.transHex = transHex;
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

	public String getTokenName() {
		return tokenName;
	}

	public void setTokenName(String tokenName) {
		this.tokenName = tokenName;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getGasFee() {
		return gasFee;
	}

	public void setGasFee(Double gasFee) {
		this.gasFee = gasFee;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getTxid() {
		return txid;
	}

	public void setTxid(String txid) {
		this.txid = txid;
	}

	public Integer getDirection() {
		return direction;
	}

	public void setDirection(Integer direction) {
		this.direction = direction;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}
	

	public String getTransHex() {
		return transHex;
	}

	public void setTransHex(String transHex) {
		this.transHex = transHex;
	}
}
