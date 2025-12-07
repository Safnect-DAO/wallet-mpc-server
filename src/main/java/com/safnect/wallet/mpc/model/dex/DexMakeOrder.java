package com.safnect.wallet.mpc.model.dex;

import java.util.Date;

import javax.persistence.Id;

public class DexMakeOrder {
	
	public DexMakeOrder() {
		super();
	}
	
	public DexMakeOrder(String orderId) {
		super();
		this.orderId = orderId;
	}

	@Id
	String id;
	
	String orderType, address, tokenId, tokenAmount, satoshis, requestId, orderId, usdFb;
	
	Date createDatetime;

	Boolean successed, taken, canceled;
	
	Integer feePerByte;

	public String getUsdFb() {
		return usdFb;
	}

	public void setUsdFb(String usdFb) {
		this.usdFb = usdFb;
	}

	public Boolean getTaken() {
		return taken;
	}

	public void setTaken(Boolean taken) {
		this.taken = taken;
	}

	public Boolean getCanceled() {
		return canceled;
	}

	public void setCanceled(Boolean canceled) {
		this.canceled = canceled;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public String getTokenAmount() {
		return tokenAmount;
	}

	public void setTokenAmount(String tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public String getSatoshis() {
		return satoshis;
	}

	public void setSatoshis(String satoshis) {
		this.satoshis = satoshis;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Date getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Boolean getSuccessed() {
		return successed;
	}

	public void setSuccessed(Boolean successed) {
		this.successed = successed;
	}

	public Integer getFeePerByte() {
		return feePerByte;
	}

	public void setFeePerByte(Integer feePerByte) {
		this.feePerByte = feePerByte;
	}
}
